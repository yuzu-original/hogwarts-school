package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.hogwarts.school.dto.*;
import ru.hogwarts.school.exception.ErrorInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FacultyControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getAllFaculties_emptyParams_shouldReturnAllFaculties() {
        List<FacultyNotDetailDTO> all = addSomeFacultiesNotDetail(
                new FacultyCreateDTO("one", "r", emptySet()),
                new FacultyCreateDTO("two", "g", emptySet()),
                new FacultyCreateDTO("three", "b", emptySet())
        );

        List<FacultyNotDetailDTO> result = restTemplate.exchange(
                "/faculty",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<FacultyNotDetailDTO>>() {
                }
        ).getBody();

        assertEquals(all, result);
    }

    @Test
    public void getAllFaculties_byColor_shouldReturnFacultiesByColor() {
        List<FacultyNotDetailDTO> all = addSomeFacultiesNotDetail(
                new FacultyCreateDTO("one", "r", emptySet()),
                new FacultyCreateDTO("two", "g", emptySet()),
                new FacultyCreateDTO("three", "r", emptySet())
        );
        String color = "r";
        List<FacultyNotDetailDTO> expected = all.stream().filter(f -> Objects.equals(f.getColor(), color)).collect(Collectors.toList());
        List<FacultyNotDetailDTO> result = restTemplate.exchange(
                "/faculty?color=" + color,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<FacultyNotDetailDTO>>() {
                }
        ).getBody();

        assertEquals(expected, result);
    }

    @Test
    public void getAllFaculties_byName_shouldReturnFacultiesByColor() {
        List<FacultyNotDetailDTO> all = addSomeFacultiesNotDetail(
                new FacultyCreateDTO("one", "r", emptySet()),
                new FacultyCreateDTO("two", "g", emptySet()),
                new FacultyCreateDTO("OnE", "b", emptySet())
        );
        String name = "one";
        List<FacultyNotDetailDTO> expected = all.stream().filter(f -> Objects.equals(f.getName().toLowerCase(), name.toLowerCase())).collect(Collectors.toList());
        List<FacultyNotDetailDTO> result = restTemplate.exchange(
                "/faculty?name=" + name,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<FacultyNotDetailDTO>>() {
                }
        ).getBody();

        assertEquals(expected, result);
    }

    @Test
    public void getFacultyById_ok_shouldReturnFacultyById() {
        List<FacultyDetailDTO> all = addSomeFacultiesDetail(
                new FacultyCreateDTO("one", "r", emptySet()),
                new FacultyCreateDTO("two", "g", emptySet()),
                new FacultyCreateDTO("three", "b", emptySet())
        );
        FacultyDetailDTO expected = all.get(1);
        FacultyDetailDTO result = restTemplate.getForObject(
                "/faculty/" + expected.getId(),
                FacultyDetailDTO.class
        );

        assertEquals(expected, result);
    }

    @Test
    public void createFaculty_ok_shouldCreateFacultyAndReturnIt() {
        FacultyCreateDTO createDTO = new FacultyCreateDTO("test", "red", emptySet());

        FacultyDetailDTO result = restTemplate.postForObject(
                "/faculty",
                createDTO,
                FacultyDetailDTO.class
        );

        assertNotNull(result.getId());
        assertEquals(createDTO.getName(), result.getName());
        assertEquals(createDTO.getColor(), result.getColor());
        assertEquals(createDTO.getStudents(), result.getStudents());
    }

    @Test
    public void updateFaculty_facultyExists_shouldUpdateFacultyAndReturnIt() {
        List<FacultyDetailDTO> all = addSomeFacultiesDetail(new FacultyCreateDTO("test", "red", emptySet()));
        Long id = all.get(0).getId();
        FacultyCreateDTO updateDTO = new FacultyCreateDTO("UPDATED", "GREEN", emptySet());

        FacultyDetailDTO result = restTemplate.exchange(
                "/faculty/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(updateDTO),
                FacultyDetailDTO.class
        ).getBody();

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(updateDTO.getName(), result.getName());
        assertEquals(updateDTO.getColor(), result.getColor());
        assertEquals(updateDTO.getStudents(), result.getStudents());
    }

    @Test
    public void updateFaculty_facultyDoesNotExist_shouldReturnErrorInfo() {
        List<FacultyDetailDTO> all = addSomeFacultiesDetail(new FacultyCreateDTO("test", "red", emptySet()));
        Long id = 10_000L;
        FacultyCreateDTO updateDTO = new FacultyCreateDTO("UPDATED", "GREEN", emptySet());

        ErrorInfo result = restTemplate.exchange(
                "/faculty/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(updateDTO),
                ErrorInfo.class
        ).getBody();

        assertNotNull(result);
        assertEquals("Faculty not found", result.getMessage());
    }

    @Test
    public void removeFaculty_facultyExists_shouldRemoveFacultyAndReturnIt() {
        List<FacultyDetailDTO> all = addSomeFacultiesDetail(new FacultyCreateDTO("test", "red", emptySet()));
        FacultyDetailDTO expected = all.get(0);

        FacultyDetailDTO result = restTemplate.exchange(
                "/faculty/" + expected.getId(),
                HttpMethod.DELETE,
                null,
                FacultyDetailDTO.class
        ).getBody();

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    public void removeFaculty_facultyDoesNotExist_shouldReturnErrorInfo() {
        List<FacultyDetailDTO> all = addSomeFacultiesDetail(new FacultyCreateDTO("test", "red", emptySet()));
        Long id = 10_000L;

        ErrorInfo result = restTemplate.exchange(
                "/faculty/" + id,
                HttpMethod.DELETE,
                null,
                ErrorInfo.class
        ).getBody();

        assertNotNull(result);
        assertEquals("Faculty not found", result.getMessage());
    }

    @Test
    public void getFacultyStudents_faultyExists_shouldReturnStudents() {
        List<StudentDetailDTO> allStudents = addSomeStudents(
                new StudentCreateDTO("1", 11, null),
                new StudentCreateDTO("2", 12, null),
                new StudentCreateDTO("3", 13, null)
        );

        List<FacultyDetailDTO> all = addSomeFacultiesDetail(
                new FacultyCreateDTO("one", "r", Set.of(allStudents.get(0).getId(), allStudents.get(1).getId())),
                new FacultyCreateDTO("two", "g", emptySet()),
                new FacultyCreateDTO("three", "b", Set.of(allStudents.get(2).getId()))
        );
        //                                                   sid  fid
        allStudents.get(0).setFaculty(all.get(0).getId()); // 1 -> 1
        allStudents.get(1).setFaculty(all.get(0).getId()); // 2 -> 1
        allStudents.get(2).setFaculty(all.get(2).getId()); // 3 -> 3

        for (FacultyDetailDTO dto : all) {
            List<StudentDetailDTO> result = restTemplate.exchange(
                    "/faculty/" + dto.getId() + "/students",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<StudentDetailDTO>>() {
                    }
            ).getBody();

            List<StudentDetailDTO> expected = allStudents
                    .stream()
                    .filter(s -> dto.getStudents().contains(s.getId()))
                    .collect(Collectors.toList());

            assertEquals(expected, result);
        }
    }

    private List<FacultyNotDetailDTO> addSomeFacultiesNotDetail(FacultyCreateDTO... list) {
        List<FacultyNotDetailDTO> res = new ArrayList<>();
        for (FacultyCreateDTO dto : list) {
            res.add(restTemplate.postForEntity(
                    "/faculty",
                    dto,
                    FacultyNotDetailDTO.class
            ).getBody());
        }
        return res;
    }

    private List<FacultyDetailDTO> addSomeFacultiesDetail(FacultyCreateDTO... list) {
        List<FacultyDetailDTO> res = new ArrayList<>();
        for (FacultyCreateDTO dto : list) {
            res.add(restTemplate.postForEntity(
                    "/faculty",
                    dto,
                    FacultyDetailDTO.class
            ).getBody());
        }
        return res;
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
