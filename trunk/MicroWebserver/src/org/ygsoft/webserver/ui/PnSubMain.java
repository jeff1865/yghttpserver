package org.ygsoft.webserver.ui;
import javax.swing.*;
import javax.swing.border.BevelBorder;

import java.awt.*;


public class PnSubMain extends JPanel {
	
	private JButton btn_ss = null, btn_file = null;
	private JLabel lb_state = null;
	private JTextField tf_dir = null;
	private JEditorPane ep_main = null;
	private JScrollPane sp_main = null;
	
	PnSubMain(){
		
		this.btn_ss = new JButton("Start");
		this.btn_file = new JButton("File");
		
		this.tf_dir = new JTextField("/root");
		
		this.lb_state = new JLabel("Stopped ..");
		
		this.ep_main = new JEditorPane();
		this.sp_main = new JScrollPane(this.ep_main);
		
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
	}
	
	public static void main(String...v){
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("YG WebServer ME");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container cp = frame.getContentPane();
		cp.setLayout(new BorderLayout());
		
		cp.add(new PnSubMain(), BorderLayout.CENTER);
		frame.setSize(600, 400);
		frame.setVisible(true);
	}
}
