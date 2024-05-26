package com.example.uberbookingservice.dto;

import com.example.uberprojectentityservice.models.ExactLocation;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingDto {

    private Long passenerId;

    private ExactLocation startLocation;

    private ExactLocation endLocation;
}
