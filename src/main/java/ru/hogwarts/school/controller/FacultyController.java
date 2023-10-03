package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.FacultyCreateDTO;
import ru.hogwarts.school.dto.FacultyDetailDTO;
import ru.hogwarts.school.dto.FacultyNotDetailDTO;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;

@RestController
@RequestMapping("faculty")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping
    public ResponseEntity<Collection<FacultyNotDetailDTO>> getAllFaculties(
            @RequestParam(value = "color", required = false) String color,
            @RequestParam(value = "name", required = false) String name
    ) {
        if (color != null && !color.isBlank()) {
            return ResponseEntity.ok(facultyService.getFacultiesByColor(color));
        }
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(facultyService.getFacultiesByName(name));
        }
        return ResponseEntity.ok(facultyService.getAllFaculties());
    }

    @GetMapping("{id}")
    public ResponseEntity<FacultyDetailDTO> getFacultyById(@PathVariable Long id) {
        FacultyDetailDTO faculty = facultyService.getFacultyById(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @PostMapping
    public ResponseEntity<FacultyDetailDTO> createFaculty(@RequestBody FacultyCreateDTO faculty) {
        FacultyDetailDTO resultFaculty = facultyService.createFaculty(faculty);
        if (resultFaculty == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(resultFaculty);
    }

    @PutMapping("{id}")
    public ResponseEntity<FacultyDetailDTO> updateFaculty(@RequestBody FacultyCreateDTO faculty, @PathVariable Long id) {
        FacultyDetailDTO resultFaculty = facultyService.updateFaculty(id, faculty);
        if (resultFaculty == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(resultFaculty);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<FacultyDetailDTO> removeFaculty(@PathVariable Long id) {
        FacultyDetailDTO faculty = facultyService.removeFaculty(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }
}
