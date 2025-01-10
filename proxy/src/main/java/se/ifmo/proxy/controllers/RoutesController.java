package se.ifmo.proxy.controllers;

import jakarta.validation.Valid;
import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPConnectionFactory;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.ifmo.model.Route;
import se.ifmo.proxy.utils.SSLUtil;

import java.util.List;

import static se.ifmo.proxy.utils.RequestCreator.createAddRouteRequest;
import static se.ifmo.proxy.utils.RequestCreator.createDeleteRouteRequest;
import static se.ifmo.proxy.utils.RequestCreator.createGetCountOfRoutesWithDistanceLowerThanRequest;
import static se.ifmo.proxy.utils.RequestCreator.createGetRouteWithMaxFromRequest;
import static se.ifmo.proxy.utils.RequestCreator.createSoapGetRouteByIdRequest;
import static se.ifmo.proxy.utils.RequestCreator.createSoapGetRoutesRequest;
import static se.ifmo.proxy.utils.RequestCreator.createUpdateRouteRequest;
import static se.ifmo.proxy.utils.xml2json.parseSoapAddResponse;
import static se.ifmo.proxy.utils.xml2json.parseSoapGetByIdResponse;
import static se.ifmo.proxy.utils.xml2json.parseSoapGetCountOfRoutesWithDistanceLowerThan;
import static se.ifmo.proxy.utils.xml2json.parseSoapGetRouteWithMaxFromResponse;
import static se.ifmo.proxy.utils.xml2json.parseSoapResponse;
import static se.ifmo.proxy.utils.xml2json.parseSoapUpdateResponse;

@RestController
@RequestMapping("/routes")
public class RoutesController {

    private static final String SERVICE_URL = "https://localhost:8181/web-app-1.0-SNAPSHOT/RoutesService";
    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", required = false) List<String> sortParams,
            @RequestParam(name = "name", required = false) String nameFilter,
            @RequestParam(name = "fromLocationId", required = false) Long fromLocationId,
            @RequestParam(name = "toLocationId", required = false) Long toLocationId,
            @RequestParam(name = "minDistance", required = false) Double minDistance,
            @RequestParam(name = "maxDistance", required = false) Double maxDistance,
            @RequestParam(name = "coordinatesX", required = false) Integer coordinatesX,
            @RequestParam(name = "coordinatesY", required = false) Long coordinatesY,
            @RequestParam(name = "fromX", required = false) Integer fromX,
            @RequestParam(name = "fromY", required = false) Long fromY,
            @RequestParam(name = "fromZ", required = false) Integer fromZ,
            @RequestParam(name = "fromName", required = false) String fromName,
            @RequestParam(name = "toX", required = false) Integer toX,
            @RequestParam(name = "toY", required = false) Long toY,
            @RequestParam(name = "toZ", required = false) Integer toZ,
            @RequestParam(name = "toName", required = false) String toName
    ) {
        SOAPConnection soapConnection = null;
        try {
            SSLUtil.disableSSLVerification();
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            soapConnection = soapConnectionFactory.createConnection();

            SOAPMessage soapRequest = createSoapGetRoutesRequest(
                    page, size, sortParams, nameFilter,
                    fromLocationId, toLocationId,
                    minDistance, maxDistance,
                    coordinatesX, coordinatesY,
                    fromX, fromY, fromZ, fromName,
                    toX, toY, toZ, toName
            );

            SOAPMessage soapResponse = soapConnection.call(soapRequest, SERVICE_URL);

            List<Route> routes = parseSoapResponse(soapResponse);

            return ResponseEntity.ok(routes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        } finally {
            if (soapConnection != null) {
                try {
                    soapConnection.close();
                } catch (SOAPException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @PostMapping
    public ResponseEntity<Route> addRoute(@RequestBody @Valid Route route) {
        try {
            SSLUtil.disableSSLVerification();
            SOAPMessage soapRequest = createAddRouteRequest(route);
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
            SOAPMessage soapResponse = soapConnection.call(soapRequest, SERVICE_URL);
            Route createdRoute = parseSoapAddResponse(soapResponse);
            return ResponseEntity.ok(createdRoute);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable long id) {
        SOAPConnection soapConnection = null;
        try {
            SSLUtil.disableSSLVerification();
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            soapConnection = soapConnectionFactory.createConnection();
            SOAPMessage soapRequest = createSoapGetRouteByIdRequest(id);
            SOAPMessage soapResponse = soapConnection.call(soapRequest, SERVICE_URL);
            Route route = parseSoapGetByIdResponse(soapResponse);
            return ResponseEntity.ok(route);
        }
        catch (Exception e) {
            return ResponseEntity.notFound().build();
        } finally {
            if (soapConnection != null) {
                try {
                    soapConnection.close();
                } catch (SOAPException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Route> updateRoute(@PathVariable long id, @RequestBody @Valid Route updatedRoute) {
        try {
            SSLUtil.disableSSLVerification();
            SOAPMessage soapRequest = createUpdateRouteRequest(updatedRoute, id);
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
            SOAPMessage soapResponse = soapConnection.call(soapRequest, SERVICE_URL);
            Route createdRoute = parseSoapUpdateResponse(soapResponse);
            return ResponseEntity.ok(createdRoute);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoute(@PathVariable long id) {
        try {
            SSLUtil.disableSSLVerification();
            SOAPMessage soapRequest = createDeleteRouteRequest(id);
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
            SOAPMessage soapResponse = soapConnection.call(soapRequest, SERVICE_URL);
            if (soapResponse.getSOAPBody().hasFault()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Маршрут с ID " + id + " не найден.");
            }
            return ResponseEntity.ok("Маршрут с ID " + id + " успешно удалён.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/from/max")
    public ResponseEntity<Route> getRouteWithMaxFrom() {
        try {
            SSLUtil.disableSSLVerification();
            SOAPMessage soapRequest = createGetRouteWithMaxFromRequest();
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
            SOAPMessage soapResponse = soapConnection.call(soapRequest, SERVICE_URL);
            Route route = parseSoapGetRouteWithMaxFromResponse(soapResponse);
            return ResponseEntity.ok(route);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/distance/lower/{value}/count")
    public ResponseEntity<String> getCountOfRoutesWithDistanceLowerThan(@PathVariable double value) {
        try {
            SSLUtil.disableSSLVerification();
            SOAPMessage soapRequest = createGetCountOfRoutesWithDistanceLowerThanRequest(value);
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
            SOAPMessage soapResponse = soapConnection.call(soapRequest, SERVICE_URL);
            Long count = parseSoapGetCountOfRoutesWithDistanceLowerThan(soapResponse);
            String jsonResponse = "{\"count\":" + count + "}";
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}