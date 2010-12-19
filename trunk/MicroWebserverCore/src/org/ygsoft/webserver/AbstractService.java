package org.ygsoft.webserver;

import java.io.IOException;
import java.net.Socket;

public abstract class AbstractService implements Runnable{
	
	protected Socket sock = null;
	
	protected XRequest request = null;
	protected XResponse response = null;
	
	private static long pCountID = 0;
	protected long serviceID = 0;
	
	private ClientSocketQueue sockQueue = null;
	
	
	
	public void bindConnection(XRequest req, XResponse res){
		this.request = req;
		this.response = res;
		this.serviceID ++;
	}
	
	public void setSocket(Socket socket, ClientSocketQueue queue){
		this.sock = socket;
		this.serviceID = pCountID++;
		this.sockQueue = queue;
	}
	
	public long getServiceID(){
		return this.serviceID;
	}
	
	public void run(){
		this.service();
		
		// This protocol cannot support 'keep-alive'because of following code
		try {
			this.sock.close();
			
			// Keep-Alive socket
//			if(this.sockQueue == null || !this.isKeepAlive()) {
//				PLogging.printv(PLogging.DEBUG, " ====> This socket is NOT keepAlive, " +
//					"socket would be closed");
//				this.sock.close();
//			} else {
//				PLogging.printv(PLogging.DEBUG, " ====> This socket is keepAlive, " +
//						"socket would be reScheduled");
//				this.sock.setSoTimeout(MicroWebServer.DefaultKeepAliveTime);
//				this.sockQueue.addSocket(this.sock);
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isKeepAlive(){
		String conVal = this.request.getOptionValue("Connection");
		if(conVal != null && conVal.trim().toLowerCase().contains("keep-alive"))
			return true;
		return false;
	}
	
	public abstract AbstractService getNewObject();
	public abstract void service();
	public abstract String getRequestExtension();
}
