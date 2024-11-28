package se.ifmo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "locations", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "x", "y", "z"}))
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "X cannot be null")
    private Integer x;

    private long y = 0;

    private int z = 0;

    @Size(min = 1, message = "Name cannot be empty")
    private String name;
}