package org.tonylin.practice.resteasy;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamSniffer extends InputStream {

	private InputStream srcInputStream;
	private StringBuilder contentStringBuilder;
	
	public InputStreamSniffer(InputStream inputStream){
		srcInputStream = inputStream;
		contentStringBuilder = new StringBuilder();
	}
	
	public String getContent(){
		return contentStringBuilder.toString();
	}
	
	@Override
	public int read() throws IOException {
		int read_c = srcInputStream.read();
		contentStringBuilder.append((char)read_c);
		return read_c;
	}
	
	private void processReadData(int count, byte[] b){
		for( int i = 0; i < count ; i++ ) {
			contentStringBuilder.append((char)b[i]);
		}
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int read_count =  srcInputStream.read(b, off, len);
		processReadData(read_count, b);
		return read_count;
	}

	@Override
	public int read(byte[] b) throws IOException {
		int read_count = srcInputStream.read(b);
		processReadData(read_count, b);
		return read_count;
	}
	
	@Override
	public int available() throws IOException {
		return srcInputStream.available();
	}
	
	@Override
	public long skip(long n) throws IOException {
		return srcInputStream.skip(n);
	}
	
	@Override
	public boolean markSupported() {
		return srcInputStream.markSupported();
	}
	
	@Override
	public synchronized void reset() throws IOException {
		srcInputStream.markSupported();
	}
	
	@Override
	public synchronized void mark(int readlimit) {
		srcInputStream.mark(readlimit);
	}
	
	@Override
	public String toString() {
		return srcInputStream.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return srcInputStream.equals(obj);
	}
	
	@Override
	public void close() throws IOException {
		srcInputStream.close();
	}
}
