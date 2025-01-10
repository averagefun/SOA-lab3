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
 * Тестовый SOAP-клиент для вызова метода getAllRoutes,
 * где передаются только page и size (фильтры не используются).
 */
public class SOAPClientGetAllRoutes {

    public static void main(String[] args) {
        try {
            // Отключаем проверку SSL (если используете самоподписанный сертификат),
            // иначе удалите эту строку или импортируйте сертификат в truststore.
            SSLUtil.disableSSLVerification();

            // Адрес вашего SOAP-сервиса (без ?wsdl)
            // Например: "https://desktop-jnccspf:8181/web-app-1.0-SNAPSHOT/RoutesService"
            String serviceURL = "https://localhost:8181/web-app-1.0-SNAPSHOT/RoutesService";

            // Создаём SOAP-соединение
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Готовим SOAP-запрос
            SOAPMessage soapRequest = createGetAllRoutesRequest();

            // Отправка и получение ответа
            SOAPMessage soapResponse = soapConnection.call(soapRequest, serviceURL);

            // Вывод ответа
            System.out.println("===== SOAP Response =====");
            soapResponse.writeTo(System.out);
            System.out.println("\n=========================");

            // Закрываем соединение
            soapConnection.close();

        } catch (Exception e) {
            System.err.println("Error occurred while sending SOAP Request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Формируем SOAP-запрос к методу getAllRoutes с минимальными параметрами (page, size).
     */
    private static SOAPMessage createGetAllRoutesRequest() throws Exception {
        // Создаём фабрику сообщений SOAP
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        // Достаём SOAPPart
        SOAPPart soapPart = soapMessage.getSOAPPart();

        // Пространства имён
        String soapEnvPrefix = "soapenv";
        String soapEnvURI = "http://schemas.xmlsoap.org/soap/envelope/";

        // Префикс и URI сервиса (см. WSDL: targetNamespace="http://soap.ifmo.se/")
        String servicePrefix = "soap";
        String serviceURI = "http://soap.ifmo.se/";

        // Формируем Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        // Удаляем дефолтный префикс, если нужно
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration(soapEnvPrefix, soapEnvURI);
        envelope.addNamespaceDeclaration(servicePrefix, serviceURI);
        envelope.setPrefix(soapEnvPrefix);

        // Создаём Header (можно оставить пустым)
        SOAPHeader header = envelope.getHeader();
        header.setPrefix(soapEnvPrefix);

        // Создаём Body
        SOAPBody body = envelope.getBody();
        body.setPrefix(soapEnvPrefix);

        // Создаём элемент <soap:getAllRoutes>
        QName getAllRoutesQName = new QName(serviceURI, "getAllRoutes", servicePrefix);
        SOAPBodyElement getAllRoutesElement = body.addBodyElement(getAllRoutesQName);

        // Добавляем обязательные параметры page и size
        getAllRoutesElement
                .addChildElement("page").addTextNode("1");
        getAllRoutesElement
                .addChildElement("size").addTextNode("2");

        // Сохраняем сообщение
        soapMessage.saveChanges();

        // Для отладки: выводим сформированный SOAP-запрос
        System.out.println("===== SOAP Request =====");
        soapMessage.writeTo(System.out);
        System.out.println("\n========================");

        return soapMessage;
    }
}
