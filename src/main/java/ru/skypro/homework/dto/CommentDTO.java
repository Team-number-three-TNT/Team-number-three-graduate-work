package ru.skypro.homework.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CommentDTO {
    //authorId
    private Integer author;
    private String authorImage;
    private String authorFirstName;
    private Long createdAt;
    //id for comment
    private Integer pk;

    @NotEmpty
    private String text;

}
