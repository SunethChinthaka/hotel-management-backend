package com.suneth.hotel.service;

import com.suneth.hotel.exception.InvalidBookingRequestException;
import com.suneth.hotel.exception.ResourceNotFoundException;
import com.suneth.hotel.model.BookedRoom;
import com.suneth.hotel.model.Room;
import com.suneth.hotel.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements IBookingService {

    private final BookingRepository bookingRepository;
    private final IRoomService roomService;

    // Method to retrieve all bookings for a specific room
    @Override
    public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingRepository.findByRoomId(roomId);
    }

    // Method to retrieve all bookings
    @Override
    public List<BookedRoom> getAllBookings() {
        return bookingRepository.findAll();
    }

    // Method to cancel a booking by its ID
    @Override
    public void cancelBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    // Method to save a booking for a room
    @Override
    public String saveBooking(Long roomId, BookedRoom bookingRequest) {
        // Check if check-out date is before check-in date
        if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
            throw new InvalidBookingRequestException("Check-In Date Must Come Before Check-Out Date.");
        }
        // Retrieve room details
        Room room = roomService.getRoomById(roomId).get();
        List<BookedRoom> existingBookings = room.getBookings();

        // Check room availability for the requested booking
        boolean isRoomAvailable = checkRoomAvailability(bookingRequest, existingBookings);
        if (isRoomAvailable) {
            room.addBooking(bookingRequest);
            bookingRepository.save(bookingRequest);
        } else {
            throw new InvalidBookingRequestException("Sorry! The Room Is Unavailable For The Dates You've Selected.");
        }
        return bookingRequest.getBookingConfirmationCode();
    }

    // Method to check check if the room is available for the requested booking dates
    private boolean checkRoomAvailability(BookedRoom bookingRequest, List<BookedRoom> existingBookings) {
        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        // Condition to check if requested dates overlap with existing bookings
                        // Return true if there is no overlap
                        // Otherwise, return false indicating the room is not available
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
                );
    }

    // Method to find a booking by its confirmation code
    @Override
    public BookedRoom findByBookingConfirmationCode(String confirmationCode) {
        return bookingRepository.findByBookingConfirmationCode(confirmationCode)
                .orElseThrow(()->new ResourceNotFoundException("No Booking Found for Confirmation Code! " + confirmationCode));
    }
}
/**
 * Let's break down the conditions:
 * <p>
 * bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate()):
 * Checks if the requested check-in date is the same as the existing booking's check-in date.
 * <p>
 * bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
 * Checks if the requested check-out date is before the existing booking's check-out date.
 * <p>
 * (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate()) && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate())):
 * Checks if the requested check-in date falls within the range of the existing booking's check-in and check-out dates.
 * <p>
 * (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate()) && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate())):
 * Checks if the requested check-in date is before the existing booking's check-in date and
 * the requested check-out date is the same as the existing booking's check-out date.
 * <p>
 * (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate()) && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate())):
 * Checks if the requested booking spans across the entire duration of the existing booking.
 * <p>
 * (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate()) && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate())):
 * Checks if the requested check-in date is the same as the existing booking's check-out date and
 * the requested check-out date is the same as the existing booking's check-in date.
 * <p>
 * (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate()) && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate())):
 * Checks if both the requested check-in and check-out dates are the same, indicating a zero-duration booking.
 * <p>
 * The noneMatch method is used to ensure that none of the existing bookings match any of the conditions above.
 * If none of the conditions are met for any existing booking, it means there is no overlap, and the method returns true,
 * indicating that the room is available. Otherwise, it returns false, indicating that the room is not available.
 */

