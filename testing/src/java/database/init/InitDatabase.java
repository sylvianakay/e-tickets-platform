/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.init;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import database.DB_Connection;
import static database.DB_Connection.getInitialConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/*
 *
 * @author micha
 */
public class InitDatabase {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        InitDatabase init = new InitDatabase();
        init.initDatabase();
        init.initTables();
       
    }

    public void dropDatabase() throws SQLException, ClassNotFoundException {
        Connection conn = getInitialConnection();
        Statement stmt = conn.createStatement();
        String sql = "DROP DATABASE  etickets";
        stmt.executeUpdate(sql);
        System.out.println("Database dropped successfully...");
    }

    public void initDatabase() throws SQLException, ClassNotFoundException {
        Connection conn = getInitialConnection();
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE DATABASE etickets");
        stmt.close();
        conn.close();
    }

    public void initTables() throws SQLException, ClassNotFoundException {
         try (Connection conn = DB_Connection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Events ("
                + "EventID INT AUTO_INCREMENT PRIMARY KEY,"
                + "EventName VARCHAR(255) NOT NULL,"
                + "EventDate DATE NOT NULL,"
                + "EventTime TIME NOT NULL,"
                + "EventType VARCHAR(100) NOT NULL,"
                + "Capacity INT NOT NULL)");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Customers ("
                + "CustomerID INT AUTO_INCREMENT PRIMARY KEY,"
                + "FullName VARCHAR(255) NOT NULL,"
                + "Email VARCHAR(255) UNIQUE NOT NULL,"
                + "CreditCardInfo VARCHAR(16) NOT NULL,"
                + "Password VARCHAR(255) NOT NULL)");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Tickets ("
                + "TicketID INT AUTO_INCREMENT PRIMARY KEY,"
                + "EventID INT NOT NULL,"
                + "TicketType VARCHAR(255) NOT NULL,"
                + "Price DECIMAL(10, 2) NOT NULL,"
                + "Availability INT NOT NULL,"
                + "FOREIGN KEY (EventID) REFERENCES Events(EventID))");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Bookings ("
                + "BookingID INT AUTO_INCREMENT PRIMARY KEY,"
                + "CustomerID INT NOT NULL,"
                + "EventID INT NOT NULL,"
                + "TicketID INT NOT NULL,"
                + "BookingDate DATE NOT NULL,"
                + "NumberOfTickets INT NOT NULL,"
                + "FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID),"
                + "FOREIGN KEY (EventID) REFERENCES Events(EventID),"
                + "FOREIGN KEY (TicketID) REFERENCES Tickets(TicketID))");  

//we could add  ON DELETE CASCADE in most foreign key refrences in order to avoid exequting sql everytime we cancel events, avoided to avoid errors

            
//            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Seats ("
//                + "SeatID INT AUTO_INCREMENT PRIMARY KEY,"
//                + "EventID INT NOT NULL,"
//                + "TicketType VARCHAR(255) NOT NULL,"
//                + "SeatNumber VARCHAR(10) NOT NULL,"
//                + "FOREIGN KEY (EventID) REFERENCES Events(EventID),"
//                + "UNIQUE (EventID, SeatNumber))");
            
            
            System.out.println("Tables created successfully.");
            
            
        }
    
    }

}
