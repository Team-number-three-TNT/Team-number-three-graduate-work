package ru.skypro.homework.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.*;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.entity.User;

@Component
public class UserMapper {
    private final ModelMapper modelMapper;

    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public UserDTO toDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public User toEntity(UserDTO userDto) {
        return modelMapper.map(userDto, User.class);
    }

}
