package se.ifmo.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Embeddable
@Data
public class Coordinates {

    @NotNull(message = "X cannot be null")
    @Min(value = -491, message = "X must be greater than -492")
    private Integer x;

    @NotNull(message = "Y cannot be null")
    private Long y;
}