package cl.demo.biblioteca.dao;

import cl.demo.biblioteca.model.Libro;
import java.util.List;

/** Lo que la aplicación necesita del almacenamiento. No dice nada de archivos ni de SQL. */
public interface LibroDao {
    List<Libro> listarTodos();
    void insertar(Libro libro);        // deja en el libro el id que se le asignó
    void actualizar(Libro libro);
    void eliminar(int id);
}
