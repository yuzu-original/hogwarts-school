package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;

@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        faculty.setId(0);
        return facultyRepository.save(faculty);
    }

    public Faculty getFacultyById(long id) {
        return facultyRepository.findById(id).orElse(null);
    }

    public Collection<Faculty> getAllFaculties() {
        return facultyRepository.findAll();
    }

    public Faculty updateFaculty(Faculty faculty) {
        if (!facultyRepository.existsById(faculty.getId())) {
            return null;
        }
        return facultyRepository.save(faculty);
    }

    public Faculty removeFaculty(long id) {
        Faculty faculty = facultyRepository.findById(id).orElse(null);
        if (faculty == null) {
            return null;
        }
        facultyRepository.delete(faculty);
        return faculty;
    }

    public Collection<Faculty> getFacultiesByColor(String color) {
        return facultyRepository.findByColorIgnoreCase(color);
    }

    public Collection<Faculty> getFacultiesByName(String name) {
        return facultyRepository.findByNameIgnoreCase(name);
    }
}
