// de/gre90r/jaxwsserver/Main.java
package de.gre90r.jaxwsserver;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import java.io.IOException;
import java.net.URI;

public class Main {
    public static final String BASE_URI = "http://localhost:8080/api/";

    public static HttpServer startServer() {
        // Create a ResourceConfig that scans for JAX-RS resources in your packages
        final ResourceConfig rc = new ResourceConfig()
                .packages("de.gre90r.jaxwsserver")
                .register(JacksonFeature.class);

        // Create and start a new instance of Grizzly HTTP server
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println(String.format("Application started at %s", BASE_URI));
        System.out.println("Hit enter to stop...");
        System.in.read();
        server.shutdownNow();
    }
}
