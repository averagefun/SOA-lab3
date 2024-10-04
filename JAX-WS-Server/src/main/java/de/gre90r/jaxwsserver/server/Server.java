package de.gre90r.jaxwsserver.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

import de.gre90r.jaxwsserver.employee.EmployeeService;

import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.net.InetSocketAddress;


public class Server {
	private static final String URL = "http://localhost:8080/employee-service";

	public static void main(String[] args) {
		try {
			// Create the HttpServer
			InetSocketAddress address = new InetSocketAddress(8080);
			HttpServer httpServer = HttpServer.create(address, 0);

			// Create the context
			HttpContext context = httpServer.createContext("/employee-service");

			// Add a filter to handle CORS and OPTIONS
			context.getFilters().add(new CORSFilter());

			// Publish the Endpoint on the context
			Endpoint endpoint = Endpoint.create(new EmployeeService());
			endpoint.publish(context);

			httpServer.start();

			System.out.println("Endpoint running... " + URL);
		} catch (Exception e) {
			System.err.println("Endpoint failed to start");
			e.printStackTrace();
		}
	}

	// CORSFilter class
	static class CORSFilter extends Filter {
		@Override
		public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
			// Add CORS headers
			exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
			exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
			exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, SOAPAction");

			if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
				// If it's an OPTIONS request, send the response with status 200 OK
				exchange.sendResponseHeaders(200, -1);
			} else {
				// Continue processing
				chain.doFilter(exchange);
			}
		}

		@Override
		public String description() {
			return "Handles CORS by adding necessary headers and responding to OPTIONS requests";
		}
	}
}
