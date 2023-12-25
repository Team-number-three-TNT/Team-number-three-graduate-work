package ru.skypro.homework.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Сущность, которая представляет изображение, относящееся к пользователю либо к объявлению
 */
@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    /**
     * Путь к файлу, куда сохранилась копия отправленной пользователем фотографии
     */
    private String filePath;

    /**
     * Размер фотографии
     */
    private Long fileSize;

    /**
     * Поле, хранящее тип контента изображения
     */
    private String mediaType;

    /**
     * Id объявления, к которому относится изображение.
     * <p>
     * Может быть null.
     * @see Ad
     */
    @OneToOne
    @JoinColumn(name = "ad_id")
    private Ad ad;

    /**
     * ID отчета, к которому была прикреплена фотография питомца.
     * <p>
     * Может быть null.
     * @see User
     */
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
