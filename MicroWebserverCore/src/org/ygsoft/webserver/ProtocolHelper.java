package org.ygsoft.webserver;

import java.io.File;
import java.net.URLConnection;

public class ProtocolHelper {
	
	public static final String ValueServer = "YG_Http_ServerME";
	public static final String ValueContentTypeText = "text/html";
	public static final String ContentType = "Content-Type";
	public static final String ContentLength = "Content-Length";
	
	public static String getDefaultErrorHeader(String errorCode){
		StringBuffer retStr = new StringBuffer("HTTP/1.0 " + errorCode + " OK\n");
		retStr.append("Connection: close\n");
		//retStr.append("Connection: Keep-Alive\n");
		retStr.append("Server : gonni server\n");
		//retStr.append("Content-Type: " + mimetype + "\n");
		retStr.append("\n");
		
		return retStr.toString();
	}
	
	public static String getDefaultOKheader(String mimetype){
		StringBuffer retStr = new StringBuffer("HTTP/1.0 200 OK\n");
		retStr.append("Connection: close\n");
		//retStr.append("Connection: Keep-Alive\n");
		retStr.append("Server : gonni server\n");
		retStr.append("Content-Type: " + mimetype + "\n");
		retStr.append("\n");
		
		return retStr.toString();
	}
	
	public static XResponse getDefaultXResponse(XResponse res){
		
		res.setAttribute("Connection", "close");
		//res.setAttribute("Connection", "Keep-Alive");
		res.setAttribute("Server", ValueServer);
		res.setAttribute(ContentType, ValueContentTypeText);
		
		return res;
	}
	
	public static String getMimeType(File resFile){
		String mimeType = URLConnection.getFileNameMap().getContentTypeFor(resFile.getAbsolutePath());
		return mimeType;
	}
	
	public static void main(String ... v){
		
		XResponse res = getDefaultXResponse(new XResponse(null));
		System.out.println(res.getRawHeaderText() + ".");
	}
}
