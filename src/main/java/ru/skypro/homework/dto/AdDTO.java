package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class AdDTO {

    private Integer author;

    /**
     * GET запрос на получение фотографии объявления
     */
    private String imageQuery;

    /**
     * Id объявления
     */
    private Integer pk;

    private Integer price;

    private String title;

}
