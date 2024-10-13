package de.gre90r.jaxwsserver;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class ApplicationConfig extends Application {
    // Классы ресурсов будут автоматически обнаружены
}