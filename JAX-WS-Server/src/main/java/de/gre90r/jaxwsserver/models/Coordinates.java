package de.gre90r.jaxwsserver.models;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "coordinates")
public class Coordinates {

    private int x; // minimum: -491
    private double y;

    // Lombok @Data generates getters, setters, toString, equals, and hashCode methods
}