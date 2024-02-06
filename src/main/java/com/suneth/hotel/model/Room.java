package com.suneth.hotel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomType;
    private BigDecimal roomPrice;
    private boolean isBooked = false;

    @Lob
    private Blob photo;

    /**
     * A list of BookedRoom entities associated with this room.
     * It's a One-to-Many relationship, indicating that one room can have multiple bookings.
     * It's fetched lazily, meaning it will be loaded from the database only when accessed.
     * CascadeType.ALL: Indicates that if any operation is performed on the Room, the same operation should be cascaded
     * to all its associated BookedRoom entities.
     * For example, if a Room is deleted, all associated BookedRoom entities should also be deleted.
     * This helps to maintain referential integrity between the entities.
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BookedRoom> bookings;

    public Room() {
        this.bookings = new ArrayList<>();
    }

    /**
     * Adds a booking to the room.
     *
     * @param booking The booked room to be added.
     */
    public void addBooking(BookedRoom booking) {
        if (bookings == null) {
            bookings = new ArrayList<>();
        }
        bookings.add(booking);
        booking.setRoom(this);
        isBooked = true;

        // Generate a random booking confirmation code
        String bookingCode = RandomStringUtils.randomNumeric(10);
        booking.setBookingConfirmationCode(bookingCode);
    }
}
