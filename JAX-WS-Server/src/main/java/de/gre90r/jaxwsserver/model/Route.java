package de.gre90r.jaxwsserver.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; // Значение поля должно генерироваться автоматически

    @NotNull(message = "Name cannot be null")
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @NotNull(message = "Coordinates cannot be null")
    @Valid
    @Embedded
    private Coordinates coordinates;

    private LocalDate creationDate; // Генерируется автоматически

    @Valid
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "from_location_id")
    private Location from;

    @Valid
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "to_location_id")
    private Location to;

    @NotNull(message = "Distance cannot be null")
    @Min(value = 2, message = "Distance must be greater than 1")
    private Double distance;

    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDate.now();
    }

}