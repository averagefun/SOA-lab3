package de.gre90r.jaxwsserver.server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.LogicalMessageContext;

public class CrossOriginFilter implements LogicalHandler<LogicalMessageContext> {

    @Override
    public boolean handleMessage(LogicalMessageContext context) {
        context.put(MessageContext.HTTP_RESPONSE_HEADERS, getResponseHeaders());
        return true;
    }

    @Override
    public boolean handleFault(LogicalMessageContext context) {
        context.put(MessageContext.HTTP_RESPONSE_HEADERS, getResponseHeaders());
        return true;
    }

    private Map<String, List<String>> getResponseHeaders() {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", Arrays.asList("*"));
        headers.put("Access-Control-Allow-Methods", Arrays.asList("GET, POST, OPTIONS"));
        headers.put("Access-Control-Allow-Headers", Arrays.asList("Content-Type, SOAPAction"));
        return headers;
    }

    @Override
    public void close(MessageContext context) {}
}
