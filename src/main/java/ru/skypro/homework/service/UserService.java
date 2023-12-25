package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.UpdateUserDTO;
import ru.skypro.homework.dto.UserDTO;

import java.io.IOException;

public interface UserService {

    UserDTO getAuthorizedUser();

    boolean updatePassword(String email, String currentPassword, String newPassword);

    UpdateUserDTO updateUser(UpdateUserDTO updateUser);

    byte[] updateAvatar(MultipartFile file);

    byte[] getAvatar(int avatarId) throws IOException;
}
