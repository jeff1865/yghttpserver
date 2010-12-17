package org.ygsoft.webserver.service;

import org.ygsoft.webserver.IServicelet;
import org.ygsoft.webserver.XRequest;
import org.ygsoft.webserver.XResponse;

public abstract class Gdplet implements IServicelet {

	protected Gdplet(){}
	
	@Override
	public String getID() {
		return this.getClass().getSimpleName();
	}
	
	protected abstract void service(XRequest req, XResponse res);
}
