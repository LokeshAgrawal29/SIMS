# SIMS🚀 Smart Inventory Management System

🌟 Overview

The Smart Inventory Management System is a robust and efficient application developed using React.js for the frontend and Spring Boot for the backend, with PostgreSQL as the database. It offers a comprehensive dashboard for both users and admins to manage inventory seamlessly.

✨ Features

📊 Dashboard: Overview of inventory statistics.

🗃️ Inventory Management: CRUD operations for items in the inventory.

👥 User Roles: Separate functionalities for users and admins.

📑 Reports: View detailed reports of the inventory.

💾 Download Reports: Admins can download inventory reports in CSV format.

🔐 User Management: Admins can access and manage all user details.

🛠️ Technologies Used

Frontend

⚛️ React.js

🎨 Tailwind CSS (for styling)

Backend

☕ Spring Boot

🐘 PostgreSQL

📥 Installation

Prerequisites

🖥️ Node.js

🪶 Java 17

🐘 PostgreSQL

🛠️ Maven

Frontend Setup

cd frontend
npm install
npm start

Backend Setup

cd backend
mvn clean install
mvn spring-boot:run

Database Setup

📂 Install PostgreSQL.

📝 Create a database named smart_inventory.

🛠️ Update the database configurations in application.properties.

🚪 Usage

🌐 Access the application at http://localhost:5173 after starting both the frontend and backend.

🔑 Admins can manage users and download reports.

👥 Users can view and update inventory based on permissions.
