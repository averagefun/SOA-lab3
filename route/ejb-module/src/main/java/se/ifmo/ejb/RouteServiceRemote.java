package se.ifmo.ejb;

import se.ifmo.model.Route;
import se.ifmo.model.exception.RouteNotFoundException;

import java.util.List;

public interface RouteServiceRemote {
    Route addRoute(Route route);

    Route updateRoute(long id, Route updatedRoute) throws RouteNotFoundException;

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
    Route getRouteById(long id) throws RouteNotFoundException;
    void deleteRoute(long id) throws RouteNotFoundException;

    Route getRouteWithMaxFrom() throws RouteNotFoundException;

    Long getCountOfRoutesWithDistanceLowerThan(double value);


}
