package cl.demo.biblioteca.dao;

import cl.demo.biblioteca.model.Libro;
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

/** Implementación con un archivo JSON: cada operación lee la lista, la modifica y reescribe el archivo. */
public class LibroDaoJson implements LibroDao {

    private static final Path ARCHIVO = Path.of("libros.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public List<Libro> listarTodos() {
        return leerArchivo();
    }

    @Override
    public void insertar(Libro nuevo) {
        List<Libro> libros = leerArchivo();
        int siguienteId = libros.stream().mapToInt(Libro::getId).max().orElse(0) + 1;
        nuevo.setId(siguienteId);                 // en JSON el id lo calcula el DAO
        libros.add(nuevo);
        escribirArchivo(libros);
    }

    @Override
    public void actualizar(Libro modificado) {
        List<Libro> libros = leerArchivo();
        for (int i = 0; i < libros.size(); i++) {
            if (libros.get(i).getId() == modificado.getId()) {
                libros.set(i, modificado);
                break;
            }
        }
        escribirArchivo(libros);
    }

    @Override
    public void eliminar(int id) {
        List<Libro> libros = leerArchivo();
        libros.removeIf(l -> l.getId() == id);
        escribirArchivo(libros);
    }

    private List<Libro> leerArchivo() {
        if (!Files.exists(ARCHIVO)) {
            return new ArrayList<>();
        }
        try (Reader lector = Files.newBufferedReader(ARCHIVO, StandardCharsets.UTF_8)) {
            Libro[] leidos = gson.fromJson(lector, Libro[].class);
            return leidos == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(leidos));
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException("No se pudo leer libros.json.", e);
        }
    }

    private void escribirArchivo(List<Libro> libros) {
        try (Writer escritor = Files.newBufferedWriter(ARCHIVO, StandardCharsets.UTF_8)) {
            gson.toJson(libros, escritor);
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo guardar libros.json.", e);
        }
    }
}
