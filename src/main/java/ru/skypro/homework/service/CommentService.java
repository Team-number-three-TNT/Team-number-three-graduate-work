package ru.skypro.homework.service;

import ru.skypro.homework.dto.CreateOrUpdateCommentDTO;

public interface CommentService {
    List<CommentsDTO> getCommentsForAd(int adId);

    CommentDTO addCommentToAd(int adId, CreateOrUpdateCommentDTO createOrUpdateCommentDTO);

    void deleteComment(int adId, int commentId);

    CommentDTO updateCommentText(int adId, int commentId, CreateOrUpdateCommentDTO createOrUpdateCommentDTO);
}
