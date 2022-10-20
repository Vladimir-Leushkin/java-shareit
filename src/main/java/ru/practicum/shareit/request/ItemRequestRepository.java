package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Long userId);

    Page<ItemRequest> findAllByRequestorIdIsNotOrderByCreatedDesc(Long userId, PageRequest pageRequest);
}
