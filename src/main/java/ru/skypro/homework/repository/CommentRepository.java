package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findCommentsByAdId(int adId);

    @Query(value = "SELECT author_id FROM comments WHERE id = :id", nativeQuery = true)
    Optional<Integer> findAuthorIdById(@Param("id") int id);
}
