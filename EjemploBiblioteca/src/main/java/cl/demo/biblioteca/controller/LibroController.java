package cl.demo.biblioteca.controller;

import cl.demo.biblioteca.dao.FabricaDao;
import cl.demo.biblioteca.dao.PersistenciaException;
import cl.demo.biblioteca.database.DatabaseConnection;
import cl.demo.biblioteca.model.Libro;
import cl.demo.biblioteca.repository.LibroRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/** Solo habla con el repositorio. No conoce el DAO concreto ni java.sql. */
public class LibroController {

    @FXML private TableView<Libro> tablaLibros;
    @FXML private TableColumn<Libro, Integer> colId;
    @FXML private TableColumn<Libro, String>  colTitulo;
    @FXML private TableColumn<Libro, String>  colAutor;
    @FXML private TableColumn<Libro, Double>  colPrecio;
    @FXML private TextField txtTitulo, txtAutor, txtPrecio;
    @FXML private Label lblAlmacenamiento;

    private final LibroRepository repositorio = new LibroRepository(FabricaDao.crear());
    private final ObservableList<Libro> datos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        tablaLibros.setItems(datos);
        lblAlmacenamiento.setText("Almacenamiento: " + DatabaseConnection.almacenamiento());

        try {
            repositorio.cargar();
            refrescarTabla();
        } catch (PersistenciaException e) {
            mostrarError(e.getMessage());
        }
    }

    private void refrescarTabla() {
        datos.setAll(repositorio.listar());
    }

    @FXML
    private void seleccionarLibro() {
        Libro sel = tablaLibros.getSelectionModel().getSelectedItem();
        if (sel != null) {
            txtTitulo.setText(sel.getTitulo());
            txtAutor.setText(sel.getAutor());
            txtPrecio.setText(String.valueOf(sel.getPrecio()));
        }
    }

    @FXML
    private void guardarLibro() {
        try {
            repositorio.agregar(leerFormulario(0));
            refrescarTabla();
            limpiarFormulario();
        } catch (IllegalArgumentException | PersistenciaException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void actualizarLibro() {
        Libro sel = tablaLibros.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarError("Selecciona un libro de la tabla.");
            return;
        }
        try {
            repositorio.actualizar(leerFormulario(sel.getId()));
            refrescarTabla();
            limpiarFormulario();
        } catch (IllegalArgumentException | PersistenciaException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void eliminarLibro() {
        Libro sel = tablaLibros.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarError("Selecciona un libro de la tabla.");
            return;
        }
        try {
            repositorio.eliminar(sel);
            refrescarTabla();
            limpiarFormulario();
        } catch (PersistenciaException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void limpiarFormulario() {
        txtTitulo.clear();
        txtAutor.clear();
        txtPrecio.clear();
        tablaLibros.getSelectionModel().clearSelection();
    }

    /** Valida el formulario y arma el libro; lanza IllegalArgumentException si hay datos inválidos. */
    private Libro leerFormulario(int id) {
        String titulo = txtTitulo.getText().trim();
        String autor = txtAutor.getText().trim();
        if (titulo.isEmpty() || autor.isEmpty()) {
            throw new IllegalArgumentException("El título y el autor son obligatorios.");
        }
        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            return new Libro(id, titulo, autor, precio);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El precio debe ser un número (ejemplo: 12990).");
        }
    }

    private void mostrarError(String mensaje) {
        new Alert(Alert.AlertType.ERROR, mensaje).showAndWait();
    }
}
