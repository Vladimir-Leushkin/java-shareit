package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User requestor;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "request")
    private List<Item> items = new ArrayList<>();

    public ItemRequest(String description, User requestor) {
        this.description = description;
        this.requestor = requestor;
        this.created = LocalDateTime.now();
    }
}
