package ru.skypro.homework.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDTO;
import ru.skypro.homework.dto.UpdateUserDTO;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.service.UserService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/set_password")
    public ResponseEntity<String> setPassword(@RequestBody NewPasswordDTO newPasswordDTO,
                                              @NonNull Authentication authentication) {
        if (userService.updatePassword(authentication.getName(),
                newPasswordDTO.getCurrentPassword(),
                newPasswordDTO.getNewPassword())) {
            return ResponseEntity.ok("Password was update");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Password don`t match!");
        }

    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getUser() {
        return ResponseEntity.ok(userService.getAuthorizedUser());
    }

    @PatchMapping("/me")
    public ResponseEntity<UpdateUserDTO> updateUser(@RequestBody UpdateUserDTO updateUserDTO) {
        UpdateUserDTO updateUser = userService.updateUser(updateUserDTO);
        return ResponseEntity.ok(updateUser);
    }

    @PatchMapping(path = "/me/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public ResponseEntity<byte[]> updateAvatar(@RequestParam MultipartFile image) throws IOException {
        String fileName = userService.updateAvatar(image);
        if (fileName != null) {
            byte[] avatar = userService.getAvatar(fileName);
            return ResponseEntity.ok().body(avatar);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
