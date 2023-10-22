package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByAge(Integer age);

    List<Student> findByAgeBetween(Integer min, Integer max);

    @Query(value = "select count(*) from student", nativeQuery = true)
    Long getCount();

    @Query(value = "select avg(age) from student", nativeQuery = true)
    Double getAvgAge();

    @Query(value = "select * from student order by id desc limit 5", nativeQuery = true)
    List<Student> getLastStudents();
}
