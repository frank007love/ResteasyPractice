package org.tonylin.practice.resteasy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

import org.apache.commons.io.IOUtils;

public class DebugReadInterceptor  implements ReaderInterceptor{

	private String content;
	private InputStream inputStream;

	@Override
	public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
		inputStream = context.getInputStream();
		content = IOUtils.toString(inputStream);
		
		context.setInputStream(new ByteArrayInputStream(content.getBytes()));
		return context.proceed();
	} 
	
	public InputStream getInputStream(){
		return inputStream;
	}
	
	public String getContent(){
		return content;
	}
}
