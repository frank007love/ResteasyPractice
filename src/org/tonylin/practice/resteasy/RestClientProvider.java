package org.tonylin.practice.resteasy;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder.HostnameVerificationPolicy.ANY;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import org.jboss.resteasy.plugins.providers.multipart.MimeMultipartProvider;
import org.tonylin.practice.httpclient.ClientHttpEngineBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class RestClientProvider {
	
	// http://www.baeldung.com/resteasy-client-tutorial
	// https://stackoverflow.com/questions/19517538/ignoring-ssl-certificate-in-apache-httpclient-4-3

	private volatile static Client httpClient;

	public Client getClient() {
		if (httpClient != null) {
			return httpClient;
		}
		
		initClient();
		return httpClient;
	}
	
	private void initClient(){
		synchronized (RestClientProvider.class) {
			if( httpClient != null )
				return;
			
			ResteasyJackson2Provider jackson2Provider = initResteasyJackson2Provider();
			ClientHttpEngine httpEngine = new ClientHttpEngineBuilder().build();
	
			ResteasyClientBuilder clientBuilder = ((ResteasyClientBuilder) ResteasyClientBuilder.newBuilder())
					.httpEngine(httpEngine).register(new SnifferReadInterceptor()).register(jackson2Provider).register(new MimeMultipartProvider()).hostnameVerification(ANY);
			
			httpClient = clientBuilder.build();
		}
	}
	
	private ResteasyJackson2Provider initResteasyJackson2Provider(){
		ResteasyJackson2Provider jackson2Provider = new ResteasyJackson2Provider();
		ObjectMapper mapper = new ObjectMapper()
				.registerModule(new JavaTimeModule())
				.disable(FAIL_ON_UNKNOWN_PROPERTIES)
				.enable(FAIL_ON_NULL_FOR_PRIMITIVES);
		jackson2Provider.setMapper(mapper);
		return jackson2Provider;
	}
}
