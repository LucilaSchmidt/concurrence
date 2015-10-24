package com.despegar.university.exercises.concurrence.domain.exceptions;

public class BookingServiceException
    extends RequestException {

    private static final long serialVersionUID = 1L;

    public BookingServiceException(String message) {
        super(message);
    }

}
