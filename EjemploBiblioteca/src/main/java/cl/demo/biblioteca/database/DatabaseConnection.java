package cl.demo.biblioteca.database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Único lugar que conoce la URL, el usuario y la clave. Lee db.properties. */
public class DatabaseConnection {

    private static final Properties CONFIG = new Properties();

    static {
        try (InputStream in = DatabaseConnection.class.getResourceAsStream("/db.properties")) {
            CONFIG.load(in);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private DatabaseConnection() { }

    /** "sqlite", "json" o "mysql", según db.properties. */
    public static String almacenamiento() {
        return CONFIG.getProperty("almacenamiento", "sqlite").trim().toLowerCase();
    }

    public static boolean esMySql() {
        return almacenamiento().equals("mysql");
    }

    public static Connection getConnection() throws SQLException {
        if (esMySql()) {
            return DriverManager.getConnection(
                    CONFIG.getProperty("mysql.url"),
                    CONFIG.getProperty("mysql.user"),
                    CONFIG.getProperty("mysql.password"));
        }
        return DriverManager.getConnection(CONFIG.getProperty("sqlite.url"));
    }
}
