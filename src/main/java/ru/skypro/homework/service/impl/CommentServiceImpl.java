package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.dto.CommentsDTO;
import ru.skypro.homework.dto.CreateOrUpdateCommentDTO;
import ru.skypro.homework.dto.Role;
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
import ru.skypro.homework.service.CommentService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper mapper;
    private final CommentRepository commentRepository;
    private final AdRepository adRepository;
    private final UserRepository userRepository;

    /**
     * Возвращет все комментарии для конкретного объявления
     *
     * @param adId Id объявления
     * @return CommentsDTO
     * @see CommentsDTO
     */
    @Override
    public CommentsDTO getCommentsForAd(int adId) {
        return mapper.toCommentsDTO(commentRepository.findCommentsByAdId(adId));
    }

    /**
     * Добавление нового комментария к объявлению
     *
     * @param adId                     Id объявления
     * @param createOrUpdateCommentDTO DTO объект, представляющий новый комментарий
     * @return CommentDTO нового комментария
     * @throws AdNotFoundException, если объявления с переданным Id не существует
     */
    @Override
    public CommentDTO addCommentToAd(int adId, CreateOrUpdateCommentDTO createOrUpdateCommentDTO) {
        Comment newComment = new Comment();
        newComment.setCreatedAt(LocalDateTime.now());
        newComment.setText(createOrUpdateCommentDTO.getText());
        newComment.setAd(adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException("Объявление с Id: " + adId + " не найдено")));
        newComment.setUser(getCurrentUser());
        return mapper.toDto(commentRepository.save(newComment));
    }

    /**
     * Удаление ранее опубликованного комментария, принадлежащего текущему пользователю
     *
     * @param adId      Id объявления
     * @param commentId Id комменатрия
     * @throws CommentNotFoundException   если комментария с переданным Id не существует
     * @throws UserNotAuthorizedException если пользователь не является автором комментария и не админ
     */
    @Override
    public void deleteComment(int adId, int commentId) {
        if (isCurrentUserAuthorized(commentId)) {
            commentRepository.delete(findByCommentId(commentId));
        } else {
            throw new UserNotAuthorizedException("У пользователя c id: " + getCurrentUser().getId() +
                    " недостаточно прав для удаления комментария: " + commentId);
        }
    }

    /**
     * Обновление текста комментария пользователя
     *
     * @param adId                     id объявления, к которому был опубликован комментарий
     * @param commentId                id комментария
     * @param createOrUpdateCommentDTO DTO объект, представляющий обновленный комментарий
     * @return обновленный CommentDTO
     * @throws CommentNotFoundException   если комментария с переданным Id не существует
     * @throws UserNotAuthorizedException если пользователь не является автором комментария и не админ
     */
    @Override
    public CommentDTO updateCommentText(int adId,
                                        int commentId,
                                        CreateOrUpdateCommentDTO createOrUpdateCommentDTO) {
        if (isCurrentUserAuthorized(commentId)) {
            Comment commentToUpdate = findByCommentId(commentId);
            commentToUpdate.setText(createOrUpdateCommentDTO.getText());
            commentToUpdate.setCreatedAt(LocalDateTime.now());
            return mapper.toDto(commentRepository.save(commentToUpdate));
        }
        throw new UserNotAuthorizedException("У пользователя c id: " + getCurrentUser().getId() +
                " недостаточно прав для редактирования комментария: " + commentId);
    }

    /**
     * Метод, который возвращает текущего пользователя
     *
     * @return User
     * @see User
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email: " + email + " is not found"));
    }

    /**
     * Метод, который определяет, уполномочен ли текущий пользователь осуществлять определенные действия,
     * такие как: редактирование и удаление комментария (пользователь вправе делать это
     * в случае, если он является автором данного комментария либо имеет роль админ).
     *
     * @param commentId Id комментария
     * @return true, если текущий пользователь - это автор комментария (или админ),
     * false - если нет.
     * @see Role
     */
    private boolean isCurrentUserAuthorized(int commentId) {
        User currentUser = getCurrentUser();
        User commentAuthor = userRepository.findById(commentRepository.findAuthorIdById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий с id: " + commentId + " не найден")))
                .orElseThrow(() -> new UserNotFoundException("Пользователь, написавший комментария с id: " + commentId + " не найден"));
        return currentUser.equals(commentAuthor) || currentUser.getRole() == Role.ADMIN;
    }

    private Comment findByCommentId(int commentId) {
        checkIfCommentExist(commentId);
        return commentRepository.findById(commentId).get();
    }

    private void checkIfCommentExist(int commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new CommentNotFoundException("Комментарий с id: " + commentId + " не найден");
        }
    }
}
