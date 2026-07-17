package ui.librarian;

import database.LibraryRepository;
import database.LibraryRepository.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.DisplayIds;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BorrowReturnController extends NavigationController {
    @FXML private ComboBox<UserRow> member;
    @FXML private ComboBox<TitleRow> copy;
    @FXML private TableView<CheckoutRow> table;
    @FXML private TableColumn<CheckoutRow,String> idCol,memberCol,copyCol,titleCol,dueCol,statusCol;
    @FXML private Label count;
    private final LibraryRepository repo=new LibraryRepository();

    @FXML private void initialize(){
        table.setRowFactory(view->{TableRow<CheckoutRow> row=new TableRow<>();row.setOnMouseClicked(event->{if(event.getClickCount()==2&&!row.isEmpty())try{showUserProfile(new javafx.event.ActionEvent(row,row),row.getItem().userId());}catch(Exception error){showError("User profile could not be opened",error);}});return row;});
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        member.setCellFactory(v->memberCell()); member.setButtonCell(memberCell());
        copy.setCellFactory(v->titleCell()); copy.setButtonCell(titleCell());
        idCol.setCellValueFactory(v->new ReadOnlyStringWrapper(DisplayIds.shortId("co",v.getValue().id())));
        memberCol.setCellValueFactory(v->new ReadOnlyStringWrapper(v.getValue().member()));
        copyCol.setCellValueFactory(v->new ReadOnlyStringWrapper(DisplayIds.shortId("c",v.getValue().copyId())));
        titleCol.setCellValueFactory(v->new ReadOnlyStringWrapper(v.getValue().title()));
        dueCol.setCellValueFactory(v->new ReadOnlyStringWrapper(v.getValue().dueAt()==null?"—":v.getValue().dueAt().format(DateTimeFormatter.ofPattern("MMM d, yyyy"))));
        statusCol.setCellValueFactory(v->{var row=v.getValue();return new ReadOnlyStringWrapper(row.returnedAt()!=null?"Returned":row.dueAt()!=null&&row.dueAt().toLocalDate().isBefore(LocalDate.now())?"Overdue":"On loan");});
        refresh();
    }
    private ListCell<UserRow> memberCell(){return new ListCell<>(){protected void updateItem(UserRow u,boolean empty){super.updateItem(u,empty);setText(empty||u==null?null:u.name()+" ("+DisplayIds.shortId("u",u.id())+")");}};}
    private ListCell<TitleRow> titleCell(){return new ListCell<>(){protected void updateItem(TitleRow title,boolean empty){super.updateItem(title,empty);setText(empty||title==null?null:title.name());}};}
    @FXML private void refresh(){try{member.setItems(FXCollections.observableArrayList(repo.findUsers("").stream().filter(u->"patron".equals(u.role())).toList()));copy.setItems(FXCollections.observableArrayList(repo.findTitles("","","Available")));var rows=repo.findCheckouts("",true);table.setItems(FXCollections.observableArrayList(rows));count.setText(rows.isEmpty()?"No books are currently borrowed.":rows.size()+" borrowed book"+(rows.size()==1?"":"s"));}catch(SQLException e){showError("Borrowing data could not be loaded",e);}}
    @FXML private void checkout(){if(member.getValue()==null||copy.getValue()==null){new Alert(Alert.AlertType.WARNING,"Select a member and an available title.").showAndWait();return;}try{repo.checkoutTitle(member.getValue().id(),copy.getValue().id(),LocalDateTime.now().plusDays(14));refresh();}catch(SQLException e){showError("Checkout could not be processed",e);}}
    @FXML private void returned(){var row=table.getSelectionModel().getSelectedItem();if(row==null||row.returnedAt()!=null){new Alert(Alert.AlertType.INFORMATION,"Select an active checkout.").showAndWait();return;}try{repo.returnCheckout(row.id());refresh();}catch(SQLException e){showError("Return could not be processed",e);}}
}
