package org.ygsoft.webserver.ui;

import org.ygsoft.webserver.MicroWebServer;
import org.ygsoft.webserver.PLogging;
import org.ygsoft.webserver.ServiceManager;
import org.ygsoft.webserver.ServiceMapper;
import org.ygsoft.webserver.ServiceScheduler;
import org.ygsoft.webserver.service.GdpService;
import org.ygsoft.webserver.service.Gdplet;
import org.ygsoft.webserver.service.StaticResourceService;
import org.ygsoft.webserver.service.gdp.Exam;
import org.ygsoft.webserver.service.gdp.FileTransExam;
import org.ygsoft.webserver.service.gdp.Hello;

public class ConsoleMain {
	public static void main(String...v){
		// V2
		ServiceMapper fCont = new ServiceMapper();
		
		ServiceManager<Gdplet> sMng = new ServiceManager<Gdplet>();
		sMng.addService(new Hello());
		sMng.addService(new Exam());
		sMng.addService(new FileTransExam());
		
		fCont.addContainer(new GdpService(sMng));
				
		fCont.addContainer(new StaticResourceService("HttpRoot/"));
		PLogging.printv(PLogging.DEBUG, fCont.getContainers().size() + " containers registered ..");
		
		MicroWebServer server = new MicroWebServer(2012, fCont, ServiceScheduler.getDefaultScheduler());
		server.startServer();
		
		PLogging.printv(PLogging.DEBUG, "YG XServer stopped..");
	}
}
