package ru.hogwarts.school.repository;

import org.springframework.stereotype.Repository;
import ru.hogwarts.school.entities.Avatar;

import java.util.Optional;

@Repository
public interface AvatarRepository {
    Avatar save(Avatar avatar);

    Optional<Avatar> findById(long id);
}
