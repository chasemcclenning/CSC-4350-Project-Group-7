package ui.librarian;

import database.LibraryRepository;
import database.LibraryRepository.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import session.UserSession;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class LibrarianDashboardController extends NavigationController {
    @FXML private Label welcomeLabel,titleCount,checkoutCount,overdueSummary,overdueCount,profileName,profileInitials;
    @FXML private TextField dashboardSearch;
    @FXML private VBox searchResults,recentActivity,overdueItems;
    private final LibraryRepository repository=new LibraryRepository();

    @FXML private void initialize(){
        if(UserSession.isSignedIn()){String name=UserSession.getCurrentUser().getName();welcomeLabel.setText("Good morning, "+name.split(" ")[0]);profileName.setText(name);profileInitials.setText(java.util.Arrays.stream(name.split(" ")).limit(2).map(part->part.substring(0,1).toUpperCase()).reduce("",String::concat));}
        dashboardSearch.textProperty().addListener((observable,oldValue,newValue)->{if(newValue==null||newValue.isBlank()){searchResults.getChildren().clear();searchResults.setManaged(false);searchResults.setVisible(false);}});
        loadDashboard();
    }
    private void loadDashboard(){try{titleCount.setText(String.format("%,d",repository.countTitles()));checkoutCount.setText(String.format("%,d",repository.countActiveCheckouts()));var recent=repository.findCheckouts("",false).stream().limit(4).toList();recentActivity.getChildren().clear();if(recent.isEmpty())recentActivity.getChildren().add(empty("No borrowing activity yet."));else recent.forEach(row->recentActivity.getChildren().add(activity(row)));var overdue=repository.findOverdueCheckouts();overdueCount.setText(String.valueOf(overdue.size()));overdueSummary.setText(overdue.isEmpty()?"No overdue books.":overdue.size()+" item"+(overdue.size()==1?"":"s")+" require follow-up");overdueItems.getChildren().clear();if(overdue.isEmpty())overdueItems.getChildren().add(empty("No books are overdue."));else overdue.stream().limit(3).forEach(row->overdueItems.getChildren().add(overdue(row)));}catch(SQLException e){showError("Dashboard data could not be loaded",e);}}

    @FXML private void searchDashboard(){
        String query=dashboardSearch.getText().trim();searchResults.getChildren().clear();
        if(query.isBlank()){searchResults.setManaged(false);searchResults.setVisible(false);return;}
        try{
            var books=repository.findTitles(query,"","").stream().limit(5).toList();
            var users=repository.findUsers(query).stream().limit(5).toList();
            if(books.isEmpty()&&users.isEmpty())searchResults.getChildren().add(empty("No books or users match “"+query+"”."));
            books.forEach(book->{Button result=new Button("BOOK  ·  "+book.name()+"  ·  "+book.author()+"  ·  "+book.availableCopies()+" of "+book.totalCopies()+" available");result.setMaxWidth(Double.MAX_VALUE);result.getStyleClass().add("search-result");result.setOnAction(event->{try{showBookProfile(event,book.id());}catch(Exception e){showError("Book profile could not be opened",e);}});searchResults.getChildren().add(result);});
            users.forEach(user->{Button result=new Button(("librarian".equals(user.role())?"LIBRARIAN":"MEMBER")+"  ·  "+user.name()+"  ·  "+user.email());result.setMaxWidth(Double.MAX_VALUE);result.getStyleClass().add("search-result");result.setOnAction(event->{try{showUserProfile(event,user.id());}catch(Exception e){showError("User profile could not be opened",e);}});searchResults.getChildren().add(result);});
            searchResults.setManaged(true);searchResults.setVisible(true);
        }catch(SQLException e){showError("Search could not be completed",e);}
    }
    private Label empty(String text){Label label=new Label(text);label.getStyleClass().add("section-subtitle");return label;}
    private VBox activity(CheckoutRow row){VBox box=new VBox(3);box.getStyleClass().add("dashboard-link-row");String verb=row.returnedAt()==null?" was checked out":" was returned";Label title=new Label(row.title()+verb);title.getStyleClass().add("row-title");Label meta=new Label(row.member()+" · "+relative(row.checkedOutAt()));meta.getStyleClass().add("row-meta");box.getChildren().addAll(title,meta);box.setOnMouseClicked(event->{try{var book=repository.findTitles(row.title(),"","").stream().filter(t->t.name().equals(row.title())).findFirst().orElse(null);if(book!=null)showBookProfile(new javafx.event.ActionEvent(box,box),book.id());}catch(Exception e){showError("Book profile could not be opened",e);}});return box;}
    private VBox overdue(CheckoutRow row){VBox box=new VBox(5);box.getStyleClass().addAll("overdue-card","dashboard-link-row");Label title=new Label(row.title());title.getStyleClass().add("row-title");Label meta=new Label(row.member()+" · Due "+row.dueAt().format(DateTimeFormatter.ofPattern("MMM d, yyyy")));meta.getStyleClass().add("row-meta");box.getChildren().addAll(title,meta);box.setOnMouseClicked(event->{try{var user=repository.findUserById(row.userId());if(user!=null)showUserProfile(new javafx.event.ActionEvent(box,box),user.id());}catch(Exception e){showError("User profile could not be opened",e);}});return box;}
    private String relative(LocalDateTime time){long hours=Math.max(0,Duration.between(time,LocalDateTime.now()).toHours());return hours<1?"less than an hour ago":hours+" hour"+(hours==1?"":"s")+" ago";}
}
