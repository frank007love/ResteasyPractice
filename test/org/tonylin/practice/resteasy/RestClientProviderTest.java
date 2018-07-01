package org.tonylin.practice.resteasy;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HttpClientConnectionManager;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.tonylin.practice.httpclient.ClientHttpEngineBuilder;
import org.tonylin.practice.httpclient.HttpClientConnectionManagerBuilder;

public class RestClientProviderTest {
	private String target = "http://192.168.1.120/dokuwiki/doku.php";
	// httpclient, 4.3.6 -> 4.5.4
	
	private HttpClientConnectionManager connectionManager = null;
	
	@After
	public void teardown(){
		if( connectionManager != null )
			connectionManager.shutdown();
	}
	
	private HttpClientConnectionManager initConnectionManager(int poolSize, int perRoute) {
		HttpClientConnectionManagerBuilder builder = new HttpClientConnectionManagerBuilder();
		return builder.withMaxTotal(poolSize).withDefaultMaxPerRoute(perRoute).build();
	}
	
	@Test(timeout=20000)
	public void testRequestTimeoutWithCustomHttpEngine() throws Exception {
		int expectTimeout = 2*1000;
		
		connectionManager = initConnectionManager(20 , 1);
		
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(expectTimeout)
				.build();
		
		ClientHttpEngine httpEngin = new ClientHttpEngineBuilder()
				.withRequestConfig(requestConfig)
				.withConnectionManager(connectionManager).build();
		
		ResteasyClientBuilder clientBuilder = (ResteasyClientBuilder)ResteasyClientBuilder.newBuilder();
		clientBuilder.httpEngine(httpEngin);
		
		Client client = clientBuilder.build();
		
		Response leak_repsonse = client.target(target).request().get();
		Response second_response = null;

		long before = System.currentTimeMillis();
		try {
			second_response = client.target(target).request().get();
			fail("should be timeout");
		} catch (Exception e) {
			assertNotNull(e.getCause());
			assertTrue(e.getCause() instanceof ConnectionPoolTimeoutException);
			assertEquals(expectTimeout, System.currentTimeMillis() - before, 500);
		} finally {
			leak_repsonse.close();
			if( second_response != null )
				second_response.close();
		}
	}
}

