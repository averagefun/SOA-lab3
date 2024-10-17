package ru.ifmo.models;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate creationDate;
}