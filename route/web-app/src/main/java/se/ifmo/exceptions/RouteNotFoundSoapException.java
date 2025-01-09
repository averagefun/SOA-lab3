package se.ifmo.exceptions;

import jakarta.xml.ws.WebFault;

@WebFault(name = "RouteNotFoundFault")
public class RouteNotFoundSoapException extends Exception {

    public RouteNotFoundSoapException(String message, Throwable cause) {
        super(message, cause);
    }

    public RouteNotFoundSoapException(String message) {
        super(message);
    }
}
