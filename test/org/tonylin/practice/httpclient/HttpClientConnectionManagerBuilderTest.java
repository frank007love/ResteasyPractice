package org.tonylin.practice.httpclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.After;
import org.junit.Test;

public class HttpClientConnectionManagerBuilderTest {
	private String target = "http://192.168.1.120/dokuwiki/doku.php";
	
	private HttpClientConnectionManager connectionManager = null;
	
	@After
	public void teardown(){
		if( connectionManager != null )
			connectionManager.shutdown();
	}
	
	@Test(timeout=20*1000)
	public void testRequestTimeout() throws Exception {
		int expect_timeout = 2*1000;
		connectionManager = new HttpClientConnectionManagerBuilder().build();
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(expect_timeout)
				.build();
		
		HttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager)
				.setDefaultRequestConfig(requestConfig)
				.build();
		
		HttpUriRequest uriRequest = new HttpGet(target);
		HttpResponse response = httpClient.execute(uriRequest);
		assertEquals(200, response.getStatusLine().getStatusCode());
		
		long before = System.currentTimeMillis();
		try {
			uriRequest = new HttpGet(target);
			response = httpClient.execute(uriRequest);
		} catch( ConnectionPoolTimeoutException e ) {
			assertEquals("Timeout waiting for connection from pool", e.getMessage());
		}
		long duration = System.currentTimeMillis() - before;
		assertEquals(expect_timeout, duration, 500);
	}
	
	@Test
	public void testBuild(){
		int expect_max_total = 123;
		int expect_max_per_route = 22;
		
		HttpClientConnectionManagerBuilder builder = new HttpClientConnectionManagerBuilder();
		
		connectionManager = builder.withMaxTotal(expect_max_total)
				.withDefaultMaxPerRoute(expect_max_per_route)
				.build();
		
		assertTrue(connectionManager instanceof PoolingHttpClientConnectionManager);
		
		assertEquals(expect_max_total, ((PoolingHttpClientConnectionManager)connectionManager).getMaxTotal());
		assertEquals(expect_max_per_route, ((PoolingHttpClientConnectionManager)connectionManager).getDefaultMaxPerRoute());
	}
}
