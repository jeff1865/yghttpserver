package org.ygsoft.webserver;
import java.util.*;

public class PLogging {
	public  static int DEBUG = 0;
	public static int INFO = 0xF0;
	private static List<Observer> observers = null;
	
	public static void addObserver(Observer obs){
		if(observers == null)
			observers = new ArrayList<Observer>();
		observers.add(obs);
	}
	
	public static void printv(int level, String msg){
		System.out.println(msg);
			
		if(level == INFO && observers != null){
			for(Observer obs : observers){
				obs.update(null, msg);
			}
		}
	}
	
	
}
