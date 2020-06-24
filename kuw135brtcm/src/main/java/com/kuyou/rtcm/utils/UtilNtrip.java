package com.kuyou.rtcm.utils;
import android.util.Base64;
import android.util.Log;

public class UtilNtrip {
	 public static  byte[] CreateHttpRequsets(String mountPoint, String userId, String password) {        
		 String msg = "GET /" + mountPoint + " HTTP/1.0\r\n";  
		 //String msg = "SOURCE " + password + " /" + mountPoint + "\r\n";    
		 msg += "User-Agent: NTRIP GNSSInternetRadio/1.4.10\r\n";
	     //msg += "User-Agent: NTRIP NtripServerCMD/1.0\r\n";
	     msg += "Accept: */*\r\n";        
	     msg += "Connection: Keep-Alive\r\n";        
	     String tempString = userId + ":" + password;        
	     byte[] buf = tempString.getBytes();
	     String code = Base64.encodeToString(buf, 2);        
	     msg += "Authorization: Basic " + code + "\r\n\r\n"; 
        Log.d("zheng ", "请求差分"+msg.toString());
	     byte[] bytes = msg.getBytes();
	     return bytes;
	 }    
	 public static byte[]  SourceList (String userId,String password){
		 String msg = "GET/HTTP/1.0\r\n";
		 msg += "User-Agent: NTRIP GNSSInternetRadio/1.4.10\r\n";
		 msg +="Accept: */*\r\n";
		 msg+="Connection: close\r\n";	
		 String tempString = userId + ":" + password;
		 byte[] buf = tempString.getBytes();
		 String code = Base64.encodeToString(buf, 2);
		 msg += "Authorization: Basic " + code + "\r\n\r\n";
	 Log.d("zheng ", "请求节点"+msg);
		 byte[] bytes = msg.getBytes();
		return bytes;
		 
	 }
//	 public static byte[]  SourceList2(String userId,String password){
//		 String msg = "GET/HTTP/1.1\r\n";
//		 msg +="Host: ntrip.example.com\r\n";
//		 msg +="Ntrip-Version: Ntrip/2.0\r\n";
//		 msg +="User-Agent: NTRIP ExampleClient/2.0\r\n";
//		 String tempString = userId + ":" + password;
//		 byte[] buf = tempString.getBytes();
//		 String code = Base64.encodeToString(buf, 2); 
//		 msg += "Authorization: Basic " + code + "\r\n"; 
//		 msg+="Connection: close\r\n";
//		 msg+="\r\n";
//		 Log.d("zheng ", "请求节点"+msg);
//		 byte[] bytes = msg.getBytes();
//		return bytes;
//		 
//	 }
//	 public static  byte[] CreateHttpRequsets2(String mountPoint, String userId, String password) {    
//	     String msg = "GET /" + mountPoint + " HTTP/1.1\r\n";        
//	     msg += "Host: ntrip.example.com\r\n";        
//	     msg += "Ntrip-Version: Ntrip/2.0\r\n";        
//	     msg +=  "User-Agent: NTRIP ExampleClient/2.0\r\n";     
//	     String tempString = userId + ":" + password;        
//	     byte[] buf = tempString.getBytes();
//	     String code = Base64.encodeToString(buf, 2);        
//	     msg += "Authorization: Basic " + code + "\r\n";        
//	     msg += "Connection: close\r\n";        
//	     msg +="\r\n";
//	     Log.d("zheng ", "请求差分"+msg);
//	     byte[] bytes = msg.getBytes();
//	     return bytes;    
//	 }    
	 public static final String bytesToHexString(byte[] bArray) {        
	     StringBuffer sb = new StringBuffer(bArray.length);        
	     String sTemp;        
	     for (int i = 0; i < bArray.length; i++) {            
	         sTemp = Integer.toHexString(0xFF & bArray[i]);            
	             if (sTemp.length() < 2)                
	                 sb.append(0);            
	                 sb.append(sTemp.toUpperCase());        
	             }        
	     return sb.toString();    
	 }
	}
