package ru.skypro.homework.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public void setPassword(NewPasswordDTO newPasswordDTO) {

    }

    @Override
    public UserDTO getInfo() {
        return null;
    }

    @Override
    public UpdateUserDTO updateUser(UpdateUserDTO updateUserDTO) {
        return null;
    }

    @Override
    public void updateUserImage(MultipartFile newImage) {

    }
}
