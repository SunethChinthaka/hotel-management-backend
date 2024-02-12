package com.suneth.hotel.controller;

import com.suneth.hotel.exception.InvalidBookingRequestException;
import com.suneth.hotel.exception.ResourceNotFoundException;
import com.suneth.hotel.model.BookedRoom;
import com.suneth.hotel.model.Room;
import com.suneth.hotel.response.BookingResponse;
import com.suneth.hotel.response.RoomResponse;
import com.suneth.hotel.service.IBookingService;
import com.suneth.hotel.service.IRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class BookingController {

    private final IBookingService bookingService;
    private final IRoomService roomService;

    // Endpoint to retrieve all bookings
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        // Retrieve all bookings from the booking service
        List<BookedRoom> bookings = bookingService.getAllBookings();
        // Initialize a list to store response objects
        List<BookingResponse> bookingResponses = new ArrayList<>();

        // Iterate through each booking and create a corresponding response object
        for (BookedRoom booking : bookings) {
            BookingResponse bookingResponse = getBookingResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        // Return response with OK status and list of booking responses
        return ResponseEntity.ok(bookingResponses);
    }

    // Helper method to create a BookingResponse object from a BookedRoom object
    private BookingResponse getBookingResponse(BookedRoom booking) {
        // Retrieve room details associated with the booking
        Room room = roomService.getRoomById(booking.getRoom().getId()).get();
        // Create RoomResponse object from room details
        RoomResponse roomResponse = new RoomResponse(room.getId(), room.getRoomType(), room.getRoomPrice());

        // Create and return BookingResponse object
        return new BookingResponse(
                booking.getBookingId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getGuestFullName(),
                booking.getGuestEmail(),
                booking.getNumOfAdults(),
                booking.getNumOfChildren(),
                booking.getTotalNumOfGuests(),
                booking.getBookingConfirmationCode(),
                roomResponse);
    }

    // Endpoint to save a booking for a specific room
    @PostMapping("{roomId}/booking")
    public ResponseEntity<?> saveBooking(@PathVariable Long roomId, @RequestBody BookedRoom bookingRequest) {
        try {
            // Attempt to save the booking and retrieve the confirmation code
            String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);
            // Return success response with the confirmation code
            return ResponseEntity.ok("Room Booked Successfully! Your Booking Confirmation Code is :" + confirmationCode);
        } catch (InvalidBookingRequestException e) {
            // Return bad request response with error message if the booking request is invalid
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint to cancel a booking
    @DeleteMapping("{bookingId}")
    public void cancelBooking(@PathVariable Long bookingId) {
        // Call booking service to cancel the booking
        bookingService.cancelBooking(bookingId);
    }

    // Endpoint to retrieve a booking by confirmation code
    @GetMapping("{confirmationCode}")
    public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable String confirmationCode) {
        try {
            // Retrieve booking by confirmation code
            BookedRoom booking = bookingService.findByBookingConfirmationCode(confirmationCode);
            // Create response object for the booking
            BookingResponse bookingResponse = getBookingResponse(booking);
            // Return success response with the booking details
            return ResponseEntity.ok(bookingResponse);
        } catch (ResourceNotFoundException e) {
            // Return not found response with error message if booking is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
