package de.gre90r.jaxwsserver.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement(name = "location")
public class Location {

    @NotNull(message = "X cannot be null")
    private Integer x; // Поле не может быть null

    private long y;

    private int z;

    @Size(min = 1, message = "Name cannot be empty")
    private String name; // Строка не может быть пустой, поле может быть null

    // Lombok @Data генерирует геттеры, сеттеры и другие методы
}