package ru.ifmo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.ifmo.models.Route;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/navigator")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class NavigatorController {

    private static final Logger logger = LoggerFactory.getLogger(NavigatorController.class);
    private static final String FIRST_SERVICE_BASE_URL = "https://localhost:8283/route/routes";

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Найти маршрут между локациями.
     *
     * @param idFrom   Идентификатор начальной локации
     * @param idTo     Идентификатор конечной локации
     * @param shortest Если true — найти кратчайший маршрут, если false — самый длинный
     * @return Найденный маршрут
     */
    @GetMapping(value = "/route/{idFrom}/{idTo}/{shortest}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> findRoute(
            @PathVariable("idFrom") Long idFrom,
            @PathVariable("idTo") Long idTo,
            @PathVariable("shortest") Boolean shortest) {

        logger.info("Received request to find route from {} to {} with shortest={}", idFrom, idTo, shortest);

        try {
            // Построение URI с параметрами фильтрации
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(FIRST_SERVICE_BASE_URL)
                    .queryParam("fromLocationId", idFrom)
                    .queryParam("toLocationId", idTo)
                    .queryParam("page", 1)
                    .queryParam("size", 100);

            URI uri = builder.build().encode().toUri();

            // Получаем отфильтрованные маршруты
            ResponseEntity<Route[]> response = restTemplate.getForEntity(uri, Route[].class);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                logger.error("Error fetching routes from the first service. Status: {}", response.getStatusCode());
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Error fetching routes from the first service.");
            }

            List<Route> routes = List.of(response.getBody());
            logger.info("Retrieved {} routes after filtering", routes.size());

            if (routes.isEmpty()) {
                logger.warn("No routes found between from ID {} and to ID {}", idFrom, idTo);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No routes found between the specified locations.");
            }

            // Выбор маршрута по параметру shortest
            Optional<Route> selectedRoute;
            if (shortest) {
                selectedRoute = routes.stream()
                        .min(Comparator.comparingDouble(Route::getDistance));
            } else {
                selectedRoute = routes.stream()
                        .max(Comparator.comparingDouble(Route::getDistance));
            }

            if (selectedRoute.isEmpty()) {
                logger.warn("No suitable route found after filtering.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No suitable route found.");
            }

            logger.info("Route found: {}", selectedRoute.get());
            return ResponseEntity.ok(selectedRoute.get());

        } catch (Exception e) {
            logger.error("Exception occurred while processing request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }
    }

    /**
     * Найти все маршруты между локациями с сортировкой.
     *
     * @param idFrom  Идентификатор начальной локации
     * @param idTo    Идентификатор конечной локации
     * @param orderBy Параметр для сортировки (например, name, distance)
     * @return Список найденных маршрутов
     */
    @GetMapping(value = "/routes/{idFrom}/{idTo}/{orderBy}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> findRoutes(
            @PathVariable("idFrom") Long idFrom,
            @PathVariable("idTo") Long idTo,
            @PathVariable("orderBy") String orderBy) {

        logger.info("Received request to find routes from {} to {} with orderBy={}", idFrom, idTo, orderBy);

        try {
            // Построение URI с параметрами фильтрации и сортировки
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(FIRST_SERVICE_BASE_URL)
                    .queryParam("fromLocationId", idFrom)
                    .queryParam("toLocationId", idTo)
                    .queryParam("sort", orderBy)
                    .queryParam("order", "asc") // По умолчанию по возрастанию
                    .queryParam("page", 1) // Настроить пагинацию по необходимости
                    .queryParam("size", 1000); // Максимум 1000 маршрутов

            URI uri = builder.build().encode().toUri();

            // Получаем отфильтрованные и отсортированные маршруты
            ResponseEntity<Route[]> response = restTemplate.getForEntity(uri, Route[].class);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                logger.error("Error fetching routes from the first service. Status: {}", response.getStatusCode());
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Error fetching routes from the first service.");
            }

            List<Route> routes = List.of(response.getBody());
            logger.info("Retrieved {} routes after filtering and sorting", routes.size());

            if (routes.isEmpty()) {
                logger.warn("No routes found between from ID {} and to ID {}", idFrom, idTo);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No routes found between the specified locations.");
            }

            // Валидация параметра orderBy и корректировка сортировки при необходимости
            switch (orderBy.toLowerCase()) {
                case "name":
                case "distance":
                    // Допустимые поля для сортировки
                    break;
                default:
                    logger.error("Invalid order-by parameter: {}", orderBy);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid order-by parameter.");
            }

            logger.info("Found {} routes after sorting", routes.size());
            return ResponseEntity.ok(routes);

        } catch (Exception e) {
            logger.error("Exception occurred while processing request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }
    }
}
