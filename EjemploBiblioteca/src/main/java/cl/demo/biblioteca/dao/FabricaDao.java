package cl.demo.biblioteca.dao;

import cl.demo.biblioteca.database.DatabaseConnection;

/** Elige la implementación de LibroDao según db.properties. Es lo único que nombra las clases concretas. */
public class FabricaDao {

    private FabricaDao() { }

    public static LibroDao crear() {
        if (DatabaseConnection.almacenamiento().equals("json")) {
            return new LibroDaoJson();
        }
        return new LibroDaoSql();       // sqlite o mysql
    }
}
