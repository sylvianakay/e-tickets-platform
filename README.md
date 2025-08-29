# e-Tickets Platform

## Authors
- **Sylvana Kakarontza**  
- **Eirini Griniezaki**  

## Description
A platform for ticket registration, search, and reservation.

## Project Overview
The **e-Tickets** platform allows users to register as customers, browse events, reserve tickets, and manage reservations. Administrators can manage events, tickets, and revenue monitoring.

## Technologies Used
- **Java EE / Servlets / JSP** – server-side logic  
- **Apache Tomcat 9** – application server  
- **NetBeans IDE** – development environment  
- **XAMPP / MySQL** – database management  
- **HTML / CSS / Javascript** – front-end components  

## User Functionality (Client)
- **Customer Registration:** Users provide personal information to be recorded in the system.  
- **Session Management:** After logging in, an HTTP session is maintained for 30 minutes.  
- **Ticket Search:** Customers can search available tickets by event.  
- **Ticket Reservation:** Users can select categories (VIP/General) and reserve tickets. Reservations are stored in the system.  
- **Reservation Cancellation:** Users can cancel their reservations, simulating a refund.  


## Administrator Functionality
- **Database Management:** Initialize and delete the database.  
- **Event Entry:** Add events along with VIP and General ticket availability.  
- **Ticket Entry:** Insert tickets simultaneously with event creation.  
- **Availability Monitoring:** Track ticket availability and reservations.  
- **Revenue Monitoring:** Track income from reservations.  
- **Event Cancellation:** Cancel events with simulated refunds and update tables accordingly.  
- **Admin Login:** Admin uses predefined credentials (`admin` / `1234567890`), not stored in the database.  

> Other administrative operations are included to manage the platform efficiently.  

## Setup and Running the Project

### Install XAMPP and Start MySQL
1. Open **phpMyAdmin** to create the database `etickets`.  
2. Create the required tables: `Customers`, `Events`, `Tickets`, `Reservations` with appropriate columns.  

### Set Up NetBeans Project
1. Open **NetBeans → File → Open Project**.  
2. Select the `testing` folder containing `nbproject`.  
3. run
