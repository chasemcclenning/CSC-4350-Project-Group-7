package ui.librarian;

import database.LibraryRepository;
import database.LibraryRepository.AuditRow;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.DisplayIds;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class AuditLogController extends NavigationController {
    @FXML private TableView<AuditRow> table;
    @FXML private TableColumn<AuditRow,String> idCol,userCol,actionCol,typeCol,recordCol,notesCol,dateCol;
    @FXML private Label count;
    private final LibraryRepository repo=new LibraryRepository();

    @FXML private void initialize(){
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        idCol.setCellValueFactory(v->new ReadOnlyStringWrapper(DisplayIds.shortId("log",v.getValue().id())));
        userCol.setCellValueFactory(v->new ReadOnlyStringWrapper(v.getValue().userName()==null?"System":v.getValue().userName()));
        actionCol.setCellValueFactory(v->new ReadOnlyStringWrapper(v.getValue().action()));
        typeCol.setCellValueFactory(v->new ReadOnlyStringWrapper(v.getValue().entityType()));
        recordCol.setCellValueFactory(v->new ReadOnlyStringWrapper(DisplayIds.shortId(v.getValue().entityType(),v.getValue().entityId())));
        notesCol.setCellValueFactory(v->new ReadOnlyStringWrapper(v.getValue().notes()));
        dateCol.setCellValueFactory(v->new ReadOnlyStringWrapper(v.getValue().createdAt()==null?"":v.getValue().createdAt().format(DateTimeFormatter.ofPattern("MMM d, yyyy"))));
        refresh();
    }

    @FXML private void refresh(){
        try{
            var rows=repo.findAuditRows();
            table.setItems(FXCollections.observableArrayList(rows));
            count.setText(rows.isEmpty()?"No system activity has been recorded yet.":rows.size()+" audit entries");
        }catch(SQLException e){showError("Audit log could not be loaded",e);}
    }
}
