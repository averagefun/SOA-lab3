package de.gre90r.jaxwsserver.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "locations", uniqueConstraints = @UniqueConstraint(columnNames = {"x", "y", "z", "name"}))
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "X cannot be null")
    private Integer x;

    @NotNull(message = "Y cannot be null")
    private Long y;

    @NotNull(message = "Z cannot be null")
    private int z;

    @NotNull(message = "Name cannot be null")
    @Size(min = 1, message = "Name cannot be empty")
    private String name;
}