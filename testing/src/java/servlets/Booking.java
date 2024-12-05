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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author pswmi64
 */
@WebServlet(name = "Booking", urlPatterns = {"/Booking"})
public class Booking extends HttpServlet {

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
            out.println("<title>Servlet Booking</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Booking at " + request.getContextPath() + "</h1>");
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
        int customerId = Integer.parseInt(request.getParameter("customerId"));
        int eventId = Integer.parseInt(request.getParameter("eventId"));
        int numberOfTickets = Integer.parseInt(request.getParameter("numberOfTickets"));
        java.sql.Date bookingDate = new java.sql.Date(System.currentTimeMillis()); // Current date

        Connection conn = null;

        try {
            conn = DB_Connection.getConnection();
            conn.setAutoCommit(false); // Disable auto-commit for transactional behavior

            // Step 1: Find a TicketID with enough availability
            String sqlFindTicket = "SELECT TicketID, Availability FROM Tickets WHERE EventID = ? AND Availability >= ? LIMIT 1";
            PreparedStatement pstmtFind = conn.prepareStatement(sqlFindTicket);
            pstmtFind.setInt(1, eventId);
            pstmtFind.setInt(2, numberOfTickets);
            ResultSet rs = pstmtFind.executeQuery();

            if (!rs.next()) {
                throw new SQLException("No available tickets found for the specified EventID and requested quantity.");
            }

            int ticketId = rs.getInt("TicketID");
            int currentAvailability = rs.getInt("Availability");

            // Step 2: Deduct the number of tickets from the ticket's availability
            String sqlUpdateAvailability = "UPDATE Tickets SET Availability = Availability - ? WHERE TicketID = ?";
            PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdateAvailability);
            pstmtUpdate.setInt(1, numberOfTickets);
            pstmtUpdate.setInt(2, ticketId);
            pstmtUpdate.executeUpdate();

            // Step 3: Insert the booking into the Bookings table
            String sqlInsertBooking = "INSERT INTO Bookings (CustomerID, EventID, TicketID, BookingDate, NumberOfTickets) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmtBooking = conn.prepareStatement(sqlInsertBooking);
            pstmtBooking.setInt(1, customerId);
            pstmtBooking.setInt(2, eventId);
            pstmtBooking.setInt(3, ticketId);
            pstmtBooking.setDate(4, bookingDate);
            pstmtBooking.setInt(5, numberOfTickets);
            pstmtBooking.executeUpdate();

            conn.commit(); // Commit transaction

            // Success response
            response.setContentType("text/html");
            response.getWriter().println("<h3>Tickets booked successfully!</h3>");
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction on failure
                } catch (SQLException ex) {
                    Logger.getLogger(Booking.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            response.setContentType("text/html");
            response.getWriter().println("<h3>Error: " + e.getMessage() + "</h3>");
        } finally {
            if (conn != null) {
                try {
                    conn.close(); // Close the connection
                } catch (SQLException ex) {
                    Logger.getLogger(Booking.class.getName()).log(Level.SEVERE, null, ex);
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
