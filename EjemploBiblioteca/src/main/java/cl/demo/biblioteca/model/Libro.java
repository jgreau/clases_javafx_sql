package cl.demo.biblioteca.model;

public class Libro {
    private int id;
    private String titulo;
    private String autor;
    private double precio;

    public Libro() { }                       // Gson necesita un constructor vacío

    public Libro(int id, String titulo, String autor, double precio) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.precio = precio;
    }

    public int getId()          { return id; }
    public void setId(int id)   { this.id = id; }
    public String getTitulo()   { return titulo; }
    public String getAutor()    { return autor; }
    public double getPrecio()   { return precio; }
}
