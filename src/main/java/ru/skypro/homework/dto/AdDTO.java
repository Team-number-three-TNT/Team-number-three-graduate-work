package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class AdDTO {
    private Integer author;
    private String image;
    //ID for ad
    private Integer pk;
    private Integer price;
    private String title;

}
