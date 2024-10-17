package ru.ifmo.models;

import lombok.Data;

import jakarta.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "location")
public class Location {
    private Long id;
    private Integer x;
    private Long y;
    private Integer z;
    private String name;
}