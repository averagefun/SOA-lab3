package ru.ifmo.models;

import lombok.Data;

import jakarta.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "coordinates")
public class Coordinates {
    private Integer x;
    private Double y;
}