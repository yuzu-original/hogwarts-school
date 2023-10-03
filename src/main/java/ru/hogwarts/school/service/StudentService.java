package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.StudentCreateDTO;
import ru.hogwarts.school.dto.StudentDetailDTO;
import ru.hogwarts.school.dto.mapper.StudentDTOMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final StudentDTOMapper studentDTOMapper;

    public StudentService(StudentRepository studentRepository, FacultyRepository facultyRepository, StudentDTOMapper studentDTOMapper) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.studentDTOMapper = studentDTOMapper;
    }

    public StudentDetailDTO createStudent(StudentCreateDTO studentInput) {
        Student student = new Student();
        student.setName(studentInput.getName());
        student.setAge(studentInput.getAge());
        if (studentInput.getFaculty() != null) {
            Faculty faculty = facultyRepository
                    .findById(studentInput.getFaculty())
                    .orElse(null);
            if (faculty != null) {
                faculty.addStudent(student);
            } else {
                return null;
            }
        }
        studentRepository.save(student);
        return studentDTOMapper.toDetailDTO(student);
    }

    public StudentDetailDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            return null;
        }
        return studentDTOMapper.toDetailDTO(student);
    }

    public Collection<StudentDetailDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(studentDTOMapper::toDetailDTO)
                .collect(Collectors.toList());
    }

    public StudentDetailDTO updateStudent(Long id, StudentCreateDTO studentInput) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            return null;
        }
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
        studentRepository.save(student);
        return studentDTOMapper.toDetailDTO(student);
    }

    public StudentDetailDTO removeStudent(Long id) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            return null;
        }
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

    public Collection<StudentDetailDTO> getStudentsByFacultyId(Long facultyId) {
        return studentRepository.findByFacultyId(facultyId)
                .stream()
                .map(studentDTOMapper::toDetailDTO)
                .collect(Collectors.toList());
    }
}
