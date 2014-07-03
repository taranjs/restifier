package restifiercloud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;

public class PrimeTest {
	@Test
	public void testValidRequest() {
		Client client = new Client(Protocol.HTTP);
		Request request = new Request(Method.GET,
				"http://localhost:16999/prime/29");
		Response response = client.handle(request);

		assertEquals(200, response.getStatus().getCode());
		assertEquals(true, response.isEntityAvailable());
		assertEquals(MediaType.APPLICATION_JSON, response.getEntity()
				.getMediaType());
	}

	@Test
	public void testInvalidRequest() {
		Client client = new Client(Protocol.HTTP);
		Request request = new Request(Method.GET,
				"http://localhost:16999/whatever/29");
		Response response = client.handle(request);

		assertEquals(404, response.getStatus().getCode());
		assertEquals(true, response.isEntityAvailable());
		assertEquals(MediaType.TEXT_HTML, response.getEntity().getMediaType());
	}

	@Test
	public void testResponseData() {
		Client client = new Client(Protocol.HTTP);
		Request request = new Request(Method.GET,
				"http://localhost:16999/prime/29");
		Response response = client.handle(request);

		String responseText = "";
		try {
			responseText = response.getEntity().getText();
		} catch (IOException e) {
			fail("Exception: " + e.getMessage());
		}
		assertTrue(responseText.contains("\"initial\": 29"));
		assertTrue(responseText
				.contains("\"primes\": [2,3,5,7,11,13,17,19,23,29]"));
	}

	@Test
	public void testCache() {
		Client client = new Client(Protocol.HTTP);

		// Initial request with a large number takes more than 9 ms
		Request request = new Request(Method.GET,
				"http://localhost:16999/prime/9999");
		Response response = client.handle(request);

		String responseText = "";
		try {
			responseText = response.getEntity().getText();
		} catch (IOException e) {
			fail("Exception: " + e.getMessage());
		}
		assertTrue(responseText.contains("\"initial\": 9999"));
		assertTrue(responseText.matches(".*\"processingTime\":\\s\\d{1,},.*"));

		// Repeat request takes 0 ms
		request = new Request(Method.GET, "http://localhost:16999/prime/9999");
		response = client.handle(request);

		responseText = "";
		try {
			responseText = response.getEntity().getText();
		} catch (IOException e) {
			fail("Exception: " + e.getMessage());
		}
		assertTrue(responseText.contains("\"initial\": 9999"));
		assertTrue(responseText.matches(".*\"processingTime\":\\s0,.*"));
	}

}
