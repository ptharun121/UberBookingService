package com.example.uberbookingservice.dto;

import com.example.uberprojectentityservice.models.BookingStatus;
import com.example.uberprojectentityservice.models.Driver;
import lombok.*;

import java.util.Optional;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookingResponseDto {

    private long bookingId;

    private BookingStatus bookingStatus;

    private Optional<Driver> driver;

}
