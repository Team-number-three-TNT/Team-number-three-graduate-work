package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDTO;
import ru.skypro.homework.dto.UpdateUserDTO;
import ru.skypro.homework.dto.UserDTO;

import java.io.IOException;

public interface UserService {

    UserDTO getAuthorizedUser();

    boolean updatePassword(String email, String currentPassword, String newPassword);

    byte[] getAvatar(String fileName) throws IOException;

    UpdateUserDTO updateUser(UpdateUserDTO updateUser);

    String updateAvatar(MultipartFile file);
}
