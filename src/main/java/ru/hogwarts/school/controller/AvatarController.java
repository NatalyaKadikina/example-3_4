package ru.hogwarts.school.controller;



import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.entities.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.IOException;
import java.util.Collection;

@RestController
@RequestMapping("/avatars")
public class AvatarController {

    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping
    public void uploadAvatar(@RequestParam MultipartFile avatar) throws IOException {
        avatarService.uploadAvatar(avatar);
    }

    @GetMapping("/{id}/from-db")
    public ResponseEntity<byte[]> readAvatarFromDb(@PathVariable long id) throws IOException {
        return readAvatar(avatarService.readAvatarFromDb(id));
    }

    @GetMapping("/{id}/from-fs")
    public ResponseEntity<byte[]> readAvatarFromFs(@PathVariable long id) throws IOException {
        return readAvatar(avatarService.readAvatarFromFs(id));
    }

    private ResponseEntity<byte[]> readAvatar(Pair<String, byte[]> pair) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(pair.getFirst()))
                .contentLength(pair.getSecond().length)
                .body(pair.getSecond());
    }

    @GetMapping(value = "/all")
    public ResponseEntity<Collection<Avatar>> getAll(@RequestParam("page") Integer pageNumber,
                                                     @RequestParam("size") Integer pageSize) {
        return avatarService.getAll(pageNumber, pageSize);
    }

}
