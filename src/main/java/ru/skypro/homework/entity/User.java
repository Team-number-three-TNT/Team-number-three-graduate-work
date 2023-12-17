package ru.skypro.homework.entity;

import lombok.*;
import ru.skypro.homework.dto.Role;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
@ToString
@EqualsAndHashCode
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    private String image;

}
