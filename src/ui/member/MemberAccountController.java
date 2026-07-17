package ui.member;

import database.LibraryRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.User;import model.DisplayIds;
import session.UserSession;
import ui.librarian.NavigationController;
import java.sql.SQLException;

public class MemberAccountController extends NavigationController {
 @FXML private TextField accountName, accountId, accountEmail;
 @FXML private Label accountStatus, accountFines, accountCheckouts;
 @FXML private VBox holdsList, finesList;
 private final LibraryRepository repository=new LibraryRepository();
 @FXML private void initialize(){refresh();}
 private void refresh(){holdsList.getChildren().clear();finesList.getChildren().clear();if(!UserSession.isSignedIn()){accountName.clear();accountEmail.clear();accountId.clear();empty(holdsList,"No member account is signed in.");empty(finesList,"No fine information is available.");return;}User u=UserSession.getCurrentUser();accountName.setText(u.getName());accountEmail.setText(u.getEmail());accountId.setText(DisplayIds.shortId("u",u.getUserID()));accountStatus.setText("Active");try{var checkouts=repository.findCheckouts(u.getUserID(),true);var holds=repository.findHolds(u.getUserID());var fines=repository.findFines(u.getUserID());double unpaid=fines.stream().filter(f->"outstanding".equalsIgnoreCase(f.status())).mapToDouble(f->f.amount()).sum();accountFines.setText(String.format("$%.2f",unpaid));accountCheckouts.setText(String.valueOf(checkouts.size()));if(holds.isEmpty())empty(holdsList,"No holds have been placed yet.");else holds.forEach(h->{HBox row=new HBox(10);row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);Label text=new Label(h.title()+" · "+h.status()+" · Queue #"+h.queuePosition());HBox.setHgrow(text,Priority.ALWAYS);Button cancel=new Button("Cancel");cancel.getStyleClass().add("member-secondary");cancel.setOnAction(e->{try{repository.cancelHold(h.id());refresh();}catch(SQLException ex){showError("The hold could not be cancelled",ex);}});row.getChildren().addAll(text,cancel);holdsList.getChildren().add(row);});if(fines.isEmpty())empty(finesList,"No fines have been recorded.");else fines.forEach(f->{Label label=new Label(DisplayIds.shortId("f",f.id())+" · "+String.format("$%.2f",f.amount())+" · "+f.status());label.getStyleClass().add("member-meta");finesList.getChildren().add(label);});}catch(SQLException e){showError("Account activity could not be loaded",e);}}
 @FXML private void saveProfile(){if(!UserSession.isSignedIn())return;if(accountName.getText().isBlank()||accountEmail.getText().isBlank()){new Alert(Alert.AlertType.WARNING,"Name and email are required.").showAndWait();return;}User old=UserSession.getCurrentUser();try{repository.updateUser(old.getUserID(),accountName.getText().trim(),accountEmail.getText().trim());UserSession.signIn(new User(old.getUserID(),accountName.getText().trim(),accountEmail.getText().trim(),old.getPassword(),old.getRole(),old.getFinesOwed()));new Alert(Alert.AlertType.INFORMATION,"Profile saved.").showAndWait();}catch(SQLException e){showError("The profile could not be saved",e);}}
 private void empty(VBox box,String text){Label label=new Label(text);label.getStyleClass().add("member-meta");box.getChildren().add(label);}
}
