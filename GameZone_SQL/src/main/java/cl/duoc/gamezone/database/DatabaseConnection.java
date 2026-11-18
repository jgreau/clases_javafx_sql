package cl.duoc.gamezone.database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Único lugar que conoce la URL, el usuario y la clave. Los lee de db.properties. */
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

    public static boolean esMySql() {
        return CONFIG.getProperty("db.url", "").startsWith("jdbc:mysql");
    }

    public static Connection getConnection() throws SQLException {
        String url = CONFIG.getProperty("db.url");
        String usuario = CONFIG.getProperty("db.user", "").trim();
        if (usuario.isEmpty()) {                       // SQLite no usa usuario ni clave
            return DriverManager.getConnection(url);
        }
        return DriverManager.getConnection(url, usuario, CONFIG.getProperty("db.password"));
    }
}
