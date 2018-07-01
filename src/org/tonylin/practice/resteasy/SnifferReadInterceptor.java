package org.tonylin.practice.resteasy;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

@Provider
public class SnifferReadInterceptor implements ReaderInterceptor {

	private InputStreamSniffer inputStreamSniffer = null;
	
	@Override
	public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
		InputStream inputStream = context.getInputStream();
		
		inputStreamSniffer = new InputStreamSniffer(inputStream);
		context.setInputStream(inputStreamSniffer);
		
		return context.proceed();
	}

	public String getReadContent(){
		return inputStreamSniffer.getContent();
	}
}
