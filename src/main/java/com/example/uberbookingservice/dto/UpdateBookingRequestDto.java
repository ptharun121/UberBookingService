package com.example.uberbookingservice.dto;

import com.example.uberprojectentityservice.models.BookingStatus;
import lombok.*;

import java.util.Optional;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookingRequestDto {

    private String bookingStatus;

    private Optional<Long> driverId;
}
