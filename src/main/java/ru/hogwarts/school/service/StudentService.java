package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final Logger logger = LoggerFactory.getLogger(StudentService.class);
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
        logger.info("createStudent method is called");

        Student student = new Student();
        student.setName(studentInput.getName());
        student.setAge(studentInput.getAge());
        if (studentInput.getFaculty() != null) {
            Faculty faculty = facultyRepository
                    .findById(studentInput.getFaculty())
                    .orElseThrow(() -> {
                        String message = "Faculty not found";
                        logger.error(message);
                        return new BadDataException(message);
                    });
            faculty.addStudent(student);
        }
        student = studentRepository.save(student);
        return studentDTOMapper.toDetailDTO(student);
    }

    public StudentDetailDTO getStudentById(Long id) {
        logger.info("getStudentById method is called");

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> {
                    String message = "Student not found";
                    logger.error(message);
                    return new NotFoundResourceException(message);
                });
        return studentDTOMapper.toDetailDTO(student);
    }

    public Collection<StudentDetailDTO> getAllStudents() {
        logger.info("getAllStudents method is called");

        return studentRepository.findAll()
                .stream()
                .map(studentDTOMapper::toDetailDTO)
                .collect(Collectors.toList());
    }

    public StudentDetailDTO updateStudent(Long id, StudentCreateDTO studentInput) {
        logger.info("updateStudent method is called");

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> {
                    String message = "Student not found";
                    logger.error(message);
                    return new NotFoundResourceException(message);
                });
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
        logger.info("removeStudent method is called");

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> {
                    String message = "Student not found";
                    logger.error(message);
                    return new NotFoundResourceException(message);
                });
        Faculty faculty = student.getFaculty();
        if (faculty != null) {
            faculty.removeStudent(student);
        }
        studentRepository.delete(student);
        return studentDTOMapper.toDetailDTO(student);
    }

    public Collection<StudentDetailDTO> getStudentsByAge(Integer age) {
        logger.info("getStudentsByAge method is called");

        return studentRepository.findByAge(age)
                .stream()
                .map(studentDTOMapper::toDetailDTO)
                .collect(Collectors.toList());
    }

    public Collection<StudentDetailDTO> getStudentsBetweenAge(Integer min, Integer max) {
        logger.info("getStudentsBetweenAge method is called");

        return studentRepository.findByAgeBetween(min, max)
                .stream()
                .map(studentDTOMapper::toDetailDTO)
                .collect(Collectors.toList());
    }


    public FacultyDetailDTO getStudentFacultyById(Long id) {
        logger.info("getStudentFacultyById method is called");

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourceException("Student not found"));
        Faculty faculty = student.getFaculty();
        if (faculty == null) {
            return null;
        }
        return facultyDTOMapper.toDetailDTO(faculty);
    }


    public Long getStudentCount() {
        logger.info("getStudentCount method is called");

        return studentRepository.getCount();
    }

    public Double getStudentAvgAge() {
        logger.info("getStudentAvgAge method is called");

        return studentRepository.getAvgAge();
    }

    public List<StudentDetailDTO> getLastStudents() {
        logger.info("getLastStudents method is called");

        return studentRepository.getLastStudents()
                .stream()
                .map(studentDTOMapper::toDetailDTO)
                .collect(Collectors.toList());
    }

    public Collection<String> findNamesStartingWithA() {
        // FIXME: bad practice
        return studentRepository.findAll()
                .stream()
                .parallel()
                .map(s -> s.getName().toUpperCase())
                .filter(s -> s.startsWith("A"))
                .collect(Collectors.toList());
    }

    public Double getAvgAge() {
        // FIXME: bad practice
        return studentRepository.findAll()
                .stream()
                .parallel()
                .mapToDouble(Student::getAge)
                .average()
                .orElse(0);
    }

    public void runCommand1() {
        PageRequest pageRequest = PageRequest.of(0, 6);
        List<Student> students = studentRepository.findAll(pageRequest).getContent();
        List<Student> students1 = students.subList(0, 2);
        List<Student> students2 = students.subList(2, 4);
        List<Student> students3 = students.subList(4, 6);

        printNames(students1);
        new Thread(() -> printNames(students2)).start();
        new Thread(() -> printNames(students3)).start();
    }

    public void runCommand2() {
        PageRequest pageRequest = PageRequest.of(0, 6);
        List<Student> students = studentRepository.findAll(pageRequest).getContent();
        List<Student> students1 = students.subList(0, 2);
        List<Student> students2 = students.subList(2, 4);
        List<Student> students3 = students.subList(4, 6);

        printNamesSync(students1);
        new Thread(() -> printNamesSync(students2)).start();
        new Thread(() -> printNamesSync(students3)).start();
    }

    private void printNames(List<Student> students) {
        for (Student student : students) {
            System.out.println(student);
        }
    }

    private synchronized void printNamesSync(List<Student> students) {
        for (Student student : students) {
            System.out.println(student);
        }
    }
}
