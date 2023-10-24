SELECT s."name", s.age, f."name" AS faculty_name
FROM student AS s
INNER JOIN faculty AS f ON s.faculty_id = f.id;

SELECT s."name", s.age, a.file_path
FROM avatar AS a
INNER JOIN student AS s ON a.student_id = s.faculty_id;