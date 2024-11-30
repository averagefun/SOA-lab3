package se.ifmo;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import se.ifmo.controllers.RoutesResource;

@ApplicationPath("/api")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(RoutesResource.class);
        return resources;
    }

    public ApplicationConfig() {
        registerServiceInConsul();
    }

    private void registerServiceInConsul() {
        try {
            String consulUrl = "http://localhost:8500/v1/agent/service/register";
            String serviceDefinition = """
                    {
                        "ID": "web-app",
                        "Name": "web-app",
                        "Address": "localhost",
                        "Port": 8282,
                        "Check": {
                            "HTTP": "http://localhost:8282/web-app-1.0-SNAPSHOT/api/routes",
                            "Interval": "10s"
                        }
                    }
                    """;

            URL url = new URL(consulUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(serviceDefinition.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Service registered successfully in Consul.");
            } else {
                System.err.println("Failed to register service in Consul. Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
