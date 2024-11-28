package se.ifmo.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
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

    @ManyToOne(optional = true)
    @JoinColumn(name = "from_location_id", nullable = true)
    private Location from;

    @ManyToOne(optional = true) // Устанавливаем связь как опциональную
    @JoinColumn(name = "to_location_id", nullable = true)
    private Location to;

    @NotNull(message = "Distance cannot be null")
    @DecimalMin(value = "1.0", inclusive = false, message = "Distance must be greater than 1")
    private Double distance;

    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDate.now();
    }
}