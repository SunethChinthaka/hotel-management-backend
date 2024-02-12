package com.suneth.hotel.controller;

import com.suneth.hotel.exception.PhotoRetrievalException;
import com.suneth.hotel.exception.ResourceNotFoundException;
import com.suneth.hotel.model.BookedRoom;
import com.suneth.hotel.model.Room;
import com.suneth.hotel.response.BookingResponse;
import com.suneth.hotel.response.RoomResponse;
import com.suneth.hotel.service.IBookingService;
import com.suneth.hotel.service.IRoomService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class RoomController {
    private final IRoomService roomService;
    private final IBookingService bookingService;

    // Endpoint to add a new room
    @PostMapping
    public ResponseEntity<RoomResponse> addNewRoom(
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("roomType") String roomType,
            @RequestParam("roomPrice") BigDecimal roomPrice) throws SQLException, IOException {
        // Save the room with provided details
        Room savedRoom = roomService.addNewRoom(photo, roomType, roomPrice);

        // Construct a response object from the saved room details
        RoomResponse response = new RoomResponse(savedRoom.getId(), savedRoom.getRoomType(), savedRoom.getRoomPrice());
        return ResponseEntity.ok(response);
    }

    // Endpoint to get all room types
    @GetMapping("/room-types")
    public List<String> getRoomTypes() {
        return roomService.getAllRoomTypes();
    }

    // Endpoint to get all rooms
    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAllRooms() throws SQLException {
        List<Room> rooms = roomService.getAllRooms();
        // Initialize a list to store room responses
        List<RoomResponse> roomResponses = new ArrayList<>();

        // Iterate through each room to create room responses
        for (Room room : rooms) {
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            if (photoBytes != null && photoBytes.length > 0) {
                // Convert photo bytes to base64 string
                String base64Photo = Base64.encodeBase64String(photoBytes);

                // Create a room response and add it to the list
                RoomResponse roomResponse = getRoomResponse(room);
                roomResponse.setPhoto(base64Photo);
                roomResponses.add(roomResponse);
            }
        }
        // Return response with list of room responses
        return ResponseEntity.ok(roomResponses);
    }

    // Constructs a RoomResponse based on the details of a given Room entity.
    private RoomResponse getRoomResponse(Room room) {
        // Retrieve bookings for the room
        List<BookedRoom> bookings = getAllBookingsByRoomId(room.getId());
    /*    List<BookingResponse> bookingInfo = bookings
                .stream()
                .map(booking -> new BookingResponse(
                        booking.getBookingId(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.getBookingConfirmationCode()))
                .toList();*/

        // Retrieve room photo bytes
        byte[] photoBytes = null;
        Blob photoBlob = room.getPhoto();
        if (photoBlob != null) {
            try {
                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
            } catch (SQLException e) {
                throw new PhotoRetrievalException("Error retrieving photo");
            }
        }
        // Create and return room response object
        return new RoomResponse(room.getId(), room.getRoomType(), room.getRoomPrice(), room.isBooked(), photoBytes);
    }

    // Retrieves all bookings associated with the provided room ID.
    private List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingService.getAllBookingsByRoomId(roomId);

    }

    // Endpoint to deletes a room by its ID.
    @DeleteMapping("{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Endpoint to update a room by its ID.
    @PutMapping("{roomId}")
    public ResponseEntity<RoomResponse> updateRoom(
            @PathVariable Long roomId,
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) BigDecimal roomPrice,
            @RequestParam(required = false) MultipartFile photo) throws IOException, SQLException {

        // Retrieve photo bytes if provided, else get existing photo bytes
        byte[] photoBytes = photo != null && !photo.isEmpty() ?
                photo.getBytes() : roomService.getRoomPhotoByRoomId(roomId);

        // Create photo blob from photo bytes
        Blob photoBlob = photoBytes != null && photoBytes.length > 0 ? new SerialBlob(photoBytes) : null;

        // Update the room with provided details
        Room room = roomService.updateRoom(roomId, roomType, roomPrice, photoBytes);
        room.setPhoto(photoBlob);

        // Construct a response object from the updated room details
        RoomResponse roomResponse = getRoomResponse(room);
        return ResponseEntity.ok(roomResponse);

    }

    // Retrieves a room by its ID.
    @GetMapping("{roomId}")
    public ResponseEntity<Optional<RoomResponse>> getRoomById(@PathVariable Long roomId) {
        // Retrieve the room by ID
        Optional<Room> theRoom = roomService.getRoomById(roomId);
        // If room is found, construct a response object and return
        return theRoom.map(room -> {
            RoomResponse roomResponse = getRoomResponse(room);
            return ResponseEntity.ok(Optional.of(roomResponse));
        }).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }
}
