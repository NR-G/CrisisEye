package com.controller;

import java.sql.Connection;

public class DBInsertTest {
	
	public static void main(String[] args) {
		
		
		
		DbUtils db = new DbUtils();
		
		Connection connect = db.create_Connection();
		System.out.println("connection created");
		
		
		db.insertDeviceDetails("lat", "lon", "time", "portrait", "battery_level", "samsung galaxy s6", connect);
		System.out.println("inserted succesfully into database");
	}
	
	
	

}
