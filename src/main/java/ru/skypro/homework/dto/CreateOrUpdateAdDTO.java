package ru.skypro.homework.dto;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class CreateOrUpdateAdDTO {


    @Size(min = 4, max = 32)
    private String title;
    @Size(min = 0, max = 1000000)
    private Integer price;

    @Size(min = 8, max = 64)

    private String description;
}
