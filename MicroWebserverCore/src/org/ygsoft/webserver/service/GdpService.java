package org.ygsoft.webserver.service;
import java.io.*;
import java.net.*;

import org.ygsoft.webserver.AbstractService;
import org.ygsoft.webserver.PLogging;
import org.ygsoft.webserver.ProtocolHelper;
import org.ygsoft.webserver.ResourceDescription;
import org.ygsoft.webserver.ServiceManager;

public class GdpService extends AbstractService {
	
	static final String extension = "gdp";
	private ServiceManager<Gdplet> sMng = null;
	
	public GdpService(ServiceManager<Gdplet> sm){
		this.sMng = sm;
	}
	
	@Override
	public String getRequestExtension() {
		return extension;
	}

	@Override
	public void service() {
		
		if(this.request != null) {
			PLogging.printv(PLogging.DEBUG, "Received : \n" + this.request);
			ResourceDescription rd = this.request.getParsedMainHeader();
			if(rd != null){
				// set InputStream as a HTTP body
				if(rd.getResType().equals("POST")){
					try {
						this.request.setInputStream(super.sock.getInputStream());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				// set service controller
				if(rd.getResName().endsWith("gdp")){
					String serName = rd.getResName().substring(0, rd.getResName().indexOf("."));
					PLogging.printv(PLogging.INFO, "Requested Service Name :" + serName);
					PLogging.printv(PLogging.DEBUG, "Detailed Info : " + rd);
					
					Gdplet gdplet = this.sMng.getService(serName);
					if(gdplet != null){
						gdplet.service(this.request, this.response);
					} else {
						PLogging.printv(PLogging.DEBUG, "Cannot find service .. send 404 ..");
						try {
							this.response.getOutputStream().write(ProtocolHelper.getDefaultErrorHeader("404").getBytes());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
				} 
//				else {	//TODO MIME type set
//					PLogging.printv(PLogging.DEBUG, "Find resource .. ");
//					
//					File resFile = new File(resRoot + rd.getResName());
//					if(resFile.exists()){
//						//.getDefaultOKheader(MimetypesFileTypeMap.);	// cannot support under JVM1.6
//						
//						String mimeType = URLConnection.getFileNameMap().getContentTypeFor(resFile.getAbsolutePath());
//						PLogging.printv(PLogging.DEBUG, "Send resouce file :" + mimeType + "---" + resFile.getName());
//						String header = ProtocolHelper.getDefaultOKheader(mimeType);
//						
//						try {
//							OutputStream os = this.response.getOutputStream();
//							// write header
//							os.write(header.getBytes());
//							// write body
//							InputStream is = new FileInputStream(resFile);
//							byte[] buf = new byte[bufSize];
//							int c = 0;
//							while((c=is.read(buf)) > 0){
//								os.write(buf, 0, c);
//							}
//							os.flush();
//							
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//						
//					} else {
//						PLogging.printv(PLogging.DEBUG, "Cannot find resource .. send 404 ..");
//						try {
//							this.response.getOutputStream().write(ProtocolHelper.getDefaultErrorHeader("500").getBytes());
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//				}
			} else {
				PLogging.printv(PLogging.DEBUG, "Invalid request ..");
			}
		}
	}

	@Override
	public AbstractService getNewObject() {
		// TODO Auto-generated method stub
		return new GdpService(this.sMng);
	}
}
