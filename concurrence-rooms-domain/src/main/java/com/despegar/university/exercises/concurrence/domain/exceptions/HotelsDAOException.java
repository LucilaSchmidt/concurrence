package com.despegar.university.exercises.concurrence.domain.exceptions;

public class HotelsDAOException
    extends RuntimeException {

    public HotelsDAOException(String message, Throwable cause) {
        super(message, cause);
    }

    private static final long serialVersionUID = 1L;

}
