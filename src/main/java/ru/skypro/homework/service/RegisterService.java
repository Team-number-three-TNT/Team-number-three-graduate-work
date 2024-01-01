package ru.skypro.homework.service;

import ru.skypro.homework.dto.RegisterDTO;

public interface RegisterService {
    boolean registerUser(RegisterDTO registerDTO);
}
