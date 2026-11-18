# GameZone con SQL

Copia de `1_GameZone` ya migrada de JSON a SQL (SQLite por defecto). Sigue el documento `../guias/Migracion_GameZone_JSON_a_SQL.pdf`.

```bash
mvn javafx:run                                                    # ejecutar la aplicación
mvn compile exec:java -Dexec.mainClass=cl.duoc.gamezone.MigrarJsonASql   # copiar JSON antiguos a la base (una vez)
```

Para usar MySQL, edita `src/main/resources/db.properties`.
