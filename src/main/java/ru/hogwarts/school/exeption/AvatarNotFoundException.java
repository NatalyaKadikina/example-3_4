package ru.hogwarts.school.exeption;

public class AvatarNotFoundException extends  RuntimeException {

    private final long id;

    public AvatarNotFoundException(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
