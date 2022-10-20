package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;

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
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;


    public Item(Long id, String name, String description, Boolean available, Long userId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isAvailable = available;
        this.owner = userId;
    }
}
