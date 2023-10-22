package ru.hogwarts.school.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.FacultyDetailDTO;
import ru.hogwarts.school.dto.StudentCreateDTO;
import ru.hogwarts.school.dto.StudentDetailDTO;
import ru.hogwarts.school.exception.BadDataException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.StudentService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

@RestController
@RequestMapping("student")
public class StudentController {
    private final StudentService studentService;
    private final AvatarService avatarService;

    public StudentController(StudentService studentService, AvatarService avatarService) {
        this.studentService = studentService;
        this.avatarService = avatarService;
    }

    @GetMapping
    public ResponseEntity<Collection<StudentDetailDTO>> findAllStudents(@RequestParam(value = "age", required = false) Integer age, @RequestParam(value = "min-age", required = false) Integer minAge, @RequestParam(value = "max-age", required = false) Integer maxAge) {
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

    @GetMapping("count")
    public ResponseEntity<Long> getCount() {
        return ResponseEntity.ok(studentService.getStudentCount());
    }

    @GetMapping("avg-age")
    public ResponseEntity<Double> getAvgAge() {
        return ResponseEntity.ok(studentService.getStudentAvgAge());
    }

    @GetMapping("last")
    public ResponseEntity<Collection<StudentDetailDTO>> getLastStudents() {
        return ResponseEntity.ok(studentService.getLastStudents());
    }

    @GetMapping("{id}/faculty")
    public ResponseEntity<FacultyDetailDTO> getStudentFaculty(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentFacultyById(id));
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAvatar(@PathVariable Long id, @RequestParam MultipartFile avatar) throws IOException {
        if (avatar.getSize() > 1024 * 300) {
            throw new BadDataException("File is too big");
        }

        avatarService.uploadAvatar(id, avatar);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}/avatar/preview")
    public ResponseEntity<byte[]> downloadAvatar(@PathVariable Long id) {
        Avatar avatar = avatarService.findAvatar(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getData().length);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(avatar.getData());
    }

    @GetMapping(value = "/{id}/avatar")
    public void downloadAvatar(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Avatar avatar = avatarService.findAvatar(id);

        Path path = Path.of(avatar.getFilePath());

        try (InputStream is = Files.newInputStream(path);
             OutputStream os = response.getOutputStream()) {
            response.setStatus(200);
            response.setContentType(avatar.getMediaType());
            response.setContentLength(avatar.getFileSize().intValue());
            is.transferTo(os);
        }
    }
}
