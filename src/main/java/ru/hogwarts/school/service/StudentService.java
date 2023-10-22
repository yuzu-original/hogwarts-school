package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.FacultyDetailDTO;
import ru.hogwarts.school.dto.StudentCreateDTO;
import ru.hogwarts.school.dto.StudentDetailDTO;
import ru.hogwarts.school.dto.mapper.FacultyDTOMapper;
import ru.hogwarts.school.dto.mapper.StudentDTOMapper;
import ru.hogwarts.school.exception.BadDataException;
import ru.hogwarts.school.exception.NotFoundResourceException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class StudentService {
    @Value("${avatars.dir.path}")
    private String avatarsDirPath;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final AvatarRepository avatarRepository;
    private final StudentDTOMapper studentDTOMapper;
    private final FacultyDTOMapper facultyDTOMapper;

    public StudentService(StudentRepository studentRepository, FacultyRepository facultyRepository, AvatarRepository avatarRepository, StudentDTOMapper studentDTOMapper, FacultyDTOMapper facultyDTOMapper) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.avatarRepository = avatarRepository;
        this.studentDTOMapper = studentDTOMapper;
        this.facultyDTOMapper = facultyDTOMapper;
    }

    public StudentDetailDTO createStudent(StudentCreateDTO studentInput) {
        Student student = new Student();
        student.setName(studentInput.getName());
        student.setAge(studentInput.getAge());
        if (studentInput.getFaculty() != null) {
            Faculty faculty = facultyRepository
                    .findById(studentInput.getFaculty())
                    .orElseThrow(() -> new BadDataException("Faculty not found"));
            faculty.addStudent(student);
        }
        student = studentRepository.save(student);
        return studentDTOMapper.toDetailDTO(student);
    }

    public StudentDetailDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourceException("Student not found"));
        return studentDTOMapper.toDetailDTO(student);
    }

    public Collection<StudentDetailDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(studentDTOMapper::toDetailDTO)
                .collect(Collectors.toList());
    }

    public StudentDetailDTO updateStudent(Long id, StudentCreateDTO studentInput) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourceException("Student not found"));
        student.setName(studentInput.getName());
        student.setAge(studentInput.getAge());
        Faculty faculty = student.getFaculty();
        Long facultyNewId = studentInput.getFaculty();
        Faculty facultyNew = (facultyNewId != null) ? facultyRepository.findById(facultyNewId).orElse(null) : null;
        if (!Objects.equals(faculty, facultyNew)) {
            if (faculty != null) {
                faculty.removeStudent(student);
            }
            if (facultyNew != null) {
                facultyNew.addStudent(student);
            }
        }
        student = studentRepository.save(student);
        return studentDTOMapper.toDetailDTO(student);
    }

    public StudentDetailDTO removeStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourceException("Student not found"));
        Faculty faculty = student.getFaculty();
        if (faculty != null) {
            faculty.removeStudent(student);
        }
        studentRepository.delete(student);
        return studentDTOMapper.toDetailDTO(student);
    }

    public Collection<StudentDetailDTO> getStudentsByAge(Integer age) {
        return studentRepository.findByAge(age)
                .stream()
                .map(studentDTOMapper::toDetailDTO)
                .collect(Collectors.toList());
    }

    public Collection<StudentDetailDTO> getStudentsBetweenAge(Integer min, Integer max) {
        return studentRepository.findByAgeBetween(min, max)
                .stream()
                .map(studentDTOMapper::toDetailDTO)
                .collect(Collectors.toList());
    }


    public FacultyDetailDTO getStudentFacultyById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourceException("Student not found"));
        Faculty faculty = student.getFaculty();
        if (faculty == null) {
            return null;
        }
        return facultyDTOMapper.toDetailDTO(faculty);
    }

    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundResourceException("Student not found"));

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
        return avatarRepository.findByStudentId(id).orElseThrow(() -> new NotFoundResourceException("Avatar not found"));
    }

    public Long getStudentCount() {
        return studentRepository.getCount();
    }

    public Double getStudentAvgAge() {
        return studentRepository.getAvgAge();
    }

    public List<StudentDetailDTO> getLastStudents() {
        return studentRepository.getLastStudents()
                .stream()
                .map(studentDTOMapper::toDetailDTO)
                .collect(Collectors.toList());
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
