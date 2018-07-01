package org.tonylin.practice.httpclient;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;

public class HttpClientConnectionManagerBuilder {

	private int maxTotal = 1;
	private int defaultMaxPerRoute = 1;

	public HttpClientConnectionManagerBuilder withMaxTotal(int value) {
		this.maxTotal = value;
		return this;
	}

	public HttpClientConnectionManagerBuilder withDefaultMaxPerRoute(int value) {
		this.defaultMaxPerRoute = value;
		return this;
	}

	public HttpClientConnectionManager build() {
		try {
			SSLContextBuilder builder = new SSLContextBuilder();
			builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
			
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(),
					NoopHostnameVerifier.INSTANCE);

			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslsf).build();

			PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			cm.setMaxTotal(maxTotal);
			cm.setDefaultMaxPerRoute(defaultMaxPerRoute);

			return cm;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
