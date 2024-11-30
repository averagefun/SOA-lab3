package se.ifmo.configuration;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewService;

public class ConsulRegistration {

    public static void registerService() {
        // Подключение к Consul
        ConsulClient consulClient = new ConsulClient("localhost");

        // Конфигурация сервиса
        NewService newService = new NewService();
        newService.setId("route-web-app"); // Уникальный ID
        newService.setName("route-service"); // Имя сервиса
        newService.setAddress("127.0.0.1"); // IP-адрес сервиса
        newService.setPort(8080); // Порт, на котором работает веб-приложение

        // Добавляем health-check
        NewService.Check serviceCheck = new NewService.Check();
        serviceCheck.setHttp("http://127.0.0.1:8080/health");
        serviceCheck.setInterval("10s"); // Интервал проверок
        serviceCheck.setTimeout("5s"); // Таймаут проверки
        newService.setCheck(serviceCheck);

        // Регистрация сервиса
        consulClient.agentServiceRegister(newService);

        System.out.println("Сервис зарегистрирован в Consul.");
    }
}
