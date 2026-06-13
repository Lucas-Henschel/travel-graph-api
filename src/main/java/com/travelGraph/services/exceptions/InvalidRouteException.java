package com.travelGraph.services.exceptions;

public class InvalidRouteException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidRouteException(String msg) {
        super(msg);
    }
}

