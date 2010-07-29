package org.ygsoft.webserver;
import java.io.*;
import java.net.*;

import org.ygsoft.webserver.gdp.Exam;
import org.ygsoft.webserver.gdp.FileTransExam;
import org.ygsoft.webserver.gdp.Hello;

/**
 * Thread based web server
 */
public class MicroWebServer implements Runnable {
	
	private static int port = 2012;
	static int delayTime = 1000;
	private ServerSocket sSocket = null;
	private volatile boolean started = false;
	GdpletController procCont = null;
		
	MicroWebServer() {
		;
	}
	
	public void initService(GdpletController cont){
		this.procCont = cont;
	}
		
	private void socketInit() { 
		PLogging.printv(PLogging.DEBUG, "Init Micro WebServer ..");
		
		try {
			this.sSocket = new ServerSocket(port);
			
			while(this.started){
				PLogging.printv(PLogging.DEBUG, "Waiting connection ..");
				Socket socket = this.sSocket.accept();
				PLogging.printv(PLogging.DEBUG, "Client socket connected ..");
				this.procClient(socket);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void procClient(Socket sock){
		this.procCont.setSocket(sock);
		new Thread(this.procCont).start();
	}
	
	public void startServer() {
		this.started = true;
		this.socketInit();
	}
		
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String...v){
		MicroWebServer server = new MicroWebServer();
		//ArrayList<Gdplet> lstService = new ArrayList<Gdplet>();
		//lstService.add(new Hello());
		ServiceManager<Gdplet> sMng = new ServiceManager<Gdplet>();
		sMng.addService(new Hello());
		sMng.addService(new Exam());
		sMng.addService(new FileTransExam());
		
		PLogging.printv(PLogging.DEBUG, "Init GDP Serivice ..");
		
		server.initService(new GdpletController(sMng));
		server.startServer();
		PLogging.printv(PLogging.DEBUG, "GDP Server closed ..");
	}
}
