package ru.skypro.homework.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CommentDTO {
    //authorId
    private Integer author;
    private String authorImage;
    private String authorFirstName;
    private Integer createdAt;
    //id for comment
    private Integer pk;

    @NotEmpty
    private String text;

}
