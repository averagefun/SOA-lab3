package de.gre90r.jaxwsserver.controllers;

import de.gre90r.jaxwsserver.exception.RouteNotFoundException;
import de.gre90r.jaxwsserver.model.Route;
import de.gre90r.jaxwsserver.service.RouteService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.List;


@Path("/routes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class RoutesResource {

    @PersistenceContext(unitName = "RoutesPU")
    private EntityManager em;

    @Inject
    private RouteService routeService;

    // Обработчик исключений валидации
    @Provider
    public static class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
        @Override
        public Response toResponse(ConstraintViolationException exception) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<?> cv : exception.getConstraintViolations()) {
                sb.append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append("\n");
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(sb.toString()).build();
        }
    }

    @GET
    public Response getAllRoutes(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sort") List<String> sortParams,
            @QueryParam("name") String nameFilter,
            @QueryParam("fromLocationId") Long fromLocationId,
            @QueryParam("toLocationId") Long toLocationId,
            @QueryParam("minDistance") Double minDistance,
            @QueryParam("maxDistance") Double maxDistance,
            @QueryParam("coordinatesX") Integer coordinatesX,
            @QueryParam("coordinatesY") Long coordinatesY,
            @QueryParam("fromX") Integer fromX,
            @QueryParam("fromY") Long fromY,
            @QueryParam("fromZ") Integer fromZ,
            @QueryParam("fromName") String fromName,
            @QueryParam("toX") Integer toX,
            @QueryParam("toY") Long toY,
            @QueryParam("toZ") Integer toZ,
            @QueryParam("toName") String toName
    ) {
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

        List<Route> routes = query.getResultList();

        GenericEntity<List<Route>> entity = new GenericEntity<>(routes) {};
        return Response.ok(entity).build();
    }



    /**
     * Добавление нового маршрута.
     *
     * @param route маршрут для добавления
     * @param uriInfo информация о URI
     * @return ответ с созданным маршрутом
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addRoute(@Valid Route route, @Context UriInfo uriInfo) {
        Route createdRoute = routeService.addRoute(route);

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Long.toString(createdRoute.getId()));
        return Response.created(builder.build()).entity(createdRoute).build();
    }

    // Получить маршрут по ID
    @Path("/{id}")
    @GET
    public Response getRouteById(@PathParam("id") long id) {
        Route route = em.find(Route.class, id);
        if (route == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Route not found").build();
        }
        return Response.ok(route).build();
    }

    /**
     * Обновление существующего маршрута.
     *
     * @param id          идентификатор маршрута для обновления
     * @param updatedRoute обновленные данные маршрута
     * @return ответ с обновленным маршрутом или ошибкой
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRoute(@PathParam("id") long id, @Valid Route updatedRoute) {
        try {
            Route route = routeService.updateRoute(id, updatedRoute);
            return Response.ok(route).build();
        } catch (RouteNotFoundException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getMessage()).build();
        }
    }

    // Удалить маршрут
    @Path("/{id}")
    @DELETE
    @Transactional
    public Response deleteRoute(@PathParam("id") long id) {
        Route route = em.find(Route.class, id);
        if (route == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Route not found").build();
        }
        em.remove(route);
        return Response.noContent().build();
    }

    // Получить маршрут с максимальным значением 'from'
    @Path("/from/max")
    @GET
    public Response getRouteWithMaxFrom() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Route> cq = cb.createQuery(Route.class);
        Root<Route> root = cq.from(Route.class);

        // Проверяем, что поле 'from' не null
        cq.where(cb.isNotNull(root.get("from")));

        // Вычисляем сумму x + y + z для 'from'
        Expression<Integer> sumExpr = cb.sum(
                cb.sum(
                        cb.coalesce(root.get("from").get("x"), 0),
                        cb.coalesce(root.get("from").get("y"), 0)
                ),
                cb.coalesce(root.get("from").get("z"), 0)
        );

        // Сортируем по сумме в порядке убывания
        cq.orderBy(cb.desc(sumExpr));

        TypedQuery<Route> query = em.createQuery(cq);
        query.setMaxResults(1);

        List<Route> result = query.getResultList();
        if (result.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No routes found").build();
        }
        return Response.ok(result.get(0)).build();
    }

    // Получить количество маршрутов с расстоянием меньше заданного
    @Path("/distance/lower/{value}/count")
    @GET
    public Response getCountOfRoutesWithDistanceLowerThan(@PathParam("value") double value) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Route> root = cq.from(Route.class);

        cq.select(cb.count(root));
        cq.where(cb.lessThan(root.get("distance"), value));

        Long count = em.createQuery(cq).getSingleResult();

        return Response.ok("{\"count\":" + count + "}").build();
    }
}