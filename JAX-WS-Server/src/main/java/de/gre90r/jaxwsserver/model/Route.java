package de.gre90r.jaxwsserver.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

import java.time.LocalDate;

@Data
@XmlRootElement(name = "route")
public class Route {

    private long id; // Убрали аннотации валидации

    @NotNull(message = "Name cannot be null")
    @NotEmpty(message = "Name cannot be empty")
    private String name; // Поле не может быть null, строка не может быть пустой

    @NotNull(message = "Coordinates cannot be null")
    @Valid // Добавляем @Valid для валидации вложенного объекта
    private Coordinates coordinates; // Поле не может быть null

    private LocalDate creationDate; // Убрали аннотацию @NotNull

    private Location from; // Поле может быть null

    private Location to; // Поле может быть null

    @NotNull(message = "Distance cannot be null")
    @Min(value = 2, message = "Distance must be greater than 1")
    private Double distance; // Поле не может быть null, значение должно быть больше 1
}