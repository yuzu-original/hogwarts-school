package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.FacultyCreateDTO;
import ru.hogwarts.school.dto.FacultyDetailDTO;
import ru.hogwarts.school.dto.FacultyNotDetailDTO;
import ru.hogwarts.school.dto.StudentDetailDTO;
import ru.hogwarts.school.dto.mapper.FacultyDTOMapper;
import ru.hogwarts.school.dto.mapper.StudentDTOMapper;
import ru.hogwarts.school.exception.BadDataException;
import ru.hogwarts.school.exception.NotFoundResourceException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FacultyService {
    private final Logger logger = LoggerFactory.getLogger(FacultyService.class);
    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;
    private final FacultyDTOMapper facultyDTOMapper;
    private final StudentDTOMapper studentDTOMapper;

    public FacultyService(FacultyRepository facultyRepository, StudentRepository studentRepository, FacultyDTOMapper facultyDTOMapper, StudentDTOMapper studentDTOMapper) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
        this.facultyDTOMapper = facultyDTOMapper;
        this.studentDTOMapper = studentDTOMapper;
    }

    public FacultyDetailDTO createFaculty(FacultyCreateDTO facultyInput) {
        logger.info("createFaculty method is called");

        Faculty faculty = new Faculty();
        faculty.setName(facultyInput.getName());
        faculty.setColor(facultyInput.getColor());
        Set<Long> studentsId = facultyInput.getStudents();
        if (studentsId != null) {
            for (Long id : studentsId) {
                Student student = studentRepository.findById(id).orElse(null);
                if (student == null) {
                    String message = "Student by id=" + id + " not found";
                    logger.error(message);
                    throw new BadDataException(message);
                }
                Faculty oldFaculty = student.getFaculty();
                if (oldFaculty != null) {
                    oldFaculty.removeStudent(student);
                }
                faculty.addStudent(student);
            }
        }
        faculty = facultyRepository.save(faculty);
        return facultyDTOMapper.toDetailDTO(faculty);
    }

    public FacultyDetailDTO getFacultyById(Long id) {
        logger.info("getFacultyById method is called");

        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> {
                    String message = "Faculty not found";
                    logger.error(message);
                    return new NotFoundResourceException(message);
                });
        return facultyDTOMapper.toDetailDTO(faculty);
    }

    public Collection<FacultyNotDetailDTO> getAllFaculties() {
        logger.info("getAllFaculties method is called");

        return facultyRepository.findAll()
                .stream()
                .map(facultyDTOMapper::toNotDetailDTO)
                .collect(Collectors.toList());
    }

    public FacultyDetailDTO updateFaculty(Long id, FacultyCreateDTO facultyInput) {
        logger.info("updateFaculty method is called");

        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> {
                    String message = "Faculty not found";
                    logger.error(message);
                    return new NotFoundResourceException(message);
                });
        faculty.setName(facultyInput.getName());
        faculty.setColor(facultyInput.getColor());

        Set<Long> newStudentsId = facultyInput.getStudents();
        Set<Student> oldStudents = faculty.getStudents();
        if (newStudentsId == null) {
            newStudentsId = Collections.emptySet();
        }
        if (oldStudents == null) {
            oldStudents = Collections.emptySet();
        }
        for (Student oldStudent : Set.copyOf(oldStudents)) {
            faculty.removeStudent(oldStudent);
        }
        for (Long i : newStudentsId) {
            Student student = studentRepository.findById(i).orElse(null);
            if (student == null) {
                String message = "Student by id=" + id + " not found";
                logger.error(message);
                throw new BadDataException(message);
            }
            Faculty oldFaculty = student.getFaculty();
            if (oldFaculty != null) {
                oldFaculty.removeStudent(student);
            }
            faculty.addStudent(student);
        }

        faculty = facultyRepository.save(faculty);
        return facultyDTOMapper.toDetailDTO(faculty);
    }

    public FacultyDetailDTO removeFaculty(Long id) {
        logger.info("removeFaculty method is called");

        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> {
                    String message = "Faculty not found";
                    logger.error(message);
                    return new NotFoundResourceException(message);
                });
        if (faculty.getStudents() != null) {
            for (Student student : Set.copyOf(faculty.getStudents())) {
                faculty.removeStudent(student);
            }
        }
        facultyRepository.delete(faculty);
        return facultyDTOMapper.toDetailDTO(faculty);
    }

    public Collection<FacultyNotDetailDTO> getFacultiesByColor(String color) {
        logger.info("getFacultiesByColor method is called");

        return facultyRepository.findByColorIgnoreCase(color)
                .stream()
                .map(facultyDTOMapper::toNotDetailDTO)
                .collect(Collectors.toList());
    }

    public Collection<FacultyNotDetailDTO> getFacultiesByName(String name) {
        logger.info("getFacultiesByName method is called");

        return facultyRepository.findByNameIgnoreCase(name)
                .stream()
                .map(facultyDTOMapper::toNotDetailDTO)
                .collect(Collectors.toList());
    }

    public Collection<StudentDetailDTO> getFacultyStudentsById(Long id) {
        logger.info("getFacultyStudentsById method is called");

        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> {
                    String message = "Faculty not found";
                    logger.error(message);
                    return new NotFoundResourceException(message);
                });
        return faculty.getStudents()
                .stream()
                .map(studentDTOMapper::toDetailDTO)
                .collect(Collectors.toList());
    }
}
