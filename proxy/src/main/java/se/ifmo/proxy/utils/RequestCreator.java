package se.ifmo.proxy.utils;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPBodyElement;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPHeader;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPPart;
import se.ifmo.model.Route;

import javax.xml.namespace.QName;
import java.util.List;

public class RequestCreator {
    private static final String SOAP_ENV_PREFIX = "soapenv";
    private static final String SOAP_ENV_URI = "http://schemas.xmlsoap.org/soap/envelope/";
    private static final String SERVICE_PREFIX = "soap";
    private static final String SERVICE_URI = "http://soap.ifmo.se/";

    public static SOAPMessage createAddRouteRequest(Route route) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        SOAPBody body = getSoapBody(soapMessage);

        // <soap:addRoute>
        QName addRouteQName = new QName(SERVICE_URI, "addRoute", SERVICE_PREFIX);
        SOAPBodyElement addRouteElement = body.addBodyElement(addRouteQName);

        // <route>
        addRouteToSoap(route, addRouteElement, "route");

        soapMessage.saveChanges();
        return soapMessage;
    }

    public static SOAPMessage createUpdateRouteRequest(Route route, Long id) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        SOAPBody body = getSoapBody(soapMessage);

        QName addRouteQName = new QName(SERVICE_URI, "updateRoute", SERVICE_PREFIX);
        SOAPBodyElement updateRouteElement = body.addBodyElement(addRouteQName);
        addChildElementWithText(updateRouteElement, "id", String.valueOf(id));

        addRouteToSoap(route, updateRouteElement, "updatedRoute");

        soapMessage.saveChanges();
        return soapMessage;
    }

    public static SOAPMessage createGetRouteWithMaxFromRequest() throws Exception{

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        SOAPBody body = getSoapBody(soapMessage);

        QName addRouteQName = new QName(SERVICE_URI, "getRouteWithMaxFrom", SERVICE_PREFIX);
        body.addBodyElement(addRouteQName);

        soapMessage.saveChanges();
        return soapMessage;
    }

    public static SOAPMessage createDeleteRouteRequest(Long id) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        SOAPBody body = getSoapBody(soapMessage);

        QName addRouteQName = new QName(SERVICE_URI, "deleteRoute", SERVICE_PREFIX);
        SOAPBodyElement updateRouteElement = body.addBodyElement(addRouteQName);
        addChildElementWithText(updateRouteElement, "id", String.valueOf(id));

        soapMessage.saveChanges();
        return soapMessage;
    }

    public static SOAPMessage createGetCountOfRoutesWithDistanceLowerThanRequest(Double value) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        SOAPBody body = getSoapBody(soapMessage);

        QName addRouteQName = new QName(SERVICE_URI, "getCountOfRoutesWithDistanceLowerThan", SERVICE_PREFIX);
        SOAPBodyElement updateRouteElement = body.addBodyElement(addRouteQName);
        addChildElementWithText(updateRouteElement, "distance", String.valueOf(value));

        soapMessage.saveChanges();
        return soapMessage;
    }

    private static void addRouteToSoap(Route route, SOAPBodyElement element, String routeName) throws Exception {
        SOAPElement routeElement = element.addChildElement(routeName);
        routeElement.addChildElement("name").addTextNode(route.getName());

        SOAPElement coordinates = routeElement.addChildElement("coordinates");
        coordinates.addChildElement("x").addTextNode(String.valueOf(route.getCoordinates().getX()));
        coordinates.addChildElement("y").addTextNode(String.valueOf(route.getCoordinates().getY()));

        SOAPElement from = routeElement.addChildElement("from");
        from.addChildElement("x").addTextNode(String.valueOf(route.getFrom().getX()));
        from.addChildElement("y").addTextNode(String.valueOf(route.getFrom().getY()));
        from.addChildElement("z").addTextNode(String.valueOf(route.getFrom().getZ()));
        from.addChildElement("name").addTextNode(route.getFrom().getName());

        SOAPElement to = routeElement.addChildElement("to");
        to.addChildElement("x").addTextNode(String.valueOf(route.getTo().getX()));
        to.addChildElement("y").addTextNode(String.valueOf(route.getTo().getY()));
        to.addChildElement("z").addTextNode(String.valueOf(route.getTo().getZ()));
        to.addChildElement("name").addTextNode(route.getTo().getName());

        routeElement.addChildElement("distance").addTextNode(String.valueOf(route.getDistance()));
    }



    public static SOAPMessage createSoapGetRoutesRequest(
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
    ) throws SOAPException {

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        SOAPBody body = getSoapBody(soapMessage);

        QName getAllRoutesQName = new QName(SERVICE_URI, "getAllRoutes", SERVICE_PREFIX);
        SOAPBodyElement getAllRoutesElement = body.addBodyElement(getAllRoutesQName);

        addChildElementWithText(getAllRoutesElement, "page", String.valueOf(page));
        addChildElementWithText(getAllRoutesElement, "size", String.valueOf(size));

        if (sortParams != null) {
            int i = 0;
            while (i < sortParams.size()) {
                String field = sortParams.get(i);
                String direction = "asc";

                if (i + 1 < sortParams.size()) {
                    String nextParam = sortParams.get(i + 1).toLowerCase();
                    if (nextParam.equals("asc") || nextParam.equals("desc")) {
                        direction = nextParam;
                        i++;
                    }
                }
                i++;
                String sortParam = field + "," +direction;
                addChildElementWithText(getAllRoutesElement, "sortParams",sortParam);
            }
        }

        if (nameFilter != null) {
            addChildElementWithText(getAllRoutesElement, "nameFilter", nameFilter);
        }
        if (fromLocationId != null) {
            addChildElementWithText(getAllRoutesElement, "fromLocationId", fromLocationId.toString());
        }
        if (toLocationId != null) {
            addChildElementWithText(getAllRoutesElement, "toLocationId", toLocationId.toString());
        }
        if (minDistance != null) {
            addChildElementWithText(getAllRoutesElement, "minDistance", minDistance.toString());
        }
        if (maxDistance != null) {
            addChildElementWithText(getAllRoutesElement, "maxDistance", maxDistance.toString());
        }
        if (coordinatesX != null) {
            addChildElementWithText(getAllRoutesElement, "coordinatesX", coordinatesX.toString());
        }
        if (coordinatesY != null) {
            addChildElementWithText(getAllRoutesElement, "coordinatesY", coordinatesY.toString());
        }
        if (fromX != null) {
            addChildElementWithText(getAllRoutesElement, "fromX", fromX.toString());
        }
        if (fromY != null) {
            addChildElementWithText(getAllRoutesElement, "fromY", fromY.toString());
        }
        if (fromZ != null) {
            addChildElementWithText(getAllRoutesElement, "fromZ", fromZ.toString());
        }
        if (fromName != null) {
            addChildElementWithText(getAllRoutesElement, "fromName", fromName);
        }
        if (toX != null) {
            addChildElementWithText(getAllRoutesElement, "toX", toX.toString());
        }
        if (toY != null) {
            addChildElementWithText(getAllRoutesElement, "toY", toY.toString());
        }
        if (toZ != null) {
            addChildElementWithText(getAllRoutesElement, "toZ", toZ.toString());
        }
        if (toName != null) {
            addChildElementWithText(getAllRoutesElement, "toName", toName);
        }

        soapMessage.saveChanges();
        return soapMessage;
    }

    public static SOAPMessage createSoapGetRouteByIdRequest(Long routeId) throws SOAPException {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        SOAPBody body = getSoapBody(soapMessage);

        QName getRouteByIdQName = new QName(SERVICE_URI, "getRouteById", SERVICE_PREFIX);
        SOAPBodyElement getRouteByIdElement = body.addBodyElement(getRouteByIdQName);

        addChildElementWithText(getRouteByIdElement, "id", routeId.toString());

        soapMessage.saveChanges();

        return soapMessage;
    }

    private static SOAPBody getSoapBody(SOAPMessage soapMessage) throws SOAPException {
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration(SOAP_ENV_PREFIX, SOAP_ENV_URI);
        envelope.addNamespaceDeclaration(SERVICE_PREFIX, SERVICE_URI);
        envelope.setPrefix(SOAP_ENV_PREFIX);

        SOAPHeader header = envelope.getHeader();
        header.setPrefix(SOAP_ENV_PREFIX);

        SOAPBody body = envelope.getBody();
        body.setPrefix(SOAP_ENV_PREFIX);
        return body;
    }

    private static void addChildElementWithText(SOAPElement parent, String childName, String text) throws SOAPException {
        parent.addChildElement(childName).addTextNode(text);
    }
}
