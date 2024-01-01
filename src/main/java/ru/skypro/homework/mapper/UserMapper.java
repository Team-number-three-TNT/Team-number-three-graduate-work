package ru.skypro.homework.mapper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.dto.UserPrincipalDTO;
import ru.skypro.homework.entity.User;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final ModelMapper mapper;

    @Value("${query.to.get.image}")
    private String imageQuery;

    public UserDTO toDTO(@NonNull User user) {
        UserDTO userDTO = mapper.map(user, UserDTO.class);
        Optional.ofNullable(user.getImage())
                .ifPresent(elem -> userDTO.setImage(imageQuery + user.getImage().getId()));
        return userDTO;
    }

    public UserPrincipalDTO toUserPrincipalDTO(@NonNull User user) {
        return mapper.map(user, UserPrincipalDTO.class);
    }
}
