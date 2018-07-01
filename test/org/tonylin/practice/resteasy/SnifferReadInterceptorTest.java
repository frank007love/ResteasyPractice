package org.tonylin.practice.resteasy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import org.junit.Before;
import org.junit.Test;

public class SnifferReadInterceptorTest {
	
	private Client restClient;
	private SnifferReadInterceptor readInterceptor = null;
	final private String testURL = "http://tonylin.idv.tw/dokuwiki"; 
	
	@Before
	public void initRestClient(){
		ResteasyJackson2Provider jacksonProvider = new ResteasyJackson2Provider();
		restClient = ResteasyClientBuilder.newBuilder().register(jacksonProvider).build();
	}
	
	@Before
	public void initReadInterceptor(){
		readInterceptor = new SnifferReadInterceptor();
	}
	
	public Invocation initInvocation(){
		Invocation invocation = restClient.target(testURL).request().buildGet();
		ClientConfiguration clientConfig = getClientConfig(invocation);
		clientConfig.register(readInterceptor);
		return invocation;
	}
	
	private ClientConfiguration getClientConfig(Invocation invocation){
		return ((ClientInvocation)invocation).getClientConfiguration();
	}
	
	@Test
	public void testSnifferReadInterceptorWithReadFailed(){
		Invocation invocation = initInvocation();
		
		Response response = null;
		try {
			response =  invocation.invoke();
			response.readEntity(TestModel.class);
			fail("should be failed	");
		} catch( ProcessingException e ) {
			// Can't find valid messageBodyReader
			assertTrue(readInterceptor.getReadContent().isEmpty());
		} finally {
			response.close();
		}
	}
	
	@Test
	public void testInstanceOfReadInterceptor(){
		Invocation invocation1 = initInvocation();
		Invocation invocation2 = initInvocation();
		
		ClientConfiguration config1 = getClientConfig(invocation1);
		ClientConfiguration config2 = getClientConfig(invocation2);
		
		assertEquals(config2.getReaderInterceptors(null, null).length, 
				config1.getReaderInterceptors(null, null).length);
	}
	
	@Test
	public void testContentOfSnifferReadInterceptor(){
		Invocation invocation = initInvocation();
		
		Response response = null;
		try {
			response = invocation.invoke();
			String result = response.readEntity(String.class);
			
			assertFalse(result.isEmpty());
			assertEquals(result, readInterceptor.getReadContent());
		} finally {
			response.close();
		}		
	}
	
}
