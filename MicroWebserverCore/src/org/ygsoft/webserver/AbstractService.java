package org.ygsoft.webserver;

import java.io.IOException;
import java.net.Socket;

public abstract class AbstractService implements Runnable{
	
	protected Socket sock = null;
	
	protected XRequest request = null;
	protected XResponse response = null;
	
	private static long pCountID = 0;
	protected long serviceID = 0;
	
	public void bindConnection(XRequest req, XResponse res){
		this.request = req;
		this.response = res;
	}
	
	public void setSocket(Socket socket){
		this.sock = socket;
		this.serviceID = pCountID++;
	}
	
	public long getServiceID(){
		return this.serviceID;
	}
	
	public void run(){
		this.service();
		// This protocol cannot support 'keep-alive'because of following code
		try {
			this.sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public abstract void service();
	public abstract String getRequestExtension();
}
