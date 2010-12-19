package org.ygsoft.webserver.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;

import org.ygsoft.webserver.AbstractService;
import org.ygsoft.webserver.PLogging;
import org.ygsoft.webserver.ProtocolHelper;
import org.ygsoft.webserver.ResourceDescription;

public class StaticResourceService extends AbstractService{
	
	public static String resRoot = "HttpRoot/";
	private static final int bufSize = 1024 * 8;
	static final String extension = "*";
	
	public StaticResourceService(String rootDir){
		PLogging.printv(PLogging.INFO, "Init Static resource container ..");
		if(rootDir.endsWith("/") || rootDir.endsWith("\\")){
			resRoot = rootDir;
		} else {
			resRoot = rootDir + "/";
		}
	}
	
	@Override
	public String getRequestExtension() {
		// TODO Auto-generated method stub
		return extension;
	}

	@Override
	public void service() {
		ResourceDescription rd = this.request.getParsedMainHeader();
		if(rd != null)
		{
			File resFile = new File(resRoot + rd.getResName());
			if(resFile.exists()){
				PLogging.printv(PLogging.INFO, "[Request Static Res]" + resFile.getName());

				this.response = ProtocolHelper.getDefaultXResponse(this.response);
				//set MimeType
				this.response.setAttribute(ProtocolHelper.ContentType, ProtocolHelper.getMimeType(resFile));
				// set ContentLength
				this.response.setAttribute(ProtocolHelper.ContentLength, resFile.length() + "");
				
				String header = this.response.getRawHeaderText();
				PLogging.printv(PLogging.DEBUG, "Send Header >" + header);
				
				try {
					OutputStream os = this.response.getOutputStream();
					System.out.println("Write Service ID:" + this.getServiceID());
					// write header
					os.write(header.getBytes());
					os.flush();
					// write body
					PLogging.printv(PLogging.DEBUG, "Send Data Body ..");
					InputStream is = new FileInputStream(resFile);
					byte[] buf = new byte[bufSize];
					int c = 0;
					while((c=is.read(buf)) > 0){
						os.write(buf, 0, c);
					}
					os.flush();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			} else {
				PLogging.printv(PLogging.DEBUG, "Cannot find resource .. send 404 ..");
				try {
					this.response.getOutputStream().write(ProtocolHelper.getDefaultErrorHeader("500").getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public AbstractService getNewObject() {
		// TODO Auto-generated method stub
		return new StaticResourceService(resRoot);
	}
	
}
