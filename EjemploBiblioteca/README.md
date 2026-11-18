# EjemploBiblioteca

Proyecto JavaFX completo con la arquitectura `controller → repository → dao`.
Por defecto guarda los libros en **SQLite** (no hay que instalar nada más).

## Requisitos

- Java 21 (`java -version`)
- Maven 3.9 (`mvn -v`)
- Conexión a internet la primera vez (Maven descarga JavaFX y los drivers)

## Ejecutar la aplicación

```bash
cd EjemploBiblioteca
mvn javafx:run
```

Se abre una ventana con una tabla y un formulario. Se crea el archivo `biblioteca.db` automáticamente.

## Probar sin ventana (consola)

```bash
mvn compile exec:java -Dexec.mainClass=cl.demo.biblioteca.PruebaConsola
```

## Cambiar el almacenamiento

Editar `src/main/resources/db.properties`:

| `almacenamiento=` | Qué usa | Archivo / requisito |
|---|---|---|
| `sqlite` | `LibroDaoSql` con SQLite | `biblioteca.db` |
| `json` | `LibroDaoJson` | `libros.json` |
| `mysql` | `LibroDaoSql` con MySQL | servidor con base `biblioteca_fx` y usuario `app` |

Solo cambia esa línea: el controlador, el repositorio y la interfaz son los mismos.
