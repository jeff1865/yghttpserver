package org.ygsoft.webserver;

import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientSocketQueue implements Runnable {
	
	private LinkedBlockingQueue<Socket> queue = null;
	private volatile Thread thrMain = null;
	private long sockCount = 0;
	private MicroWebServer webServer = null;
	
	public ClientSocketQueue(MicroWebServer server){
		this.queue = new LinkedBlockingQueue<Socket>();
		this.thrMain = new Thread(this);
		this.webServer = server;
	}
	
	public synchronized void addSocket(Socket socket){
		PLogging.printv(PLogging.INFO, "==> Client Socket#" + this.sockCount++ + "added to Queue..");
		this.queue.add(socket);
		PLogging.printv(PLogging.INFO, "==> Socket Queue scheduled :" + this.queue.size());
	}
	
	public void run(){
		Thread thisThread = Thread.currentThread();
		Socket takedSock = null;
		while(this.thrMain == thisThread){
			try {
				PLogging.printv(PLogging.DEBUG, "==> SocketQueue is waiting new client socket..");
				takedSock = this.queue.take();
				
				if(takedSock != null) this.webServer.procClient(takedSock);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
				break ;	//
			}
		}
		PLogging.printv(PLogging.INFO, "==> Socket Queue Thread stopped ..");
	}
	
	public void stopQueue(){
		this.thrMain = null;
		this.queue.add(null);
	}
	
	public void startQueue(){
		if(this.thrMain == null)
			this.thrMain = new Thread(this);
		this.thrMain.start();
		PLogging.printv(PLogging.INFO, "Socket Queue Thread started..");
	}
}
