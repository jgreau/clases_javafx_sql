package cl.duoc.gamezone;

import cl.duoc.gamezone.dao.VideojuegoDao;
import cl.duoc.gamezone.dao.VideojuegoJsonDao;
import cl.duoc.gamezone.dao.VideojuegoSqlDao;
import cl.duoc.gamezone.model.Videojuego;

/** Se ejecuta UNA vez: copia los videojuegos de los archivos JSON a la base de datos. */
public class MigrarJsonASql {

    public static void main(String[] args) {
        VideojuegoDao origen = new VideojuegoJsonDao();
        VideojuegoDao destino = new VideojuegoSqlDao();

        int copiados = 0;
        for (Videojuego v : origen.listarTodos()) {
            destino.insertar(v);               // la base de datos le asigna un id nuevo
            copiados++;
        }
        System.out.println("Videojuegos copiados: " + copiados);
    }
}
