package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.entities.Faculty;
import ru.hogwarts.school.entities.Student;
import ru.hogwarts.school.exeption.FacultyNotFoundException;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;


import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class FacultyService{

    private final FacultyRepository facultyRepository;

    private final StudentRepository studentRepository;

    public FacultyService(FacultyRepository facultyRepository,
                          StudentRepository studentRepository) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
    }

    public Faculty create(Faculty faculty) {
        faculty.setId(0);
        return facultyRepository.save(faculty);
    }

    public Faculty read(long id) {
        return facultyRepository.findById(id).orElseThrow(() -> new FacultyNotFoundException(id));
    }

    public Faculty update(long id,
                          Faculty faculty) {
        Faculty oldFaculty = read(id);
        oldFaculty.setName(faculty.getName());
        oldFaculty.setColor(faculty.getColor());
        return facultyRepository.save(oldFaculty);
    }

    public Faculty delete(long id) {
        Faculty faculty = read(id);
        facultyRepository.delete(faculty);
        return faculty;
    }

    public Collection<Faculty> findByColor(String color) {
        return facultyRepository.findAllByColor(color);
    }

    public Collection<Faculty> findByColorOrName(String colorOrName) {
        return facultyRepository.findAllByColorIgnoreCaseOrNameIgnoreCase(colorOrName, colorOrName);
    }

    public Collection<Student> getStudentsByFaculty(long id) {
        Faculty faculty = read(id);
        return studentRepository.findAllByFaculty_Id(faculty.getId());
    }
}

