package ru.hogwarts.school.dto;

import java.util.Objects;
import java.util.Set;

public class FacultyCreateDTO {
    private String name;
    private String color;
    private Set<Long> students;

    public FacultyCreateDTO(String name, String color, Set<Long> students) {
        this.name = name;
        this.color = color;
        this.students = students;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Set<Long> getStudents() {
        return students;
    }

    public void setStudents(Set<Long> students) {
        this.students = students;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FacultyCreateDTO that = (FacultyCreateDTO) o;
        return Objects.equals(name, that.name) && Objects.equals(color, that.color) && Objects.equals(students, that.students);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color, students);
    }

    @Override
    public String toString() {
        return "FacultyCreateDAO{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", students=" + students +
                '}';
    }
}
