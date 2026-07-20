package ui.librarian;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import database.UserDAO;
import database.DatabaseBackup;

import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.User;
import session.UserSession;
import session.ProfileSelection;

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
    @FXML private TextField loginEmail;
    @FXML private PasswordField loginPassword;
    @FXML private Label loginError;
    @FXML private Label sidebarProfileName;
    @FXML private Label sidebarProfileInitials;

    public void showDashboard(ActionEvent event) throws IOException {
        navigate(event, "LibrarianDashboard.fxml", "librarian-dashboard.css");
    }

    public void showLogin(ActionEvent event) throws IOException {
        UserSession.signOut();
        navigateFromRoot(event, "/ui/Login.fxml", "/css/login.css");
    }

    public void signIn(ActionEvent event) throws IOException {
        String email = loginEmail.getText().trim();
        String password = loginPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showLoginError("Enter your email and password.");
            return;
        }

        try {
            Optional<User> authenticatedUser = new UserDAO().authenticate(email, password);
            if (authenticatedUser.isPresent()) {
                UserSession.signIn(authenticatedUser.get());
                if (authenticatedUser.get().isLibrarian()) showDashboard(event);
                else showMemberHome(event);
                return;
            }
            showLoginError("The email or password is incorrect.");
            return;
        } catch (SQLException databaseError) {
            showLoginError("The local library data could not be opened. Contact support.");
            return;
        }
    }

    public void forgotPassword() {
        TextField email = new TextField(loginEmail == null ? "" : loginEmail.getText().trim());
        PasswordField password = new PasswordField();
        PasswordField confirmation = new PasswordField();
        email.setPromptText("Account email");
        password.setPromptText("New password");
        confirmation.setPromptText("Confirm new password");

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10);
        form.addRow(0, new Label("Email"), email);
        form.addRow(1, new Label("New password"), password);
        form.addRow(2, new Label("Confirm password"), confirmation);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Reset password");
        dialog.setHeaderText("Set a new password for your library account");
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        if (dialog.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        if (email.getText().isBlank() || password.getText().isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Enter the account email and a new password.").showAndWait();
            return;
        }
        if (password.getText().length() < 8) {
            new Alert(Alert.AlertType.WARNING, "The new password must contain at least 8 characters.").showAndWait();
            return;
        }
        if (!password.getText().equals(confirmation.getText())) {
            new Alert(Alert.AlertType.WARNING, "The passwords do not match.").showAndWait();
            return;
        }
        try {
            boolean updated = new UserDAO().resetPassword(email.getText().trim(), password.getText());
            if (!updated) {
                new Alert(Alert.AlertType.WARNING, "No account was found for that email address.").showAndWait();
                return;
            }
            if (loginEmail != null) loginEmail.setText(email.getText().trim());
            if (loginPassword != null) loginPassword.clear();
            new Alert(Alert.AlertType.INFORMATION, "Password updated. You can now sign in with the new password.").showAndWait();
        } catch (SQLException error) {
            showError("The password could not be updated", error);
        }
    }

    public void showSupport() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sign-in help");
        alert.setHeaderText("Contact library support");
        TextField supportEmail = new TextField("ogohourou1@student.gsu.edu");
        supportEmail.setEditable(false);
        supportEmail.setFocusTraversable(true);
        VBox content = new VBox(8, new Label("Email this address for help accessing your account:"), supportEmail);
        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }

    public void showDocumentation() {
        new Alert(Alert.AlertType.INFORMATION,
                "Use the navigation menu to manage books, members, checkouts, holds, fines, reports, and the audit log.")
                .showAndWait();
    }

    public void saveSettings() {
        new Alert(Alert.AlertType.INFORMATION,
                "Settings were applied for the current application session.").showAndWait();
    }

    public void backupDatabase() {
        FileChooser chooser=new FileChooser();
        chooser.setTitle("Save database backup");
        chooser.setInitialFileName("librarydb-backup-"+java.time.LocalDate.now()+".sql");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL backup","*.sql"));
        java.io.File destination=chooser.showSaveDialog(backupSettingsPage==null?null:backupSettingsPage.getScene().getWindow());
        if(destination==null)return;
        try{
            DatabaseBackup.write(destination.toPath());
            new Alert(Alert.AlertType.INFORMATION,"Database backup saved to:\n"+destination.getAbsolutePath()).showAndWait();
        }catch(Exception error){showError("The database backup could not be created",error);}
    }

    public void restoreDatabase() {
        new Alert(Alert.AlertType.INFORMATION,
                "No application backup is available to restore.").showAndWait();
    }

    private void showLoginError(String message) {
        loginError.setText(message);
        loginError.setVisible(true);
        loginError.setManaged(true);
    }

    protected void showError(String context, Exception error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Library database");
        alert.setHeaderText(context);
        alert.setContentText(error.getMessage());
        alert.showAndWait();
    }

    public void showMemberHome(ActionEvent event) throws IOException {
        navigateMember(event, "MemberHome.fxml");
    }

    public void showMemberCatalog(ActionEvent event) throws IOException {
        navigateMember(event, "MemberCatalog.fxml");
    }

    public void showMemberBooks(ActionEvent event) throws IOException {
        navigateMember(event, "MemberBooks.fxml");
    }

    public void showMemberAccount(ActionEvent event) throws IOException {
        navigateMember(event, "MemberAccount.fxml");
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

    protected void showBookProfile(ActionEvent event,String bookId)throws IOException{
        ProfileSelection.setBookId(bookId);
        navigate(event,"BookProfile.fxml","profiles.css");
    }

    protected void showUserProfile(ActionEvent event,String userId)throws IOException{
        ProfileSelection.setUserId(userId);
        navigate(event,"UserProfile.fxml","profiles.css");
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
        if (!(event.getSource() instanceof Button button)) return;
        String action = button.getText().replaceAll("^[^A-Za-z]+", "").trim();

        if (action.equalsIgnoreCase("Add a book")) { showBooks(event); return; }
        if (action.equalsIgnoreCase("Add member")) { showMembers(event); return; }

        Alert.AlertType type = action.matches("(?i).*(delete|cancel|waive|disable|restore).*")
                ? Alert.AlertType.CONFIRMATION : Alert.AlertType.INFORMATION;
        Alert alert = new Alert(type);
        alert.setTitle(action);
        alert.setHeaderText(action);
        alert.setContentText("This action is available from the selected record on its management screen.");
        alert.showAndWait();
    }

    private void navigate(ActionEvent event, String fxmlFile, String pageStylesheet) throws IOException {
        navigateFromRoot(event, "/ui/librarian/" + fxmlFile, "/css/librarian/" + pageStylesheet);
    }

    private void navigateMember(ActionEvent event, String fxmlFile) throws IOException {
        navigateFromRoot(event, "/ui/member/" + fxmlFile, "/css/member/member-portal.css");
    }

    private void navigateFromRoot(ActionEvent event, String fxmlPath, String pageStylesheet) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                NavigationController.class.getResource(fxmlPath)
        );
        Parent page = loader.load();
        Object loadedController = loader.getController();
        if (loadedController instanceof NavigationController controller) {
            controller.populateDropdowns(page);
            controller.populateSignedInUser();
        }
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

    private void populateDropdowns(Node node) {
        if (node instanceof ComboBox<?> raw && raw.getItems().isEmpty()) {
            @SuppressWarnings("unchecked") ComboBox<String> combo = (ComboBox<String>) raw;
            String prompt = Optional.ofNullable(combo.getPromptText()).orElse("").toLowerCase();
            List<String> values;
            if (prompt.contains("status")) values = List.of("All statuses", "Active", "Ready", "Waiting", "Paid", "Unpaid", "Waived");
            else if (prompt.contains("genre")) values = List.of("All genres", "Fiction", "Nonfiction", "Science Fiction", "History", "Self-help");
            else if (prompt.contains("report type")) values = List.of("Book inventory", "Borrowing records", "Overdue books", "Fine records");
            else if (prompt.contains("pdf") || prompt.contains("format")) values = List.of("PDF", "CSV");
            else if (prompt.contains("frequency")) values = List.of("Daily", "Weekly", "Monthly");
            else if (prompt.contains("loan") || prompt.contains("14 days")) values = List.of("7 days", "14 days", "21 days", "30 days");
            else if (prompt.contains("hold") || prompt.contains("7 days")) values = List.of("3 days", "5 days", "7 days", "14 days");
            else values = List.of("All", "Active", "Completed");
            combo.setItems(FXCollections.observableArrayList(values));
            if (!values.isEmpty()) combo.getSelectionModel().selectFirst();
        }
        if (node instanceof Parent parent) parent.getChildrenUnmodifiable().forEach(this::populateDropdowns);
    }

    private void populateSignedInUser() {
        if (!UserSession.isSignedIn()) return;
        String name = UserSession.getCurrentUser().getName();
        if (sidebarProfileName != null) sidebarProfileName.setText(name);
        if (sidebarProfileInitials != null) {
            String initials = java.util.Arrays.stream(name.trim().split("\\s+"))
                    .limit(2).filter(part -> !part.isBlank())
                    .map(part -> part.substring(0,1).toUpperCase())
                    .reduce("", String::concat);
            sidebarProfileInitials.setText(initials);
        }
    }
}
