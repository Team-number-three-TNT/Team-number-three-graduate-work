package ru.skypro.homework.dto;


import lombok.Data;

@Data
public class UserDTO {

    private Integer id;

    private String email;

    private String firstName;

    private String lastName;

    private String phone;

    private Role role;

    /**
     * GET запрос на получение аватара пользователя
     */
    private String avatarQuery;
}
