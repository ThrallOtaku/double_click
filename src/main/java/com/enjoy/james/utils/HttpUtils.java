package com.enjoy.james.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class HttpUtils {
/**/
	public static String send(String urls, String param){
		URL url = null;  
		HttpURLConnection http = null;  
		  
		try {  
		    url = new URL(urls);  
		    http = (HttpURLConnection) url.openConnection();  
		    http.setDoInput(true);  
		    http.setDoOutput(true);  
		    http.setUseCaches(false);  
		    http.setConnectTimeout(50000);//设置连接超时  
		    //如果在建立连接之前超时期满，则会引发一个 java.net.SocketTimeoutException。超时时间为零表示无穷大超时。  
		    http.setReadTimeout(50000);//设置读取超时  
		    //如果在数据可读取之前超时期满，则会引发一个 java.net.SocketTimeoutException。超时时间为零表示无穷大超时。            
		    http.setRequestMethod("POST");  
		    // http.setRequestProperty("Content-Type","text/xml; charset=UTF-8");  
		    http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  
		    http.connect();  
		    param = "&orderid=" + param   ;  
		  
		    OutputStreamWriter osw = new OutputStreamWriter(http.getOutputStream(), "utf-8");  
		    osw.write(param);  
		    osw.flush();  
		    osw.close();  
		    String result = "";
		    if (http.getResponseCode() == 200) {  
		        BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream(), "utf-8"));  
		        String inputLine;  
		        while ((inputLine = in.readLine()) != null) {  
		            result += inputLine;  
		        }  
		        in.close();  
		    }  
		    return result;
		} catch (Exception e) {  
		    System.out.println("err");  
		} finally {  
		    if (http != null) http.disconnect();  
		    //if (osw != null) osw.close();  
		} 
		return "";
	}
}