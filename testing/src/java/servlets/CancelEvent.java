package servlets;

import database.DB_Connection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "CancelEvent", urlPatterns = {"/CancelEvent"})
public class CancelEvent extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String eventName = request.getParameter("eventName");

        if (eventName == null || eventName.trim().isEmpty()) {
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Event name is required.\"}");
            return;
        }

        Connection conn = null;

        try {
            conn = DB_Connection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // Step 1: Get EventID from EventName
            String sqlGetEventID = "SELECT EventID FROM Events WHERE EventName = ?";
            PreparedStatement pstmtGetEventID = conn.prepareStatement(sqlGetEventID);
            pstmtGetEventID.setString(1, eventName);
            ResultSet rsEvent = pstmtGetEventID.executeQuery();

            if (!rsEvent.next()) {
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Event does not exist.\"}");
                return;
            }

            int eventId = rsEvent.getInt("EventID");

            // Step 2: Refund customers
            String sqlRefunds = "SELECT CustomerID, SUM(b.NumberOfTickets * t.Price) AS RefundAmount " +
                                "FROM Bookings b " +
                                "JOIN Tickets t ON b.TicketID = t.TicketID " +
                                "WHERE b.EventID = ? GROUP BY CustomerID";
            PreparedStatement pstmtRefunds = conn.prepareStatement(sqlRefunds);
            pstmtRefunds.setInt(1, eventId);
            ResultSet rsRefunds = pstmtRefunds.executeQuery();

            while (rsRefunds.next()) {
                int customerId = rsRefunds.getInt("CustomerID");
                double refundAmount = rsRefunds.getDouble("RefundAmount");
                System.out.println("Refunding customer " + customerId + ": $" + refundAmount);
                // Add logic for processing refunds if necessary
            }

            // Step 3: Delete from Bookings table
            String sqlDeleteBookings = "DELETE FROM Bookings WHERE EventID = ?";
            PreparedStatement pstmtDeleteBookings = conn.prepareStatement(sqlDeleteBookings);
            pstmtDeleteBookings.setInt(1, eventId);
            pstmtDeleteBookings.executeUpdate();

            // Step 4: Delete from Tickets table
            String sqlDeleteTickets = "DELETE FROM Tickets WHERE EventID = ?";
            PreparedStatement pstmtDeleteTickets = conn.prepareStatement(sqlDeleteTickets);
            pstmtDeleteTickets.setInt(1, eventId);
            pstmtDeleteTickets.executeUpdate();

            // Step 5: Delete from Events table
            String sqlDeleteEvent = "DELETE FROM Events WHERE EventID = ?";
            PreparedStatement pstmtDeleteEvent = conn.prepareStatement(sqlDeleteEvent);
            pstmtDeleteEvent.setInt(1, eventId);
            pstmtDeleteEvent.executeUpdate();

            conn.commit(); // Commit transaction

            response.setContentType("application/json");
            response.getWriter().println("{\"message\":\"Event canceled and refunds issued successfully.\"}");
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            response.setContentType("application/json");
            response.getWriter().println("{\"error\":\"" + e.getMessage() + "\"}");
        } finally {
            if (conn != null) {
                try {
                    conn.close(); // Close connection
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "Handles event cancellations, refunds customers, and removes associated data.";
    }
}
