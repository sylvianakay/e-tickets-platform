/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlets;

import database.DB_Connection;
import java.io.BufferedReader;
import java.sql.*;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.System.console;
import java.sql.SQLException;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 *
 * @author pswmi64
 */
@WebServlet(name = "RegisterUser", urlPatterns = {"/RegisterUser"})
public class RegisterUser extends HttpServlet {


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
            out.println("<title>Servlet RegisterUser</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RegisterUser at " + request.getContextPath() + "</h1>");
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
    
    @Override 
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
         String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String creditCardInfo = request.getParameter("creditCardInfo");
        String Password = request.getParameter("password");

        try (Connection conn = DB_Connection.getConnection()) {
            String sql = "INSERT INTO Customers (FullName, Email, CreditCardInfo, Password) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, fullName);
            pstmt.setString(2, email);
            pstmt.setString(3, creditCardInfo);
            pstmt.setString(4, Password);
            pstmt.executeUpdate();

            response.sendRedirect("index.html");
        } catch (Exception e) {
            response.getWriter().println("Error: " + e.getMessage());
        }
    }

}