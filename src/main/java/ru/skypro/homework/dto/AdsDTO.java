package ru.skypro.homework.dto;

import lombok.Data;

import java.util.List;
@Data
public class AdsDTO {
    private Integer count;
    private List<AdDTO> results;

   public AdsDTO(Integer count, List<AdDTO> results) {
        this.count = count;
        this.results = results;
    }
}
