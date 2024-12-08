/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlets;


import database.DB_Connection;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "AdminServlet", urlPatterns = {"/AdminServlet"})
public class AdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        response.setContentType("application/json");

        try (Connection conn = DB_Connection.getConnection();
             PrintWriter out = response.getWriter()) {

            if ("availableAndReserved".equals(action)) {
                handleAvailableAndReserved(conn, out);
            } else if ("revenuePerEvent".equals(action)) {
                handleRevenuePerEvent(conn, out);
            } else if ("popularEvent".equals(action)) {
                handlePopularEvent(conn, out);
            } else if ("highestRevenueEvent".equals(action)) {
                handleHighestRevenueEvent(conn, request, out);
            } else if ("bookingsByPeriod".equals(action)) {
                handleBookingsByPeriod(conn, request, out);
            } else if ("ticketTypeRevenue".equals(action)) {
                handleTicketTypeRevenue(conn, request, out);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

   private void handleAvailableAndReserved(Connection conn, PrintWriter out) {
    String sql = "SELECT e.EventName, " +
                 "SUM(t.Availability) AS Available, " +
                 "SUM(t.InitialAvailability - t.Availability) AS Reserved " +
                 "FROM Events e " +
                 "JOIN Tickets t ON e.EventID = t.EventID " +
                 "GROUP BY e.EventName";
    try (PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        StringBuilder json = new StringBuilder("[");
        while (rs.next()) {
            if (json.length() > 1) json.append(",");
            json.append("{")
                .append("\"EventName\":\"").append(rs.getString("EventName")).append("\",")
                .append("\"Available\":").append(rs.getInt("Available")).append(",")
                .append("\"Reserved\":").append(rs.getInt("Reserved"))
                .append("}");
        }
        json.append("]");
        out.println(json.toString());
    } catch (SQLException e) {
        e.printStackTrace(); // Log the error
        out.println("{\"error\":\"SQL Error: " + e.getMessage() + "\"}");
    } catch (Exception e) {
        e.printStackTrace(); // Log other exceptions
        out.println("{\"error\":\"Unexpected Error: " + e.getMessage() + "\"}");
    }
}


    private void handleRevenuePerEvent(Connection conn, PrintWriter out) throws SQLException {
        String sql = "SELECT e.EventName, SUM(b.NumberOfTickets * t.Price) AS TotalRevenue " +
                     "FROM Bookings b " +
                     "JOIN Tickets t ON b.TicketID = t.TicketID " +
                     "JOIN Events e ON b.EventID = e.EventID " +
                     "GROUP BY e.EventName";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            StringBuilder json = new StringBuilder("[");
            while (rs.next()) {
                if (json.length() > 1) json.append(",");
                json.append("{")
                    .append("\"EventName\":\"").append(rs.getString("EventName")).append("\",")
                    .append("\"TotalRevenue\":").append(rs.getDouble("TotalRevenue"))
                    .append("}");
            }
            json.append("]");
            out.println(json.toString());
        }
    }

    private void handlePopularEvent(Connection conn, PrintWriter out) throws SQLException {
        String sql = "SELECT e.EventName, COUNT(b.BookingID) AS Bookings " +
                     "FROM Bookings b " +
                     "JOIN Events e ON b.EventID = e.EventID " +
                     "GROUP BY e.EventName " +
                     "ORDER BY Bookings DESC LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                out.println("{\"EventName\":\"" + rs.getString("EventName") + "\",\"Bookings\":" + rs.getInt("Bookings") + "}");
            } else {
                out.println("{\"error\":\"No data available.\"}");
            }
        }
    }

    private void handleHighestRevenueEvent(Connection conn, HttpServletRequest request, PrintWriter out) throws SQLException {
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");

        String sql = "SELECT e.EventName, SUM(b.NumberOfTickets * t.Price) AS TotalRevenue " +
                     "FROM Bookings b " +
                     "JOIN Tickets t ON b.TicketID = t.TicketID " +
                     "JOIN Events e ON b.EventID = e.EventID " +
                     "WHERE b.BookingDate BETWEEN ? AND ? " +
                     "GROUP BY e.EventName " +
                     "ORDER BY TotalRevenue DESC LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    out.println("{\"EventName\":\"" + rs.getString("EventName") + "\",\"TotalRevenue\":" + rs.getDouble("TotalRevenue") + "}");
                } else {
                    out.println("{\"error\":\"No data available.\"}");
                }
            }
        }
    }

    private void handleBookingsByPeriod(Connection conn, HttpServletRequest request, PrintWriter out) throws SQLException {
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");

        String sql = "SELECT b.BookingID, e.EventName, b.NumberOfTickets, b.BookingDate " +
                     "FROM Bookings b " +
                     "JOIN Events e ON b.EventID = e.EventID " +
                     "WHERE b.BookingDate BETWEEN ? AND ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
            try (ResultSet rs = pstmt.executeQuery()) {
                StringBuilder json = new StringBuilder("[");
                while (rs.next()) {
                    if (json.length() > 1) json.append(",");
                    json.append("{")
                        .append("\"BookingID\":").append(rs.getInt("BookingID")).append(",")
                        .append("\"EventName\":\"").append(rs.getString("EventName")).append("\",")
                        .append("\"NumberOfTickets\":").append(rs.getInt("NumberOfTickets")).append(",")
                        .append("\"BookingDate\":\"").append(rs.getDate("BookingDate")).append("\"")
                        .append("}");
                }
                json.append("]");
                out.println(json.toString());
            }
        }
    }

    private void handleTicketTypeRevenue(Connection conn, HttpServletRequest request, PrintWriter out) throws SQLException {
        String ticketType = request.getParameter("ticketType"); // VIP or General

        String sql = "SELECT e.EventName, SUM(b.NumberOfTickets * t.Price) AS Revenue " +
                     "FROM Bookings b " +
                     "JOIN Tickets t ON b.TicketID = t.TicketID " +
                     "JOIN Events e ON b.EventID = e.EventID " +
                     "WHERE t.TicketType = ? " +
                     "GROUP BY e.EventName";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ticketType);
            try (ResultSet rs = pstmt.executeQuery()) {
                StringBuilder json = new StringBuilder("[");
                while (rs.next()) {
                    if (json.length() > 1) json.append(",");
                    json.append("{")
                        .append("\"EventName\":\"").append(rs.getString("EventName")).append("\",")
                        .append("\"Revenue\":").append(rs.getDouble("Revenue"))
                        .append("}");
                }
                json.append("]");
                out.println(json.toString());
            }
        }
    }
}
