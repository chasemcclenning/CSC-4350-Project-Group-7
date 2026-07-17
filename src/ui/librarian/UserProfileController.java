package ui.librarian;

import database.LibraryRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.DisplayIds;
import session.ProfileSelection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class UserProfileController extends NavigationController {
    @FXML private Label userName,userEmail,userId,userRole,userFines,userLoans,userHolds;
    @FXML private VBox loanList,holdList,fineList;
    private final LibraryRepository repository=new LibraryRepository();
    @FXML private void initialize(){try{var user=repository.findUserById(ProfileSelection.getUserId());if(user==null){userName.setText("User not found");return;}userName.setText(user.name());userEmail.setText(user.email());userId.setText(DisplayIds.shortId("u",user.id()));userRole.setText("librarian".equals(user.role())?"Librarian":"Member");var loans=repository.findCheckouts(user.id(),true);var holds=repository.findHolds(user.id());var fines=repository.findFines(user.id());userLoans.setText(String.valueOf(loans.size()));userHolds.setText(String.valueOf(holds.size()));double outstanding=fines.stream().filter(f->"outstanding".equalsIgnoreCase(f.status())).mapToDouble(f->f.amount()).sum();userFines.setText(String.format("$%.2f",outstanding));if(loans.isEmpty())empty(loanList,"No books currently borrowed.");else loans.forEach(row->line(loanList,row.title()+" · Due "+row.dueAt().format(DateTimeFormatter.ofPattern("MMM d, yyyy"))));if(holds.isEmpty())empty(holdList,"No active holds.");else holds.forEach(row->line(holdList,row.title()+" · Queue #"+row.queuePosition()+" · "+row.status()));if(fines.isEmpty())empty(fineList,"No fines recorded.");else fines.forEach(row->line(fineList,DisplayIds.shortId("f",row.id())+" · "+String.format("$%.2f",row.amount())+" · "+row.status()));}catch(SQLException e){showError("User profile could not be loaded",e);}}
    private void line(VBox box,String text){Label label=new Label(text);label.getStyleClass().add("profile-row");box.getChildren().add(label);}
    private void empty(VBox box,String text){Label label=new Label(text);label.getStyleClass().add("section-subtitle");box.getChildren().add(label);}
}
