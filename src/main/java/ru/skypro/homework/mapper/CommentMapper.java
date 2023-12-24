package ru.skypro.homework.mapper;

import org.modelmapper.ModelMapper;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.dto.CreateOrUpdateCommentDTO;
import ru.skypro.homework.entity.Comment;

public class CommentMapper {

    private final ModelMapper modelMapper;

    public CommentMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public CommentDTO toDto(Comment comment) {
        CommentDTO commentDTO = modelMapper.map(comment, CommentDTO.class);
        commentDTO.setPk(comment.getId());
        if (comment.getAuthor() != null) {
            commentDTO.setAuthor(comment.getAuthor().getId());
            commentDTO.setAuthorFirstName(comment.getAuthor().getFirstName());
            commentDTO.setAuthorImage(comment.getAuthor().getImage());
        }
        commentDTO.setCreatedAt(comment.getCreatedAt() != null ? comment.getCreatedAt().getTime() : null);
        return commentDTO;
    }

    public Comment toEntity(CreateOrUpdateCommentDTO createOrUpdateCommentDTO) {
        return modelMapper.map(createOrUpdateCommentDTO, Comment.class);
    }

}
