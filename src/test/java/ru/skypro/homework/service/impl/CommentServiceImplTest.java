package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.skypro.homework.dto.CommentsDTO;
import ru.skypro.homework.dto.CreateOrUpdateCommentDTO;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.AdNotFoundException;
import ru.skypro.homework.exception.CommentNotFoundException;
import ru.skypro.homework.exception.UserNotAuthorizedException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.skypro.homework.utils.Constants.*;
import static ru.skypro.homework.utils.EntityDtoFactory.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    CommentRepository commentRepository;

    @Mock
    SecurityContext securityContext;

    @Mock
    Authentication authentication;

    @Mock
    AdRepository adRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    CommentMapper commentMapper;

    @InjectMocks
    CommentServiceImpl out;

    @Test
    @DisplayName("'getCommentsForAd' should return CommentsDTO with the corresponding List of comments that were stored in DB and also right 'count' value")
    void testGetCommentsForAd() {
        Comment comment = getComment();
        when(commentMapper.toDto(any(Comment.class))).thenReturn(getCommentDto());
        when(commentRepository.findCommentsByAdId(ID1)).thenReturn(List.of(comment));
        when(commentMapper.toCommentsDTO(List.of(comment))).thenReturn(new CommentsDTO(1, List.of(getCommentDto())));

        CommentsDTO expected = new CommentsDTO(1, List.of(commentMapper.toDto(comment)));
        assertThat(out.getCommentsForAd(ID1)).isEqualTo(expected);
        assertThat(out.getCommentsForAd(ID1).getCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("'addCommentToAd' should return CommentDTO with 'text' field which is equal to given CreateOrUpdateComment DTO")
    void testAddCommentToAd() {
        setAuthentication();
        when(adRepository.findById(ID1)).thenReturn(Optional.of(getAd()));
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(getUser()));
        when(commentRepository.save(any(Comment.class))).thenReturn(getComment());
        when(commentMapper.toDto(getComment())).thenReturn(getCommentDto());

        CreateOrUpdateCommentDTO createOrUpdateCommentDTO = new CreateOrUpdateCommentDTO();
        createOrUpdateCommentDTO.setText(COMMENT_TEXT);
        assertThat(out.addCommentToAd(ID1, createOrUpdateCommentDTO).getText()).isEqualTo(COMMENT_TEXT);
    }

    @Test
    @DisplayName("'addCommentToAd' should throw AdNotFoundException if DB does not contain the Ad of the given Id")
    void addCommentToAd_shouldThrow() {
        when(adRepository.findById(ID1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> out.addCommentToAd(ID1, new CreateOrUpdateCommentDTO()))
                .isInstanceOf(AdNotFoundException.class)
                .hasMessageContaining("Объявление с Id: " + ID1 + " не найдено");
    }

    @Test
    @DisplayName("'deleteComment' should be executed correctly if current user is the author of comment")
    void deleteComment() {
        setAuthentication();
        when(userRepository.findById(ID1)).thenReturn(Optional.of(getUser()));
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(getUser()));
        when(commentRepository.findAuthorIdById(ID1)).thenReturn(Optional.of(ID1));
        when(userRepository.findById(ID1)).thenReturn(Optional.of(getUser()));
        when(commentRepository.existsById(ID1)).thenReturn(true);
        when(commentRepository.findById(ID1)).thenReturn(Optional.of(getComment()));
        assertThatCode((() -> out.deleteComment(ID1, ID1)))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("'deleteComment' should throw CommentNotFoundException if DB does not contain the Comment of the given Id")
    void deleteComment_shouldThrow1() {
        setAuthentication();
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(getUser()));
        when(commentRepository.findAuthorIdById(ID1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> out.deleteComment(ID1, ID1))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessageContaining("Комментарий с id: " + ID1 + " не найден");
    }

    @Test
    @DisplayName("'deleteComment' should throw UserNotFoundException if DB does not contain the User of the given Id")
    void deleteComment_shouldThrow2() {
        setAuthentication();
        when(userRepository.findById(ID1)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(getUser()));
        when(commentRepository.findAuthorIdById(ID1)).thenReturn(Optional.of(ID1));
        when(userRepository.findById(ID1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> out.deleteComment(ID1, ID1))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь, написавший комментария с id: " + ID1 + " не найден");
    }

    @Test
    @DisplayName("'updateComment' should throw UserNotAuthorizedException if User is not the author of the comment")
    void deleteComment_shouldThrow3() {
        setAuthentication();
        when(userRepository.findById(ID1)).thenReturn(Optional.of(getUser()));
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(getUser()));
        when(commentRepository.findAuthorIdById(ID1)).thenReturn(Optional.of(ID1));
        User commentAuthor = getUser();
        commentAuthor.setId(ID2);
        when(userRepository.findById(ID1)).thenReturn(Optional.of(commentAuthor));
        assertThatThrownBy(() -> out.updateCommentText(ID1, ID1, new CreateOrUpdateCommentDTO()))
                .isInstanceOf(UserNotAuthorizedException.class)
                .hasMessageContaining("У пользователя c id: " + ID1 + " недостаточно прав для редактирования комментария: " + ID1);
    }

    private void setAuthentication() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(getUserPrincipal());
    }
}
