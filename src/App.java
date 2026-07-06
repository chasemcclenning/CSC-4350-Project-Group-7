import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/Dashboard.fxml"));
        Scene scene = new Scene(loader.load(), 900, 600);

    scene.getStylesheets().add(
        getClass().getResource("css/dashboard.css").toExternalForm()
    );

        stage.setTitle("Library Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
