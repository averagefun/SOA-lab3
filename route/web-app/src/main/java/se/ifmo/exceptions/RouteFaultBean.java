package se.ifmo.exceptions;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteFaultBean implements Serializable {
    private int statusCode;
    private String error;

    public RouteFaultBean() {}

    public RouteFaultBean(int statusCode, String error) {
        this.statusCode = statusCode;
        this.error = error;
    }
}