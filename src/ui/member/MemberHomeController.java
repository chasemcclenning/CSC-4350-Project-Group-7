package ui.member;

import database.LibraryRepository;
import database.LibraryRepository.CheckoutRow;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.DisplayIds;
import session.UserSession;
import ui.librarian.NavigationController;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MemberHomeController extends NavigationController {
 @FXML private Label welcome, borrowedCount, borrowedDetail, holdCount, holdDetail, fineTotal, fineDetail;
 @FXML private VBox borrowedItems, readyHold;
 private final LibraryRepository repository=new LibraryRepository();
 @FXML private void initialize(){if(!UserSession.isSignedIn()){welcome.setText("Welcome");empty(borrowedItems,"No member account is signed in.");empty(readyHold,"No hold information is available.");return;}var user=UserSession.getCurrentUser();welcome.setText("Welcome back, "+user.getName().split(" ")[0]);try{var loans=repository.findCheckouts(user.getUserID(),true);var holds=repository.findHolds(user.getUserID());var fines=repository.findFines(user.getUserID());borrowedCount.setText(String.valueOf(loans.size()));long dueSoon=loans.stream().filter(r->!r.dueAt().toLocalDate().isAfter(LocalDate.now().plusDays(7))).count();borrowedDetail.setText(loans.isEmpty()?"No books borrowed":dueSoon+" due within seven days");holdCount.setText(String.valueOf(holds.size()));long ready=holds.stream().filter(h->"ready".equalsIgnoreCase(h.status())).count();holdDetail.setText(holds.isEmpty()?"No active holds":ready+" ready for pickup");double unpaid=fines.stream().filter(f->"outstanding".equalsIgnoreCase(f.status())).mapToDouble(f->f.amount()).sum();fineTotal.setText(String.format("$%.2f",unpaid));fineDetail.setText(unpaid==0?"No balance due":"Payment required");if(loans.isEmpty())empty(borrowedItems,"No books have been borrowed yet.");else loans.stream().limit(3).forEach(r->borrowedItems.getChildren().add(loan(r)));var readyRows=holds.stream().filter(h->"ready".equalsIgnoreCase(h.status())).toList();if(readyRows.isEmpty())empty(readyHold,"No holds are ready for pickup.");else{var h=readyRows.get(0);Label t=new Label(h.title());t.getStyleClass().add("member-book-title");Label m=new Label(h.expiresAt()==null?"Ready for pickup":"Pickup by "+h.expiresAt().toLocalDate().format(DateTimeFormatter.ofPattern("MMM d")));m.getStyleClass().add("member-meta");readyHold.getChildren().addAll(t,m);}}catch(SQLException e){showError("Member activity could not be loaded",e);}}
 private VBox loan(CheckoutRow r){VBox box=new VBox(3);box.getStyleClass().add("member-row");Label t=new Label(r.title());t.getStyleClass().add("member-book-title");Label m=new Label("Copy "+DisplayIds.shortId("c",r.copyId())+" · Due "+r.dueAt().toLocalDate().format(DateTimeFormatter.ofPattern("MMM d")));m.getStyleClass().add("member-meta");box.getChildren().addAll(t,m);return box;}
 private void empty(VBox box,String text){Label label=new Label(text);label.getStyleClass().add("member-meta");box.getChildren().add(label);}
}
