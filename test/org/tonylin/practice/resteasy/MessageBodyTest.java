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

public class MessageBodyTest {
	private Client restClient;
	final private String testURL = "http://tonylin.idv.tw/dokuwiki"; 
	
	@Before
	public void initRestClient(){
		ResteasyJackson2Provider jacksonProvider = new ResteasyJackson2Provider();
		CompositeMessageBodyProvider provider = new CompositeMessageBodyProvider();
		provider.addReader(jacksonProvider);
		provider.addWriter(jacksonProvider);
		
		restClient = ResteasyClientBuilder.newBuilder().register(provider).build();
	}
	
	public Invocation initInvocation(){
		Invocation invocation = restClient.target(testURL).request().buildGet();
		return invocation;
	}
	
	@Test
	public void testReadFailed(){
		Invocation invocation = initInvocation();
		
		Response response = null;
		try {
			response =  invocation.invoke();
			response.readEntity(TestModel.class);
			fail("should be failed	");
		} catch( ProcessingException e ) {
			assertTrue(e.getMessage().startsWith("Unexpected content:"));
		} finally {
			response.close();
		}
	}
}
