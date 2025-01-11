//package se.ifmo.controllers;
//
//import jakarta.ejb.EJB;
//import jakarta.validation.Valid;
//import jakarta.ws.rs.Consumes;
//import jakarta.ws.rs.DELETE;
//import jakarta.ws.rs.DefaultValue;
//import jakarta.ws.rs.GET;
//import jakarta.ws.rs.POST;
//import jakarta.ws.rs.PUT;
//import jakarta.ws.rs.Path;
//import jakarta.ws.rs.PathParam;
//import jakarta.ws.rs.Produces;
//import jakarta.ws.rs.QueryParam;
//import jakarta.ws.rs.core.Context;
//import jakarta.ws.rs.core.GenericEntity;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.core.Response;
//import jakarta.ws.rs.core.UriBuilder;
//import jakarta.ws.rs.core.UriInfo;
//import se.ifmo.ejb.RouteServiceRemote;
//import se.ifmo.model.Route;
//import se.ifmo.model.exception.RouteNotFoundException;
//
//import java.util.List;
//
//@Path("/routes")
//@Produces(MediaType.APPLICATION_JSON)
//@Consumes(MediaType.APPLICATION_JSON)
//public class RoutesResource {
//    @EJB
//    private RouteServiceRemote routeService;
//
//    @GET
//    public Response getAllRoutes(@QueryParam("page") @DefaultValue("1") int page, @QueryParam("size") @DefaultValue("10") int size, @QueryParam("sort") List<String> sortParams, @QueryParam("name") String nameFilter, @QueryParam("fromLocationId") Long fromLocationId, @QueryParam("toLocationId") Long toLocationId, @QueryParam("minDistance") Double minDistance, @QueryParam("maxDistance") Double maxDistance, @QueryParam("coordinatesX") Integer coordinatesX, @QueryParam("coordinatesY") Long coordinatesY, @QueryParam("fromX") Integer fromX, @QueryParam("fromY") Long fromY, @QueryParam("fromZ") Integer fromZ, @QueryParam("fromName") String fromName, @QueryParam("toX") Integer toX, @QueryParam("toY") Long toY, @QueryParam("toZ") Integer toZ, @QueryParam("toName") String toName) {
//        List<Route> routes = routeService.getAllRoutes(page, size, sortParams, nameFilter, fromLocationId, toLocationId, minDistance, maxDistance, coordinatesX, coordinatesY, fromX, fromY, fromZ, fromName, toX, toY, toZ, toName);
//
//        GenericEntity<List<Route>> entity = new GenericEntity<>(routes) {
//        };
//        return Response.ok(entity).build();
//    }
//
//    /**
//     * Добавление нового маршрута.
//     *
//     * @param route   маршрут для добавления
//     * @param uriInfo информация о URI
//     * @return ответ с созданным маршрутом
//     */
//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response addRoute(@Valid Route route, @Context UriInfo uriInfo) {
//        Route createdRoute = routeService.addRoute(route);
//
//        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
//        builder.path(Long.toString(createdRoute.getId()));
//        return Response.created(builder.build()).entity(createdRoute).build();
//    }
//
//    // Получить маршрут по ID
//    @GET
//    @Path("/{id}")
//    public Response getRouteById(@PathParam("id") long id) {
//        try {
//            Route route = routeService.getRouteById(id);
//            return Response.ok(route).build();
//        } catch (RouteNotFoundException e) {
//            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
//        }
//    }
//
//    /**
//     * Обновление существующего маршрута.
//     *
//     * @param id           идентификатор маршрута для обновления
//     * @param updatedRoute обновленные данные маршрута
//     * @return ответ с обновленным маршрутом или ошибкой
//     */
//    @PUT
//    @Path("/{id}")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response updateRoute(@PathParam("id") long id, @Valid Route updatedRoute) {
//        try {
//            Route route = routeService.updateRoute(id, updatedRoute);
//            return Response.ok(route).build();
//        } catch (RouteNotFoundException ex) {
//            return Response.status(Response.Status.NOT_FOUND).entity(ex.getMessage()).build();
//        }
//    }
//
//    // Удалить маршрут
//    @DELETE
//    @Path("/{id}")
//    public Response deleteRoute(@PathParam("id") long id) {
//        try {
//            routeService.deleteRoute(id);
//            return Response.noContent().build();
//        } catch (RouteNotFoundException e) {
//            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
//        }
//    }
//
//    // Получить маршрут с максимальным значением 'from'
//    @Path("/from/max")
//    @GET
//    public Response getRouteWithMaxFrom() {
//        return Response.ok(routeService.getRouteWithMaxFrom()).build();
//    }
//
//    // Получить количество маршрутов с расстоянием меньше заданного
//    @Path("/distance/lower/{value}/count")
//    @GET
//    public Response getCountOfRoutesWithDistanceLowerThan(@PathParam("value") double value) {
//        return Response.ok("{\"count\":" + routeService.getCountOfRoutesWithDistanceLowerThan(value) + "}").build();
//    }
//}
