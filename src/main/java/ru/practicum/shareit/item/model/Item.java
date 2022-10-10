package ru.practicum.shareit.item.model;

import lombok.*;

import javax.persistence.*;


@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Boolean isAvailable;
    @Column(name = "owner_id", nullable = false)
    private Long owner;
    @Column(name = "request_id")
    private Long request;


    public Item(Long id, String name, String description, Boolean available, Long userId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isAvailable = available;
        this.owner = userId;
    }
}
