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
import ru.hogwarts.school.dto.mapper.AvatarDTOMapper;
import ru.hogwarts.school.dto.mapper.FacultyDTOMapper;
import ru.hogwarts.school.dto.mapper.StudentDTOMapper;
import ru.hogwarts.school.exception.ErrorInfo;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.StudentService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private FacultyRepository facultyRepository;

    @MockBean
    private AvatarRepository avatarRepository;

    @SpyBean
    private StudentDTOMapper studentDTOMapper;

    @SpyBean
    private FacultyDTOMapper facultyDTOMapper;

    @SpyBean
    private AvatarDTOMapper avatarDTOMapper;

    @SpyBean
    private StudentService studentService;

    @SpyBean
    private AvatarService avatarService;

    @InjectMocks
    private StudentController studentController;


    @Test
    public void findAllStudents_noParams_shouldReturnAllStudents() throws Exception {
        List<Student> students = List.of(
                new Student(1L, "one", 10),
                new Student(2L, "two", 20),
                new Student(3L, "three", 30)
        );
        when(studentRepository.findAll()).thenReturn(students);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/student")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0]").value(students.get(0)))
                .andExpect(jsonPath("$[1]").value(students.get(1)))
                .andExpect(jsonPath("$[2]").value(students.get(2)));
    }

    @Test
    public void findAllStudents_byAge_shouldReturnStudentsByAge() throws Exception {
        List<Student> students = List.of(
                new Student(1L, "one", 10),
                new Student(2L, "two", 10)
        );
        when(studentRepository.findByAge(anyInt())).thenReturn(students);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/student")
                                .param("age", "10")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0]").value(students.get(0)))
                .andExpect(jsonPath("$[1]").value(students.get(1)));
    }

    @Test
    public void findAllStudents_betweenAge_shouldReturnStudentsBetweenAge() throws Exception {
        List<Student> students = List.of(
                new Student(1L, "two", 20),
                new Student(2L, "three", 30)
        );
        when(studentRepository.findByAgeBetween(anyInt(), anyInt())).thenReturn(students);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/student")
                                .param("min-age", "20")
                                .param("max-age", "40")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0]").value(students.get(0)))
                .andExpect(jsonPath("$[1]").value(students.get(1)));
    }

    @Test
    public void getStudentById_studentExists_shouldReturnStudentById() throws Exception {
        Student student = new Student(1L, "cool", 20);

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/student/{id}", 1L)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(student));
    }

    @Test
    public void getStudentById_studentDoesNotExist_shouldReturnErrorInfo() throws Exception {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/student/{id}", 1L)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value(new ErrorInfo("Student not found")));
    }

    @Test
    public void createStudent_ok_shouldCreateStudentAndReturnIt() throws Exception {
        Long id = 1L;
        String name = "test";
        Integer age = 10;

        JSONObject studentJSON = new JSONObject();
        studentJSON.put("name", name);
        studentJSON.put("age", age);
        studentJSON.put("faculty", null);

        Student student = new Student(id, name, age);

        when(studentRepository.save(any(Student.class))).thenReturn(student);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/student")
                                .content(studentJSON.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age))
                .andExpect(jsonPath("$.faculty").isEmpty());
    }

    @Test
    public void updateStudent_studentExists_shouldUpdateStudentAndReturnIt() throws Exception {
        Long id = 1L;
        String name = "test";
        Integer age = 10;

        JSONObject studentJSON = new JSONObject();
        studentJSON.put("name", name);
        studentJSON.put("age", age);
        studentJSON.put("faculty", null);

        Student studentOld = new Student(id, "old", 9);
        Student student = new Student(id, name, age);

        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(studentOld));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/student/{id}", id)
                                .content(studentJSON.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age))
                .andExpect(jsonPath("$.faculty").isEmpty());
    }

    @Test
    public void updateStudent_studentDoesNotExist_shouldReturnErrorInfo() throws Exception {
        Long id = 10_000L;
        JSONObject studentJSON = new JSONObject();
        studentJSON.put("name", "test");
        studentJSON.put("age", "10");
        studentJSON.put("faculty", null);

        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/student/{id}", id)
                                .content(studentJSON.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value(new ErrorInfo("Student not found")));
    }

    @Test
    public void removeStudent_studentExists_shouldRemoveStudentAndReturnIt() throws Exception {
        Student student = new Student(1L, "cool", 20);

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/student/{id}", 1L)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(student));
    }

    @Test
    public void removeStudent_studentDoesNotExist_shouldReturnErrorInfo() throws Exception {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/student/{id}", 1L)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value(new ErrorInfo("Student not found")));
    }

    @Test
    public void getStudentFaculty_studentExists_shouldReturnFaculty() throws Exception {
        Student student = new Student(1L, "cool", 20);
        Faculty faculty = new Faculty(1L, "test", "red");
        faculty.addStudent(student);

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/student/{id}/faculty", 1L)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faculty.getId()))
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()))
                .andExpect(jsonPath("$.students.length()").value(1))
                .andExpect(jsonPath("$.students[0]").value(1L));
    }

    @Test
    public void getStudentFaculty_studentDoesNotExist_shouldReturnErrorInfo() throws Exception {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/student/{id}/faculty", 1L)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value(new ErrorInfo("Student not found")));
    }
}
