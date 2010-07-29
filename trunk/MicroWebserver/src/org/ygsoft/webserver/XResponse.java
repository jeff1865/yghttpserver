package org.ygsoft.webserver;
import java.io.*;

public class XResponse {
	
	private OutputStream os = null;
	private String mimetype = "text/html";
		
	XResponse(OutputStream os){
		this.os = os;
	}
	
	public void setMimeType(String mimeType){
		this.mimetype = mimeType;
	}
	
	public OutputStream getOutputStream(){
		return this.os;
	}
	
	public String getRawHeaderText(){
		return null;
	}
	
	public void reDirect(String url){
		;
	}
	
	//TODO - need to define additionally
}
