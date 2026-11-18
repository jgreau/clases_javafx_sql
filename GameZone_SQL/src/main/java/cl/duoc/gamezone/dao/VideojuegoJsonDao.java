package cl.duoc.gamezone.dao;

import cl.duoc.gamezone.model.JuegoDigital;
import cl.duoc.gamezone.model.JuegoFisico;
import cl.duoc.gamezone.model.Videojuego;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementación de {@link VideojuegoDao} con dos archivos JSON (uno por subtipo).
 * Cada operación lee los archivos, modifica la lista y los vuelve a escribir.
 */
public class VideojuegoJsonDao implements VideojuegoDao {

    private static final Path ARCHIVO_DIGITALES = Path.of("juegos_digitales.json");
    private static final Path ARCHIVO_FISICOS = Path.of("juegos_fisicos.json");

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public List<Videojuego> listarTodos() {
        return leerTodos();
    }

    @Override
    public void insertar(Videojuego nuevo) {
        List<Videojuego> todos = leerTodos();
        nuevo.setId(siguienteId(todos));           // en JSON el id lo calcula el DAO
        todos.add(nuevo);
        escribirTodos(todos);
    }

    @Override
    public void eliminar(int id) {
        List<Videojuego> todos = leerTodos();
        todos.removeIf(v -> v.getId() == id);
        escribirTodos(todos);
    }

    // ---- detalles de los archivos: nadie fuera de esta clase los conoce ----

    private List<Videojuego> leerTodos() {
        List<Videojuego> todos = new ArrayList<>();
        todos.addAll(leer(ARCHIVO_DIGITALES, JuegoDigital[].class));
        todos.addAll(leer(ARCHIVO_FISICOS, JuegoFisico[].class));
        // Los archivos antiguos no traen id (queda en 0): se les asigna uno para poder eliminarlos por id.
        for (Videojuego v : todos) {
            if (v.getId() == 0) {
                v.setId(siguienteId(todos));
            }
        }
        return todos;
    }

    private int siguienteId(List<Videojuego> todos) {
        return todos.stream().mapToInt(Videojuego::getId).max().orElse(0) + 1;
    }

    private <T extends Videojuego> List<T> leer(Path archivo, Class<T[]> tipoArreglo) {
        if (!Files.exists(archivo)) {
            return new ArrayList<>();
        }
        try (Reader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            T[] leidos = gson.fromJson(lector, tipoArreglo);
            return leidos == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(leidos));
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException("No se pudo leer el archivo " + archivo + ".", e);
        }
    }

    private void escribirTodos(List<Videojuego> todos) {
        List<JuegoDigital> digitales = new ArrayList<>();
        List<JuegoFisico> fisicos = new ArrayList<>();
        for (Videojuego v : todos) {
            if (v instanceof JuegoDigital d) {
                digitales.add(d);
            } else if (v instanceof JuegoFisico f) {
                fisicos.add(f);
            }
        }
        escribir(ARCHIVO_DIGITALES, digitales);
        escribir(ARCHIVO_FISICOS, fisicos);
    }

    private void escribir(Path archivo, List<?> datos) {
        try (Writer escritor = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            gson.toJson(datos, escritor);
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo guardar el archivo " + archivo + ".", e);
        }
    }
}
