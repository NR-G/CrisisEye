package com.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.QueryParam;

import org.json.JSONException;
import org.json.JSONObject;

public class SendPushNotification {
	 
	
	 public static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
	
	 public static final String FCM_SERVER_API_KEY    = "AAAA2pXZAKc:APA91bF7YUo04QJbh5VuiRzB6n4XBBobZr7AHDn2d9J3yJ99wBWQfUnmXYyG1j47BfefX-9r_xu7JdAaH3muCBWyXhid6VZUdtuov2TMqcHeHsxc49i2kOZh1sF3DwPz0LYYn2S8PtLF";
	
	 
//	 private static final String deviceRegistrationId =  "dPvJDIld3ns:APA91bFs9lrYSSR61uP2DtszTVo-navjsz3YlzpH5K6AgpuhKwpcmyiQaXB_CJGSTOSREA4POIseiLmo2A-9Jgjxw_uaBEqvghMr82CJCSDbt_1LyQ3tkXQh7tbkc1WKyVIK1rjhKuTq";
//	 d2q7UH_sldg:APA91bHuijFb5624uVwxhdbgaHEvyg0kv_Fyvuue125BjfRDPnQB8LxloqVnnGv-GwsRY8GHZvNxAEQgd17sxq-Cr1YiyhFaVytylcBLxAUgZXvLa9M54pQxu3q7W5KJXiJgCWJfIz9W

	 
	 private static final String deviceRegistrationId =  "dDVU230QYEE:APA91bGRwpI9rpHpJw5Tm7WAKSLt_lZ57_n-4Jb1ItDVqJbLUoPOZkS-AuhXJBiV6z1LmQReZzZ1sOhxWdwE0K6NPvRNY3ERAnevYCFRaCjf4JixrbrM_2y-WmnFQ_70AR1MvK_Seu7v" ;
	 
	 public void send_msg(String framerate, String k_frame_interval,  String bitrate)
	    {
	        int responseCode = -1;
	        String responseBody = null;
	        try
	        {
	            System.out.println("Sending FCM request");
	            byte[] postData = getPostData(deviceRegistrationId,framerate,k_frame_interval,bitrate);
	            
	            URL url = new URL(FCM_URL);
	            HttpsURLConnection httpURLConnection = (HttpsURLConnection)url.openConnection();
	 
	            //set timeputs to 10 seconds
	            httpURLConnection.setConnectTimeout(10000);
	            httpURLConnection.setReadTimeout(10000);
	 
	            httpURLConnection.setDoOutput(true);
	            httpURLConnection.setUseCaches(false);
	            httpURLConnection.setRequestMethod("POST");
	            httpURLConnection.setRequestProperty("Content-Type", "application/json");
	            httpURLConnection.setRequestProperty("Content-Length", Integer.toString(postData.length));
	            httpURLConnection.setRequestProperty("Authorization", "key="+FCM_SERVER_API_KEY);
	 
	             
	 
	            OutputStream out = httpURLConnection.getOutputStream();
	            out.write(postData);
	            out.close();
	            responseCode = httpURLConnection.getResponseCode();
	            //success
	            if (responseCode == 200)
	            {
	                responseBody = convertStreamToString(httpURLConnection.getInputStream());
	                System.out.println("FCM message sent : " + responseBody);
	            }
	            //failure
	            else
	            {
	                responseBody = convertStreamToString(httpURLConnection.getErrorStream());
	                System.out.println("Sending FCM request failed for regId: " + deviceRegistrationId + " response: " + responseBody);
	            }
	        }
	        catch (IOException ioe)
	        {
	            System.out.println("IO Exception in sending FCM request. regId: " + deviceRegistrationId);
	            ioe.printStackTrace();
	        }
	        catch (Exception e)
	        {
	            System.out.println("Unknown exception in sending FCM request. regId: " + deviceRegistrationId);
	            e.printStackTrace();
	        }
	    }
	     
	    public static byte[] getPostData(String registrationId, String framerate, String k_frame_interval, String bitrate) throws JSONException {
	        HashMap<String, String> dataMap = new HashMap<>();
	        JSONObject payloadObject = new JSONObject();
	 
	        dataMap.put("framerate", framerate);
	        dataMap.put("k_frame_interval", k_frame_interval);
	        dataMap.put("bitrate", bitrate);
	         
	        JSONObject data = new JSONObject(dataMap);
	        payloadObject.put("data", data);
	        payloadObject.put("to", registrationId);
	        
	        HashMap<String, String> notificationMap = new HashMap<>();
	        notificationMap.put("body", "Thriller !!!");
	        notificationMap.put("title", "ULL v/s LSU");
	        
	        JSONObject notification = new JSONObject(notificationMap);
	        payloadObject.put("notification", notification);
	 
	        return payloadObject.toString().getBytes();
	    }
	     
	    public static String convertStreamToString (InputStream inStream) throws Exception
	    {
	        InputStreamReader inputStream = new InputStreamReader(inStream);
	        BufferedReader bReader = new BufferedReader(inputStream);
	 
	        StringBuilder sb = new StringBuilder();
	        String line = null;
	        while((line = bReader.readLine()) != null)
	        {
	            sb.append(line);
	        }
	 
	        return sb.toString();
	    }

}
