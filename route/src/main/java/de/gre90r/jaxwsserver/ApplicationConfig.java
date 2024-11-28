package de.gre90r.jaxwsserver;

import de.gre90r.jaxwsserver.configuration.CorsFilter;
import de.gre90r.jaxwsserver.controllers.RoutesResource;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class ApplicationConfig extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(RoutesResource.class);
        resources.add(RoutesResource.ValidationExceptionMapper.class);
        resources.add(CorsFilter.class);
        return resources;
    }
}
