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
        String eventIdParam = request.getParameter("eventName"); //retrieved the eventId
        System.out.println("Received EventName: " + eventIdParam);

        if (eventIdParam == null || eventIdParam.trim().isEmpty()) {
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Event name is required.\"}");
            return;
        }

        Connection conn = null;
        
        try {
            int eventId = Integer.parseInt(eventIdParam);
            conn = DB_Connection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

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
            }

            String sqlDeleteBookings = "DELETE FROM Bookings WHERE EventID = ?";
            PreparedStatement pstmtDeleteBookings = conn.prepareStatement(sqlDeleteBookings);
            pstmtDeleteBookings.setInt(1, eventId);
            pstmtDeleteBookings.executeUpdate();

            String sqlDeleteTickets = "DELETE FROM Tickets WHERE EventID = ?";
            PreparedStatement pstmtDeleteTickets = conn.prepareStatement(sqlDeleteTickets);
            pstmtDeleteTickets.setInt(1, eventId);
            pstmtDeleteTickets.executeUpdate();

            String sqlDeleteEvent = "DELETE FROM Events WHERE EventID = ?";
            PreparedStatement pstmtDeleteEvent = conn.prepareStatement(sqlDeleteEvent);
            pstmtDeleteEvent.setInt(1, eventId);
            pstmtDeleteEvent.executeUpdate();

            conn.commit(); // Commit transaction

            response.setContentType("application/json");
            response.getWriter().println("{\"message\":\"Event canceled and refunds issued successfully.\"}");
        } catch (Exception e) {
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
