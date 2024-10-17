package ru.ifmo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.List;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "ru.ifmo")
public class WebConfig implements WebMvcConfigurer {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // Добавляем конвертер для JSON
        converters.add(new MappingJackson2HttpMessageConverter());

        // Добавляем конвертер для XML с использованием JAXB
        converters.add(new Jaxb2RootElementHttpMessageConverter());

        // Добавляем конвертер для XML с использованием Jackson
        converters.add(new MappingJackson2XmlHttpMessageConverter());
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .favorParameter(false)
                .favorPathExtension(false)
                .ignoreAcceptHeader(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML);
    }
}