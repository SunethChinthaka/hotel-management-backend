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
     * <p>
     * mappedBy = "room": This parameter specifies the name of the attribute in the BookedRoom entity that owns the relationship.
     * In this case, it's the room attribute in the BookedRoom class.
     * This means that the BookedRoom entity is the owner of the relationship,
     * and the mapping is defined by the room attribute in the BookedRoom class.
     * <p>
     * fetch = FetchType.LAZY: This parameter specifies the fetch type for the association.
     * With FetchType.LAZY, the associated BookedRoom entities will be loaded lazily,
     * meaning they will not be fetched from the database until they are explicitly accessed.
     * This can help improve performance by avoiding unnecessary loading of associated entities until they are actually needed.
     * <p>
     * cascade = CascadeType.ALL: This parameter specifies the cascade operations to be applied to the
     * associated BookedRoom entities when operations are performed on the owning Room entity.
     * With CascadeType.ALL, all cascade operations (including persist, merge, remove, and refresh)
     * are applied to the associated BookedRoom entities when corresponding operations are performed on the Room entity.
     * For example, if a Room is deleted, all associated BookedRoom entities will also be deleted.
     */
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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
