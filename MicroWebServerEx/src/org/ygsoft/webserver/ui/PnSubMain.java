package org.ygsoft.webserver.ui;
import javax.swing.*;

import org.ygsoft.webserver.IServiceScheduler;
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

import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

public class PnSubMain extends JPanel implements ActionListener, Observer{
	private static final long serialVersionUID = 1L;
	
	private JButton btn_ss = null, btn_file = null;
	private JLabel lb_state = null;
	private JTextField tf_dir = null;
	private JEditorPane ep_main = null;
	private JScrollPane sp_main = null;
	private JProgressBar pb_conn = null;	
	private JLabel lb_conn = null;
	
	private boolean started = false;
	
	private MicroWebServer server = null;
	private Thread thrMonCon = null;
	
	PnSubMain(){
		
		this.btn_ss = new JButton("Start");
		this.btn_ss.addActionListener(this);
		this.btn_file = new JButton("File");
		this.btn_file.addActionListener(this);
		
		this.tf_dir = new JTextField("/root");
		
		this.lb_state = new JLabel("Stopped ..");
		
		this.ep_main = new JEditorPane();
		this.sp_main = new JScrollPane(this.ep_main);
		
		this.pb_conn = new JProgressBar(SwingConstants.VERTICAL, 0, 10);
		this.lb_conn = new JLabel("(0/10)");
		this.initLayout();
		
		
	}
	
	private void initLayout(){
		this.setLayout(new BorderLayout());
		
		JPanel pnTop = new JPanel(new BorderLayout(2, 2));
		{
			JPanel pnLeft = new JPanel(new GridLayout(2, 1, 1, 2));
			pnLeft.add(new JLabel(" Root DIR "));
			pnLeft.add(new JLabel(" State"));
			pnTop.add(pnLeft, BorderLayout.WEST);
			
			JPanel pnCenter = new JPanel(new GridLayout(2, 1, 1, 2));
			pnCenter.add(this.tf_dir);
			pnCenter.add(this.lb_state);
			pnTop.add(pnCenter, BorderLayout.CENTER);
			
			JPanel pnRight = new JPanel(new GridLayout(2, 1, 1, 2));
			pnRight.add(this.btn_file);
			pnRight.add(this.btn_ss);
			
			pnTop.add(pnRight, BorderLayout.EAST);
			
		}
		pnTop.setBorder(BorderFactory.createTitledBorder(" Server Info "));
		
		this.add(pnTop, BorderLayout.NORTH);
		
		this.sp_main.setBorder(BorderFactory.createTitledBorder(" Log "));
		this.add(sp_main, BorderLayout.CENTER);
		
		JPanel pnProg = new JPanel(new BorderLayout());
		pnProg.setBorder(BorderFactory.createTitledBorder("Conn"));
		pnProg.add(pb_conn, BorderLayout.CENTER);
		pnProg.add(this.lb_conn, BorderLayout.SOUTH);
		
		this.add(pnProg, BorderLayout.EAST);
	}
	
	public void closeService(){
		
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		System.out.println("Cmd>" + cmd);
		if(cmd.equals("File")){
			new Thread(){
				public void run(){
					showFileChooser();
				}
			}.start();
		} else if(cmd.equals("Start")){
			
			this.btn_ss.setText("Stop ");
			this.started = true;
			new Thread(){
				public void run(){
					startServer();
				}
			}.start();
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			this.thrMonCon = new Thread(){
				public void run(){
					
					IServiceScheduler ss = server.getScheduler();
					while(true){
						
						int ac = ss.getActiveServiceCount();
						
						if(pb_conn.getValue() != ac){
							pb_conn.setValue(ac);
							lb_conn.setText(ac + "/10");
							pb_conn.updateUI();
							
						}
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};
			this.thrMonCon.start();
			
			this.lb_state.setText("Started..");
			//this.btn_ss.updateUI();
		} else if(cmd.equals("Stop ")){
			
			this.btn_ss.setText("Start");
			this.started = false;
			
			this.thrMonCon.stop();
			this.stopServer();
			
			this.lb_state.setText("Stopped..");
			//this.btn_ss.updateUI();
		}
		this.updateUI();
	}
	
	private void startServer(){
		PLogging.addObserver(this);
		
		ServiceMapper fCont = new ServiceMapper();
		
		ServiceManager<Gdplet> sMng = new ServiceManager<Gdplet>();
		sMng.addService(new Hello());
		sMng.addService(new Exam());
		sMng.addService(new FileTransExam());
		
		fCont.addContainer(new GdpService(sMng));
				
		fCont.addContainer(new StaticResourceService(this.tf_dir.getText()));
		PLogging.printv(PLogging.INFO, fCont.getContainers().size() + " containers registered ..");
		
		this.server = new MicroWebServer(2012, fCont, ServiceScheduler.getDefaultScheduler());
		server.startServer();
		
	}
	
	private void stopServer(){
		if(this.server != null)
			this.server.stopServer();
		this.server = null;
		PLogging.printv(PLogging.INFO, "YG XServer stopped..");
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		try{
		String text = this.ep_main.getText();
		text += arg1.toString() + "\n";
		this.ep_main.setText(text);
		//this.ep_main.updateUI();
		this.updateUI();
		} catch(Exception e){}
	}
	
	
	
	private void showFileChooser(){
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int retVal = chooser.showOpenDialog(this);
		if(retVal == JFileChooser.APPROVE_OPTION){
			String loc = chooser.getSelectedFile().getAbsolutePath();
			this.tf_dir.setText(loc);
			this.tf_dir.updateUI();
		}
	}
	
	private static void createFrame(){
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("YG WebServer ME");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container cp = frame.getContentPane();
		cp.setLayout(new BorderLayout());
		
		cp.add(new PnSubMain(), BorderLayout.CENTER);
		frame.setSize(600, 400);
		frame.setVisible(true);
	}
	
	public static void main(String...v){
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createFrame();
            }
        });
	}
}
