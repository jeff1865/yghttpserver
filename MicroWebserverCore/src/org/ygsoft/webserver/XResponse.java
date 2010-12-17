package org.ygsoft.webserver;
import java.io.*;
import java.util.*;

public class XResponse {
	
	private OutputStream os = null;
	
	private String httpResCode = "200 OK";
	private Hashtable<String, String> htAttrs = null;
	
	public XResponse(OutputStream os){
		this.os = os;
	}
	
	public void writeStringToBody(String body) throws IOException{
		this.os.write(body.getBytes("utf-8"), 0, body.getBytes("utf-8").length);
		this.os.flush();
	}
	
	public void writeStringToHeader() throws IOException{
		String header = this.getRawHeaderText();
		this.os.write(header.getBytes(), 0, header.length());
	}
	
	public void setHttpResCode(String resCode){
		this.httpResCode = resCode;
	}
	
	public void setAttribute(String key, String value){
		if(this.htAttrs == null)
			this.htAttrs = new Hashtable<String, String>();
		
		if(key != null && value != null)
			this.htAttrs.put(key, value);
	}
		
	public OutputStream getOutputStream(){
		return this.os;
	}
	
	public String getRawHeaderText(){
		StringBuffer retStr = new StringBuffer("HTTP/1.0 " + this.httpResCode + "\n");
		if(this.htAttrs != null){
			Iterator<String> keys = this.htAttrs.keySet().iterator();
			while(keys.hasNext()){
				String key = keys.next();
				retStr.append(key + " : " + this.htAttrs.get(key) + "\n");
			}
		}
		retStr.append("\n");
		
		return retStr.toString();
	}
	
	public void reDirect(String url){
		;
	}
	
	//TODO - need to define additionally
}
