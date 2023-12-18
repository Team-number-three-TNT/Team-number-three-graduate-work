package ru.skypro.homework.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entity.Ad;

import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.User;
@Component
@RequiredArgsConstructor
public class Mapper {
    private final ModelMapper modelMapper;

    public UserDTO toUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public User toUser(UserDTO userDto) {
        return modelMapper.map(userDto, User.class);
    }

    public CommentDTO toCommentDTO(Comment comment) {
        return modelMapper.map(comment, CommentDTO.class);
    }

    public Comment toComment(CommentDTO commentDTO) {
        return modelMapper.map(commentDTO, Comment.class);
    }

    public AdDTO toAdDTO(Ad ad) {
        return modelMapper.map(ad, AdDTO.class);
    }

    public Ad toAd(CreateOrUpdateAdDTO createOrUpdateAdDTO) {
        return modelMapper.map(createOrUpdateAdDTO, Ad.class);
    }

    public ExtendedAdDTO toExtendedAdDTO(Ad ad) {
        return modelMapper.map(ad, ExtendedAdDTO.class);
    }

}