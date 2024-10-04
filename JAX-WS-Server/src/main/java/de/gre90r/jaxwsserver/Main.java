package de.gre90r.jaxwsserver;

import de.gre90r.jaxwsserver.server.CORSFilter;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import java.io.IOException;
import java.net.URI;

public class Main {
    public static final String BASE_URI = "https://localhost:8443/api/";
    final ResourceConfig rc = new ResourceConfig()
            .packages("de.gre90r.jaxwsserver")
            .register(JacksonFeature.class)
            .register(CORSFilter.class);


    public static HttpServer startServer() {
        // Создаем конфигурацию ресурсов
        final ResourceConfig rc = new ResourceConfig()
                .packages("de.gre90r.jaxwsserver")
                .register(JacksonFeature.class);

        // Настраиваем SSLContext
        SSLContextConfigurator sslContext = new SSLContextConfigurator();
        sslContext.setKeyStoreFile("keystore.jks"); // путь к вашему хранилищу ключей
        sslContext.setKeyStorePass("password"); // пароль для хранилища ключей

        // Создаем и запускаем Grizzly HTTP сервер с SSL
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
                URI.create(BASE_URI),
                rc,
                true,
                new SSLEngineConfigurator(sslContext).setClientMode(false).setNeedClientAuth(false)
        );

        // Отключаем HTTP, оставляя только HTTPS
        for (NetworkListener listener : server.getListeners()) {
            if (!"grizzly".equals(listener.getName())) {
                server.removeListener(listener.getName());
            }
        }

        return server;
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println(String.format("Приложение запущено по адресу %s", BASE_URI));
        System.out.println("Нажмите Enter для остановки...");
        System.in.read();
        server.shutdownNow();
    }
}
