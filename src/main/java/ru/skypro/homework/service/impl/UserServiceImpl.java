package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.UpdateUserDTO;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.UserService;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;
    private final ImageService imageService;

    @Override
    public User getAuthorizedUser() {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principalUser = (UserDetails) currentUser.getPrincipal();
        return userRepository.findByEmail(principalUser.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Пользователь с email: " + principalUser.getUsername() + " не найден"));
    }

    @Override
    public boolean updatePassword(final String email, final String currentPassword, final String newPassword) {
        if (checkCurrentPassword(email, currentPassword)) {
            setNewPassword(email, newPassword);
            return true;
        }
        return false;
    }

    @Override
    public UpdateUserDTO updateUser(final UpdateUserDTO updateUser) {
        User user = this.getAuthorizedUser();
        userRepository
                .findById(user.getId())
                .map(oldUser -> {
                    oldUser.setFirstName(updateUser.getFirstName());
                    oldUser.setLastName(updateUser.getLastName());
                    oldUser.setPhone(updateUser.getPhone());
                    return userMapper.toDTO(userRepository.save(oldUser));
                });
        return updateUser;
    }

    /**
     * Сохранение нового / обновление старого аватара пользователя.
     * В зависимости от того, есть ли у пользователя уже аватар, будут
     * использованы разные методы {@link ImageService}
     *
     * @param file переданный пользвателем файл
     * @return обновленный аватар в массиве байтов
     */
    @Override
    public byte[] updateAvatar(final MultipartFile file) throws IOException {
        User user = this.getAuthorizedUser();
        Image image;

        if (imageService.checkIfUserHasAvatar(user.getId())) {
            image = imageService.updateImage(file, user.getId());
        } else {
            image = imageService.saveImageExceptHolderField(file);
            image.setUser(user);
            user.setImage(image);
            userRepository.save(user);
            imageService.saveImageToDb(image);
        }
        return imageService.getImage(image.getId());
    }

    private void setNewPassword(final String email, final String password) {
        String encodedPassword = encoder.encode(password);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с email: " + email + " не найден"));
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    private boolean checkCurrentPassword(final String email, final String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с email: " + email + " не найден"));
        return encoder.matches(password, user.getPassword());
    }
}