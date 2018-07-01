package org.tonylin.practice.httpclient;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClients;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;

public class ClientHttpEngineBuilder {

	private HttpClientConnectionManager connectionManager = null;
	private RequestConfig requestConfig = null;
	
	public ClientHttpEngineBuilder withConnectionManager(HttpClientConnectionManager cm){
		connectionManager = cm;
		return this;
	}
	
	public ClientHttpEngineBuilder withRequestConfig(RequestConfig config){
		requestConfig = config;
		return this;
	}
	
	private static int socketTimeout = 5000;  
	private static int connectTimeout = 5000;  
	private static int connectionRequestTimeout = 10000;  
	
	private RequestConfig getDefaultRequestConfig(){
		return RequestConfig.custom()
				.setConnectionRequestTimeout(connectionRequestTimeout)
				.setSocketTimeout(socketTimeout)
				.setConnectTimeout(connectTimeout)
				.build();
	}
	
	public ClientHttpEngine build(){
		HttpClientConnectionManager cm = this.connectionManager;
		if( cm == null ) {
			HttpClientConnectionManagerBuilder builder = new HttpClientConnectionManagerBuilder();
			cm = builder.withDefaultMaxPerRoute(20).withMaxTotal(200).build();
		}
		
		RequestConfig requestConfig = this.requestConfig;
		if( requestConfig == null ) {
			requestConfig = getDefaultRequestConfig();
		}
		HttpClient realHttpClient = HttpClients.custom().setConnectionManager(cm)
				.setDefaultRequestConfig(requestConfig)
				.build();
		
		return new ApacheHttpClient4Engine(realHttpClient);
	}
}
