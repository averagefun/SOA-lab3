package de.gre90r.jaxwsserver.models;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

@Data
@XmlRootElement(name = "route")
public class Route {

    private int id; // minimum: 1
    private String name; // minLength: 1
    private Coordinates coordinates;
    private Location from;
    private Location to;
    private int distance; // minimum: 2

    // Lombok @Data generates getters, setters, toString, equals, and hashCode methods
}