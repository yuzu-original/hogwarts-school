package ru.hogwarts.school.dto;

import java.util.Objects;

public class StudentCreateDTO {
    private String name;
    private Integer age;
    private Long faculty;

    public StudentCreateDTO(String name, Integer age, Long faculty) {
        this.name = name;
        this.age = age;
        this.faculty = faculty;
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
        StudentCreateDTO that = (StudentCreateDTO) o;
        return Objects.equals(age, that.age) && Objects.equals(faculty, that.faculty) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, faculty);
    }

    @Override
    public String toString() {
        return "StudentCreateDTO{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", faculty=" + faculty +
                '}';
    }
}
