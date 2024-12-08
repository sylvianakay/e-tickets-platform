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
import javax.servlet.http.HttpSession;

/**
 *
 * @author pswmi64
 */
@WebServlet(name = "GetBookings", urlPatterns = {"/GetBookings"})
public class GetBookings extends HttpServlet {

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
            out.println("<title>Servlet GetBookings</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet GetBookings at " + request.getContextPath() + "</h1>");
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
        HttpSession session = request.getSession(false); // Get the existing session
        if (session == null || session.getAttribute("CustomerID") == null) {
            response.setContentType("text/html");
            response.getWriter().println("<h3>Error: User not logged in.</h3>");
            return;
        }

        int customerId = (int) session.getAttribute("CustomerID"); // Get the CustomerID from the session

        response.setContentType("application/json");
        try (Connection conn = DB_Connection.getConnection()) {
            // Fetch bookings for the logged-in user
            String sql = "SELECT b.BookingID, e.EventName, t.TicketType, b.NumberOfTickets, b.BookingDate, " +
             "t.Price, (t.Price * b.NumberOfTickets) AS TotalPrice, t.TicketID " +
             "FROM Bookings b " +
             "JOIN Events e ON b.EventID = e.EventID " +
             "JOIN Tickets t ON b.TicketID = t.TicketID " +
             "WHERE b.CustomerID = ?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            // Build JSON response
            StringBuilder json = new StringBuilder("[");
            while (rs.next()) {
                if (json.length() > 1) json.append(",");
                json.append("{")
    .append("\"BookingID\":").append(rs.getInt("BookingID")).append(",")
    .append("\"EventName\":\"").append(rs.getString("EventName")).append("\",")
    .append("\"TicketType\":\"").append(rs.getString("TicketType")).append("\",")
    .append("\"NumberOfTickets\":").append(rs.getInt("NumberOfTickets")).append(",")
    .append("\"BookingDate\":\"").append(rs.getDate("BookingDate")).append("\",")
    .append("\"Price\":").append(rs.getDouble("Price")).append(",")
    .append("\"TotalPrice\":").append(rs.getDouble("TotalPrice")).append(",")
    .append("\"TicketID\":").append(rs.getInt("TicketID"))
    .append("}");

            }
            json.append("]");
            response.getWriter().println(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("{\"error\":\"" + e.getMessage() + "\"}");
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
