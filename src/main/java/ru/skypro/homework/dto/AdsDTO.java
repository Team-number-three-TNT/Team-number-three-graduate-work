package ru.skypro.homework.dto;

import lombok.Data;

import java.util.List;


@Data
public class AdsDTO {

    /**
     * Общее количество объявлений
     */
    private Integer count;

    /**
     * Список объявлений
     */
    private List<AdDTO> results;
}
