package org.ygsoft.webserver;

import java.util.Hashtable;

public class ServiceManager<T extends IServicelet> {
	private Hashtable<String, T> th_serRepository = null;
	
	public ServiceManager(){
		this.th_serRepository = new Hashtable<String, T>();
	}
	
	public void addService(T t){
		// old
//		PLogging.printv(PLogging.DEBUG, "Register Service>" + t.getClass().getSimpleName());
//		this.th_serRepository.put(t.getClass().getSimpleName(), t);
		
		// new
		PLogging.printv(PLogging.DEBUG, "Register ServiceLet>" + t.getID());
		this.th_serRepository.put(t.getID(), t);
	}
	
	public T getService(String serId){
		if(this.th_serRepository.containsKey(serId)){
			return this.th_serRepository.get(serId);
		} else return null;
	}
	
}
