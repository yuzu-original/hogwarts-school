package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.StudentCreateDTO;
import ru.hogwarts.school.dto.StudentDetailDTO;
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
            if (minAge != null && maxAge != null) {
                return ResponseEntity.ok(studentService.getStudentsBetweenAge(minAge, maxAge));
            }
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("{id}")
    public ResponseEntity<StudentDetailDTO> getStudentById(@PathVariable Long id) {
        StudentDetailDTO student = studentService.getStudentById(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @PostMapping
    public ResponseEntity<StudentDetailDTO> createStudent(@RequestBody StudentCreateDTO student) {
        StudentDetailDTO resultStudent = studentService.createStudent(student);
        if (resultStudent == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(resultStudent);
    }

    @PutMapping("{id}")
    public ResponseEntity<StudentDetailDTO> updateStudent(@RequestBody StudentCreateDTO student, @PathVariable Long id) {
        StudentDetailDTO resultStudent = studentService.updateStudent(id, student);
        if (resultStudent == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(resultStudent);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<StudentDetailDTO> removeStudent(@PathVariable Long id) {
        StudentDetailDTO student = studentService.removeStudent(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }
}
