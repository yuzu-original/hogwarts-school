package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.FacultyDetailDTO;
import ru.hogwarts.school.dto.StudentCreateDTO;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final StudentDTOMapper studentDTOMapper;
    private final FacultyDTOMapper facultyDTOMapper;

    public StudentService(StudentRepository studentRepository, FacultyRepository facultyRepository, StudentDTOMapper studentDTOMapper, FacultyDTOMapper facultyDTOMapper) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
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
        studentRepository.save(student);
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
        studentRepository.save(student);
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
}
