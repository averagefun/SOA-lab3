package de.gre90r.jaxwsserver.server;

import de.gre90r.jaxwsserver.models.Coordinates;
import de.gre90r.jaxwsserver.models.Location;
import de.gre90r.jaxwsserver.models.Route;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.time.LocalDateTime;

@Path("/routes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoutesResource {

    // In-memory data store for demonstration purposes
    private static Map<Integer, Route> routeStore = new HashMap<>();
    private static int idCounter = 1;

    static {
        // Initialize with some data
        Route route = new Route();
        route.setId(idCounter++);
        route.setName("Sample Route");
        route.setDistance(100);
        route.setCoordinates(new Coordinates());
        route.setFrom(new Location());
        route.setTo(new Location());
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

        // Filter by name if provided
        if (nameFilter != null && !nameFilter.isEmpty()) {
            routes.removeIf(route -> !route.getName().contains(nameFilter));
        }

        // Implement sorting if needed
        // Implement pagination
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, routes.size());
        if (fromIndex > routes.size()) {
            routes = Collections.emptyList();
        } else {
            routes = routes.subList(fromIndex, toIndex);
        }

        GenericEntity<List<Route>> entity = new GenericEntity<List<Route>>(routes) {};
        return Response.ok(entity).build();
    }

    @POST
    public Response addRoute(Route route, @Context UriInfo uriInfo) {
        // Validate route
        if (route.getName() == null || route.getName().isEmpty() || route.getDistance() < 2) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid route data").build();
        }

        route.setId(idCounter++);
        routeStore.put(route.getId(), route);

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Integer.toString(route.getId()));
        return Response.created(builder.build()).entity(route).build();
    }

    @Path("/{id}")
    @GET
    public Response getRouteById(@PathParam("id") int id) {
        Route route = routeStore.get(id);
        if (route == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Route not found").build();
        }
        return Response.ok(route).build();
    }

    @Path("/{id}")
    @PUT
    public Response updateRoute(@PathParam("id") int id, Route updatedRoute) {
        Route existingRoute = routeStore.get(id);
        if (existingRoute == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Route not found").build();
        }

        // Validate updatedRoute
        if (updatedRoute.getName() == null || updatedRoute.getName().isEmpty() || updatedRoute.getDistance() < 2) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid route data").build();
        }

        updatedRoute.setId(id);
        routeStore.put(id, updatedRoute);

        return Response.ok(updatedRoute).build();
    }

    @Path("/{id}")
    @DELETE
    public Response deleteRoute(@PathParam("id") int id) {
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

        GenericEntity<List<Route>> entity = new GenericEntity<List<Route>>(matchingRoutes) {};
        return Response.ok(entity).build();
    }

    @Path("/from/max")
    @GET
    public Response getRouteWithMaxFrom() {
        // Assuming 'from' can be compared, implement comparison logic
        Route maxRoute = null;
        for (Route route : routeStore.values()) {
            if (maxRoute == null || compareLocations(route.getFrom(), maxRoute.getFrom()) > 0) {
                maxRoute = route;
            }
        }

        if (maxRoute == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No routes found").build();
        }

        return Response.ok(maxRoute).build();
    }

    @Path("/distance/lower/{value}/count")
    @GET
    public Response getCountOfRoutesWithDistanceLowerThan(@PathParam("value") int value) {
        long count = routeStore.values().stream()
                .filter(route -> route.getDistance() < value)
                .count();

        return Response.ok("<count>" + count + "</count>").build();
    }

    // Helper method to compare two Location objects
    private int compareLocations(Location loc1, Location loc2) {
        // Implement comparison logic based on your criteria
        // For example, compare by sum of x, y, z
        double sum1 = loc1.getX() + loc1.getY() + loc1.getZ();
        double sum2 = loc2.getX() + loc2.getY() + loc2.getZ();
        return Double.compare(sum1, sum2);
    }
}