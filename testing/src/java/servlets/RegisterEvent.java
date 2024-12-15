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
@WebServlet(name = "RegisterEvent", urlPatterns = {"/RegisterEvent"})
public class RegisterEvent extends HttpServlet {

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
           
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet RegisterEvent</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RegisterEvent at " + request.getContextPath() + "</h1>");
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
       String eventName = request.getParameter("eventName");
    String eventDate = request.getParameter("eventDate");
    String eventTime = request.getParameter("eventTime");
    String eventType = request.getParameter("eventType");

    int generalCapacity = Integer.parseInt(request.getParameter("generalCapacity"));
    double generalPrice = Double.parseDouble(request.getParameter("generalPrice"));
    int vipCapacity = Integer.parseInt(request.getParameter("vipCapacity"));
    double vipPrice = Double.parseDouble(request.getParameter("vipPrice"));

    Connection conn = null;

    try {
        conn = DB_Connection.getConnection();
        conn.setAutoCommit(false); // Disable auto-commit

        // Insert event into the Events table
        String sqlEvent = "INSERT INTO Events (EventName, EventDate, EventTime, EventType, Capacity) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmtEvent = conn.prepareStatement(sqlEvent, PreparedStatement.RETURN_GENERATED_KEYS);
        pstmtEvent.setString(1, eventName);
        pstmtEvent.setString(2, eventDate);
        pstmtEvent.setString(3, eventTime);
        pstmtEvent.setString(4, eventType);
        pstmtEvent.setInt(5, generalCapacity + vipCapacity); // Total capacity
        pstmtEvent.executeUpdate();

        ResultSet rsEvent = pstmtEvent.getGeneratedKeys();
        int eventId = 0;
        if (rsEvent.next()) {
            eventId = rsEvent.getInt(1);
        } else {
            throw new SQLException("Failed to retrieve EventID.");
        }

        String sqlTickets = "INSERT INTO Tickets (EventID, TicketType, Price, Availability) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmtTickets = conn.prepareStatement(sqlTickets);
        pstmtTickets.setInt(1, eventId);
        pstmtTickets.setString(2, "General");
        pstmtTickets.setDouble(3, generalPrice);
        pstmtTickets.setInt(4, generalCapacity);
        pstmtTickets.executeUpdate();
        
        pstmtTickets.setString(2, "VIP");
        pstmtTickets.setDouble(3, vipPrice);
        pstmtTickets.setInt(4, vipCapacity);
        pstmtTickets.executeUpdate();

        conn.commit(); // Commit the transaction

        // Success response
//        response.setContentType("text/html");
//        response.getWriter().println("<h3>Event and Tickets Registered Successfully!</h3>");
        response.sendRedirect("admin.html");
    } catch (Exception e) {
        e.printStackTrace();
        if (conn != null) {
            try {
                conn.rollback(); // Rollback on error
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
        response.setContentType("text/html");
        response.getWriter().println("<h3>Error: " + e.getMessage() + "</h3>");
    } finally {
        if (conn != null) {
            try {
                conn.close(); // Close connection
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
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
