package com.innvo.jasper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

	 public  Connection getConnection() {
		  Connection conn = null;
		    try {
		             Class.forName("org.postgresql.Driver");
		            } catch (ClassNotFoundException e) {
		                e.printStackTrace();
		            }  
		         try {
					conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/apprefactory","postgres","postgres");
				} catch (SQLException e) {
					e.printStackTrace();
				}
		         return conn;
	}
	

}