package ru.skypro.homework.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.User;

@Component
public class Mapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static UserDTO toUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public static User toUser(UserDTO userDto) {
        return modelMapper.map(userDto, User.class);
    }

    public static CommentDTO toCommentDTO(Comment comment) {
        return modelMapper.map(comment, CommentDTO.class);
    }

    public static Comment toComment(CommentDTO commentDTO) {
        return modelMapper.map(commentDTO, Comment.class);
    }
}
