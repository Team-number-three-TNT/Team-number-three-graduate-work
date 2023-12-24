package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.dto.CommentsDTO;
import ru.skypro.homework.dto.CreateOrUpdateCommentDTO;
import ru.skypro.homework.service.CommentService;

import java.util.List;

@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/ads")
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/{id}/comments")
    public ResponseEntity<?> getCommentsForAd(@PathVariable int id) {
        CommentsDTO results = commentService.getCommentsForAd(id);
        if (results.getResults().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(results);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addCommentToAd(@PathVariable int id,
                                            @RequestBody CreateOrUpdateCommentDTO createOrUpdateCommentDTO) {
        CommentDTO commentDTO = commentService.addCommentToAd(id, createOrUpdateCommentDTO);
        if (commentDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(commentDTO);
    }

    @DeleteMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable int adId,
                                           @PathVariable int commentId) {
        commentService.deleteComment(adId, commentId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable int adId,
                                           @PathVariable int commentId,
                                           @RequestBody CreateOrUpdateCommentDTO createOrUpdateCommentDTO) {
        CommentDTO commentDTO = commentService.updateCommentText(adId, commentId, createOrUpdateCommentDTO);
        if (commentDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(commentDTO);
    }
}
