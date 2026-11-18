package cl.demo.biblioteca.repository;

import cl.demo.biblioteca.dao.LibroDao;
import cl.demo.biblioteca.model.Libro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Guarda la lista en memoria y delega en el DAO. Primero el DAO; la lista solo cambia si funcionó. */
public class LibroRepository {

    private final LibroDao dao;                         // la interfaz, no una clase concreta
    private final List<Libro> libros = new ArrayList<>();

    public LibroRepository(LibroDao dao) {
        this.dao = dao;
    }

    public void cargar() {
        libros.clear();
        libros.addAll(dao.listarTodos());
    }

    public List<Libro> listar() {
        return Collections.unmodifiableList(libros);    // vista de solo lectura
    }

    public void agregar(Libro nuevo) {
        dao.insertar(nuevo);                            // si falla, lanza y la lista no cambia
        libros.add(nuevo);
    }

    public void actualizar(Libro modificado) {
        dao.actualizar(modificado);
        for (int i = 0; i < libros.size(); i++) {
            if (libros.get(i).getId() == modificado.getId()) {
                libros.set(i, modificado);
                return;
            }
        }
    }

    public void eliminar(Libro existente) {
        dao.eliminar(existente.getId());
        libros.removeIf(l -> l.getId() == existente.getId());
    }
}
