package ui.librarian;

import database.LibraryRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import session.ProfileSelection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class BookProfileController extends NavigationController {
    @FXML private Label bookTitle,bookAuthor,bookGenre,bookIsbn,bookAvailability,bookTotal,bookAvailable,bookOnLoan,bookHolds;
    @FXML private VBox loanList,holdList;
    private final LibraryRepository repository=new LibraryRepository();
    @FXML private void initialize(){try{var book=repository.findTitleById(ProfileSelection.getBookId());if(book==null){bookTitle.setText("Book not found");return;}bookTitle.setText(book.name());bookAuthor.setText(book.author());bookGenre.setText(blank(book.genre()));bookIsbn.setText(blank(book.isbn()));bookTotal.setText(String.valueOf(book.totalCopies()));bookAvailable.setText(String.valueOf(book.availableCopies()));bookOnLoan.setText(String.valueOf(book.totalCopies()-book.availableCopies()));bookAvailability.setText(book.availableCopies()+" of "+book.totalCopies()+" copies available");var loans=repository.findCheckoutsForTitle(book.id()).stream().filter(row->row.returnedAt()==null).toList();var holds=repository.findHoldsForTitle(book.id());bookHolds.setText(String.valueOf(holds.size()));if(loans.isEmpty())empty(loanList,"No copies are currently on loan.");else loans.forEach(row->line(loanList,row.member()+" · Due "+row.dueAt().format(DateTimeFormatter.ofPattern("MMM d, yyyy"))));if(holds.isEmpty())empty(holdList,"No active holds for this book.");else holds.forEach(row->line(holdList,row.member()+" · Queue #"+row.queuePosition()+" · "+row.status()));}catch(SQLException e){showError("Book profile could not be loaded",e);}}
    private String blank(String value){return value==null||value.isBlank()?"Not provided":value;}
    private void line(VBox box,String text){Label label=new Label(text);label.getStyleClass().add("profile-row");box.getChildren().add(label);}
    private void empty(VBox box,String text){Label label=new Label(text);label.getStyleClass().add("section-subtitle");box.getChildren().add(label);}
}
