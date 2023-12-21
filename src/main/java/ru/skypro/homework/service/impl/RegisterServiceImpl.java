package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.RegisterDTO;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.RegisterService;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private static final String PHONE_NUMBER_PATTERN = "\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}";
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    @Override
    public boolean registerUser(RegisterDTO registerDTO) {
        User foundUser = userRepository.findByEmail(registerDTO.getUsername());
        if (foundUser != null || !validateRegister(registerDTO)) {
            return false;
        }

        String password = encoder.encode(registerDTO.getPassword());

        User newUser = new User();
        newUser.setEmail(registerDTO.getUsername());
        newUser.setPassword(password);
        newUser.setFirstName(registerDTO.getFirstName());
        newUser.setLastName(registerDTO.getLastName());
        newUser.setPhone(registerDTO.getPhone());
        newUser.setRole(registerDTO.getRole());
        userRepository.save(newUser);
        return true;
    }

    private boolean validateRegister(RegisterDTO registerDTO) {
        Pattern pattern = Pattern.compile(PHONE_NUMBER_PATTERN);
        boolean matcher = pattern.matcher(registerDTO.getPhone()).matches();

        return (registerDTO.getUsername().length() >= 4 && registerDTO.getUsername().length() <= 32)
                && (registerDTO.getPassword().length() >= 8 && registerDTO.getPassword().length() <= 16)
                && (registerDTO.getFirstName().length() >= 2 && registerDTO.getFirstName().length() <= 16)
                && (registerDTO.getLastName().length() >= 2 && registerDTO.getLastName().length() <= 16)
                && (matcher);
    }

}
