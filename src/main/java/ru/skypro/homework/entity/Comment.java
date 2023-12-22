package ru.skypro.homework.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    public Integer id;

    /**
     * Дата и время создания комментария в миллисекундах с 00:00:00 01.01.1970
     */
    public Long createdAt;

    /**
     * Id объявления, к которому относится данный комментарий.
     * Комментарий может быть опубликован только один раз и к одному объявлению.
     * @see Ad
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_id", referencedColumnName = "id")
    public Ad ad;

    /**
     * Id пользователя, который отправил данный комментария
     * @see User
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Текст комментария
     */
    public String text;
}
