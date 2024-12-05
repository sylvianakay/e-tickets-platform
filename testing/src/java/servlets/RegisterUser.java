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

//    /**
//     * Handles the HTTP <code>POST</code> method.
//     *
//     * @param request servlet request
//     * @param response servlet response
//     * @throws ServletException if a servlet-specific error occurs
//     * @throws IOException if an I/O error occurs
//     */
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        processRequest(request, response);
//    }
//
//    /**
//     * Returns a short description of the servlet.
//     *
//     * @return a String containing servlet description
//     */
//    @Override
//    public String getServletInfo() {
//        return "Short description";
//    }// </editor-fold>
//
//}


    
    
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

            response.getWriter().println("Customer added successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }


private String extractValue(String json, String fieldName) {
    String[] fields = json.replace("{", "").replace("}", "").replace("\"", "").split(",");
    for (String field : fields) {
        String[] keyValue = field.split(":");
        if (keyValue[0].trim().equals(fieldName)) {
            return keyValue[1].trim();
        }
    }
    throw new IllegalArgumentException("Field " + fieldName + " not found in JSON.");
}

    /**
     * Manually parses a JSON string into a User object.
     *
     * @param jsonData The JSON string to parse.
     * @return A User object populated with data from the JSON.
     * @throws IllegalArgumentException If required fields are missing or invalid.
     */
//    private User parseJsonToUser(String jsonData) {
//        User user = new User();
//
//        try {
//            // Extract fields manually
//            String[] fields = jsonData.replace("{", "").replace("}", "").replace("\"", "").split(",");
//
//            for (String field : fields) {
//                String[] keyValue = field.split(":");
//                String key = keyValue[0].trim();
//                String value = keyValue[1].trim();
//
//                switch (key) {
//                    case "username":
//                        user.setUsername(value);
//                        break;
//                    case "email":
//                        user.setEmail(value);
//                        break;
//                    case "password":
//                        user.setPassword(value);
//                        break;
//                    case "firstname":
//                        user.setFirstname(value);
//                        break;
//                    case "lastname":
//                        user.setLastname(value);
//                        break;
//                    case "card":
//                        user.setCard(value);
//                        break;
//                    default:
//                        //throw new IllegalArgumentException("Unknown field: " + key);
//                }
//            }
//        } catch (Exception e) {
//            throw new IllegalArgumentException("Error parsing JSON: " + e.getMessage());
//        }
//
//        return user;
//    }

    /**
     * Checks if a specific field is available (not already in use).
     *
     * @param field       The field to check (e.g., "username", "email", "telephone").
     * @param value       The value of the field.
     * @param editUsers   Instance of EditUsersTable to perform the database query.
     * @return True if the field is available, false otherwise.
     */
//    private boolean isFieldAvailable(String field, String value, EditUsersTable editUsers) {
//        String query = String.format("SELECT COUNT(*) FROM users WHERE %s = '%s'", field, value); // Treat as not available in case of an error
//        return editUsers.isFieldAvailable(query);
//    }
}