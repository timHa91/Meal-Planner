package mealplanner.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseService {
    final static String DB_URL = "jdbc:postgresql:meals_db";
    final static String USER = "postgres";
    final static String PASS = "1111";
    private static Connection connection;
    // Creates a DB Connection
    public static Connection connect() throws SQLException {
        if(connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            initializeDatabase(connection);
        }
        return connection;
    }
    public static void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // Init DB with meals and ingredients tables
    public static void initializeDatabase(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS meals(" +
                    "meal_id INTEGER PRIMARY KEY," +
                    "category VARCHAR(30) NOT NULL," +
                    "meal VARCHAR(30) NOT NULL)");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS ingredients(" +
                    "ingredient_id INTEGER PRIMARY KEY," +
                    "meal_id INTEGER," +
                    "ingredient VARCHAR(100) NOT NULL)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
