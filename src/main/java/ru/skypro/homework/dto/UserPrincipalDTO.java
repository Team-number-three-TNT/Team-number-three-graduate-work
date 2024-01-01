package ru.skypro.homework.dto;

import lombok.*;

@Data
public class UserPrincipalDTO {

    private Integer id;
    private String email;
    private String password;
    private Role role;
}
