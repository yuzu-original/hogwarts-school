package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class FacultyService {
    private final Map<Long, Faculty> faculties = new HashMap<>();
    private long lastId = 0;

    public FacultyService() {
    }

    public Faculty createFaculty(Faculty faculty) {
        faculty.setId(++lastId);
        faculties.put(lastId, faculty);
        return faculty;
    }

    public Faculty getFacultyById(long id) {
        return faculties.get(id);
    }

    public Collection<Faculty> getAllFaculties() {
        return faculties.values();
    }

    public Faculty updateFaculty(Faculty faculty) {
        return faculties.replace(faculty.getId(), faculty);
    }

    public Faculty removeFaculty(long id) {
        return faculties.remove(id);
    }
}
