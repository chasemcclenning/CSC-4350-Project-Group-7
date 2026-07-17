package ui.librarian;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class NavigationController {

    public void showDashboard(ActionEvent event) throws IOException {
        navigate(event, "LibrarianDashboard.fxml", "librarian-dashboard.css");
    }

    public void showBooks(ActionEvent event) throws IOException {
        navigate(event, "BookCatalog.fxml", "book-catalog.css");
    }

    public void showMembers(ActionEvent event) throws IOException {
        navigate(event, "MemberManagement.fxml", "member-management.css");
    }

    public void showBorrowReturn(ActionEvent event) throws IOException {
        navigate(event, "BorrowReturn.fxml", "borrow-return.css");
    }

    public void showHolds(ActionEvent event) throws IOException {
        showPlaceholder(event);
    }

    public void showFines(ActionEvent event) throws IOException {
        showPlaceholder(event);
    }

    public void showAuditLog(ActionEvent event) throws IOException {
        showPlaceholder(event);
    }

    public void showReports(ActionEvent event) throws IOException {
        showPlaceholder(event);
    }

    public void showSettings(ActionEvent event) throws IOException {
        showPlaceholder(event);
    }

    public void showPlaceholder(ActionEvent event) throws IOException {
        navigate(event, "Placeholder.fxml", "placeholder.css");
    }

    private void navigate(ActionEvent event, String fxmlFile, String pageStylesheet) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                NavigationController.class.getResource("/ui/librarian/" + fxmlFile)
        );
        Parent page = loader.load();
        Scene scene = ((Node) event.getSource()).getScene();

        scene.setRoot(page);
        scene.getStylesheets().setAll(
                stylesheet("/css/shared/base.css"),
                stylesheet("/css/shared/components.css"),
                stylesheet("/css/librarian/" + pageStylesheet)
        );
    }

    private String stylesheet(String path) {
        return NavigationController.class.getResource(path).toExternalForm();
    }
}
