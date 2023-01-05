SELECT student.name, student.age, faculty.name
FROM students
INNER JOIN faculty ON students.faculty_id = faculty.id;

SELECT student.name, student.age
FROM student
INNER JOIN avatar a ON student.id = a.students_id