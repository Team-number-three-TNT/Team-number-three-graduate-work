package ru.skypro.homework.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CommentDTO {

    private Integer author;

    /**
     * GET запрос на получение аватара пользователя
     */
    private String avatarQuery;

    private String authorFirstName;

    /**
     * Дата и время создания комментария в миллисекундах с 00:00:00 01.01.1970
     */
    private Long createdAt;

    /**
     * Id комментария
     */
    private Integer pk;

    @NotEmpty
    private String text;

}
