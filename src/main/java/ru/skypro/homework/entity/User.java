package ru.skypro.homework.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.skypro.homework.dto.Role;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
        private Set<Ad> ads = new HashSet<>();

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
        private Set<Comment> comments = new HashSet<>();

}
