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
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author pswmi64
 */
@WebServlet(name = "AvailableTickets", urlPatterns = {"/AvailableTickets"})
public class AvailableTickets extends HttpServlet {

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
            out.println("<title>Servlet AvailableTickets</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AvailableTickets at " + request.getContextPath() + "</h1>");
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
    int eventId = Integer.parseInt(request.getParameter("eventId"));

    try (Connection conn = DB_Connection.getConnection()) {
        // Check if the event exists
        String sqlCheckEvent = "SELECT EventName FROM Events WHERE EventID = ?";
        PreparedStatement pstmtCheckEvent = conn.prepareStatement(sqlCheckEvent);
        pstmtCheckEvent.setInt(1, eventId);
        ResultSet rsEvent = pstmtCheckEvent.executeQuery();

        if (!rsEvent.next()) {
            // Event does not exist
            response.setContentType("text/html");
            response.getWriter().println("<h3>Error: Event ID " + eventId + " does not exist.</h3>");
            return;
        }

        String eventName = rsEvent.getString("EventName");

        // Query available tickets
        String sql = "SELECT TicketID, TicketType, Price, Availability FROM Tickets WHERE EventID = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, eventId);
        ResultSet rs = pstmt.executeQuery();

        // Build HTML response
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><head><title>Available Tickets</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; background-color: #f4f4f9; color: #333; }");
        out.println("h3 { color: #4CAF50; text-align: center; }");
        out.println("table { width: 80%; margin: 20px auto; border-collapse: collapse; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }");
        out.println("table th, table td { padding: 10px; text-align: left; border: 1px solid #ddd; }");
        out.println("table th { background-color: #4CAF50; color: white; }");
        out.println("table tr:nth-child(even) { background-color: #f9f9f9; }");
        out.println("table tr:hover { background-color: #f1f1f1; }");
        out.println("</style></head><body>");

        out.println("<h3>Available Tickets for Event: " + eventName + "</h3>");
        out.println("<table>");
        out.println("<tr><th>Ticket ID</th><th>Type</th><th>Price</th><th>Availability</th></tr>");

        boolean hasTickets = false; // Check if tickets exist for the event
        while (rs.next()) {
            hasTickets = true;
            out.println("<tr>");
            out.println("<td>" + rs.getInt("TicketID") + "</td>");
            out.println("<td>" + rs.getString("TicketType") + "</td>");
            out.println("<td>$" + rs.getDouble("Price") + "</td>");
            out.println("<td>" + rs.getInt("Availability") + "</td>");
            out.println("</tr>");
        }

        if (!hasTickets) {
            out.println("<tr><td colspan='4' style='text-align:center;'>No tickets available for this event.</td></tr>");
        }

        out.println("</table>");
        out.println("</body></html>");
    } catch (Exception e) {
        e.printStackTrace();
        response.setContentType("text/html");
        response.getWriter().println("<h3>Error: " + e.getMessage() + "</h3>");
    }
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
        processRequest(request, response);
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
