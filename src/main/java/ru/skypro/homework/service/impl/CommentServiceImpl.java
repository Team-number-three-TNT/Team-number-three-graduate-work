package ru.skypro.homework.service.impl;

import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.dto.CommentsDTO;
import ru.skypro.homework.dto.CreateOrUpdateCommentDTO;
import ru.skypro.homework.service.CommentService;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Override
    public List<CommentsDTO> getCommentsForAd(int adId) {
        return null;
    }

    @Override
    public CommentDTO addCommentToAd(int adId, CreateOrUpdateCommentDTO createOrUpdateCommentDTO) {
        return null;
    }

    @Override
    public void deleteComment(int adId, int commentId) {

    }

    @Override
    public CommentDTO updateCommentText(int adId, int commentId, CreateOrUpdateCommentDTO createOrUpdateCommentDTO) {
        return null;
    }
}
