package org.ygsoft.webserver.service.gdp;

import java.io.*;

import org.ygsoft.webserver.*;
import org.ygsoft.webserver.service.GdpService;
import org.ygsoft.webserver.service.Gdplet;


/**
 * process POST method to receive binary data
 * @author gonni21c@gmail.com
 *
 */
public class FileTransExam extends Gdplet {
	
	private String storeRoot = "download/";
	public static final int bufSize = 8192;
	
	public FileTransExam(){
		this.initFileLoc();		
	}
	
	public FileTransExam(String downloadDir){
		this.storeRoot = downloadDir;
		this.initFileLoc();
	}
	
	private void initFileLoc(){
		File file = new File(storeRoot);
		if(!file.exists())
			file.mkdirs();
	}
	
	private static void copyCleanFile(String strFile, String targetFile){
				
		File file = new File(strFile);
		RandomAccessFile raf = null;
		OutputStream fos = null;
		
		try {
			raf = new RandomAccessFile(file, "rw");
			long initSize = raf.length();
			
			String boundary = raf.readLine();
			String line = null;
			while((line = raf.readLine()) != null){
				System.out.println("Header :" + line);
				if(line.length() == 0) break;
			}
			initSize -= (raf.getFilePointer() + boundary.length() + 6);
			
			
			File tFile = new File(targetFile);
			fos = new FileOutputStream(tFile);
			
			int c = 0;
			byte[] buf = new byte[4096];
			while((c = raf.read(buf)) > 0){
				if(initSize > c)
					fos.write(buf, 0, c);
				else
					fos.write(buf, 0, (int)initSize);
				
				initSize -= c;
			}
			
			PLogging.printv(PLogging.DEBUG, "Transfer completed ..");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(raf != null)
				try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if(fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	@Override
	protected void service(XRequest req, XResponse res) {
		//System.out.println("Request from Client --- \n" + req);
		String method = req.getParsedMainHeader().getResType();
		
		if(method.equals("GET")){
			
			OutputStream os = res.getOutputStream();
			try {
				os.write(ProtocolHelper.getDefaultOKheader("text/html").getBytes());
				
				String strBody = "<html><head><title>Exam :: FileTrans</title></head>" +
						"<body>FileTrans :" + System.currentTimeMillis() + "<br>" +
								"<form action='FileTransExam.gdp' method='POST' enctype='multipart/form-data'>" +
								"File to be Uploaded : <input type='file' name='UserUL'></input>" +
								"<input type='submit' value='send'></input></form>" +
								"</body></html>";
				
				os.write(strBody.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else if(method.equals("POST")){
			InputStream bodyStream = req.getInputStream();
			if(bodyStream != null){
				File dnDir = new File(storeRoot);
				if(!dnDir.exists()){
					PLogging.printv(PLogging.DEBUG, "make download Dir ..");
					dnDir.mkdirs();
				}
				
				try {
					final File dnFile = new File(storeRoot + "dn" + System.currentTimeMillis() + ".dat");
					OutputStream os = new FileOutputStream(dnFile);
					
					byte[] buf = new byte[bufSize];
					int c = 0;
					
					String strLeng = req.getOptionValue("Content-Length");
					if(strLeng != null)
					{
						long rdLength = Long.parseLong(strLeng);
						
						PLogging.printv(PLogging.DEBUG, "Requested context length :" + rdLength);
						int x = 0, mb = 0;
						
						while((c = bodyStream.read(buf)) > 0){
							if(((x++) % 128) == 0) System.out.println("recv size :" + (++mb) + "mb ..");
							
							os.write(buf, 0, c);
							if((rdLength - c) > 0) rdLength -= c;
							else break;	
						}
						
					} else {
						while((c = bodyStream.read(buf)) > 0){
							os.write(buf, 0, c);
						}
					}
					
					new Thread(){
						public void run(){
							copyCleanFile(dnFile.getAbsolutePath(), new File("download/uf" + System.currentTimeMillis() + ".dat")
								.getAbsolutePath());
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							//dnFile.delete();
							PLogging.printv(PLogging.DEBUG, "File copy completed ..");
						}
					}.start();
					
					PLogging.printv(PLogging.DEBUG, "Received data : " + dnFile.length() + "bytes ..");
					
					String header = ProtocolHelper.getDefaultOKheader("text/html");					
					res.getOutputStream().write(header.getBytes());
					
					String body = "<html><body><h1>File Upload Success!!</ht></body></html>";
					res.getOutputStream().write(body.getBytes());
					res.getOutputStream().flush();
					
				} catch (IOException e) {
					e.printStackTrace();
					try {
						res.getOutputStream().write(ProtocolHelper.getDefaultErrorHeader("404").getBytes());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					return ;
				}
				
			}
		}
	}
}
