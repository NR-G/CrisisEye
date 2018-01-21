package com.controller;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DbUtils {
	
	String url ="jdbc:mysql://localhost:3306/";
	String dbname ="CrisisEye";
	String driver = "com.mysql.jdbc.Driver";
	String userName = "root";
	String password = "r22";
	
	public Connection create_Connection()
	{
	  Connection connect = null;
	  try
	  {
		 Class.forName(driver).newInstance();
		 connect = DriverManager.getConnection(url + dbname,userName,password);
		  
		  
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
		return connect;
		
	}

	public String insertDeviceDetails(String lat,String lon,String device_time,String device_orientation ,String bat_level,String device_logged_in_fcm_id , String device_imei,Connection connect)
	{
		String flag = "failure";
		PreparedStatement ps=null;
		System.out.println("attempting to insert data");
		
		try{
			String stmnt = "Insert into locationinfo " +"(latitude,longitude,device_time,battery_level,device_orientation,device_logged_in_fcm_id,device_imei)" + "values(?,?,?,?,?,?,?)";
			System.out.println("latitude is >> "+lat);
			System.out.println("longitude is >> "+lon);
			System.out.println("device_time is >> "+device_time);
			System.out.println("battery level is >> "+bat_level);
			System.out.println("device_orientation is >> "+device_orientation);
			System.out.println("device_logged_in_fcm_id is >> "+device_logged_in_fcm_id);
			System.out.println("device_imei is  >> "+device_imei);
			
			ps = connect.prepareStatement(stmnt);
			
			ps.setString(1, lat);
			ps.setString(2, lon);
			ps.setString(3, device_time);
			ps.setString(4, bat_level);
			ps.setString(5, device_orientation);
			ps.setString(6, device_logged_in_fcm_id);
			ps.setString(7, device_imei);
			
			int count = ps.executeUpdate();
			System.out.println("count is "+ count);
			if(count >=1)
			  flag = "success";
			else
				flag ="failure";
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(connect !=null)
			{
				try
				{
					connect.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				if(ps!=null)
				{
					try
					{
						ps.close();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		
		System.out.println("value of flag >> "+flag);
		return flag;
	}

	public void update_DeviceDetails(String email,String device_logged_in_fcm_id,String device_imei, Connection connect) {
	try {
	String device_fcm_stmt= "select device_logged_in_fcm_id from CrisisEye.user_info_table  where email = '"+ email+"' ";
	PreparedStatement device_fcm_id_stmt =connect.prepareStatement(device_fcm_stmt);
	ResultSet rs1 = device_fcm_id_stmt.executeQuery();
	
		while(rs1.next())
		{
			System.out.println("in rs1 is :: >> ");
			String device_fcm_id = rs1.getString("device_logged_in_fcm_id");
			System.out.println("> device_fcm_id is :: "+device_fcm_id);
			System.out.println(">> device_logged_in_fcm_id is :: "+device_logged_in_fcm_id);
			
			if(! device_fcm_id.equals(device_logged_in_fcm_id))
			{
				String update_stmnt =" UPDATE CrisisEye.user_info_table " +
				"SET device_logged_in_fcm_id = ?, device_imei = ?," +
				"WHERE email = ? ";

				PreparedStatement device_fcm_id_update_stmt =connect.prepareStatement(update_stmnt);
	
						
					
				device_fcm_id_update_stmt.setString (1, device_logged_in_fcm_id);
				device_fcm_id_update_stmt.setString(2, email);
				device_fcm_id_update_stmt.setString(3, device_imei);
				device_fcm_id_update_stmt.executeUpdate();
			}
				
		}
	}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public String checkLogin(String email, String password,String device_logged_in_fcm_id, String device_imei,Connection connect) {
		PreparedStatement ps=null;
		ResultSet rs = null;
		System.out.println("attempting to insert data");
		String status = "failure";
		try{
			String stmnt = "select password from user_info_table where email = '" +email + "'";
			System.out.println("email is >> "+email);
			System.out.println("password2 is >> "+password);
			
			ps = connect.prepareStatement(stmnt);
			rs = ps.executeQuery();
			System.out.println(">> connected");
			
			while(rs.next())
			{
				String passwd = rs.getString("password");
				System.out.println("passwd is >> "+passwd);
				System.out.println("password2 is >> "+password);
//				 String query = "{call get_device_fcm_token(?,?)}";
				if(passwd.equals(password))
				{
//					 CallableStatement stmt = connect.prepareCall(query) ;
//				     stmt.setString(1, email);
				     
				     
					String device_fcm_stmt= "select device_logged_in_fcm_id from CrisisEye.user_info_table  where email = '"+ email+"' ";
					PreparedStatement device_fcm_id_stmt =connect.prepareStatement(device_fcm_stmt);
					ResultSet rs1 = device_fcm_id_stmt.executeQuery();
					
					while(rs1.next())
					{
						System.out.println("in rs1 is :: >> ");
						String device_fcm_id = rs1.getString("device_logged_in_fcm_id");
						System.out.println("> device_fcm_id is :: "+device_fcm_id);
						System.out.println(">> device_logged_in_fcm_id is :: "+device_logged_in_fcm_id);
						if(! device_fcm_id.equals(device_logged_in_fcm_id))
						{
							String update_stmnt =" UPDATE CrisisEye.user_info_table " +
							"SET device_logged_in_fcm_id = ?, device_imei = ? " +
							"WHERE email = ? "; 
//							update users set num_points = ? where first_name = ?
							
							PreparedStatement device_fcm_id_update_stmt =connect.prepareStatement(update_stmnt);
//							ResultSet rs2 = 
									
								
							device_fcm_id_update_stmt.setString (1, device_logged_in_fcm_id);
							device_fcm_id_update_stmt.setString(2, device_imei);
							device_fcm_id_update_stmt.setString(3,  email);
							device_fcm_id_update_stmt.executeUpdate();
						}
							
					}
					
					 status = "success";
				
				}
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(connect !=null)
			{
				try
				{
					connect.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				if(ps!=null)
				{
					try
					{
						ps.close();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		
		System.out.println("status is >> "+status);
		return status;
	}

	public String registerUser(String firstname, String lastname, String email, String password2,
			String confirm_password,Connection connect) {
		
		String flag = "failure";
			PreparedStatement ps=null;
			System.out.println("attempting to insert data");
			
			try{
				if(password2.equals(confirm_password))
				{
					System.out.println("in password match  success condition");
					String stmnt = "Insert into user_info_table " +"(firstname,lastname,email,password,device_logged_in_fcm_id)" + "values(?,?,?,?,?)";
					System.out.println("firstname is >> "+firstname);
					System.out.println("lastname is >> "+lastname);
					System.out.println("email is >> "+email);
					System.out.println("password2 is >> "+password2);
					System.out.println("confirm_password is >> "+confirm_password);
					
					ps = connect.prepareStatement(stmnt);
					
					ps.setString(1, firstname);
					ps.setString(2, lastname);
					ps.setString(3, email);
					ps.setString(4, password2);
					ps.setString(5, "");
					int count = ps.executeUpdate();
					System.out.println("count is "+ count);
					if(count >=1)
					  flag = "success";
					else
					  flag = "failure";
				}
				else
				{
					flag = "Password does not match. Confirm pasword again";
				}
				
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(connect !=null)
				{
					try
					{
						connect.close();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					if(ps!=null)
					{
						try
						{
							ps.close();
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}
			
			System.out.println("value of flag >> "+flag);
			return flag;
		
	}
	
}
