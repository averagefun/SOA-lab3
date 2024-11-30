package se.ifmo.ejb;

import java.util.List;

import se.ifmo.model.Route;

public interface RouteServiceRemote {
    Route addRoute(Route route);

    Route updateRoute(long id, Route updatedRoute);
    List<Route> getAllRoutes(
            int page,
            int size,
            List<String> sortParams,
            String nameFilter,
            Long fromLocationId,
            Long toLocationId,
            Double minDistance,
            Double maxDistance,
            Integer coordinatesX,
            Long coordinatesY,
            Integer fromX,
            Long fromY,
            Integer fromZ,
            String fromName,
            Integer toX,
            Long toY,
            Integer toZ,
            String toName
    );

    Route getRouteById(long id);

    void deleteRoute(long id);

    Route getRouteWithMaxFrom();
    Long getCountOfRoutesWithDistanceLowerThan(double value);
}
