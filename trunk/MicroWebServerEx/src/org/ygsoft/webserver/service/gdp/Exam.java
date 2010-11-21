package org.ygsoft.webserver.service.gdp;

import java.io.IOException;
import java.util.Date;

import org.ygsoft.webserver.ProtocolHelper;
import org.ygsoft.webserver.XRequest;
import org.ygsoft.webserver.XResponse;
import org.ygsoft.webserver.service.Gdplet;

public class Exam extends Gdplet {
	
	@Override
	public void service(XRequest req, XResponse res) {
		try {
			res.setAttribute(ProtocolHelper.ContentType, ProtocolHelper.ValueContentTypeText);
			
			// write HttpHeader
			res.writeStringToHeader();	
			
			String contBody = "<html><head><title>Exam Title</title></head><body bgcolor=#" 
				+ (System.currentTimeMillis()+"").substring((System.currentTimeMillis()+"").length()-6)
				+">Hello Exam .. "
				+ new Date(System.currentTimeMillis()) + "</body></html>";
			
			// write HttpBody
			res.writeStringToBody(contBody);	
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
}
