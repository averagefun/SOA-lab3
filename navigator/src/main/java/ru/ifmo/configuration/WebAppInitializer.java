package ru.ifmo.configuration;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.servlet.DispatcherServlet;

public class WebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // Создаем Контекст Spring
        org.springframework.web.context.support.AnnotationConfigWebApplicationContext context =
                new org.springframework.web.context.support.AnnotationConfigWebApplicationContext();
        context.register(WebConfig.class);

        // Создаем DispatcherServlet
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);

        // Регистрируем DispatcherServlet
        ServletRegistration.Dynamic registration = servletContext.addServlet("dispatcher", dispatcherServlet);
        registration.setLoadOnStartup(1);
        registration.addMapping("/");
    }
}