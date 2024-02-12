package com.suneth.hotel.repository;

import com.suneth.hotel.model.BookedRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<BookedRoom, Long> {

    // Method to find all bookings for a specific room by room ID
    List<BookedRoom> findByRoomId(Long roomId);

    // Method to find a booking by its confirmation code
    BookedRoom findByBookingConfirmationCode(String confirmationCode);
}
