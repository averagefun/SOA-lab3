package ru.ifmo.models;

import lombok.Data;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAccessType;
import java.time.LocalDate;

@Data
@XmlRootElement(name = "route")
@XmlAccessorType(XmlAccessType.FIELD)
public class Route {
    private Long id;
    private String name;
    private Coordinates coordinates;
    private Location from;
    private Location to;
    private Double distance;
    private LocalDate creationDate;

    // Конструкторы, геттеры и сеттеры генерируются Lombok
}