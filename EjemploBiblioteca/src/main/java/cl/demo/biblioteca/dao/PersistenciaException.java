package cl.demo.biblioteca.dao;

/** Error al leer o escribir en el medio de almacenamiento (archivo o base de datos). */
public class PersistenciaException extends RuntimeException {
    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
