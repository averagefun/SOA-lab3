package de.gre90r.jaxwsserver.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement(name = "coordinates")
public class Coordinates {

    @NotNull(message = "X cannot be null")
    @Min(value = -491, message = "X must be greater than -492")
    private Integer x; // Значение поля должно быть больше -492, поле не может быть null

    @NotNull(message = "Y cannot be null")
    private Long y; // Поле не может быть null

    // Lombok @Data генерирует геттеры, сеттеры и другие методы
}