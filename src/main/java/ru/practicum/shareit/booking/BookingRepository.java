package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker_idOrderByStartDesc(long bookerId);


    List<Booking> findBookingByItem_IdAndEndIsBefore(long itemId, LocalDateTime end);

    List<Booking> findBookingByItem_IdAndStartIsAfter(long itemId, LocalDateTime start);

    @Query(value = "SELECT b FROM Booking b JOIN Item i on b.item = i.id WHERE i.owner = :ownerId ORDER BY b.start DESC")
    List<Booking> findOwnerAll(long ownerId);

}