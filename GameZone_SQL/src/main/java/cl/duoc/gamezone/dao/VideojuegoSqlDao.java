package cl.duoc.gamezone.dao;

import cl.duoc.gamezone.database.DatabaseConnection;
import cl.duoc.gamezone.model.JuegoDigital;
import cl.duoc.gamezone.model.JuegoFisico;
import cl.duoc.gamezone.model.Videojuego;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de {@link VideojuegoDao} con SQL (SQLite o MySQL).
 * Los dos subtipos van en una sola tabla: la columna "tipo" dice cuál es,
 * y las columnas que no corresponden al tipo quedan en NULL.
 */
public class VideojuegoSqlDao implements VideojuegoDao {

    private static final String TIPO_DIGITAL = "DIGITAL";
    private static final String TIPO_FISICO = "FISICO";

    public VideojuegoSqlDao() {
        crearTablaSiNoExiste();
    }

    @Override
    public List<Videojuego> listarTodos() {
        List<Videojuego> lista = new ArrayList<>();
        String sql = "SELECT id, tipo, titulo, anio_lanzamiento, tamanio_gb, plataforma, "
                   + "parche_dia_uno, acceso_anticipado, dlc_incluidos FROM videojuego ORDER BY id";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudieron leer los videojuegos.", e);
        }
        return lista;
    }

    @Override
    public void insertar(Videojuego v) {
        String sql = "INSERT INTO videojuego(tipo, titulo, anio_lanzamiento, tamanio_gb, "
                   + "plataforma, parche_dia_uno, acceso_anticipado, dlc_incluidos) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(2, v.getTitulo());
            ps.setInt(3, v.getAnioLanzamiento());
            ps.setDouble(4, v.getTamanioGb());

            if (v instanceof JuegoDigital d) {
                ps.setString(1, TIPO_DIGITAL);
                ps.setString(5, d.getPlataforma());
                ps.setBoolean(6, d.isParcheDiaUno());
                ps.setBoolean(7, d.tieneAccesoAnticipado());
                ps.setNull(8, Types.INTEGER);                    // dlc no aplica a un juego digital
            } else if (v instanceof JuegoFisico f) {
                ps.setString(1, TIPO_FISICO);
                ps.setNull(5, Types.VARCHAR);                    // estos tres no aplican a un juego físico
                ps.setNull(6, Types.BOOLEAN);
                ps.setNull(7, Types.BOOLEAN);
                ps.setInt(8, f.getDlcIncluidos());
            }
            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {     // el id lo asignó la base de datos
                if (claves.next()) {
                    v.setId(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo guardar el videojuego.", e);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM videojuego WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo eliminar el videojuego.", e);
        }
    }

    /** Convierte una fila del ResultSet en el subtipo que indica la columna "tipo". */
    private Videojuego mapear(ResultSet rs) throws SQLException {
        String titulo = rs.getString("titulo");
        int anio = rs.getInt("anio_lanzamiento");
        double tamanio = rs.getDouble("tamanio_gb");

        Videojuego resultado;
        if (TIPO_DIGITAL.equals(rs.getString("tipo"))) {
            JuegoDigital digital = new JuegoDigital(titulo, anio, tamanio,
                    rs.getString("plataforma"), rs.getBoolean("parche_dia_uno"));
            if (rs.getBoolean("acceso_anticipado")) {
                digital.activarAccesoAnticipado();
            }
            resultado = digital;
        } else {
            resultado = new JuegoFisico(titulo, anio, tamanio, rs.getInt("dlc_incluidos"));
        }
        resultado.setId(rs.getInt("id"));
        return resultado;
    }

    /** Crea la tabla la primera vez, para que la aplicación funcione sin preparar nada a mano. */
    private void crearTablaSiNoExiste() {
        String sql = DatabaseConnection.esMySql()
                ? "CREATE TABLE IF NOT EXISTS videojuego ("
                  + "id INT AUTO_INCREMENT PRIMARY KEY, tipo VARCHAR(10) NOT NULL, "
                  + "titulo VARCHAR(150) NOT NULL, anio_lanzamiento INT NOT NULL, "
                  + "tamanio_gb DOUBLE NOT NULL, plataforma VARCHAR(60), "
                  + "parche_dia_uno BOOLEAN, acceso_anticipado BOOLEAN, dlc_incluidos INT)"
                : "CREATE TABLE IF NOT EXISTS videojuego ("
                  + "id INTEGER PRIMARY KEY AUTOINCREMENT, tipo TEXT NOT NULL, "
                  + "titulo TEXT NOT NULL, anio_lanzamiento INTEGER NOT NULL, "
                  + "tamanio_gb REAL NOT NULL, plataforma TEXT, "
                  + "parche_dia_uno INTEGER, acceso_anticipado INTEGER, dlc_incluidos INTEGER)";

        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo preparar la base de datos.", e);
        }
    }
}
