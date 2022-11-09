package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId, PageRequest pageRequest);

    List<Booking> findBookingByItemIdAndEndIsBefore(long itemId, LocalDateTime end);

    List<Booking> findBookingByItemIdAndStartIsAfter(long itemId, LocalDateTime start);

    List<Booking> findAllByItemOwnerOrderByStartDesc(long ownerId, PageRequest pageRequest);

}
