package de.gre90r.jaxwsserver.models;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "location")
public class Location {

    private int x;
    private double y;
    private double z;
    private String name; // maxLength: 729

    // Lombok @Data generates getters, setters, toString, equals, and hashCode methods
}