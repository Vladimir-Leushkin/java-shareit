package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE (upper(i.name) LIKE upper(concat('%',:text,'%')) OR " +
            "upper(i.description) LIKE upper(concat('%',:text,'%')) AND i.isAvailable = true )")
    List<Item> searchByText(@Param("text") String text);

    List<Item> findByOwnerOrderByIdAsc(long userId);
}
