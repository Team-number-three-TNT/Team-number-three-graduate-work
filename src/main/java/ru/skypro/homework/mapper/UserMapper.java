package ru.skypro.homework.mapper;

import lombok.*;
import org.springframework.stereotype.*;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.entity.User;

@Component
public class UserMapper {
    public UserDTO toDTO(@NonNull User user) {
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setPhone(user.getPhone());
        userDTO.setRole(user.getRole());

        return userDTO;
    }

    public User toEntity(UserDTO userDto) {
        User user = new User();

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setImage(userDto.getImage());
        user.setPhone(userDto.getPhone());
        user.setRole(userDto.getRole());

        return user;
    }

}
