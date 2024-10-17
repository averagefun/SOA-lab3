package ru.ifmo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.ifmo.models.Route;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/navigator")
public class NavigatorController {

    private static final Logger logger = LoggerFactory.getLogger(NavigatorController.class);
    private static final String FIRST_SERVICE_BASE_URL = "http://localhost:8080/route-1.0-SNAPSHOT/api/routes";

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
            // Получаем все маршруты
            ResponseEntity<Route[]> response = restTemplate.getForEntity(FIRST_SERVICE_BASE_URL, Route[].class);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                logger.error("Error fetching routes from the first service. Status: {}", response.getStatusCode());
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Error fetching routes from the first service.");
            }

            List<Route> routes = Arrays.asList(response.getBody());

            // Фильтруем маршруты по from и to
            List<Route> filteredRoutes = routes.stream()
                    .filter(route -> route.getFrom() != null && route.getTo() != null)
                    .filter(route -> route.getFrom().getId().equals(idFrom) && route.getTo().getId().equals(idTo))
                    .collect(Collectors.toList());

            if (filteredRoutes.isEmpty()) {
                logger.warn("No routes found between from ID {} and to ID {}", idFrom, idTo);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No routes found between the specified locations.");
            }

            // Выбираем маршрут по параметру shortest
            Route selectedRoute;
            if (shortest) {
                selectedRoute = filteredRoutes.stream()
                        .min(Comparator.comparingDouble(Route::getDistance))
                        .orElse(null);
            } else {
                selectedRoute = filteredRoutes.stream()
                        .max(Comparator.comparingDouble(Route::getDistance))
                        .orElse(null);
            }

            if (selectedRoute == null) {
                logger.warn("No suitable route found after filtering.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No suitable route found.");
            }

            logger.info("Route found: {}", selectedRoute);
            return ResponseEntity.ok(selectedRoute);

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
            // Получаем все маршруты
            ResponseEntity<Route[]> response = restTemplate.getForEntity(FIRST_SERVICE_BASE_URL, Route[].class);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                logger.error("Error fetching routes from the first service. Status: {}", response.getStatusCode());
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Error fetching routes from the first service.");
            }

            List<Route> routes = Arrays.asList(response.getBody());

            // Фильтруем маршруты по from и to
            List<Route> filteredRoutes = routes.stream()
                    .filter(route -> route.getFrom() != null && route.getTo() != null)
                    .filter(route -> route.getFrom().getId().equals(idFrom) && route.getTo().getId().equals(idTo))
                    .collect(Collectors.toList());

            if (filteredRoutes.isEmpty()) {
                logger.warn("No routes found between from ID {} and to ID {}", idFrom, idTo);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No routes found between the specified locations.");
            }

            // Сортируем маршруты по параметру orderBy
            Comparator<Route> comparator;
            switch (orderBy.toLowerCase()) {
                case "name":
                    comparator = Comparator.comparing(Route::getName, Comparator.nullsLast(String::compareTo));
                    break;
                case "distance":
                    comparator = Comparator.comparing(Route::getDistance, Comparator.nullsLast(Double::compareTo));
                    break;
                default:
                    logger.error("Invalid order-by parameter: {}", orderBy);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid order-by parameter.");
            }

            List<Route> sortedRoutes = filteredRoutes.stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());

            logger.info("Found {} routes after sorting", sortedRoutes.size());
            return ResponseEntity.ok(sortedRoutes);

        } catch (Exception e) {
            logger.error("Exception occurred while processing request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }
    }
}
