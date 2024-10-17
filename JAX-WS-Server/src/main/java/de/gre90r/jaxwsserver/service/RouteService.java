package de.gre90r.jaxwsserver.service;

import de.gre90r.jaxwsserver.exception.RouteNotFoundException;
import de.gre90r.jaxwsserver.model.Location;
import de.gre90r.jaxwsserver.model.Route;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;

@RequestScoped
public class RouteService {

    @PersistenceContext(unitName = "RoutesPU")
    private EntityManager em;

    /**
     * Добавление нового маршрута. Проверяет наличие локаций и использует существующие или создает новые.
     *
     * @param route маршрут для добавления
     * @return созданный маршрут
     */
    @Transactional
    public Route addRoute(Route route) {
        // Обработка локации "from"
        Location fromLocation = findOrCreateLocation(route.getFrom());
        route.setFrom(fromLocation);

        // Обработка локации "to"
        Location toLocation = findOrCreateLocation(route.getTo());
        route.setTo(toLocation);

        // Поле creationDate устанавливается автоматически через @PrePersist
        em.persist(route);
        return route;
    }

    /**
     * Обновление существующего маршрута. Проверяет наличие локаций и использует существующие или создает новые.
     *
     * @param id           идентификатор маршрута для обновления
     * @param updatedRoute обновленные данные маршрута
     * @return обновленный маршрут
     * @throws RouteNotFoundException если маршрут с указанным ID не найден
     */
    @Transactional
    public Route updateRoute(long id, Route updatedRoute) {
        Route existingRoute = em.find(Route.class, id);
        if (existingRoute == null) {
            throw new RouteNotFoundException("Route not found");
        }

        // Обработка локации "from"
        Location fromLocation = findOrCreateLocation(updatedRoute.getFrom());
        existingRoute.setFrom(fromLocation);

        // Обработка локации "to"
        Location toLocation = findOrCreateLocation(updatedRoute.getTo());
        existingRoute.setTo(toLocation);

        // Обновление остальных полей
        existingRoute.setName(updatedRoute.getName());
        existingRoute.setCoordinates(updatedRoute.getCoordinates());
        existingRoute.setDistance(updatedRoute.getDistance());

        // Поле creationDate и id не изменяются

        em.merge(existingRoute);
        return existingRoute;
    }

    /**
     * Поиск существующей локации по параметрам или создание новой.
     *
     * @param location локация для поиска или создания
     * @return существующая или новая локация
     */
    private Location findOrCreateLocation(Location location) {
        TypedQuery<Location> query = em.createQuery(
                "SELECT l FROM Location l WHERE l.x = :x AND l.y = :y AND l.z = :z AND l.name = :name",
                Location.class);
        query.setParameter("x", location.getX());
        query.setParameter("y", location.getY());
        query.setParameter("z", location.getZ());
        query.setParameter("name", location.getName());

        List<Location> results = query.getResultList();
        if (!results.isEmpty()) {
            return results.get(0);
        } else {
            em.persist(location);
            return location;
        }
    }
}