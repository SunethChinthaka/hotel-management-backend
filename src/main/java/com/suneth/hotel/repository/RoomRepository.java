package com.suneth.hotel.repository;

import com.suneth.hotel.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    // Custom query to find distinct room types
    @Query("SELECT DISTINCT r.roomType FROM Room r")
    List<String> findDistinctRoomTypes();

    //Custom query to retrieve all rooms of a certain type (roomType) that are not currently booked for a specified time period.
    @Query("SELECT r FROM Room r " +
            "WHERE r.roomType LIKE %:roomType% AND r.id " +
            "NOT IN (SELECT br.room.id FROM BookedRoom br " +
            "WHERE ((br.checkInDate <= :checkOutDate) AND (br.checkOutDate >= :checkInDate)))")
    List<Room> findAvailableRoomsByDatesAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType);
}
