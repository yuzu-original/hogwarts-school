package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.hogwarts.school.dto.FacultyCreateDTO;
import ru.hogwarts.school.dto.FacultyDetailDTO;
import ru.hogwarts.school.dto.StudentCreateDTO;
import ru.hogwarts.school.dto.StudentDetailDTO;
import ru.hogwarts.school.exception.ErrorInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class StudentControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void findAllStudents_emptyParams_shouldReturnAllStudents() {
        List<StudentDetailDTO> expected = addSomeStudents(
                new StudentCreateDTO("first", 32, null),
                new StudentCreateDTO("second", 18, null),
                new StudentCreateDTO("third", 16, null)
        );

        ResponseEntity<List<StudentDetailDTO>> responseEntity = restTemplate.exchange(
                "/student",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<StudentDetailDTO>>() {
                }
        );
        assertEquals(expected, responseEntity.getBody());
    }

    @Test
    public void findAllStudents_byAge_shouldReturnStudentsByAge() {
        Integer age = 16;

        List<StudentDetailDTO> all = addSomeStudents(
                new StudentCreateDTO("first", 32, null),
                new StudentCreateDTO("second", 16, null),
                new StudentCreateDTO("third", 16, null)
        );

        List<StudentDetailDTO> expected = all
                .stream()
                .filter(s -> Objects.equals(s.getAge(), age))
                .collect(Collectors.toList());

        ResponseEntity<List<StudentDetailDTO>> responseEntity = restTemplate.exchange(
                "/student?age=" + age,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<StudentDetailDTO>>() {
                }
        );

        assertEquals(expected, responseEntity.getBody());
    }

    @Test
    public void findAllStudents_betweenMinAgeAndMaxAge_shouldReturnStudentsBetweenMinAgeAndMaxAge() {
        Integer minAge = 16;
        Integer maxAge = 64;

        List<StudentDetailDTO> all = addSomeStudents(
                new StudentCreateDTO("one", 128, null),
                new StudentCreateDTO("two", 32, null),
                new StudentCreateDTO("three", 16, null),
                new StudentCreateDTO("four", 15, null)
        );

        List<StudentDetailDTO> expected = all
                .stream()
                .filter(s -> minAge <= s.getAge() && s.getAge() <= maxAge)
                .collect(Collectors.toList());

        ResponseEntity<List<StudentDetailDTO>> responseEntity = restTemplate.exchange(
                "/student?min-age=" + minAge + "&max-age=" + maxAge,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<StudentDetailDTO>>() {
                }
        );

        assertEquals(expected, responseEntity.getBody());
    }

    @Test
    public void getStudentById_studentExists_shouldReturnStudentById() {
        List<StudentDetailDTO> all = addSomeStudents(
                new StudentCreateDTO("first", 32, null),
                new StudentCreateDTO("second", 16, null)
        );

        StudentDetailDTO expected = all.get(0);
        Long id = expected.getId();

        ResponseEntity<StudentDetailDTO> responseEntity = restTemplate.getForEntity(
                "/student/" + id,
                StudentDetailDTO.class
        );

        assertEquals(expected, responseEntity.getBody());
    }

    @Test
    public void getStudentById_studentDoesNotExist_shouldReturnErrorInfo() {
        List<StudentDetailDTO> all = addSomeStudents(
                new StudentCreateDTO("first", 32, null),
                new StudentCreateDTO("second", 16, null)
        );

        Long id = 10_000L;

        ErrorInfo errorInfo = restTemplate.getForObject(
                "/student/" + id,
                ErrorInfo.class
        );

        assertEquals("Student not found", errorInfo.getMessage());
    }

    @Test
    public void createStudent_ok_shouldCreateStudentAndReturnIt() {
        StudentCreateDTO studentCreateDTO = new StudentCreateDTO("new", 64, null);

        StudentDetailDTO studentDetailDTO = restTemplate.postForEntity(
                "/student",
                studentCreateDTO,
                StudentDetailDTO.class
        ).getBody();

        assertNotNull(studentDetailDTO);
        assertNotNull(studentDetailDTO.getId());
        assertEquals(studentCreateDTO.getName(), studentDetailDTO.getName());
        assertEquals(studentCreateDTO.getAge(), studentDetailDTO.getAge());
        assertEquals(studentCreateDTO.getFaculty(), studentDetailDTO.getFaculty());

        // check whether it has been created
        List<StudentDetailDTO> all = restTemplate.exchange(
                "/student",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<StudentDetailDTO>>() {
                }
        ).getBody();

        assertEquals(List.of(studentDetailDTO), all);
    }

    @Test
    public void updateStudent_studentExists_shouldUpdateStudentAndReturnIt() {
        List<StudentDetailDTO> added = addSomeStudents(new StudentCreateDTO("abc", 123, null));
        Long id = added.get(0).getId();
        StudentCreateDTO toUpdateStudent = new StudentCreateDTO("updated", 10, null);

        StudentDetailDTO updatedStudent = restTemplate.exchange(
                "/student/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(toUpdateStudent),
                StudentDetailDTO.class
        ).getBody();

        assertNotNull(updatedStudent);
        assertEquals(id, updatedStudent.getId());
        assertEquals(toUpdateStudent.getName(), updatedStudent.getName());
        assertEquals(toUpdateStudent.getAge(), updatedStudent.getAge());
        assertEquals(toUpdateStudent.getFaculty(), updatedStudent.getFaculty());

        // check whether it has been updated
        List<StudentDetailDTO> all = restTemplate.exchange(
                "/student",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<StudentDetailDTO>>() {
                }
        ).getBody();

        assertEquals(List.of(updatedStudent), all);
    }

    @Test
    public void updateStudent_studentDoesNotExist_shouldReturnErrorInfo() {
        ErrorInfo errorInfo = restTemplate.exchange(
                "/student/" + 10_000L,
                HttpMethod.PUT,
                new HttpEntity<>(new StudentCreateDTO("updated", 10, null)),
                ErrorInfo.class
        ).getBody();

        assertNotNull(errorInfo);
        assertEquals("Student not found", errorInfo.getMessage());
    }

    @Test
    public void removeStudent_studentExists_shouldReturnStudentAndRemoveIt() {
        List<StudentDetailDTO> added = addSomeStudents(
                new StudentCreateDTO("abc", 123, null),
                new StudentCreateDTO("def", 456, null)
        );
        StudentDetailDTO student = added.get(0);

        StudentDetailDTO deletedStudent = restTemplate.exchange(
                "/student/" + student.getId(),
                HttpMethod.DELETE,
                null,
                StudentDetailDTO.class
        ).getBody();

        assertNotNull(deletedStudent);
        assertEquals(student, deletedStudent);

        // check whether it has been deleted
        List<StudentDetailDTO> all = restTemplate.exchange(
                "/student",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<StudentDetailDTO>>() {
                }
        ).getBody();

        added.remove(0);
        assertEquals(added, all);
    }

    @Test
    public void removeStudent_studentDoesNotExist_shouldReturnErrorInfo() {
        List<StudentDetailDTO> added = addSomeStudents(
                new StudentCreateDTO("abc", 123, null),
                new StudentCreateDTO("def", 456, null)
        );
        ErrorInfo errorInfo = restTemplate.exchange(
                "/student/" + 10_000L,
                HttpMethod.DELETE,
                null,
                ErrorInfo.class
        ).getBody();

        assertNotNull(errorInfo);
        assertEquals("Student not found", errorInfo.getMessage());
    }

    @Test
    public void getStudentFaculty_studentExists_shouldReturnFaculty() {
        FacultyDetailDTO faculty = restTemplate.postForObject(
                "/faculty",
                new FacultyCreateDTO("test", "red", Collections.emptySet()),
                FacultyDetailDTO.class
        );
        List<StudentDetailDTO> students = addSomeStudents(
                new StudentCreateDTO("first", 32, null),
                new StudentCreateDTO("second", 18, faculty.getId()),
                new StudentCreateDTO("third", 16, null)
        );
        faculty.getStudents().add(students.get(1).getId()); // add student id to facultyDTO

        StudentDetailDTO student1 = students.get(0);
        FacultyDetailDTO resultFaculty1 = restTemplate.getForObject(
                "/student/" + student1.getId() + "/faculty",
                FacultyDetailDTO.class
        );

        assertNull(resultFaculty1);

        StudentDetailDTO student2 = students.get(1);
        FacultyDetailDTO resultFaculty2 = restTemplate.getForObject(
                "/student/" + student2.getId() + "/faculty",
                FacultyDetailDTO.class
        );

        assertEquals(faculty, resultFaculty2);
    }

    private List<StudentDetailDTO> addSomeStudents(StudentCreateDTO... list) {
        List<StudentDetailDTO> res = new ArrayList<>();
        for (StudentCreateDTO dto : list) {
            res.add(restTemplate.postForEntity(
                    "/student",
                    dto,
                    StudentDetailDTO.class
            ).getBody());
        }
        return res;
    }
}