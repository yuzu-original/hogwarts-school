package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.AvatarNotDetailDTO;
import ru.hogwarts.school.dto.mapper.AvatarDTOMapper;
import ru.hogwarts.school.exception.NotFoundResourceException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AvatarService {
    @Value("${avatars.dir.path}")
    private String avatarsDirPath;

    private final Logger logger = LoggerFactory.getLogger(AvatarService.class);
    private final StudentRepository studentRepository;
    private final AvatarRepository avatarRepository;
    private final AvatarDTOMapper avatarDTOMapper;

    public AvatarService(StudentRepository studentRepository, AvatarRepository avatarRepository, AvatarDTOMapper avatarDTOMapper) {
        this.studentRepository = studentRepository;
        this.avatarRepository = avatarRepository;
        this.avatarDTOMapper = avatarDTOMapper;
    }


    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        logger.info("uploadAvatar method is called");

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    String message = "Student not found";
                    logger.error(message);
                    return new NotFoundResourceException(message);
                });

        Path filePath = Path.of(avatarsDirPath, studentId + "." + getExtension(file.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        ) {
            bis.transferTo(bos);
        }

        Avatar avatar = avatarRepository.findByStudentId(studentId).orElse(new Avatar());
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(file.getBytes());

        avatarRepository.save(avatar);
    }

    public Avatar findAvatar(Long id) {
        logger.info("findAvatar method is called");

        return avatarRepository.findByStudentId(id).orElseThrow(() -> {
            String message = "Avatar not found";
            logger.error(message);
            return new NotFoundResourceException(message);
        });
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public List<AvatarNotDetailDTO> findAllByPage(Integer page, Integer pageSize) {
        logger.info("findAllByPage method is called");

        PageRequest pageRequest = PageRequest.of(page, pageSize);
        List<Avatar> avatars = avatarRepository.findAll(pageRequest).getContent();
        return avatars
                .stream()
                .map(avatarDTOMapper::toNotDetail)
                .collect(Collectors.toList());
    }
}
