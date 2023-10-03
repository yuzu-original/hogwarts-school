package ru.hogwarts.school.dto.mapper;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.FacultyDetailDTO;
import ru.hogwarts.school.dto.FacultyNotDetailDTO;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FacultyDTOMapper {
    public FacultyDetailDTO toDetailDTO(Faculty faculty) {
        Set<Student> students = faculty.getStudents();
        return new FacultyDetailDTO(
                faculty.getId(),
                faculty.getName(),
                faculty.getColor(),
                students == null ? Collections.emptySet() : students
                        .stream()
                        .map(Student::getId)
                        .collect(Collectors.toSet())
        );
    }

    public FacultyNotDetailDTO toNotDetailDTO(Faculty faculty) {
        return new FacultyNotDetailDTO(
                faculty.getId(),
                faculty.getName(),
                faculty.getColor()
        );
    }
}
