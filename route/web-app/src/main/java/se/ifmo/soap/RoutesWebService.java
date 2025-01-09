package se.ifmo.soap;

import jakarta.ejb.EJB;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.WebResult;
import se.ifmo.ejb.RouteServiceRemote;
import se.ifmo.exceptions.RouteNotFoundSoapException;
import se.ifmo.model.Route;
import se.ifmo.model.exception.RouteNotFoundException;

import java.util.List;

/**
 * Пример SOAP-сервиса, который заменяет REST-контроллер.
 */
@WebService(
        name = "RoutesWebService",          // Имя веб-сервиса (порт-тип)
        serviceName = "RoutesService"       // Имя самого сервиса
)
public class RoutesWebService {

    @EJB
    private RouteServiceRemote routeService;

    /**
     * Получить все маршруты с использованием фильтров/параметров
     */
    @WebMethod
    @WebResult(name = "routes")
    public List<Route> getAllRoutes(
            @WebParam(name = "page") int page,
            @WebParam(name = "size") int size,
            @WebParam(name = "sortParams") List<String> sortParams,
            @WebParam(name = "nameFilter") String nameFilter,
            @WebParam(name = "fromLocationId") Long fromLocationId,
            @WebParam(name = "toLocationId") Long toLocationId,
            @WebParam(name = "minDistance") Double minDistance,
            @WebParam(name = "maxDistance") Double maxDistance,
            @WebParam(name = "coordinatesX") Integer coordinatesX,
            @WebParam(name = "coordinatesY") Long coordinatesY,
            @WebParam(name = "fromX") Integer fromX,
            @WebParam(name = "fromY") Long fromY,
            @WebParam(name = "fromZ") Integer fromZ,
            @WebParam(name = "fromName") String fromName,
            @WebParam(name = "toX") Integer toX,
            @WebParam(name = "toY") Long toY,
            @WebParam(name = "toZ") Integer toZ,
            @WebParam(name = "toName") String toName
    ) {
        StringBuilder logBuilder = new StringBuilder("Starting getAllRoutes with parameters: \n");
        logBuilder.append("page=").append(page).append("\n")
                .append("size=").append(size).append("\n")
                .append("sortParams=").append(sortParams).append("\n")
                .append("nameFilter=").append(nameFilter).append("\n")
                .append("fromLocationId=").append(fromLocationId).append("\n")
                .append("toLocationId=").append(toLocationId).append("\n")
                .append("minDistance=").append(minDistance).append("\n")
                .append("maxDistance=").append(maxDistance).append("\n")
                .append("coordinatesX=").append(coordinatesX).append("\n")
                .append("coordinatesY=").append(coordinatesY).append("\n")
                .append("fromX=").append(fromX).append("\n")
                .append("fromY=").append(fromY).append("\n")
                .append("fromZ=").append(fromZ).append("\n")
                .append("fromName=").append(fromName).append("\n")
                .append("toX=").append(toX).append("\n")
                .append("toY=").append(toY).append("\n")
                .append("toZ=").append(toZ).append("\n")
                .append("toName=").append(toName).append("\n");

        System.out.println(logBuilder);
        List<Route> routes = routeService.getAllRoutes(
                page,
                size,
                sortParams,
                nameFilter,
                fromLocationId,
                toLocationId,
                minDistance,
                maxDistance,
                coordinatesX,
                coordinatesY,
                fromX,
                fromY,
                fromZ,
                fromName,
                toX,
                toY,
                toZ,
                toName
        );
        System.out.println("Get routes " + routes);
        return routes;
    }

    /**
     * Создать новый маршрут
     */
    @WebMethod
    @WebResult(name = "createdRoute")
    public Route addRoute(
            @WebParam(name = "route") Route route
    ) {
        if (route == null) {
            throw new IllegalArgumentException("Route cannot be null");
        }
        Route addedRoute = routeService.addRoute(route);
        System.out.println("Route added: " + addedRoute);
        return addedRoute;
    }

    /**
     * Получить маршрут по его ID
     */
    @WebMethod
    @WebResult(name = "route")
    public Route getRouteById(
            @WebParam(name = "id") long id
    ) throws RouteNotFoundSoapException {
        try {
            return routeService.getRouteById(id);
        } catch (RouteNotFoundException e) {
            // Перехватываем RouteNotFoundException и пробрасываем в виде SOAPFault или собственного @WebFault
            throw new RouteNotFoundSoapException(e.getMessage(), e);
        }
    }

    /**
     * Обновить маршрут по его ID
     */
    @WebMethod
    @WebResult(name = "updatedRoute")
    public Route updateRoute(
            @WebParam(name = "id") long id,
            @WebParam(name = "updatedRoute") Route updatedRoute
    ) throws RouteNotFoundSoapException {
        try {
            return routeService.updateRoute(id, updatedRoute);
        } catch (RouteNotFoundException e) {
            throw new RouteNotFoundSoapException(e.getMessage(), e);
        }
    }

    /**
     * Удалить маршрут по ID
     */
    @WebMethod
    public void deleteRoute(
            @WebParam(name = "id") long id
    ) throws RouteNotFoundSoapException {
        try {
            routeService.deleteRoute(id);
        } catch (RouteNotFoundException e) {
            throw new RouteNotFoundSoapException(e.getMessage(), e);
        }
    }

    /**
     * Получить маршрут с максимальным значением поля "from"
     */
    @WebMethod
    @WebResult(name = "routeWithMaxFrom")
    public Route getRouteWithMaxFrom() {
        return routeService.getRouteWithMaxFrom();
    }

    /**
     * Получить количество маршрутов с расстоянием меньше заданного
     */
    @WebMethod
    @WebResult(name = "countOfRoutes")
    public Long getCountOfRoutesWithDistanceLowerThan(
            @WebParam(name = "distance") double value
    ) {
        return routeService.getCountOfRoutesWithDistanceLowerThan(value);
    }
}
