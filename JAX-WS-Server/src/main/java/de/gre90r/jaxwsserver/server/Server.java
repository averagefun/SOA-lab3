package de.gre90r.jaxwsserver.server;

import com.sun.net.httpserver.HttpsServer;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

import de.gre90r.jaxwsserver.employee.EmployeeService;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.ws.Endpoint;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyStore;

public class Server {
	private static final String URL = "https://localhost:8443/employee-service";

	public static void main(String[] args) {
		try {
			// Setup the SSL context
			char[] passphrase = "password".toCharArray(); // Replace with your keystore password
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream("server.keystore"), passphrase);

			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, passphrase);

			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(ks);

			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

			// Create the HttpsServer
			InetSocketAddress address = new InetSocketAddress(8443);
			HttpsServer httpsServer = HttpsServer.create(address, 0);
			httpsServer.setHttpsConfigurator(new com.sun.net.httpserver.HttpsConfigurator(sslContext));

			// Create the context
			HttpContext context = httpsServer.createContext("/employee-service");

			// Add a filter to handle CORS and OPTIONS
			context.getFilters().add(new CORSFilter());

			// Publish the Endpoint on the context
			Endpoint endpoint = Endpoint.create(new EmployeeService());
			endpoint.publish(context);

			httpsServer.start();

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
