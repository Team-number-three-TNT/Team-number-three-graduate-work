package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.dto.CommentsDTO;
import ru.skypro.homework.dto.CreateOrUpdateCommentDTO;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.AdNotFoundException;
import ru.skypro.homework.exception.CommentNotFoundException;
import ru.skypro.homework.mapper.Mapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.CommentService;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final Mapper mapper;
    private final CommentRepository commentRepository;
    private final AdRepository adRepository;
    private final UserRepository userRepository;

    /**
     * Возвращет все комментарии для конкретного объявления
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
     * @param adId Id объявления
     * @param createOrUpdateCommentDTO DTO объект, представляющий новый комментарий
     * @return CommentDTO нового комментария
     * @throws AdNotFoundException, если объявления с переданным Id не существует
     */
    @Override
    public CommentDTO addCommentToAd(int adId, CreateOrUpdateCommentDTO createOrUpdateCommentDTO) {
        Comment newComment = new Comment();
        newComment.setCreatedAt(System.currentTimeMillis());
        newComment.setText(createOrUpdateCommentDTO.getText());
        newComment.setAd(adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException("Объявление с Id: " + adId + " не найдено")));
        newComment.setUser(getCurrentUser());
        return mapper.toCommentDTO(commentRepository.save(newComment));
    }

    /**
     * Удаление ранее опубликованного комментария, принадлежащего текущему пользователю
     * @param adId Id объявления
     * @param commentId Id комменатрия
     * @throws CommentNotFoundException, если комментария с переданным Id не существует
     */
    @Override
    public void deleteComment(int adId, int commentId) {
        //проверка, что комментария принадлежит данному пользователю
        commentRepository.delete(findByCommentId(commentId));
    }

    /**
     * Обновление текста комментария пользователя
     * @param adId id объявления, к которому был опубликован комментарий
     * @param commentId id комментария
     * @param createOrUpdateCommentDTO DTO объект, представляющий обновленный комментарий
     * @return обновленный CommentDTO
     * @throws CommentNotFoundException, если комментария с переданным Id не существует
     */
    @Override
    public CommentDTO updateCommentText(int adId,
                                        int commentId,
                                        CreateOrUpdateCommentDTO createOrUpdateCommentDTO) {
        //проверка, что комментария принадлежит данному пользователю
        Comment commentToUpdate = findByCommentId(commentId);
        commentToUpdate.setText(createOrUpdateCommentDTO.getText());
        commentToUpdate.setCreatedAt(System.currentTimeMillis());
        return mapper.toCommentDTO(commentRepository.save(commentToUpdate));
    }

    /**
     * Метод, который возвращает текущего пользователя
     * @return User
     * @see User
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(((UserDetails) authentication.getPrincipal()).getUsername());
    }

    private Comment findByCommentId(int commentId) {
        checkIfCommentExist(commentId);
        return commentRepository.findById(commentId).get();
    }

    private void checkIfCommentExist(int commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new CommentNotFoundException("Комментария с id: " + commentId + " не существует");
        }
    }
}
