package se.ifmo.proxy.utils;

import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import se.ifmo.model.Coordinates;
import se.ifmo.model.Location;
import se.ifmo.model.Route;
import se.ifmo.model.exception.RouteNotFoundException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class xml2json {
    public static List<Route> parseSoapResponse(SOAPMessage soapResponse) throws Exception {
        // Преобразуем SOAPMessage в строку
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        soapResponse.writeTo(baos);
        String responseXml = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        // Создаем DOM парсер
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(responseXml.getBytes(StandardCharsets.UTF_8)));

        // Получаем корневой элемент
        Element root = document.getDocumentElement();

        // Ищем список маршрутов
        NodeList routeNodes = root.getElementsByTagName("routes");

        List<Route> routes = new ArrayList<>();

        // Обрабатываем каждый маршрут
        for (int i = 0; i < routeNodes.getLength(); i++) {
            Element routeElement = (Element) routeNodes.item(i);
            Route route = new Route();

            setRouteFields(route, routeElement);

            routes.add(route);
        }

        return routes;
    }

    public static Route parseSoapUpdateResponse(SOAPMessage soapResponse) throws Exception {
        Route route = new Route();

        SOAPBody body = soapResponse.getSOAPBody();
        Node createdRouteNode = body.getElementsByTagName("updatedRoute").item(0);

        if (!(createdRouteNode instanceof Element createdRouteElement)) {
            throw new Exception("SOAP response does not contain a update element.");
        }

        setRouteFields(route, createdRouteElement);

        return route;
    }
    public static Route parseSoapAddResponse(SOAPMessage soapResponse) throws Exception {
        Route route = new Route();

        SOAPBody body = soapResponse.getSOAPBody();
        Node createdRouteNode = body.getElementsByTagName("createdRoute").item(0);

        if (!(createdRouteNode instanceof Element createdRouteElement)) {
            throw new Exception("SOAP response does not contain a createdRoute element.");
        }

        // Парсим поля Route
        setRouteFields(route, createdRouteElement);

        return route;
    }

    public static Route parseSoapGetByIdResponse(SOAPMessage soapResponse) throws Exception {
        Route route = new Route();

        SOAPBody body = soapResponse.getSOAPBody();
        Node createdRouteNode = body.getElementsByTagName("route").item(0);

        if (!(createdRouteNode instanceof Element createdRouteElement)) {
            throw new RouteNotFoundException("Route not found.");
        }
        setRouteFields(route, createdRouteElement);
        return route;
    }

    private static void setRouteFields(Route route, Element element){
        route.setId(parseLong(getChildElementText(element, "id")));
        route.setName(getChildElementText(element, "name"));

        Element coordinatesElement = getChildElement(element, "coordinates");
        if (coordinatesElement != null) {
            Coordinates coordinates = new Coordinates();
            coordinates.setX(parseInt(getChildElementText(coordinatesElement, "x")));
            coordinates.setY(parseLong(getChildElementText(coordinatesElement, "y")));
            route.setCoordinates(coordinates);
        }

        route.setCreationDate(LocalDate.parse(Objects.requireNonNull(getChildElementText(element, "creationDate"))));
        Element fromElement = getChildElement(element, "from");
        if (fromElement != null) {
            Location from = parseLocation(fromElement);
            route.setFrom(from);
        }
        Element toElement = getChildElement(element, "to");
        if (toElement != null) {
            Location to = parseLocation(toElement);
            route.setTo(to);
        }
        route.setDistance(parseDouble(getChildElementText(element, "distance")));
    }
    private static Long parseLong(String value) {
        try {
            return value != null ? Long.parseLong(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Integer parseInt(String value) {
        try {
            return value != null ? Integer.parseInt(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Double parseDouble(String value) {
        try {
            return value != null ? Double.parseDouble(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Location parseLocation(Element locationElement) {
        Location location = new Location();
        location.setId(parseLong(getChildElementText(locationElement, "id")));
        location.setX(parseInt(getChildElementText(locationElement, "x")));
        Long y = parseLong(getChildElementText(locationElement, "y"));
        if (y == null) {
            location.setY(0L);
        } else {
            location.setY(y);;
        }
        Integer z = (parseInt(getChildElementText(locationElement, "z")));
        location.setZ(Objects.requireNonNullElse(z, 0));
        location.setName(getChildElementText(locationElement, "name"));
        return location;
    }

    private static String getChildElementText(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }


    private static Element getChildElement(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return (Element) nodeList.item(0);
        }
        return null;
    }

}
