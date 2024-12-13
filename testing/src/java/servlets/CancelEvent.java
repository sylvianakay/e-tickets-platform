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

/**
 *
 * @author pswmi64
 */
@WebServlet(name = "CancelEvent", urlPatterns = {"/CancelEvent"})
public class CancelEvent extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet CancelEvent</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CancelEvent at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int eventId = Integer.parseInt(request.getParameter("eventId"));

        Connection conn = null;

        try {
            conn = DB_Connection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // Step 1: Refund customers
            String sqlRefunds = "SELECT CustomerID, SUM(b.NumberOfTickets * t.Price) AS RefundAmount " +
                                "FROM Bookings b " +
                                "JOIN Tickets t ON b.TicketID = t.TicketID " +
                                "WHERE b.EventID = ? GROUP BY CustomerID";
            PreparedStatement pstmtRefunds = conn.prepareStatement(sqlRefunds);
            pstmtRefunds.setInt(1, eventId);
            ResultSet rs = pstmtRefunds.executeQuery();

            while (rs.next()) {
                int customerId = rs.getInt("CustomerID");
                double refundAmount = rs.getDouble("RefundAmount");
                System.out.println("Refunding customer " + customerId + ": $" + refundAmount);
                // Log or handle refunds here (e.g., update a refunds table or trigger refund logic)
            }

            // Step 2: Delete from Bookings table
            String sqlDeleteBookings = "DELETE FROM Bookings WHERE EventID = ?";
            PreparedStatement pstmtDeleteBookings = conn.prepareStatement(sqlDeleteBookings);
            pstmtDeleteBookings.setInt(1, eventId);
            pstmtDeleteBookings.executeUpdate();

            // Step 3: Delete from Tickets table
            String sqlDeleteTickets = "DELETE FROM Tickets WHERE EventID = ?";
            PreparedStatement pstmtDeleteTickets = conn.prepareStatement(sqlDeleteTickets);
            pstmtDeleteTickets.setInt(1, eventId);
            pstmtDeleteTickets.executeUpdate();

            // Step 4: Delete from Events table
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

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
