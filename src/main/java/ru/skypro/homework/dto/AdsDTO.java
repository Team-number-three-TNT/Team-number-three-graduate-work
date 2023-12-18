package ru.skypro.homework.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
public class AdsDTO {

    /**
     * Общее количество объявлений
     */
    private Integer count;

    /**
     * Список объявлений
     */
    private List<AdDTO> results;

    public AdsDTO(Integer count, List<AdDTO> results) {
        this.count = count;
        this.results = results;
    }
}
