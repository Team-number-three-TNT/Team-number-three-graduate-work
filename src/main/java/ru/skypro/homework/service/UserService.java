package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.UserDTO;

public interface UserService {
    void setPassword(NewPasswordDTO newPasswordDTO);

    UserDTO getInfo();

    UpdateUserDTO updateUser(UpdateUserDTO updateUserDTO);

    void updateUserImage(MultipartFile newImage);
}
