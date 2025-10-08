import java.sql.*;

public class DBHelper {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "expense_manager";
    private static final String USER = "root";      
    private static final String PASSWORD = "root";  

    
    public static Connection connectServer() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

    public static Connection connectDB() throws SQLException {
        return DriverManager.getConnection(DB_URL + DB_NAME + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", USER, PASSWORD);
    }

    
    public static void createDatabase() {
        try (Connection conn = connectServer();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            System.out.println("Database ready ✅");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public static void createTable() {
        try (Connection conn = connectDB();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS expenses (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "date VARCHAR(15)," +
                    "description VARCHAR(255)," +
                    "amount DOUBLE," +
                    "category VARCHAR(100))");
            System.out.println("Table ready ✅");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
