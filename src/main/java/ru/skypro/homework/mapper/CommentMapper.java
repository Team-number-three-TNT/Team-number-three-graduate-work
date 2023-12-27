package ru.skypro.homework.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.dto.CommentsDTO;
import ru.skypro.homework.dto.CreateOrUpdateCommentDTO;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.User;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final ModelMapper mapper;

    @Value("${query.to.get.image}")
    private String imageQuery;

    public CommentDTO toDto(Comment comment) {
        CommentDTO commentDTO = mapper.map(comment, CommentDTO.class);
        commentDTO.setPk(comment.getId());
        commentDTO.setCreatedAt(comment.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        User user = comment.getUser();
        if (user != null) {
            commentDTO.setAuthor(user.getId());
            commentDTO.setAuthorFirstName(user.getFirstName());
            commentDTO.setAuthorImage(imageQuery + user.getImage().getId());
        }
        return commentDTO;
    }

    public Comment toEntity(CreateOrUpdateCommentDTO createOrUpdateCommentDTO) {
        return mapper.map(createOrUpdateCommentDTO, Comment.class);
    }

    public CommentsDTO toCommentsDTO(List<Comment> comments) {
        CommentsDTO commentsDTO = new CommentsDTO();
        commentsDTO.setCount(comments.size());
        commentsDTO.setResults(comments.stream().map(e -> toDto(e)).collect(Collectors.toList()));
        return commentsDTO;
    }
}
