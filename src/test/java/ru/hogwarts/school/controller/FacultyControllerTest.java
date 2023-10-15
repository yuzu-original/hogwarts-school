package ru.hogwarts.school.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.dto.mapper.FacultyDTOMapper;
import ru.hogwarts.school.dto.mapper.StudentDTOMapper;
import ru.hogwarts.school.exception.ErrorInfo;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacultyController.class)
public class FacultyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private FacultyRepository facultyRepository;

    @SpyBean
    private StudentDTOMapper studentDTOMapper;

    @SpyBean
    private FacultyDTOMapper facultyDTOMapper;

    @SpyBean
    private FacultyService facultyService;

    @InjectMocks
    private FacultyController facultyController;

    @Test
    public void getAllFaculties_noParams_shouldReturnAllFaculties() throws Exception {
        List<Faculty> faculties = List.of(
                new Faculty(1L, "one", "red"),
                new Faculty(2L, "two", "green"),
                new Faculty(3L, "three", "red")
        );
        when(facultyRepository.findAll()).thenReturn(faculties);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/faculty")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0]").value(faculties.get(0)))
                .andExpect(jsonPath("$[1]").value(faculties.get(1)))
                .andExpect(jsonPath("$[2]").value(faculties.get(2)));
    }

    @Test
    public void getAllFaculties_byColor_shouldReturnFacultiesByColor() throws Exception {
        List<Faculty> faculties = List.of(
                new Faculty(1L, "one", "red"),
                new Faculty(2L, "two", "red"),
                new Faculty(3L, "three", "red")
        );
        when(facultyRepository.findByColorIgnoreCase(anyString())).thenReturn(faculties);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/faculty")
                                .param("color", "red")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0]").value(faculties.get(0)))
                .andExpect(jsonPath("$[1]").value(faculties.get(1)))
                .andExpect(jsonPath("$[2]").value(faculties.get(2)));
    }

    @Test
    public void getAllFaculties_byName_shouldReturnFacultiesByColor() throws Exception {
        List<Faculty> faculties = List.of(
                new Faculty(1L, "Name", "r"),
                new Faculty(2L, "name", "g"),
                new Faculty(3L, "nAmE", "b")
        );
        when(facultyRepository.findByNameIgnoreCase(anyString())).thenReturn(faculties);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/faculty")
                                .param("name", "name")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0]").value(faculties.get(0)))
                .andExpect(jsonPath("$[1]").value(faculties.get(1)))
                .andExpect(jsonPath("$[2]").value(faculties.get(2)));
    }

    @Test
    public void getFacultyById_facultyExists_shouldReturnFacultyById() throws Exception {
        Faculty faculty = new Faculty(1L, "YuZu", "red");
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(faculty));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/faculty/{id}", 1L)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faculty.getId()))
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()))
                .andExpect(jsonPath("$.students.length()").value(0));
    }

    @Test
    public void getFacultyById_facultyDoesNotExist_shouldReturnErrorInfo() throws Exception {
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/faculty/{id}", 1L)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value(new ErrorInfo("Faculty not found")));
    }

    @Test
    public void createFaculty_ok_shouldCreateFacultyAndReturnIt() throws Exception {
        Long id = 1L;
        String name = "test";
        String color = "red";


        JSONObject facultyJSON = new JSONObject();
        facultyJSON.put("name", name);
        facultyJSON.put("color", color);
        facultyJSON.put("students", null);

        Faculty faculty = new Faculty(id, name, color);

        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/faculty")
                                .content(facultyJSON.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(faculty.getId()))
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()))
                .andExpect(jsonPath("$.students.length()").value(0));
    }

    @Test
    public void updateFaculty_facultyExists_shouldUpdateFacultyAndReturnIt() throws Exception {
        Long id = 1L;
        String name = "NEW NAME";
        String color = "NEW COLOR";


        JSONObject facultyJSON = new JSONObject();
        facultyJSON.put("name", name);
        facultyJSON.put("color", color);
        facultyJSON.put("students", null);

        Faculty facultyOld = new Faculty(id, "old name", "old color");
        Faculty faculty = new Faculty(id, name, color);

        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(facultyOld));
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/faculty/{id}", id)
                                .content(facultyJSON.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color))
                .andExpect(jsonPath("$.students.length()").value(0));
    }

    @Test
    public void updateFaculty_facultyDoesNotExist_shouldReturnErrorInfo() throws Exception {
        Long id = 1L;

        JSONObject facultyJSON = new JSONObject();
        facultyJSON.put("name", "NEW NAME");
        facultyJSON.put("color", "NEW COLOR");
        facultyJSON.put("students", null);

        when(facultyRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/faculty/{id}", id)
                                .content(facultyJSON.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value(new ErrorInfo("Faculty not found")));
    }

    @Test
    public void removeFaculty_facultyExists_shouldRemoveFacultyAndReturnIt() throws Exception {
        Long id = 1L;

        Faculty faculty = new Faculty(id, "test", "red");

        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(faculty));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/faculty/{id}", id)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faculty.getId()))
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()))
                .andExpect(jsonPath("$.students.length()").value(0));
    }

    @Test
    public void removeFaculty_facultyDoesNotExist_shouldReturnErrorInfo() throws Exception {
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/faculty/{id}", 1L)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value(new ErrorInfo("Faculty not found")));
    }

    @Test
    public void getFacultyStudents_facultyExists_shouldReturnStudents() throws Exception {
        Faculty faculty = new Faculty(1L, "test", "red");

        List<Student> students = List.of(
                new Student(1L, "one", 10),
                new Student(2L, "two", 20),
                new Student(3L, "three", 30)
        );
        faculty.addStudent(students.get(0));
        faculty.addStudent(students.get(1));
        faculty.addStudent(students.get(2));

        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(faculty));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/faculty/{id}/students", 1L)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }
}
