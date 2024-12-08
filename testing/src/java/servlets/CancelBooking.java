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
@WebServlet(name = "CancelBooking", urlPatterns = {"/CancelBooking"})
public class CancelBooking extends HttpServlet {

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
            out.println("<title>Servlet CancelBooking</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CancelBooking at " + request.getContextPath() + "</h1>");
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
        int bookingId = Integer.parseInt(request.getParameter("bookingId"));
        int numberOfTickets = Integer.parseInt(request.getParameter("numberOfTickets"));
        int ticketId = Integer.parseInt(request.getParameter("ticketId"));

        Connection conn = null;

        try {
            conn = DB_Connection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Step 1: Delete the booking
            String deleteBookingSql = "DELETE FROM Bookings WHERE BookingID = ?";
            PreparedStatement pstmtDelete = conn.prepareStatement(deleteBookingSql);
            pstmtDelete.setInt(1, bookingId);
            pstmtDelete.executeUpdate();

            // Step 2: Update ticket availability
            String updateAvailabilitySql = "UPDATE Tickets SET Availability = Availability + ? WHERE TicketID = ?";
            PreparedStatement pstmtUpdate = conn.prepareStatement(updateAvailabilitySql);
            pstmtUpdate.setInt(1, numberOfTickets);
            pstmtUpdate.setInt(2, ticketId);
            pstmtUpdate.executeUpdate();

            conn.commit(); // Commit transaction

            // Success response
            response.setContentType("application/json");
            response.getWriter().println("{\"message\":\"Booking cancelled successfully.\"}");
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    Logger.getLogger(CancelBooking.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            response.setContentType("application/json");
            response.getWriter().println("{\"error\":\"" + e.getMessage() + "\"}");
        } finally {
            if (conn != null) {
                try {
                    conn.close(); // Close connection
                } catch (SQLException ex) {
                    Logger.getLogger(CancelBooking.class.getName()).log(Level.SEVERE, null, ex);
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
