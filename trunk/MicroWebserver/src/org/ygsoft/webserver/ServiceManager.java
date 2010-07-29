package org.ygsoft.webserver;

import java.util.Hashtable;

public class ServiceManager<T> {
	private Hashtable<String, T> th_serRepository = null;
	
	public ServiceManager(){
		this.th_serRepository = new Hashtable<String, T>();
	}
	
	public void addService(T t){
		PLogging.printv(PLogging.DEBUG, "Register Service>" + t.getClass().getSimpleName());
		this.th_serRepository.put(t.getClass().getSimpleName(), t);
	}
	
	public T getService(String serId){
		if(this.th_serRepository.containsKey(serId)){
			return this.th_serRepository.get(serId);
		} else return null;
	}
	
}
