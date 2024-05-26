package com.example.uberbookingservice.services;

import com.example.uberbookingservice.dto.*;
import com.example.uberbookingservice.repositories.BookingRepository;
import com.example.uberbookingservice.repositories.DriverRepository;
import com.example.uberbookingservice.repositories.PassengerRepository;
import com.example.uberprojectentityservice.models.Booking;
import com.example.uberprojectentityservice.models.BookingStatus;
import com.example.uberprojectentityservice.models.Driver;
import com.example.uberprojectentityservice.models.Passenger;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServcieImpl implements BookingService {

    @Autowired
    private EurekaClient eurekaClient;

    private final BookingRepository bookingRepository;

    private final PassengerRepository passengerRepository;

    private final DriverRepository driverRepository;

    private final RestTemplate restTemplate;

    private static final String LOCATION_SERVICE = "LOCATIONSERVICE";//"http://localhost:7777";

    public BookingServcieImpl(BookingRepository bookingRepository, PassengerRepository passengerRepository, DriverRepository driverRepository) {
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.driverRepository = driverRepository;
        this.restTemplate = new RestTemplate();
    }

    private String getServiceUrl(String serviceName) {
        return eurekaClient.getNextServerFromEureka(serviceName, false).getHomePageUrl();
    }

    @Override
    public CreateBookingResponseDto createBooking(CreateBookingDto bookingDto) {
        Optional<Passenger> passenger = passengerRepository.findById(bookingDto.getPassenerId());
        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.ASSIGING_DRIVER)
                .startLocation(bookingDto.getStartLocation())
                .endLocation(bookingDto.getEndLocation())
                .passenger(passenger.get())
                .build();
        Booking newBooking = bookingRepository.save(booking);

        NearbyDriversRequestDto request = NearbyDriversRequestDto.builder()
                .latitude(bookingDto.getStartLocation().getLatitude())
                .longitude(bookingDto.getStartLocation().getLongitude())
                .build();

        System.out.println(getServiceUrl(LOCATION_SERVICE));

        ResponseEntity<DriverLocationDto[]> result = restTemplate
                .postForEntity(getServiceUrl(LOCATION_SERVICE) + "/api/location/nearby/drivers", request, DriverLocationDto[].class);

        if (result.getStatusCode().is2xxSuccessful() && result.getBody() != null) {
            List<DriverLocationDto> driverLocation = Arrays.asList(result.getBody());
            driverLocation.forEach(loc -> {
                System.out.println(loc.getDriverId()+ " " + "lat : " + loc.getLatitude() + "lon: " +loc.getLongitude());
            });
        }

        return CreateBookingResponseDto.builder()
                .bookingId(newBooking.getId())
                .bookingStatus(newBooking.getBookingStatus().toString())
                .build();
    }

    @Override
    public UpdateBookingResponseDto updateBooking(long bookingId, UpdateBookingRequestDto updateBookingRequestDto) {
        Optional<Driver> driver = driverRepository.findById(updateBookingRequestDto.getDriverId().get());
        if (driver.isPresent()) {
            bookingRepository.updateBookingById(bookingId, BookingStatus.SCHEDULED, driver.get());
            Booking updatedBooking = bookingRepository.findById(bookingId).get();
            return UpdateBookingResponseDto.builder()
                    .bookingId(bookingId)
                    .bookingStatus(updatedBooking.getBookingStatus())
                    .driver(driver)
                    .build();
        }
        return null;
    }
}
