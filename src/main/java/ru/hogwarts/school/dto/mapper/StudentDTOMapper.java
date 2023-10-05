package ru.hogwarts.school.dto.mapper;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.StudentDetailDTO;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

@Service
public class StudentDTOMapper {
    public StudentDetailDTO toDetailDTO(Student student) {
        Faculty faculty = student.getFaculty();
        return new StudentDetailDTO(
                student.getId(),
                student.getName(),
                student.getAge(),
                (faculty != null) ? faculty.getId() : null
        );
    }
}
