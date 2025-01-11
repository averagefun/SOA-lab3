package se.ifmo.model.exception;

public class RouteNotFoundException extends RuntimeException{

    public RouteNotFoundException(String message) {
        super(message);
    }
}

