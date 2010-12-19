package org.ygsoft.webserver;

import java.net.Socket;
import java.util.concurrent.*;

// ThreadPool and Queue
public class ServiceScheduler extends ThreadPoolExecutor implements IServiceScheduler{
			
	public ServiceScheduler(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}
	
	public static ServiceScheduler getDefaultScheduler(){
		LinkedBlockingQueue<Runnable> sq = new LinkedBlockingQueue<Runnable>();
		ServiceScheduler ss = new ServiceScheduler(10, 20, 3000, TimeUnit.MILLISECONDS, sq);
		PLogging.printv(PLogging.DEBUG, "Scheduling queue is initialized ..");
		return ss;
	}
		
	@Override
	public void executeService(AbstractService service) {
		PLogging.printv(PLogging.DEBUG, "[Request] Service(id:" + service.getServiceID() + ") was scheduled ..");
		super.execute(service);
		
		PLogging.printv(PLogging.DEBUG, "Active Con :" + this.getActiveCount() 
				+ " Scheduled :" + this.getScheduledServiceCount() + " Completed :" + this.getCompletedServiceCount());
	}
		
	@Override
	public void stopScheduler() {
		super.shutdown();		
	}
	
	@Override
	public int getActiveServiceCount() {
		return super.getActiveCount();
	}

	@Override
	public long getCompletedServiceCount() {
		return super.getCompletedTaskCount();
	}

	@Override
	public long getScheduledServiceCount() {
		return super.getTaskCount();
	}

	

//	public static void main(String...v){
//		LinkedBlockingQueue<Runnable> sq = new LinkedBlockingQueue<Runnable>();
//		ServiceScheduler ss = new ServiceScheduler(10, 20, 3000, TimeUnit.MILLISECONDS, sq);
//		
//		class DelayedProc implements Runnable{
//			int c;
//			
//			public DelayedProc(){
//				c = count ++;
//			}
//			
//			@Override
//			public void run() {
//				System.out.println("Proc:" + c + " excuted..");
//				try {
//					Thread.sleep(3000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				System.out.println("Proc:" + c + " terminated..");
//			}
//			
//		}
//		
//		for(int i=0;i<20;i++){
//			System.out.println("add proc" + i);
//			Runnable cmd = new DelayedProc();
//			ss.execute(cmd);
//		}
//		
//		while(ss.getActiveCount() != 0){
//			System.out.println("--> ss:ActiveCount:" + ss.getActiveCount());
//			System.out.println("--> ss:CompletedTask:" + ss.getCompletedTaskCount());
//			System.out.println("--> ss:TaskCount:" + ss.getTaskCount());
//			
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//		
//		ss.shutdown();
//		System.out.println("ss shutted down .. ");
//	}

}
