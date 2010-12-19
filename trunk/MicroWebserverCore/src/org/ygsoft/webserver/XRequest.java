package org.ygsoft.webserver;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

public class XRequest {
	
	private String strMain = null;
//TODO need to change from Vector to Hashtable in order to improve speed
	private Vector<String> strOptions = null;
	private InputStream isBody = null;
	private ResourceDescription resDesc = null;
		
	public XRequest(String strMain){
		this.strMain = strMain;
	}
	
	public void setInputStream(InputStream is){
		this.isBody = is;
	}
	
	public String getStrMain() {
		return strMain;
	}

	public String getOptionValue(String key){
		PLogging.printv(PLogging.DEBUG, "Length ->" + this.strOptions.size());
		
		try{
			for(String strOpt : this.strOptions){
				if(strOpt.startsWith(key)){
					StringTokenizer stkz = new StringTokenizer(strOpt, ":");
					stkz.nextToken();	// key
					return stkz.nextToken().trim();
				}
			}
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	public Vector<String> getStrOptions() {
		return strOptions;
	}
	
	public void addHeaderOption(String optStr) {
		if(this.strOptions == null){
			this.strOptions = new Vector<String>();
		}
		this.strOptions.add(optStr);
	}
	
	public void getParsedOptions(){
		return ;
	}
	
	public ResourceDescription getParsedMainHeader(){
		if(this.resDesc != null) return this.resDesc;
		
		// parse method part
		String method = null;
		StringTokenizer stkz = new StringTokenizer(this.strMain, " ");
		if(stkz.hasMoreTokens()) method = stkz.nextToken();
		else return null;
				
		this.resDesc = new ResourceDescription(method);
		// parse resouce part
		String res = null;
		if(stkz.hasMoreTokens()){
			try {
				res = URLDecoder.decode(stkz.nextToken(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			if(res.indexOf("?") > 0){
				this.resDesc.setResName(res.substring(1, res.indexOf("?")));
				
				res = res.substring(res.indexOf("?")+1);
				StringTokenizer st = new StringTokenizer(res, "&");
				while(st.hasMoreTokens()){
					String opt = st.nextToken();	//?a=b&
					if(opt.indexOf("=") > 0){
						this.resDesc.addParam(opt.substring(0, opt.indexOf("=")), opt.substring(opt.indexOf("=")+1));
					} else {
						this.resDesc.addParam(opt, "~");
					}
				}
			} else {
				if(res.startsWith("/")) res = res.substring(1);
				this.resDesc.setResName(res);
			}
		} else return null;
		
		// parse protocol spec part
		String spec = null;
		if(stkz.hasMoreTokens()){
			spec = stkz.nextToken();
			this.resDesc.setProtocolSpec(spec);
		} else return null;	
				
		return this.resDesc;
	}
	
	public InputStream getInputStream(){
		return this.isBody;
	}
	
	public String toString(){
		String retStr = strMain + "\n";
		if(this.strOptions != null){
			for(String str : strOptions){
				retStr += str + "\n -";
			}
		}
		return retStr;
	}
}
