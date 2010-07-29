package org.ygsoft.webserver;
import java.io.*;
import java.net.*;

public class GdpletController implements Runnable, IController{
	private ServiceManager<Gdplet> sMng = null;
	private Socket socket = null;
	
	public static final String resRoot = "HttpRoot/";
	private static final int bufSize = 8192;
	
	GdpletController(ServiceManager<Gdplet> sm){
		this.sMng = sm;
	}
	
	void setSocket(Socket socket){
		this.socket = socket;
	}
	
	public void run() {
		PLogging.printv(PLogging.DEBUG, "[IPC] Active process thread ..");
		
		boolean end = true;
		//do {	
		XRequest req = this.parseRequest();
		XResponse res = null;
		try {
			res = new XResponse(this.socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(req != null) {
			PLogging.printv(PLogging.DEBUG, "Received : \n" + req);
			ResourceDescription rd = req.getParsedMainHeader();
			if(rd != null){
				// set InputStream as a HTTP body
				if(rd.getResType().equals("POST")){
					try {
						req.setInputStream(this.socket.getInputStream());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				// set service controller
				if(rd.getResName().endsWith("gdp")){
					String serName = rd.getResName().substring(0, rd.getResName().indexOf("."));
					PLogging.printv(PLogging.DEBUG, "Requested Service Name :" + serName);
					PLogging.printv(PLogging.DEBUG, "Detailed Info : " + rd);
					
					Gdplet gdplet = this.sMng.getService(serName);
					if(gdplet != null){
						gdplet.service(req, res);
					} else {
						PLogging.printv(PLogging.DEBUG, "Cannot find service .. send 404 ..");
						try {
							res.getOutputStream().write(getDefaultErrorHeader("404").getBytes());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
//					if(rd.getResType().equals("POST")){
//						if(req.getOptionValue("Content-Length") != null)
//							continue;
//					}
					
				} else {	//TODO MIME type set
					PLogging.printv(PLogging.DEBUG, "Find resource .. ");
					
					File resFile = new File(resRoot + rd.getResName());
					if(resFile.exists()){
						//.getDefaultOKheader(MimetypesFileTypeMap.);	// cannot support under JVM1.6
						
						String mimeType = URLConnection.getFileNameMap().getContentTypeFor(resFile.getAbsolutePath());
						PLogging.printv(PLogging.DEBUG, "Send resouce file :" + mimeType + "---" + resFile.getName());
						String header = getDefaultOKheader(mimeType);
						
						try {
							OutputStream os = res.getOutputStream();
							// write header
							os.write(header.getBytes());
							// write body
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
							res.getOutputStream().write(getDefaultErrorHeader("500").getBytes());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				PLogging.printv(PLogging.DEBUG, "Invalid request ..");
			}
		}
		
		// close socket
		try {
			this.socket.close();
			PLogging.printv(PLogging.DEBUG, "Socket closed ..");
		} catch (IOException e) {
			e.printStackTrace();
		}
		//}while(!end) ;
	}
	
	private XRequest parseRequest() {
		XRequest request = null;
		if(socket == null){
			PLogging.printv(PLogging.DEBUG, "[IPC] Invalid state, socket is null ..");
		} else {
			try {
				InputStream is = this.socket.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				
				String line = br.readLine();
				if(line != null)
				{
					line = URLDecoder.decode(line, "UTF-8");
					request = new XRequest(line);
				}
				
				while((line = br.readLine()).length() > 0){
					request.addHeaderOption(line);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				request = null;
			}
			
		}
		return request;
	}
	
	public static String getDefaultErrorHeader(String errorCode){
		StringBuffer retStr = new StringBuffer("HTTP/1.1 " + errorCode + " OK\n");
		retStr.append("Connection: close\n");
		retStr.append("Server : gonni server\n");
		//retStr.append("Content-Type: " + mimetype + "\n");
		retStr.append("\n");
		
		return retStr.toString();
	}
	
	public static String getDefaultOKheader(String mimetype){
		StringBuffer retStr = new StringBuffer("HTTP/1.1 200 OK\n");
		retStr.append("Connection: close\n");
		retStr.append("Server : gonni server\n");
		retStr.append("Content-Type: " + mimetype + "\n");
		retStr.append("\n");
		
		return retStr.toString();
	}
	
	public static void main(String...v){
		String testStr = "GET /yg.html HTTP/1.1";
		//ResourceDescription rd = ProcController.parseMainHeader("GET /yg.html?aa=11&bb=cc&checked HTTP/1.1");
		//System.out.println("-ResouceDescription >" + rd);
	}
}
