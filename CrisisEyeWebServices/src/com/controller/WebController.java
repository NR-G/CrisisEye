package com.controller;

import java.sql.Connection;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/webservice")
public class WebController {
	
	
 

	@GET
	@Path("/hello")
	@Produces("text/plain")
	public String hello() {
		System.out.println(">> hello good news a request is there");
		return "Hello World!!!";
	}
	
	/*@GET
	@Path("/hello_post/{token}")
	@Produces("text/plain")
	public Response hello_token_response(@PathParam("token") String token) {
		System.out.println(">> hello good news a request is there");
//		return "Hello World!!!";
		String output = "Hello World!!!";
		  return Response.status(200).entity(output).build();
	}*/
	
	@POST
	@Path("/hello_post/{token}")
	@Produces("text/plain")
	public String hello_token_path(@PathParam("token") String token) {
		System.out.println(">> hello good news a request is there "+token);
//		return "Hello World!!!";
		String output = "Hello World!!!" + token;
		 return output;
	}
	
	@POST
	@Path("/hello_post")
	@Produces("text/plain")
	public String hello_token_query(@QueryParam("token") String token,@QueryParam("email") String email,@QueryParam("device_imei")) {
		System.out.println(">> token "+token);
		System.out.println(">> email "+email);
		System.out.println(">> email "+device_imei);
//		return "Hello World!!!";
		String output = "Hello World!!! :: " + token+ " :: "+email + " :: " + device_imei ;
		
		DbUtils db = new DbUtils();
		Connection connect = db.create_Connection();

		 db.update_DeviceDetails(email,token,device_imei,connect);
		
		
		 return output;
	}

	@GET
	@Path("/login")
	@Produces("text/plain")
	public String login(@QueryParam("email") String email,@QueryParam("deviceID") String deviceID,@QueryParam("password") String password,@QueryParam("token") String token) {
		System.out.println(">> hi good news a request is there");
		System.out.println("token is :: "+token+"username is >> " + email + " password >> " + password +" deviceID is >> " +deviceID);

		DbUtils db = new DbUtils();
		Connection connect = db.create_Connection();

		String str =  db.checkLogin(email,password,token,connect);
//http://localhost:8080/CrisisEyeWebServices/webservice/register?firstname=raju&lastname=gottumukala&password=r22&confirm_password=r22&email=raj@gmail.com
		return str;
	}

	@GET
	@Path("/register")
	@Produces("text/plain")
	public String register(@QueryParam("firstname") String firstname, @QueryParam("lastname") String lastname,
			@QueryParam("password") String password, @QueryParam("confirm_password") String confirm_password,
			@QueryParam("email") String email) {
		System.out.println(">> hi good news a request is there");
		System.out.println("firstname is >> " + firstname + " lastname >> " + lastname +
				" password >> " + password + " confirm_password >> " + confirm_password
				+ " email >> " + email);

		DbUtils db = new DbUtils();
		Connection connect = db.create_Connection();

		String str = db.registerUser(firstname, lastname, email,password, confirm_password,connect);

		return str;
	}
	
	@GET
	@Path("/device_use_info")
	@Produces("text/plain")
	public String sendDeviceData(@QueryParam("battery_level") String battery_level, @QueryParam("lat") String latitude,
			@QueryParam("lon") String longitude, @QueryParam("device_time") String device_time,
			@QueryParam("device_id") String device_id, @QueryParam("orientation") String orientation,
				     @QueryParam("device_imei") String device_imei) {
		System.out.println(">> hi good news a request is there");
		System.out.println("battery_level is >> " + battery_level + " latitude >> " + latitude +
				" longitude >> " + longitude + " device_time >> " + device_time
				+ " device_id >> " + device_id +  " orientation >> " + orientation + " device_imei >> " + device_imei);

		DbUtils db = new DbUtils();
		Connection connect = db.create_Connection();

//		String str = db.registerUser(firstname, lastname, email,password, confirm_password,connect);
		String str = db.insertDeviceDetails(latitude, longitude, device_time, orientation, battery_level, device_id, connect);
		return str;
	}
	
	@GET
	@Path("/sendupdatedvalues")
	@Produces("text/plain")
	public void send_notification(@QueryParam("framerate") String framerate, @QueryParam("keyframe_interval") String k_frame_interval, @QueryParam("bitrate") String bitrate) {
		System.out.println(">> hi good news a request is there");
		System.out.println("framerate is >> " + framerate + " k_frame_interval >> " + k_frame_interval + " bitrate >> " +bitrate);
		//http://localhost:8080/CrisisEyeWebServices/crisiseye/webservice/sendupdatedvalues?framerate=20&keyframe_interval=30&bitrate=750
		
		SendPushNotification spn = new SendPushNotification();
		spn.send_msg(framerate, k_frame_interval, bitrate);
		
		// http://localhost:8080/CrisisEyeWebServices/crisiseye/webservice/sendupdatedvalues?framerate=20&keyframe_interval=30&bitrate=750
	}
}
