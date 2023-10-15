package ru.hogwarts.school.dto;

import java.util.Objects;

public class StudentDetailDTO {
    private Long id;
    private String name;
    private Integer age;
    private Long faculty;

    public StudentDetailDTO(Long id, String name, Integer age, Long faculty) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.faculty = faculty;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Long getFaculty() {
        return faculty;
    }

    public void setFaculty(Long faculty) {
        this.faculty = faculty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentDetailDTO that = (StudentDetailDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(age, that.age) && Objects.equals(faculty, that.faculty) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age, faculty);
    }

    @Override
    public String toString() {
        return "StudentDetailDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", faculty=" + faculty +
                '}';
    }
}
