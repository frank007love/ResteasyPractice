package org.tonylin.practice.resteasy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;

@Provider
@Consumes(MediaType.WILDCARD)
@Produces(MediaType.WILDCARD)
public class CompositeMessageBodyProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object> {

	private List<MessageBodyReader<Object>> messageBodyReaders = new ArrayList<>();
	private List<MessageBodyWriter<Object>> messageBodyWriters = new ArrayList<>();
	
	public void addWriter(MessageBodyWriter<Object> writer){
		messageBodyWriters.add(writer);
	}
	
	public void addReader(MessageBodyReader<Object> reader){
		messageBodyReaders.add(reader);
	}
	
	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return true;
	}
	
	private Optional<MessageBodyReader<Object>> findReader(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType){
		return messageBodyReaders.stream().filter(writter->writter.isReadable(type, genericType, annotations, mediaType)).findAny();
	}

	@Override
	public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		
		Optional<MessageBodyReader<Object>> reader = findReader(type, genericType, annotations, mediaType);
		if(reader.isPresent()){
			return reader.get().readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);
		}
		
		String output = IOUtils.toString(entityStream);
		throw new ProcessingException("Unexpected content: " + output);
	}

	private Optional<MessageBodyWriter<Object>> findWriter(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType){
		return messageBodyWriters.stream().filter(writter->writter.isWriteable(type, genericType, annotations, mediaType)).findAny();
	}
	
	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return findWriter(type, genericType, annotations, mediaType).isPresent();
	}

	@Override
	public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return findWriter(type, genericType, annotations, mediaType).get().getSize(t, type, genericType, annotations, mediaType);
	}

	@Override
	public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException {
		findWriter(type, genericType, annotations, mediaType).get().writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
	}

}
