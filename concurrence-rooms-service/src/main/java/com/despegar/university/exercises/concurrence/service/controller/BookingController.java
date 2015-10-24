package com.despegar.university.exercises.concurrence.service.controller;


import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.despegar.university.exercises.concurrence.domain.exceptions.RequestException;
import com.despegar.university.exercises.concurrence.domain.model.BookingRequestDTO;
import com.despegar.university.exercises.concurrence.domain.services.BookingService;
import com.despegar.university.exercises.concurrence.service.editor.DateEditor;

@RestController
public class BookingController {

    private static final String BOOKING_NOT_FOUND = "Booking with id %s not found";

    @Autowired
    private BookingService bookingService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDateTime.class, new DateEditor());
    }


    @RequestMapping(value = "/bookings/reserve", method = RequestMethod.POST)
    public ResponseEntity<String> reserve(@RequestBody BookingRequestDTO request) throws Exception {
        try {
            String response = this.bookingService.reserve(request);
            return new ResponseEntity<String>(response, HttpStatus.OK);
        } catch (RequestException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/bookings/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> search(@PathVariable String id) throws Exception {
        BookingRequestDTO booking = this.bookingService.searchBooking(id);

        if (booking != null) {
            return new ResponseEntity<Object>(booking, HttpStatus.OK);
        } else {
            return new ResponseEntity<Object>(String.format(BOOKING_NOT_FOUND, id), HttpStatus.NOT_FOUND);
        }
    }

}
