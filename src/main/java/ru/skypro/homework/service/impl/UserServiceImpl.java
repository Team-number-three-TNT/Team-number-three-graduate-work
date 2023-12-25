package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.UpdateUserDTO;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.UserAvatarProcessingException;
import ru.skypro.homework.exception.UserHasNotImageException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;
    private final ImageRepository imageRepository;

    @Value("{path.to.avatars.folder}")
    private String avatarsDir;

    @Override
    public UserDTO getAuthorizedUser() {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principalUser = (UserDetails) currentUser.getPrincipal();
        return userMapper.toDTO(
                userRepository.findByEmail(principalUser.getUsername())
                        .orElseThrow(() -> new UserNotFoundException("Пользователь с email: " + principalUser.getUsername() + " не найден"))
        );
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
        UserDTO userDTO = this.getAuthorizedUser();
        userRepository
                .findById(userDTO.getId())
                .map(oldUser -> {
                    oldUser.setFirstName(updateUser.getFirstName());
                    oldUser.setLastName(updateUser.getLastName());
                    oldUser.setPhone(updateUser.getPhone());
                    return userMapper.toDTO(userRepository.save(oldUser));
                });
        return updateUser;

    }

    @Override
    public byte[] getAvatar(int avatarId) throws IOException {
        Path path = Path.of(avatarsDir + ":" + avatarId);
        return new ByteArrayResource(Files
                .readAllBytes(path)
        ).getByteArray();
    }

    /**
     * Обновление аватара пользователя
     *
     * @param file переданный пользвателем файл
     * @return обновленный аватар в массиве байтов
     */
    @Override
    public byte[] updateAvatar(final MultipartFile file) {
        UserDTO userDTO = this.getAuthorizedUser();
        try {
            String extension = getExtension(Objects.requireNonNull(file.getOriginalFilename()));
            byte[] data = file.getBytes();
            Path pathToAvatar = Path.of(avatarsDir + ":" + getAuthorizedUser().getId() + "." + extension);
            writeToFile(pathToAvatar, data);

            String avatar = userMapper.toEntity(userDTO).getImage().getFilePath();
            if (avatar != null) {
                Path path = Path.of(avatar.substring(1));
                Files.delete(path);
            }

            Image oldImage = imageRepository.findByUserId(userDTO.getId())
                    .orElseThrow(() -> new UserHasNotImageException("У пользователя с id: " + userDTO.getId() + " нет аватарки для обновления"));
            oldImage.setFilePath(pathToAvatar.toString());
            oldImage.setFileSize(file.getSize());
            oldImage.setMediaType(file.getContentType());

            userRepository
                    .findById(userDTO.getId())
                    .map(user -> {
                        user.setImage(oldImage);
                        return userMapper.toDTO(userRepository.save(user));
                    });

            return data;
        } catch (IOException e) {
            throw new UserAvatarProcessingException();
        }
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

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private void writeToFile(Path path, byte[] data) {
        try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
            fos.write(data);
        } catch (IOException e) {
            throw new UserAvatarProcessingException();
        }
    }
}