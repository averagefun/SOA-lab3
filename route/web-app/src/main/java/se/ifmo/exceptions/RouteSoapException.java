package se.ifmo.exceptions;

import jakarta.xml.ws.WebFault;
import lombok.Getter;

@Getter
@WebFault(name = "RouteFault", faultBean = "se.ifmo.exceptions.RouteFaultBean")
public class RouteSoapException extends Exception {
    private final RouteFaultBean faultInfo;

    public RouteSoapException(int statusCode, String error) {
        super(error);
        this.faultInfo = new RouteFaultBean(statusCode, error);
    }

    public RouteFaultBean getFaultInfo() {
        return faultInfo;
    }
}
