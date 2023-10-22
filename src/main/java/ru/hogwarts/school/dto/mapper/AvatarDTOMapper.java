package ru.hogwarts.school.dto.mapper;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.AvatarNotDetailDTO;
import ru.hogwarts.school.model.Avatar;

@Service
public class AvatarDTOMapper {
    public AvatarNotDetailDTO toNotDetail(Avatar avatar) {
        return new AvatarNotDetailDTO(
                avatar.getFilePath(),
                avatar.getFileSize(),
                avatar.getMediaType()
        );
    }
}
