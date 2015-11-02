package com.mnt.businessApp.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.springframework.stereotype.Service;

@Service
public class SmsSender {

	public void sendSms(String phone,String message) {
		StringBuffer response = new StringBuffer();
		if(phone.length() == 10){
			phone = "91"+phone;
		}
		String encodedString = "";
		try {
			encodedString = URLEncoder.encode(message, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
    	try {
    		String url = "http://bulkpush.mytoday.com/BulkSms/SingleMsgApi?feedid=354457&To=917709919211&Text="+encodedString+"&Username=9833444855&Password=tmddd&mtype=1";
    		System.out.println("Url : " + url);
    		URL obj = new URL(url);
    		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    		con.setRequestMethod("GET");
    		
    		BufferedReader in = new BufferedReader(
    		        new InputStreamReader(con.getInputStream()));
        		String inputLine;
        		while ((inputLine = in.readLine()) != null) {
        			response.append(inputLine);
        		}
        	in.close();
    	} catch(Exception e) {
    		System.out.println("message .........." + e.getMessage());
    	}
    	System.out.println("Response : " + response.toString());
	}
	
}
