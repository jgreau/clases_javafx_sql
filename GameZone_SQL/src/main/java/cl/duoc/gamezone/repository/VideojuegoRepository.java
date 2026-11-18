package cl.duoc.gamezone.repository;

import cl.duoc.gamezone.dao.VideojuegoDao;
import cl.duoc.gamezone.model.Videojuego;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Centraliza las operaciones sobre la colección de videojuegos y delega en el DAO
 * el almacenamiento. Cada cambio se ejecuta primero en el DAO, de modo que la memoria
 * y el almacenamiento siempre coinciden.
 */
public class VideojuegoRepository {

    private final VideojuegoDao dao;
    private final List<Videojuego> videojuegos = new ArrayList<>();

    public VideojuegoRepository(VideojuegoDao dao) {
        this.dao = dao;
    }

    /**
     * Carga en memoria los datos guardados en los archivos.
     */
    public void cargar() {
        videojuegos.clear();
        videojuegos.addAll(dao.listarTodos());
    }

    /**
     * @return vista de solo lectura de la colección
     */
    public List<Videojuego> listar() {
        // Vista de solo lectura: quien la reciba puede mirar la lista pero no modificarla; los cambios pasan por agregar/eliminar.
        return Collections.unmodifiableList(videojuegos);
    }

    /**
     * Guarda el elemento en el DAO y, solo si funcionó, lo agrega a la lista en memoria.
     */
    public void agregar(Videojuego nuevo) {
        dao.insertar(nuevo);              // si falla, lanza PersistenciaException y la lista no cambia
        videojuegos.add(nuevo);
    }

    /**
     * Elimina el elemento en el DAO y, solo si funcionó, lo quita de la lista en memoria.
     */
    public void eliminar(Videojuego existente) {
        dao.eliminar(existente.getId());
        videojuegos.removeIf(v -> v.getId() == existente.getId());
    }
}
