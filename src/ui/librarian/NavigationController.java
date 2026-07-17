package ui.librarian;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

public class NavigationController {

    @FXML private Node generalSettingsPage;
    @FXML private Node accountSettingsPage;
    @FXML private Node circulationSettingsPage;
    @FXML private Node notificationSettingsPage;
    @FXML private Node backupSettingsPage;
    @FXML private Button generalSettingsButton;
    @FXML private Button accountSettingsButton;
    @FXML private Button circulationSettingsButton;
    @FXML private Button notificationSettingsButton;
    @FXML private Button backupSettingsButton;

    public void showDashboard(ActionEvent event) throws IOException {
        navigate(event, "LibrarianDashboard.fxml", "librarian-dashboard.css");
    }

    public void showLogin(ActionEvent event) throws IOException {
        navigateFromRoot(event, "/ui/Login.fxml", "/css/login.css");
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
        navigate(event, "Holds.fxml", "holds.css");
    }

    public void showFines(ActionEvent event) throws IOException {
        navigate(event, "Fines.fxml", "fines.css");
    }

    public void showAuditLog(ActionEvent event) throws IOException {
        navigate(event, "AuditLog.fxml", "audit-log.css");
    }

    public void showReports(ActionEvent event) throws IOException {
        navigate(event, "Reports.fxml", "reports.css");
    }

    public void showSettings(ActionEvent event) throws IOException {
        navigate(event, "Settings.fxml", "settings.css");
    }

    public void showGeneralSettings() {
        activateSettingsPage(generalSettingsPage, generalSettingsButton);
    }

    public void showAccountSettings() {
        activateSettingsPage(accountSettingsPage, accountSettingsButton);
    }

    public void showCirculationSettings() {
        activateSettingsPage(circulationSettingsPage, circulationSettingsButton);
    }

    public void showNotificationSettings() {
        activateSettingsPage(notificationSettingsPage, notificationSettingsButton);
    }

    public void showBackupSettings() {
        activateSettingsPage(backupSettingsPage, backupSettingsButton);
    }

    public void showPlaceholder(ActionEvent event) throws IOException {
        navigate(event, "Placeholder.fxml", "placeholder.css");
    }

    private void navigate(ActionEvent event, String fxmlFile, String pageStylesheet) throws IOException {
        navigateFromRoot(event, "/ui/librarian/" + fxmlFile, "/css/librarian/" + pageStylesheet);
    }

    private void navigateFromRoot(ActionEvent event, String fxmlPath, String pageStylesheet) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                NavigationController.class.getResource(fxmlPath)
        );
        Parent page = loader.load();
        Scene scene = ((Node) event.getSource()).getScene();

        scene.setRoot(page);
        scene.getStylesheets().setAll(
                stylesheet("/css/shared/base.css"),
                stylesheet("/css/shared/components.css"),
                stylesheet(pageStylesheet)
        );
    }

    private String stylesheet(String path) {
        return NavigationController.class.getResource(path).toExternalForm();
    }

    private void activateSettingsPage(Node activePage, Button activeButton) {
        Node[] pages = {generalSettingsPage, accountSettingsPage, circulationSettingsPage,
                notificationSettingsPage, backupSettingsPage};
        Button[] buttons = {generalSettingsButton, accountSettingsButton, circulationSettingsButton,
                notificationSettingsButton, backupSettingsButton};

        for (Node page : pages) {
            page.setVisible(page == activePage);
            page.setManaged(page == activePage);
        }
        for (Button button : buttons) {
            button.getStyleClass().setAll(button == activeButton
                    ? "settings-menu-active"
                    : "settings-menu-button");
        }
    }
}
