package org.ygsoft.webserver;
import java.io.*;
import java.net.*;
import java.util.Observable;

/**
 * Thread based web server
 */
public class MicroWebServer extends Observable {
		
	private static int PORT = 2012;
	public static int DefaultKeepAliveTime = 15 * 1000;
	
	private volatile boolean started = false;
	private ServerSocket sSocket = null;
	
	private ServiceMapper controller = null;
	private IServiceScheduler ss = null;
	
	private long connCount = 0;
	private ClientSocketQueue sockQueue = null;
	
	public MicroWebServer(int port, ServiceMapper controller, IServiceScheduler sScheduler) {
		PORT = port;
		this.controller = controller;
		this.ss = sScheduler;
	}
		
	public IServiceScheduler getScheduler(){
		return this.ss;
	}
	
	private void serverSocketInit() { 
		PLogging.printv(PLogging.DEBUG, "Init Micro WebServer ..");
		
		try {
			this.sSocket = new ServerSocket(PORT);
			
			while(this.started){
				PLogging.printv(PLogging.DEBUG, "Waiting connection ..");
				Socket socket = this.sSocket.accept();
				PLogging.printv(PLogging.DEBUG, "Client socket connected : " + this.connCount++);
				this.sockQueue.addSocket(socket);
				//this.procClient(socket);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startServer() {
		this.sockQueue = new ClientSocketQueue(this);
		this.sockQueue.startQueue();
		this.controller.setSocketQueue(this.sockQueue);
		
		this.started = true;
		this.serverSocketInit();
	}
	
	public void stopServer() {
		this.started = false;
		
		ss.stopScheduler();
		PLogging.printv(PLogging.INFO, "ThreadPool was shutdown ..");
		
		try {
			this.sSocket.close();
			PLogging.printv(PLogging.INFO, "Server socket closed ..");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void procClient(final Socket sock){
						
		new Thread() { public void run(){
			try {
				sock.setSoTimeout(DefaultKeepAliveTime);
			} catch (SocketException e) {
				e.printStackTrace();
				return ;
			}
			// need to be controlled by front Controller
			//TODO need to be processed by Thread 
			AbstractService srvCont = controller.getServiceContainer(sock);
			if(srvCont == null){
				PLogging.printv(PLogging.INFO, "Invalid Request !!");
				return ;
			}
	//		// processing connection by creating new Thread
	//		new Thread(srvCont).start();
			
			// processing connection by using ThreadPool
			ss.executeService(srvCont);
		}}.start();
	}
}
