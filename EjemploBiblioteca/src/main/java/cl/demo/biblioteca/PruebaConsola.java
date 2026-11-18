package cl.demo.biblioteca;

import cl.demo.biblioteca.dao.FabricaDao;
import cl.demo.biblioteca.model.Libro;
import cl.demo.biblioteca.repository.LibroRepository;

/** Prueba rápida sin ventana: inserta, actualiza y elimina usando el mismo repositorio. */
public class PruebaConsola {
    public static void main(String[] args) {
        LibroRepository repo = new LibroRepository(FabricaDao.crear());
        repo.cargar();
        System.out.println("Libros al empezar: " + repo.listar().size());

        Libro l = new Libro(0, "Java en 24 horas", "Ana Pérez", 15990);
        repo.agregar(l);
        System.out.println("Insertado con id " + l.getId());

        repo.actualizar(new Libro(l.getId(), "Java en 24 horas (2ª ed.)", "Ana Pérez", 17990));
        repo.cargar();
        repo.listar().forEach(x -> System.out.println("  " + x.getId() + " | " + x.getTitulo() + " | " + x.getPrecio()));

        repo.eliminar(l);
        System.out.println("Libros al terminar: " + repo.listar().size());
    }
}
