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

            System.out.println("Tables created successfully.");
        }
    
    }

//    public void addToDatabaseExamples() throws ClassNotFoundException, SQLException {
//        //Users
//
//        EditUsersTable eut = new EditUsersTable();
//        eut.addUserFromJSON(Resources.admin);
//        eut.addUserFromJSON(Resources.user1JSON);
//        eut.addUserFromJSON(Resources.user2JSON);
//        System.out.println(Resources.user3JSON);
//        eut.addUserFromJSON(Resources.user3JSON);
//        eut.addUserFromJSON(Resources.user4JSON);
//
//        //volunteers
//        EditVolunteersTable editVolunteers = new EditVolunteersTable();
//        editVolunteers.addVolunteerFromJSON(Resources.volunteer1JSON);
//        editVolunteers.addVolunteerFromJSON(Resources.volunteer2JSON);
//        editVolunteers.addVolunteerFromJSON(Resources.volunteer3JSON);
//        editVolunteers.addVolunteerFromJSON(Resources.volunteer4JSON);
//        //incidents
//        EditIncidentsTable editIncidents = new EditIncidentsTable();
//        editIncidents.addIncidentFromJSON(Resources.incident1);
//        editIncidents.addIncidentFromJSON(Resources.incident2);
//        editIncidents.addIncidentFromJSON(Resources.incident3);
//        editIncidents.addIncidentFromJSON(Resources.incident4);
//        editIncidents.addIncidentFromJSON(Resources.incident5);
//        //participants
//        EditParticipantsTable editParticipants = new EditParticipantsTable();
//        editParticipants.addParticipantFromJSON(Resources.participant1);
//        editParticipants.addParticipantFromJSON(Resources.participant2);
//        editParticipants.addParticipantFromJSON(Resources.participant3);
//
//        EditMessagesTable editmessages = new EditMessagesTable();
//        editmessages.addMessageFromJSON(Resources.message1);
//        editmessages.addMessageFromJSON(Resources.message2);
//    }

//    public void databaseToJSON() throws ClassNotFoundException, SQLException {
////       //Get info of User
//        EditUsersTable eut = new EditUsersTable();
//        User su = eut.databaseToUsers("mountanton", "ab$A12cde");
//        String json = eut.userToJSON(su);
//        System.out.println("User\n" + json + "\n");
//
//        //all fire incidents
//        EditIncidentsTable editIncidents = new EditIncidentsTable();
//        ArrayList<Incident> allIncidents = new ArrayList<Incident>();
//        allIncidents = editIncidents.databaseToIncidentsSearch("fire", "all", "");
//        Gson gson2 = new Gson();
//        JsonArray incidentsJSON = gson2.toJsonTree(allIncidents).getAsJsonArray();
//        System.out.println("All Fire Incidents\n" + incidentsJSON);
//
//        //all running incidents in Hersonissos
//        ArrayList<Incident> allIncidents2 = new ArrayList<Incident>();
//        allIncidents2 = editIncidents.databaseToIncidentsSearch("all", "running", "Hersonissos");
//        JsonArray incidentsJSON2 = gson2.toJsonTree(allIncidents2).getAsJsonArray();
//        System.out.println("All Running Incidents in Hersonissos\n" + incidentsJSON2);
//        
//        
//        //       all messages of an incident
//        EditMessagesTable editmessages = new EditMessagesTable();
//        ArrayList<Message> messagesOfIncident = new ArrayList<Message>();
//        int incident_id = 1;
//        messagesOfIncident = editmessages.databaseToMessage(incident_id);
//        Gson gson7 = new Gson();
//        JsonArray messagesOfIncidentJSON = gson7.toJsonTree(messagesOfIncident).getAsJsonArray();
//        System.out.println("All messages of Incident with ID " + incident_id + "\n" + messagesOfIncidentJSON + "\n");
//
//        CheckForDuplicatesExample check = new CheckForDuplicatesExample();
//        System.out.println("Is username:" + "Mitsos" + " Available? " + check.isUserNameAvailable("mitsos"));
//
//        System.out.println("Is username:" + "mountanton" + " Available? " + check.isUserNameAvailable("mountanton"));
//        System.out.println("Is username:" + "nick" + " Available? " + check.isUserNameAvailable("nick"));
//
//    }

//    public void updateRecords() throws ClassNotFoundException, SQLException {
//        EditUsersTable editUsers = new EditUsersTable();
//        editUsers.updateUser("mountanton", "job", "Professor");
//
//        EditParticipantsTable editParticipants = new EditParticipantsTable();
//        editParticipants.acceptParticipant(3, "raphael");
//        editParticipants.finalStatusParticipant(3, "yes", "Voithise para polu");
//        EditIncidentsTable editIncidents = new EditIncidentsTable();
//        HashMap<String, String> updateIncident1 = new HashMap<String, String>();
//        updateIncident1.put("vehicles", "1");
//        updateIncident1.put("firemen", "5");
//        editIncidents.updateIncident("1", updateIncident1);
//
//        HashMap<String, String> updateIncident2 = new HashMap<String, String>();
//        updateIncident2.put("municipality", "Hersonissos");
//        updateIncident2.put("prefecture", "Heraklion");
//        updateIncident2.put("danger", "medium");
//        updateIncident2.put("status", "running");
//        updateIncident2.put("vehicles", "2");
//        updateIncident2.put("firemen", "3");
//        editIncidents.updateIncident("2", updateIncident2);
//
//        HashMap<String, String> updateIncident3 = new HashMap<String, String>();
//        updateIncident3.put("lat", "35.225643");
//        updateIncident3.put("lon", "25.183435");
//        updateIncident3.put("municipality", "Archanes-Asterousia");
//        updateIncident3.put("prefecture", "Heraklion");
//        updateIncident3.put("danger", "high");
//        updateIncident3.put("status", "finished");
//        updateIncident3.put("end_datetime", "2024-10-10 20:10:05");
//        updateIncident3.put("finalResult", "I epixirisi itan epituximeni");
//        updateIncident3.put("vehicles", "3");
//        updateIncident3.put("firemen", "7");
//        editIncidents.updateIncident("3", updateIncident3);
//
//    }

   

}
