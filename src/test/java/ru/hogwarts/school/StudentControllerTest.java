package ru.hogwarts.school;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.entities.Faculty;
import ru.hogwarts.school.entities.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    private final Faker faker = new Faker();

    @AfterEach
    public void afterEach() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @Test
    public void createTest() {
        addStudent(generateStudent(addFaculty(generateFaculty())));
    }

    private Faculty addFaculty(Faculty faculty) {
        ResponseEntity<Faculty> facultyResponseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/faculties", faculty, Faculty.class);
        assertThat(facultyResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(facultyResponseEntity.getBody()).isNotNull();
        assertThat(facultyResponseEntity.getBody()).usingRecursiveComparison().ignoringFields("id").isEqualTo(faculty);
        assertThat(facultyResponseEntity.getBody().getId()).isNotNull();

        return facultyResponseEntity.getBody();
    }

    private Student addStudent(Student student) {
        ResponseEntity<Student> studentResponseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/students", student, Student.class);
        assertThat(studentResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(studentResponseEntity.getBody()).isNotNull();
        assertThat(studentResponseEntity.getBody()).usingRecursiveComparison().ignoringFields("id").isEqualTo(student);
        assertThat(studentResponseEntity.getBody().getId()).isNotNull();

        return studentResponseEntity.getBody();
    }

    @Test
    public void putTest() {
        Faculty faculty1 = addFaculty(generateFaculty());
        Faculty faculty2 = addFaculty(generateFaculty());
        Student student = addStudent(generateStudent(faculty1));

        ResponseEntity<Student> getForEntityResponse = testRestTemplate.getForEntity("http://localhost:" + port + "/students/" + student.getId(), Student.class);
        assertThat(getForEntityResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getForEntityResponse.getBody()).isNotNull();
        assertThat(getForEntityResponse.getBody()).usingRecursiveComparison().isEqualTo(student);
        assertThat(getForEntityResponse.getBody().getFaculty()).usingRecursiveComparison().isEqualTo(faculty1);

        student.setFaculty(faculty2);

        ResponseEntity<Student> recordResponseEntity = testRestTemplate.exchange("http://localhost:" + port + "/students/" + student.getId(), HttpMethod.PUT, new HttpEntity<>(student), Student.class);
        assertThat(getForEntityResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(recordResponseEntity.getBody()).isNotNull();
        assertThat(recordResponseEntity.getBody()).usingRecursiveComparison().isEqualTo(student);
        assertThat(recordResponseEntity.getBody().getFaculty()).usingRecursiveComparison().isEqualTo(faculty2);
    }

    @Test
    public void findByAgeBetweenTest() {
        List<Faculty> faculties = Stream.generate(this::generateFaculty)
                .limit(5)
                .map(this::addFaculty)
                .collect(Collectors.toList());
        List<Student> students = Stream.generate(() -> generateStudent(faculties.get(faker.random().nextInt(faculties.size()))))
                .limit(50)
                .map(this::addStudent)
                .collect(Collectors.toList());

        int minAge = 14;
        int maxAge = 17;

        List<Student> expectedStudents = students.stream()
                .filter(studentRecord -> studentRecord.getAge() >= minAge && studentRecord.getAge() <= maxAge)
                .collect(Collectors.toList());


        ResponseEntity<List<Student>> getForEntityResponse = testRestTemplate.exchange("htt://localhost:" + port + "/students?minAge=14&maxAge=17", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Student>>(){});
        assertThat(getForEntityResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getForEntityResponse.getBody()).hasSize(expectedStudents.size()).usingRecursiveFieldByFieldElementComparator().containsExactlyElementsOf(null);
    }

    private Student generateStudent(Faculty faculty) {
        Student student = new Student();
        student.setName(faker.harryPotter().character());
        student.setAge(faker.random().nextInt(11, 18));
        if (faculty != null) {
            student.setFaculty(faculty);
        }
        return student;
    }

    private Faculty generateFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName(faker.harryPotter().house());
        faculty.setColor(faker.color().name());
        return faculty;
    }
}
