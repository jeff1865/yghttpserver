package org.ygsoft.webserver.service.gdp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class FileUtil {
	
	public static void main(String...v){
		String strFile = "download/dn1259482739219.dat";
		
		File file = new File(strFile);
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(file, "rw");
			long initSize = raf.length();
			
			String boundary = raf.readLine();
			String line = null;
			while((line = raf.readLine()) != null){
				System.out.println("Header :" + line);
				if(line.length() == 0) break;
			}
			initSize -= (raf.getFilePointer() + boundary.length() + 5);
			
			
			File tFile = new File("cleaned.dat");
			OutputStream fos = new FileOutputStream(tFile);
			
			int c = 0;
			byte[] buf = new byte[4096];
			while((c = raf.read(buf)) > 0){
				if(initSize > c)
					fos.write(buf, 0, c);
				else
					fos.write(buf, 0, (int)initSize);
				
				initSize -= c;
			}
			
			fos.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(raf != null)
				try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
	}
}
