import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("ui/Login.fxml")
        );

        Scene scene = new Scene(loader.load(), 1440, 900);

        scene.getStylesheets().add(
                getClass().getResource("css/shared/base.css").toExternalForm()
        );

        scene.getStylesheets().add(
                getClass().getResource("css/shared/components.css").toExternalForm()
        );

        scene.getStylesheets().add(
                getClass().getResource("css/login.css").toExternalForm()
        );

        stage.setTitle("Library Management System");
        stage.setMinWidth(1100);
        stage.setMinHeight(750);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
