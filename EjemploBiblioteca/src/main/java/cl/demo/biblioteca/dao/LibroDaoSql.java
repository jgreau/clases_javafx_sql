package cl.demo.biblioteca.dao;

import cl.demo.biblioteca.database.DatabaseConnection;
import cl.demo.biblioteca.model.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Implementación con JDBC. Funciona con SQLite o MySQL según db.properties. */
public class LibroDaoSql implements LibroDao {

    public LibroDaoSql() {
        crearTablaSiNoExiste();
    }

    /** Para que el ejemplo funcione sin preparar nada a mano. */
    private void crearTablaSiNoExiste() {
        String sql = DatabaseConnection.esMySql()
                ? "CREATE TABLE IF NOT EXISTS libro ("
                  + "id INT AUTO_INCREMENT PRIMARY KEY, titulo VARCHAR(120) NOT NULL, "
                  + "autor VARCHAR(80) NOT NULL, precio DECIMAL(10,2) NOT NULL)"
                : "CREATE TABLE IF NOT EXISTS libro ("
                  + "id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT NOT NULL, "
                  + "autor TEXT NOT NULL, precio REAL NOT NULL)";

        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo preparar la base de datos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Libro> listarTodos() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT id, titulo, autor, precio FROM libro ORDER BY titulo";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                libros.add(new Libro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getDouble("precio")));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudieron leer los libros.", e);
        }
        return libros;
    }

    @Override
    public void insertar(Libro l) {
        String sql = "INSERT INTO libro(titulo, autor, precio) VALUES (?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, l.getTitulo());
            ps.setString(2, l.getAutor());
            ps.setDouble(3, l.getPrecio());
            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {      // el id lo asignó la BD
                if (claves.next()) {
                    l.setId(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo guardar el libro.", e);
        }
    }

    @Override
    public void actualizar(Libro l) {
        String sql = "UPDATE libro SET titulo = ?, autor = ?, precio = ? WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, l.getTitulo());
            ps.setString(2, l.getAutor());
            ps.setDouble(3, l.getPrecio());
            ps.setInt(4, l.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo actualizar el libro.", e);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM libro WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo eliminar el libro.", e);
        }
    }
}
