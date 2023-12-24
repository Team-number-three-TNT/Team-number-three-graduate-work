package ru.skypro.homework.controller;

import lombok.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.service.UserService;

import java.io.IOException;

@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping(path = "/avatars")
public class AvatarController {

    private final UserService userService;

    @GetMapping(value = "/{fileName}", produces = {
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_GIF_VALUE,
            "image/*"
    })
    public ResponseEntity<byte[]> getAvatar(@PathVariable final String fileName) throws IOException {
        byte[] avatar = userService.getAvatar(fileName);
        return ResponseEntity.ok().body(avatar);
    }
}

