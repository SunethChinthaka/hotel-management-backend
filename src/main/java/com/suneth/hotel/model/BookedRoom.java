package com.suneth.hotel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookedRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @Column(name = "check_in")
    private LocalDate checkInDate;

    @Column(name = "check_out")
    private LocalDate checkOutDate;

    @Column(name = "guest_fullName")
    private String guestFullName;

    @Column(name = "guest_email")
    private String guestEmail;

    @Column(name = "adults")
    private int numOfAdults;

    @Column(name = "children")
    private int numOfChildren;

    @Column(name = "total_guests")
    private int totalNumOfGuests;

    @Column(name = "confirmation_code")
    private String bookingConfirmationCode;

    /**
     * Represents the room booked.
     * It's a Many-to-One relationship, indicating that many booked rooms can be associated with one room.
     * It's fetched lazily, meaning it will be loaded from the database only when accessed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    // Helper method to calculate total number of guests
    public void calculateTotalNumberOfGuest() {
        this.totalNumOfGuests = this.numOfAdults + numOfChildren;
    }

    // Setter method for the number of adults, triggers total guest calculation
    public void setNumOfAdults(int numOfAdults) {
        this.numOfAdults = numOfAdults;
        calculateTotalNumberOfGuest();
    }

    // Setter method for the number of children, triggers total guest calculation
    public void setNumOfChildren(int numOfChildren) {
        this.numOfChildren = numOfChildren;
        calculateTotalNumberOfGuest();
    }

//    public void setBookingConfirmationCode(String bookingConfirmationCode) {
//        this.bookingConfirmationCode = bookingConfirmationCode;
//    }
}
