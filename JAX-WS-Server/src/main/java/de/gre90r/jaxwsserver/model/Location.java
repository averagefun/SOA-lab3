package de.gre90r.jaxwsserver.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Entity
@Table(name = "locations")
@Data
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "X cannot be null")
    private Integer x;

    private long y;

    private int z;

    @Size(min = 1, message = "Name cannot be empty")
    private String name;
}