package com.despegar.university.exercises.concurrence.domain.exceptions;

public class RequestException
    extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RequestException(String message) {
        super(message);
    }

}
