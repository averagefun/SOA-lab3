package de.gre90r.jaxwsserver.controllers;

import de.gre90r.jaxwsserver.model.Coordinates;
import de.gre90r.jaxwsserver.model.Location;
import de.gre90r.jaxwsserver.model.Route;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;


import java.time.LocalDate;
import java.util.*;


@Path("/routes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoutesResource {

    private static Map<Long, Route> routeStore = new HashMap<>();
    private static long idCounter = 1;

    static {
        // Инициализация с некоторыми данными
        Route route = new Route();
        route.setId(idCounter++);
        route.setName("Sample Route");
        route.setDistance(100.0);
        Coordinates coords = new Coordinates();
        coords.setX(0);
        coords.setY(0L);
        route.setCoordinates(coords);
        route.setCreationDate(LocalDate.now());
        route.setFrom(null); // Можно установить значение или оставить null
        route.setTo(null);
        routeStore.put(route.getId(), route);
    }

    @GET
    public Response getAllRoutes(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sort") String sort,
            @QueryParam("filter") String filter,
            @QueryParam("name") String nameFilter) {

        List<Route> routes = new ArrayList<>(routeStore.values());

        // Фильтрация по имени, если задано
        if (nameFilter != null && !nameFilter.isEmpty()) {
            routes.removeIf(route -> !route.getName().contains(nameFilter));
        }

        // Реализация сортировки, если необходимо

        // Реализация пагинации
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, routes.size());
        if (fromIndex > routes.size()) {
            routes = Collections.emptyList();
        } else {
            routes = routes.subList(fromIndex, toIndex);
        }

        GenericEntity<List<Route>> entity = new GenericEntity<>(routes) {};
        return Response.ok(entity).build();
    }

    @POST
    public Response addRoute(@Valid Route route, @Context UriInfo uriInfo) {
        // Автоматическая генерация id и creationDate
        route.setId(idCounter++);
        route.setCreationDate(LocalDate.now());

        routeStore.put(route.getId(), route);

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Long.toString(route.getId()));
        return Response.created(builder.build()).entity(route).build();
    }

    @Path("/{id}")
    @GET
    public Response getRouteById(@PathParam("id") long id) {
        Route route = routeStore.get(id);
        if (route == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Route not found").build();
        }
        return Response.ok(route).build();
    }

    @Path("/{id}")
    @PUT
    public Response updateRoute(@PathParam("id") long id, @Valid Route updatedRoute) {
        Route existingRoute = routeStore.get(id);
        if (existingRoute == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Route not found").build();
        }

        // Обновляем только изменяемые поля
        updatedRoute.setId(id);
        updatedRoute.setCreationDate(existingRoute.getCreationDate()); // creationDate не меняется

        routeStore.put(id, updatedRoute);

        return Response.ok(updatedRoute).build();
    }

    @Path("/{id}")
    @DELETE
    public Response deleteRoute(@PathParam("id") long id) {
        Route removedRoute = routeStore.remove(id);
        if (removedRoute == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Route not found").build();
        }
        return Response.noContent().build();
    }

    @Path("/name/{value}")
    @GET
    public Response getRoutesByNameSubstring(@PathParam("value") String value) {
        List<Route> matchingRoutes = new ArrayList<>();
        for (Route route : routeStore.values()) {
            if (route.getName().contains(value)) {
                matchingRoutes.add(route);
            }
        }

        if (matchingRoutes.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No routes found").build();
        }

        GenericEntity<List<Route>> entity = new GenericEntity<>(matchingRoutes) {};
        return Response.ok(entity).build();
    }

    @Path("/from/max")
    @GET
    public Response getRouteWithMaxFrom() {
        // Предполагая, что 'from' можно сравнивать, реализуем логику сравнения
        Route maxRoute = null;
        for (Route route : routeStore.values()) {
            if (route.getFrom() != null) {
                if (maxRoute == null || compareLocations(route.getFrom(), maxRoute.getFrom()) > 0) {
                    maxRoute = route;
                }
            }
        }

        if (maxRoute == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No routes found").build();
        }

        return Response.ok(maxRoute).build();
    }

    @Path("/distance/lower/{value}/count")
    @GET
    public Response getCountOfRoutesWithDistanceLowerThan(@PathParam("value") double value) {
        long count = routeStore.values().stream()
                .filter(route -> route.getDistance() < value)
                .count();

        return Response.ok("{\"count\":" + count + "}").build();
    }

    // Вспомогательный метод для сравнения двух объектов Location
    private int compareLocations(Location loc1, Location loc2) {
        if (loc1 == null) return -1;
        if (loc2 == null) return 1;

        // Реализуем логику сравнения на основе ваших критериев
        // Например, сравнение по сумме x, y, z
        double sum1 = loc1.getX() + loc1.getY() + loc1.getZ();
        double sum2 = loc2.getX() + loc2.getY() + loc2.getZ();
        return Double.compare(sum1, sum2);
    }

    // Обработка исключений валидации
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
}