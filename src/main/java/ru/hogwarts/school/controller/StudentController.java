package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.FacultyDetailDTO;
import ru.hogwarts.school.dto.StudentCreateDTO;
import ru.hogwarts.school.dto.StudentDetailDTO;
import ru.hogwarts.school.exception.BadDataException;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;

@RestController
@RequestMapping("student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<Collection<StudentDetailDTO>> findAllStudents(
            @RequestParam(value = "age", required = false) Integer age,
            @RequestParam(value = "min-age", required = false) Integer minAge,
            @RequestParam(value = "max-age", required = false) Integer maxAge
    ) {
        if (age != null) {
            return ResponseEntity.ok(studentService.getStudentsByAge(age));
        }
        if (minAge != null || maxAge != null) {
            if (minAge == null) {
                throw new BadDataException("min-age is required");
            }
            if (maxAge == null) {
                throw new BadDataException("max-age is required");
            }
            return ResponseEntity.ok(studentService.getStudentsBetweenAge(minAge, maxAge));
        }
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("{id}")
    public ResponseEntity<StudentDetailDTO> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @PostMapping
    public ResponseEntity<StudentDetailDTO> createStudent(@RequestBody StudentCreateDTO student) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.createStudent(student));
    }

    @PutMapping("{id}")
    public ResponseEntity<StudentDetailDTO> updateStudent(@RequestBody StudentCreateDTO student, @PathVariable Long id) {
        return ResponseEntity.ok(studentService.updateStudent(id, student));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<StudentDetailDTO> removeStudent(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.removeStudent(id));
    }

    @GetMapping("{id}/faculty")
    public ResponseEntity<FacultyDetailDTO> getStudentFaculty(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentFacultyById(id));
    }
}
