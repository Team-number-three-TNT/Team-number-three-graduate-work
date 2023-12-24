package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.RegisterDTO;
import ru.skypro.homework.service.RegisterService;

@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping(path = "/register")
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping
    public ResponseEntity<?> registerUser(@RequestBody RegisterDTO registerDTO) {
        if (registerService.registerUser(registerDTO)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
