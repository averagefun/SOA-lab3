package se.ifmo.proxy.controllers;

import jakarta.validation.Valid;
import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPConnectionFactory;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
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
import se.ifmo.model.exception.RouteNotFoundException;
import se.ifmo.proxy.utils.SSLUtil;

import java.util.List;

import static se.ifmo.proxy.utils.RequestCreator.createAddRouteRequest;
import static se.ifmo.proxy.utils.RequestCreator.createSoapGetRouteByIdRequest;
import static se.ifmo.proxy.utils.RequestCreator.createSoapGetRoutesRequest;
import static se.ifmo.proxy.utils.xml2json.parseSoapAddResponse;
import static se.ifmo.proxy.utils.xml2json.parseSoapGetByIdResponse;
import static se.ifmo.proxy.utils.xml2json.parseSoapResponse;

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
            Route createdRoute = sendRouteAsSOAP(route);
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
    public void updateRoute(@PathVariable long id, @RequestBody @Valid Route updatedRoute) {
        // Пустая реализация
    }

    @DeleteMapping("/{id}")
    public void deleteRoute(@PathVariable long id) {
        // Пустая реализация
    }

    @GetMapping("/from/max")
    public void getRouteWithMaxFrom() {
        // Пустая реализация
    }

    @GetMapping("/distance/lower/{value}/count")
    public void getCountOfRoutesWithDistanceLowerThan(@PathVariable double value) {
        // Пустая реализация
    }
    private Route sendRouteAsSOAP(Route route) throws Exception {
        SOAPMessage soapRequest = createAddRouteRequest(route);
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        SOAPMessage soapResponse = soapConnection.call(soapRequest, SERVICE_URL);
        return parseSoapAddResponse(soapResponse);
    }
}