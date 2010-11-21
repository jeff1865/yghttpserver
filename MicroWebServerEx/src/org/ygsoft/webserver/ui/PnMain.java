package org.ygsoft.webserver.ui;

import java.awt.*;
import java.awt.event.ContainerListener;

import javax.swing.*;

public class PnMain extends JPanel{
		
	private static final long serialVersionUID = 1L;
	private JTabbedPane tPane = null;
	
	private PnSubMain subMain = null;
	
	public PnMain(){
		this.tPane = new JTabbedPane();
		
		this.subMain = new PnSubMain();
		
		this.initLayout();
	}
	
	private void initLayout(){
		this.setLayout(new BorderLayout());
		
		this.tPane.add("Main", this.subMain);
		this.tPane.add("Service", new JLabel("TBD", SwingConstants.CENTER));
		this.add(this.tPane, BorderLayout.CENTER);
	}
	
	private static void createAndShowGUI(){
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("YG WebServer ME");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container cp = frame.getContentPane();
		cp.setLayout(new BorderLayout());
		
		cp.add(new PnMain(), BorderLayout.CENTER);
		frame.setSize(600, 400);
		frame.setVisible(true);;
	}
	
	public static void main(String ...v){
		  /* Use an appropriate Look and Feel */
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        //Schedule a job for the event-dispatching thread:
        //adding TrayIcon.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}
}
