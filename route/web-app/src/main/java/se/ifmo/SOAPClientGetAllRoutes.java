//package se.ifmo;
//
//import jakarta.xml.soap.MessageFactory;
//import jakarta.xml.soap.SOAPBody;
//import jakarta.xml.soap.SOAPBodyElement;
//import jakarta.xml.soap.SOAPConnection;
//import jakarta.xml.soap.SOAPConnectionFactory;
//import jakarta.xml.soap.SOAPElement;
//import jakarta.xml.soap.SOAPEnvelope;
//import jakarta.xml.soap.SOAPHeader;
//import jakarta.xml.soap.SOAPMessage;
//import jakarta.xml.soap.SOAPPart;
//
//import javax.xml.namespace.QName;
//
///**
// * Пример кода для отправки SOAP-запроса к методу getAllRoutes.
// */
//public class SOAPClientGetAllRoutes {
//
//    public static void main(String[] args) {
//        try {
//            SSLUtil.disableSSLVerification();
//            // URL веб-сервиса (укажите свой)
//            String serviceURL = "https://desktop-jnccspf:8181/web-app-1.0-SNAPSHOT/RoutesService";
//
//            // Создание SOAP-соединения
//            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
//            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
//
//            // Создание SOAP-запроса
//            SOAPMessage soapRequest = createGetAllRoutesRequest();
//
//            // Отправка SOAP-запроса и получение ответа
//            SOAPMessage soapResponse = soapConnection.call(soapRequest, serviceURL);
//
//            // Вывод ответа
//            System.out.println("Response:");
//            soapResponse.writeTo(System.out);
//            System.out.println();
//
//            // Закрытие соединения
//            soapConnection.close();
//
//        } catch (Exception e) {
//            System.err.println("Error occurred while sending SOAP Request: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Формируем SOAP-запрос для вызова метода getAllRoutes.
//     */
//    private static SOAPMessage createGetAllRoutesRequest() throws Exception {
//        // Создаём фабрику сообщений SOAP
//        MessageFactory messageFactory = MessageFactory.newInstance();
//        SOAPMessage soapMessage = messageFactory.createMessage();
//
//        // Достаём SOAPPart для наполнения
//        SOAPPart soapPart = soapMessage.getSOAPPart();
//
//        // Пространства имён
//        String soapEnvNamespace = "soapenv";                 // Для обёртки Envelope/Header/Body
//        String soapEnvNamespaceURI = "http://schemas.xmlsoap.org/soap/envelope/";
//
//        String myServiceNamespace = "ser";                   // Пространство имён сервиса (можно назвать как угодно)
//        String myServiceNamespaceURI = "http://soap.ifmo.se/";
//
//        // Формируем Envelope
//        SOAPEnvelope envelope = soapPart.getEnvelope();
//        envelope.removeNamespaceDeclaration("SOAP-ENV"); // Иногда необходимо удалить стандартный префикс
//        envelope.addNamespaceDeclaration(soapEnvNamespace, soapEnvNamespaceURI);
//        envelope.addNamespaceDeclaration(myServiceNamespace, myServiceNamespaceURI);
//        envelope.setPrefix(soapEnvNamespace);
//
//        // Формируем Header (можно оставить пустым)
//        SOAPHeader header = envelope.getHeader();
//        header.setPrefix(soapEnvNamespace);
//
//        // Формируем Body
//        SOAPBody body = envelope.getBody();
//        body.setPrefix(soapEnvNamespace);
//
//        /*
//         * Тег в body будет выглядеть примерно так:
//         * <ser:getAllRoutes>
//         *     <ser:page>1</ser:page>
//         *     <ser:size>10</ser:size>
//         *     ...
//         * </ser:getAllRoutes>
//         */
//
//        // Создаём элемент getAllRoutes
//        QName operationQName = new QName(myServiceNamespaceURI, "getAllRoutes", myServiceNamespace);
//        SOAPBodyElement getAllRoutesElement = body.addBodyElement(operationQName);
//
//        // Пример обязательных параметров:
//        getAllRoutesElement.addChildElement("page", myServiceNamespace).addTextNode("1");
//        getAllRoutesElement.addChildElement("size", myServiceNamespace).addTextNode("10");
//
//        // Сохраняем сообщение
//        soapMessage.saveChanges();
//
//        // Для отладки: вывод сформированного сообщения
//        System.out.println("SOAP Request:");
//        soapMessage.writeTo(System.out);
//        System.out.println("\n");
//
//        return soapMessage;
//    }
//}
