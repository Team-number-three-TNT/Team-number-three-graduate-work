package ru.skypro.homework.dto;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class CreateOrUpdateCommentDTO {

    /**
     * Текст комментария
     */
    @Size(min = 8, max = 64)
    private String text;
}
