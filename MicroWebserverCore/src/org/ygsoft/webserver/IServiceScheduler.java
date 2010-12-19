package org.ygsoft.webserver;

import java.net.Socket;

public interface IServiceScheduler {
	public void executeService(AbstractService service);
	//public void executeService(Socket socket);
	public int getActiveServiceCount();
	public long getCompletedServiceCount();
	public long getScheduledServiceCount();
	public void stopScheduler();
}
