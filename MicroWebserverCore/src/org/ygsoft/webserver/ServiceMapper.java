package org.ygsoft.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.*;

public class ServiceMapper {
	private Hashtable<String, AbstractService> htContainer = null;
	private ClientSocketQueue sockQueue = null;
	
	public ServiceMapper(){
		;
	}
	
	public void setSocketQueue(ClientSocketQueue queue){
		this.sockQueue = queue;
	}
	
	public void addContainer(AbstractService cont){
		if(this.htContainer == null)
			this.htContainer = new Hashtable<String, AbstractService>();
		
		this.htContainer.put(cont.getRequestExtension(), cont);
	}
	
	public Hashtable<String, AbstractService> getContainers(){
		return this.htContainer;
	}
	
	public AbstractService getServiceContainer(Socket socket){
		XRequest req = this.parseRequest(socket);
		if(req != null) {
			PLogging.printv(PLogging.DEBUG, "[Cliet->Server]" + req.toString());
			ResourceDescription rd = req.getParsedMainHeader();
			
			if(rd != null){	// valid request
				PLogging.printv(PLogging.DEBUG, "Req ResourceDescription>" + rd);
				// set InputStream as a HTTP body
				if(rd.getResType().equals("POST")){
					try {
						req.setInputStream(socket.getInputStream());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				// set service controller
				AbstractService mCont = this.getMatchedContainer(rd.getResName());
				if(mCont != null)
				{
					mCont.setSocket(socket, this.sockQueue);
					XResponse res = null;
					try {
						res = ProtocolHelper.getDefaultXResponse(new XResponse(socket.getOutputStream()));
					} catch (IOException e) {
						PLogging.printv(PLogging.DEBUG, "Invalid socket state ..");
						e.printStackTrace();
						return null;
					}
					mCont.bindConnection(req, res);
					return mCont;
				}
			}
		}
		return null;
	}
	
	
	private AbstractService getMatchedContainer(String strRes){
		if(strRes.indexOf(".") > 0)
		{
			String strExt = strRes.substring(strRes.lastIndexOf(".") + 1);
			if(strExt != null && this.htContainer.containsKey(strExt)){
				return this.htContainer.get(strExt).getNewObject();
			}
		}
		return this.htContainer.get("*").getNewObject();
	}
	
	public static void main(String...v){
		String res = "/temp/aa.gdp";
		System.out.println(res.substring(res.lastIndexOf(".")));
	}
	
	private XRequest parseRequest(Socket socket) {
		XRequest request = null;
		if(socket == null){
			PLogging.printv(PLogging.DEBUG, "[IPC] Invalid state, socket is null ..");
		} else {
			try {
				InputStream is = socket.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				//--- TEST ---
//				char[] buf = new char[3];
//				br.read(buf,0,3);
				//--- TEST ---
				String line = br.readLine();
//				System.out.println("------------->" + new String(buf));
//				line = new String(buf) + line;
				if(line != null) {
					//TODO need to change log method
					System.out.println("Request From Client>>>" + line);
					//line = URLDecoder.decode(line, "UTF-8");
					request = new XRequest(line);
				} else {
					PLogging.printv(PLogging.DEBUG, "Invalid Request : Request Line is NULL..");
					throw new Exception("Invalid HTTP Request ..");
				}
				
				while((line = br.readLine()).length() > 0){	// 수정
				//while((line = br.readLine()) != null && line.length() >= 0){	// 수정
					request.addHeaderOption(line);
				}
				
			} catch (Exception e) {
				System.out.println("!!!!PROBLEM!!!!");
				e.printStackTrace();
				request = null;
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return null;
			} 
		}
		return request;
	}
}
