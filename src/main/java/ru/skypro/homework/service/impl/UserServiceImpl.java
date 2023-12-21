package ru.skypro.homework.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import ru.skypro.homework.dto.UpdateUserDTO;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.UserAvatarProcessingException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;
    private final String fullAvatarPath;

    public UserServiceImpl(final UserRepository userRepository,
                           final UserMapper userMapper,
                           final PasswordEncoder encoder,
                           @Value("${path.to.avatars.folder}") String pathToAvatarsDir) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.encoder = encoder;
        this.fullAvatarPath = UriComponentsBuilder.newInstance()
                .path(pathToAvatarsDir + "/")
                .build()
                .toUriString();
    }

    @Override
    public UserDTO getAuthorizedUser() {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principalUser = (UserDetails) currentUser.getPrincipal();
        return userMapper.toDTO(
                userRepository.findByEmail(principalUser.getUsername())
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
    public byte[] getAvatar(final String fileName) throws IOException {
        Path path = Path.of(fullAvatarPath, fileName);
        return new ByteArrayResource(Files
                .readAllBytes(path)
        ).getByteArray();
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
    public String updateAvatar(final MultipartFile file) {
        UserDTO userDTO = this.getAuthorizedUser();
        try {
            String extension = getExtensions(Objects.requireNonNull(file.getOriginalFilename()));
            byte[] data = file.getBytes();
            String fileName = UUID.randomUUID() + "." + extension;
            Path pathToAvatar = Path.of(fullAvatarPath, fileName);
            writeToFile(pathToAvatar, data);

            String avatar = userDTO.getImage();
            if (avatar != null) {
                Path path = Path.of(avatar.substring(1));
                Files.delete(path);
            }

            userRepository
                    .findById(userDTO.getId())
                    .map(user -> {
                        user.setImage(fileName);
                        return userMapper.toDTO(userRepository.save(user));
                    });

            return fileName;
        } catch (IOException e) {
            throw new UserAvatarProcessingException();
        }
    }


    private void setNewPassword(final String email, final String password) {
        String encodedPassword = encoder.encode(password);
        User user = userRepository.findByEmail(email);
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    private boolean checkCurrentPassword(final String email, final String password) {
        User user = userRepository.findByEmail(email);
        return encoder.matches(password, user.getPassword());
    }

    private String getExtensions(String fileName) {
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