package org.ygsoft.webserver;

public interface IServiceScheduler {
	public void executeService(AbstractService service);
	public int getActiveServiceCount();
	public long getCompletedServiceCount();
	public long getScheduledServiceCount();
	public void stopScheduler();
}
