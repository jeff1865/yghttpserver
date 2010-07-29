package org.ygsoft.webserver;

public abstract class Gdplet {

	protected Gdplet(){}
	
	public String getID() {
		return this.getClass().getSimpleName();
	}
		
	protected abstract void service(XRequest req, XResponse res);
}
