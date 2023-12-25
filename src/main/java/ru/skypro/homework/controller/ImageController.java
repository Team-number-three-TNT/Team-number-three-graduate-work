package ru.skypro.homework.controller;

import lombok.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.UserService;

import java.io.IOException;

@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping(path = "/images")
public class ImageController {

    private final UserService userService;
    private final AdService adService;

    @GetMapping(value = "/{avatarId}", produces = {
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_GIF_VALUE,
            "image/*"
    })
    public ResponseEntity<byte[]> getAvatar(@PathVariable int avatarId) throws IOException {
        byte[] avatar = userService.getAvatar(avatarId);
        return ResponseEntity.ok().body(avatar);
    }

    @GetMapping(value = "/{adId}", produces = {
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_GIF_VALUE,
            "image/*"
    })
    public ResponseEntity<byte[]> getAdImage(@PathVariable int adId) throws IOException {
        byte[] avatar = adService.getImage(adId);
        return ResponseEntity.ok().body(avatar);
    }
}

