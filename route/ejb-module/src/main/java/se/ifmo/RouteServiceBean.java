package se.ifmo;

import java.util.ArrayList;
import java.util.List;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import se.ifmo.ejb.RouteServiceRemote;
import se.ifmo.model.Location;
import se.ifmo.model.Route;
import se.ifmo.model.exception.RouteNotFoundException;

@Stateless
public class RouteServiceBean implements RouteServiceRemote {

    @PersistenceContext(unitName = "RoutesPU")
    private EntityManager em;

    /**
     * Добавление нового маршрута. Проверяет наличие локаций и использует существующие или создает новые.
     *
     * @param route маршрут для добавления
     * @return созданный маршрут
     */
    @Override
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
    @Override
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

    @Override
    public List<Route> getAllRoutes(int page, int size, List<String> sortParams, String nameFilter, Long fromLocationId, Long toLocationId, Double minDistance, Double maxDistance, Integer coordinatesX, Long coordinatesY, Integer fromX, Long fromY, Integer fromZ, String fromName, Integer toX, Long toY, Integer toZ, String toName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Route> cq = cb.createQuery(Route.class);
        Root<Route> root = cq.from(Route.class);

        List<Predicate> predicates = new ArrayList<>();

        // Filter by name
        if (nameFilter != null && !nameFilter.isEmpty()) {
            predicates.add(cb.like(root.get("name"), "%" + nameFilter + "%"));
        }

        // Filter by fromLocationId
        if (fromLocationId != null) {
            predicates.add(cb.equal(root.get("from").get("id"), fromLocationId));
        }

        // Filter by toLocationId
        if (toLocationId != null) {
            predicates.add(cb.equal(root.get("to").get("id"), toLocationId));
        }

        // Filter by minimum distance
        if (minDistance != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("distance"), minDistance));
        }

        // Filter by maximum distance
        if (maxDistance != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("distance"), maxDistance));
        }

        // Filter by Coordinates.x
        if (coordinatesX != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("coordinates").get("x"), coordinatesX));
        }

        // Filter by Coordinates.y
        if (coordinatesY != null) {
            predicates.add(cb.equal(root.get("coordinates").get("y"), coordinatesY));
        }

        // Filter by Location from (x, y, z, name)
        if (fromX != null) {
            predicates.add(cb.equal(root.get("from").get("x"), fromX));
        }
        if (fromY != null) {
            predicates.add(cb.equal(root.get("from").get("y"), fromY));
        }
        if (fromZ != null) {
            predicates.add(cb.equal(root.get("from").get("z"), fromZ));
        }
        if (fromName != null && !fromName.isEmpty()) {
            predicates.add(cb.like(root.get("from").get("name"), "%" + fromName + "%"));
        }

        // Filter by Location to (x, y, z, name)
        if (toX != null) {
            predicates.add(cb.equal(root.get("to").get("x"), toX));
        }
        if (toY != null) {
            predicates.add(cb.equal(root.get("to").get("y"), toY));
        }
        if (toZ != null) {
            predicates.add(cb.equal(root.get("to").get("z"), toZ));
        }
        if (toName != null && !toName.isEmpty()) {
            predicates.add(cb.like(root.get("to").get("name"), "%" + toName + "%"));
        }

        // Apply filters
        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        List<Order> orders = new ArrayList<>();
        if (sortParams != null && !sortParams.isEmpty()) {
            for (String sortParam : sortParams) {
                String[] parts = sortParam.split(",");
                String field = parts[0];
                String sortOrder = parts.length > 1 ? parts[1] : "asc"; // По умолчанию asc

                jakarta.persistence.criteria.Path<?> path = root;
                if (field.contains(".")) {
                    String[] fieldParts = field.split("\\.");
                    for (String part : fieldParts) {
                        path = path.get(part);
                    }
                } else {
                    path = root.get(field);
                }

                if ("desc".equalsIgnoreCase(sortOrder)) {
                    orders.add(cb.desc(path));
                } else {
                    orders.add(cb.asc(path));
                }
            }
            cq.orderBy(orders);
        } else {
            // Сортировка по умолчанию
            cq.orderBy(cb.asc(root.get("id")));
        }

        TypedQuery<Route> query = em.createQuery(cq);

        // Pagination
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    @Override
    public Route getRouteById(long id) throws RouteNotFoundException {
        Route route = em.find(Route.class, id);
        if (route == null) {
            throw new RouteNotFoundException("Route not found");
        }
        return route;
    }

    @Override
    public void deleteRoute(long id) throws RouteNotFoundException {
        Route route = em.find(Route.class, id);
        if (route == null) {
            throw new RouteNotFoundException("Route not found");
        }
        em.remove(route);
    }

    @Override
    public Route getRouteWithMaxFrom() throws RouteNotFoundException {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Route> cq = cb.createQuery(Route.class);
        Root<Route> root = cq.from(Route.class);

        // Проверяем, что поле 'from' не null
        cq.where(cb.isNotNull(root.get("from")));

        // Вычисляем сумму x + y + z для 'from'
        Expression<Integer> sumExpr = cb.sum(cb.sum(cb.coalesce(root.get("from").get("x"), 0), cb.coalesce(root.get("from").get("y"), 0)), cb.coalesce(root.get("from").get("z"), 0));

        // Сортируем по сумме в порядке убывания
        cq.orderBy(cb.desc(sumExpr));

        TypedQuery<Route> query = em.createQuery(cq);
        query.setMaxResults(1);

        List<Route> result = query.getResultList();
        if (result.isEmpty()) {
            throw new RouteNotFoundException("Route not found");
        }
        return result.get(0);
    }

    @Override
    public Long getCountOfRoutesWithDistanceLowerThan(double value) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Route> root = cq.from(Route.class);

        cq.select(cb.count(root));
        cq.where(cb.lessThan(root.get("distance"), value));

        return em.createQuery(cq).getSingleResult();
    }

    /**
     * Поиск существующей локации по параметрам или создание новой.
     *
     * @param location локация для поиска или создания
     * @return существующая или новая локация
     */
    private Location findOrCreateLocation(Location location) {
        try {
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
        catch (Exception e){
            return null;
        }
    }
}