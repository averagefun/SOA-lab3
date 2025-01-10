package se.ifmo;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPBodyElement;
import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPConnectionFactory;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPHeader;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPPart;
import javax.xml.namespace.QName;

/**
 * Пример кода для отправки SOAP-запроса к методу addRoute,
 * который ожидает объект Route в теле сообщения.
 */
public class SOAPClientAddRoute {

    public static void main(String[] args) {
        try {
            // Если у вас самоподписанный сертификат и нужно отключить проверку,
            // оставьте вызов disableSSLVerification(); (зависит от реализации SSLUtil).
            // Если класс SSLUtil недоступен, удалите эту строку или подключите нужный класс.
            SSLUtil.disableSSLVerification();

            // Адрес вашего SOAP-сервиса (без ?wsdl)
            // Например: "https://desktop-jnccspf:8181/web-app-1.0-SNAPSHOT/RoutesService"
            String serviceURL = "https://localhost:8181/web-app-1.0-SNAPSHOT/RoutesService";

            // Создание SOAP-соединения
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Создание SOAP-запроса
            SOAPMessage soapRequest = createAddRouteRequest();

            // Отправка запроса и получение ответа
            SOAPMessage soapResponse = soapConnection.call(soapRequest, serviceURL);

            // Вывод ответа в консоль
            System.out.println("===== SOAP Response =====");
            soapResponse.writeTo(System.out);
            System.out.println();

            // Закрытие соединения
            soapConnection.close();

        } catch (Exception e) {
            System.err.println("Error occurred while sending SOAP Request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Формируем SOAP-запрос для метода addRoute,
     * максимально повторяя структуру из SoapUI.
     */
    private static SOAPMessage createAddRouteRequest() throws Exception {
        // Создаём SOAP-сообщение
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        // Получаем SOAPPart
        SOAPPart soapPart = soapMessage.getSOAPPart();

        /*
         * Приводим Envelope к виду:
         * <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
         *                   xmlns:soap="http://soap.ifmo.se/">
         *     <soapenv:Header/>
         *     <soapenv:Body>
         *         <soap:addRoute>
         *             <route>
         *                 ...
         *             </route>
         *         </soap:addRoute>
         *     </soapenv:Body>
         * </soapenv:Envelope>
         */

        // Пространства имён
        String SOAP_ENV_PREFIX = "soapenv";
        String SOAP_ENV_URI = "http://schemas.xmlsoap.org/soap/envelope/";

        // В SoapUI пример использует префикс "soap" для targetNamespace
        String SERVICE_PREFIX = "soap";
        String SERVICE_URI = "http://soap.ifmo.se/";

        // Формируем Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        // Удаляем дефолтный префикс, если хотите (не всегда обязательно)
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration(SOAP_ENV_PREFIX, SOAP_ENV_URI);
        envelope.addNamespaceDeclaration(SERVICE_PREFIX, SERVICE_URI);
        envelope.setPrefix(SOAP_ENV_PREFIX);

        // Header (можно оставить пустым)
        SOAPHeader header = envelope.getHeader();
        header.setPrefix(SOAP_ENV_PREFIX);

        // Body
        SOAPBody body = envelope.getBody();
        body.setPrefix(SOAP_ENV_PREFIX);

        // <soap:addRoute>
        QName addRouteQName = new QName(SERVICE_URI, "addRoute", SERVICE_PREFIX);
        SOAPBodyElement addRouteElement = body.addBodyElement(addRouteQName);

        // <route>
        // Обратите внимание: в SoapUI пример показывает <soap:addRoute> + <route> без префикса.
        // Но фактически WSDL часто ждёт <soap:addRoute><soap:route>... Если в вашем WSDL имя "route" прописано
        // c @WebParam(name="route"), то <route> будет без префикса? Или <soap:route>?
        // Чаще всего — <soap:route>. Оставим здесь <route> без дополнительного QName,
        // чтобы получилось ровно как в SoapUI: <route>...</route>.
        SOAPElement routeElement = addRouteElement.addChildElement("route");

        // (1) <id>
        routeElement.addChildElement("id").addTextNode("0");

        // (2) <name>
        routeElement.addChildElement("name").addTextNode("My Test Route");

        // (3) <coordinates>
        SOAPElement coordinates = routeElement.addChildElement("coordinates");
        coordinates.addChildElement("x").addTextNode("100");
        coordinates.addChildElement("y").addTextNode("200");


        // (5) <from> (если опционален, можно убрать; здесь оставим для примера)
        SOAPElement from = routeElement.addChildElement("from");
        from.addChildElement("x").addTextNode("10");
        from.addChildElement("y").addTextNode("20");
        from.addChildElement("z").addTextNode("5");
        from.addChildElement("name").addTextNode("Start Location");

        // (6) <to>
        SOAPElement to = routeElement.addChildElement("to");
        to.addChildElement("x").addTextNode("30");
        to.addChildElement("y").addTextNode("40");
        to.addChildElement("z").addTextNode("6");
        to.addChildElement("name").addTextNode("End Location");

        // (7) <distance>
        routeElement.addChildElement("distance").addTextNode("12.5");

        // Сохраняем SOAPMessage
        soapMessage.saveChanges();

        // Для отладки выводим сформированный XML
        System.out.println("===== SOAP Request to addRoute =====");
        soapMessage.writeTo(System.out);
        System.out.println("\n====================================");

        return soapMessage;
    }
}
