package de.gre90r.jaxwsserver.controllers;

import de.gre90r.jaxwsserver.model.Route;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.criteria.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jakarta.validation.Valid;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;


@Path("/routes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class RoutesResource {

    @PersistenceContext(unitName = "RoutesPU")
    private EntityManager em;

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

    // Получить все маршруты с фильтрацией, сортировкой и пагинацией
    @GET
    public Response getAllRoutes(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sort") String sort,
            @QueryParam("order") @DefaultValue("asc") String order,
            @QueryParam("name") String nameFilter) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Route> cq = cb.createQuery(Route.class);
        Root<Route> root = cq.from(Route.class);

        List<Predicate> predicates = new ArrayList<>();

        // Фильтрация по имени
        if (nameFilter != null && !nameFilter.isEmpty()) {
            predicates.add(cb.like(root.get("name"), "%" + nameFilter + "%"));
        }

        // Добавьте дополнительные фильтры при необходимости

        // Применение фильтров
        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        TypedQuery<Route> query = em.createQuery(cq);

        // Пагинация
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        List<Route> routes = query.getResultList();

        GenericEntity<List<Route>> entity = new GenericEntity<>(routes) {};
        return Response.ok(entity).build();
    }

    // Добавить новый маршрут
    @POST
    @Transactional
    public Response addRoute(@Valid Route route, @Context UriInfo uriInfo) {
        route.setCreationDate(LocalDate.now());
        em.persist(route);

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Long.toString(route.getId()));
        return Response.created(builder.build()).entity(route).build();
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

    // Обновить маршрут
    @Path("/{id}")
    @PUT
    @Transactional
    public Response updateRoute(@PathParam("id") long id, @Valid Route updatedRoute) {
        Route existingRoute = em.find(Route.class, id);
        if (existingRoute == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Route not found").build();
        }

        // Обновляем поля
        existingRoute.setName(updatedRoute.getName());
        existingRoute.setCoordinates(updatedRoute.getCoordinates());
        existingRoute.setFrom(updatedRoute.getFrom());
        existingRoute.setTo(updatedRoute.getTo());
        existingRoute.setDistance(updatedRoute.getDistance());

        // Поле creationDate и id не изменяем
        em.merge(existingRoute);

        return Response.ok(existingRoute).build();
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