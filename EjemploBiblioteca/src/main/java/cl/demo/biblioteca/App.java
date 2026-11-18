package cl.demo.biblioteca;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/vista-libros.fxml"));
        stage.setScene(new Scene(loader.load(), 720, 480));
        stage.setTitle("Biblioteca – ejemplo DAO + Repository");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
