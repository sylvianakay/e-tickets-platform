package database.init;

import database.DB_Connection;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class InitDatabase {
    public void initDatabase() throws SQLException, ClassNotFoundException {
        try (Connection conn = DB_Connection.getConnection()) {
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE DATABASE IF NOT EXISTS e_tickets");
        }
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
                + "CreditCardInfo VARCHAR(16) NOT NULL)");

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

            System.out.println("Tables created successfully.");
        }
    }
}
