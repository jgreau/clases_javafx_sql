package cl.duoc.gamezone.dao;

import cl.duoc.gamezone.model.Videojuego;
import java.util.List;

/**
 * Operaciones que necesita el repositorio, sin importar dónde se guarden los datos
 * (archivos JSON, SQLite o MySQL).
 */
public interface VideojuegoDao {

    List<Videojuego> listarTodos();

    /** Guarda el videojuego y le deja asignado su id. */
    void insertar(Videojuego videojuego);

    void eliminar(int id);
}
