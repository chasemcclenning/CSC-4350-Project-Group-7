package ui.member;

import database.LibraryRepository;
import database.LibraryRepository.CheckoutRow;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.DisplayIds;
import session.UserSession;
import ui.librarian.NavigationController;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MemberBooksController extends NavigationController {
    @FXML private VBox currentLoans, borrowingHistory;
    private final LibraryRepository repository=new LibraryRepository();
    @FXML private void initialize(){refresh();}
    private void refresh(){currentLoans.getChildren().clear();borrowingHistory.getChildren().clear();if(!UserSession.isSignedIn()){empty(currentLoans,"Sign in with a database member account to view borrowed books.");empty(borrowingHistory,"No borrowing history is available.");return;}try{var rows=repository.findCheckouts(UserSession.getCurrentUser().getUserID(),false);var active=rows.stream().filter(r->r.returnedAt()==null).toList();var returned=rows.stream().filter(r->r.returnedAt()!=null).toList();if(active.isEmpty())empty(currentLoans,"No books have been borrowed yet.");else active.forEach(r->currentLoans.getChildren().add(loan(r,true)));if(returned.isEmpty())empty(borrowingHistory,"No books have been returned yet.");else returned.forEach(r->borrowingHistory.getChildren().add(loan(r,false)));}catch(SQLException e){showError("Borrowing records could not be loaded",e);}}
    private HBox loan(CheckoutRow row,boolean active){HBox line=new HBox(12);line.setAlignment(javafx.geometry.Pos.CENTER_LEFT);line.getStyleClass().add("member-row");VBox text=new VBox(3);HBox.setHgrow(text,Priority.ALWAYS);Label title=new Label(row.title());title.getStyleClass().add("member-book-title");String detail=active?"Copy "+DisplayIds.shortId("c",row.copyId())+" · Due "+format(row.dueAt().toLocalDate()):"Returned "+format(row.returnedAt().toLocalDate());Label meta=new Label(detail);meta.getStyleClass().add("member-meta");text.getChildren().addAll(title,meta);Label status=new Label(active?(row.dueAt().toLocalDate().isBefore(LocalDate.now())?"Overdue":"On loan"):"Returned");status.getStyleClass().add(active?"member-status":"member-status-good");line.getChildren().addAll(text,status);if(active){Button button=new Button("Return book");button.getStyleClass().add("member-primary");button.setOnAction(event->{try{repository.returnCheckout(row.id());refresh();}catch(SQLException e){showError("The return could not be processed",e);}});line.getChildren().add(button);}return line;}
    private void empty(VBox box,String message){Label label=new Label(message);label.getStyleClass().add("member-meta");box.getChildren().add(label);}
    private String format(LocalDate date){return date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"));}
}
