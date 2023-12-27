package ru.skypro.homework.controller;

import lombok.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.service.ImageService;

import java.io.IOException;

@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping(path = "/images")
public class ImageController {

    private final ImageService imageService;

    @GetMapping(value = "/{imageId}", produces = {
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_GIF_VALUE,
            "image/*"
    })
    public ResponseEntity<byte[]> getImageById(@PathVariable int imageId) throws IOException {
        return ResponseEntity.ok().body(imageService.getImage(imageId));
    }
}

