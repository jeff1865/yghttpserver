package org.ygsoft.webserver;
import java.util.*;

public class ResourceDescription {
	
	private String resType = null;	// post, get
	
	private String resName = null;
	private Hashtable<String, String> htResOptions = null;
	
	private String protocolSpec = null;
	
	ResourceDescription(String type){
		this.resType = type;
	}
	
	public String toString(){
		String retStr = "-Protocol :" + this.protocolSpec + "\n";
		retStr += "-Type :" + this.resType + "\n" +
			"-Name :" + this.resName + "\n";
		
		if(this.htResOptions != null){
			for(String strKey : this.htResOptions.keySet())
				retStr += "-Attr : " + strKey + "-" + this.htResOptions.get(strKey) + "\n";
		}

		return retStr;
	}
	
	public String getResName() {
		return resName;
	}

	public void setResName(String resName) {
		this.resName = resName;
	}

	public String getResType() {
		return resType;
	}
	
	public void addParam(String name, String value) {
		if(htResOptions == null)
			this.htResOptions = new Hashtable<String, String>();
		
		this.htResOptions.put(name, value);
	}
	
	public String getParam(String key){
		if(this.htResOptions != null && this.htResOptions.containsKey(key)){
			return this.htResOptions.get(key);
		}
		return null;
	}
	
	public String getProtocolSpec() {
		return protocolSpec;
	}

	public void setProtocolSpec(String protocolSpec) {
		this.protocolSpec = protocolSpec;
	}
	
}
