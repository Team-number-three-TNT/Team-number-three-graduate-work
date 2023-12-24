package ru.skypro.homework.mapper;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.dto.UserPrincipalDTO;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.repository.ImageRepository;

import java.util.Optional;

@Component
public class UserMapper {

    private final ImageRepository imageRepository;
    private final String fullAvatarPath;

    public UserMapper(@Value("${path.to.avatars.folder}") String pathToAvatarDir,
                      ImageRepository imageRepository) {
        this.fullAvatarPath = UriComponentsBuilder.newInstance()
                .path("/" + pathToAvatarDir + "/")
                .build()
                .toUriString();
        this.imageRepository = imageRepository;
    }

    public UserDTO toDTO(@NonNull User user) {
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setPhone(user.getPhone());
        userDTO.setRole(user.getRole());

        Optional.ofNullable(user.getImage())
                .ifPresent(elem -> userDTO.setImage(fullAvatarPath + user.getImage()));
        return userDTO;
    }

    public User toEntity(UserDTO userDTO) {
        User user = new User();

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setImage(imageRepository.findByUserId(userDTO.getId()).orElse(null));
        user.setPhone(userDTO.getPhone());
        user.setRole(userDTO.getRole());

        return user;
    }

    public UserPrincipalDTO toUserPrincipalDTO(@NonNull User user) {
        UserPrincipalDTO userDTO = new UserPrincipalDTO();

        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setRole(user.getRole());

        return userDTO;
    }
}
