package ui.librarian;

import database.LibraryRepository;import model.DisplayIds;
import database.LibraryRepository.UserRow;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.sql.SQLException;

public class MemberManagementController extends NavigationController {
    @FXML private TextField memberSearch;
    @FXML private ComboBox<String> accountFilter;
    @FXML private TableView<UserRow> memberTable;
    @FXML private TableColumn<UserRow,String> memberColumn,emailColumn,statusColumn,finesColumn;
    @FXML private Label memberCount;
    private final LibraryRepository repository=new LibraryRepository();
    @FXML private void initialize(){memberColumn.setCellValueFactory(v->new ReadOnlyStringWrapper(v.getValue().name()));emailColumn.setCellValueFactory(v->new ReadOnlyStringWrapper(v.getValue().email()));statusColumn.setCellValueFactory(v->new ReadOnlyStringWrapper("librarian".equals(v.getValue().role())?"Librarian":v.getValue().finesOwed()>0?"Member · Fine due":"Member · Active"));finesColumn.setCellValueFactory(v->new ReadOnlyStringWrapper(String.format("$%.2f",v.getValue().finesOwed())));accountFilter.setItems(FXCollections.observableArrayList("All accounts","Members","Librarians","Active","Fine due"));accountFilter.getSelectionModel().selectFirst();refresh();}
    @FXML private void refresh(){try{String filter=accountFilter.getValue();var rows=repository.findUsers(memberSearch.getText()).stream().filter(u->filter==null||filter.startsWith("All")||("Members".equals(filter)&&"patron".equals(u.role()))||("Librarians".equals(filter)&&"librarian".equals(u.role()))||("Active".equals(filter)&&"patron".equals(u.role())&&u.finesOwed()==0)||("Fine due".equals(filter)&&"patron".equals(u.role())&&u.finesOwed()>0)).toList();memberTable.setItems(FXCollections.observableArrayList(rows));memberCount.setText(rows.size()+" account"+(rows.size()==1?"":"s"));}catch(SQLException e){error(e);}}
    @FXML private void clearFilters(){memberSearch.clear();accountFilter.getSelectionModel().selectFirst();refresh();}
    @FXML private void addMember(){editDialog(null);}
    @FXML private void editMember(){UserRow r=selected();if(r!=null)editDialog(r);}
    @FXML private void viewMember(){UserRow r=selected();if(r!=null)try{showUserProfile(new javafx.event.ActionEvent(memberTable,memberTable),r.id());}catch(Exception error){showError("User profile could not be opened",error);}}
    @FXML private void deleteMember(){UserRow r=selected();if(r==null)return;Alert a=new Alert(Alert.AlertType.CONFIRMATION,"Delete "+r.name()+"?",ButtonType.CANCEL,ButtonType.OK);if(a.showAndWait().orElse(ButtonType.CANCEL)==ButtonType.OK)try{repository.deleteUser(r.id());refresh();}catch(SQLException e){error(e);}}
    private void editDialog(UserRow r){TextField name=new TextField(r==null?"":r.name()),email=new TextField(r==null?"":r.email());PasswordField pass=new PasswordField();ComboBox<String> role=new ComboBox<>(FXCollections.observableArrayList("Member","Librarian"));role.getSelectionModel().select(r!=null&&"librarian".equals(r.role())?"Librarian":"Member");GridPane g=new GridPane();g.setHgap(10);g.setVgap(10);g.add(new Label("Name"),0,0);g.add(name,1,0);g.add(new Label("Email"),0,1);g.add(email,1,1);g.add(new Label("Account type"),0,2);g.add(role,1,2);if(r==null){g.add(new Label("Password"),0,3);g.add(pass,1,3);}Dialog<ButtonType>d=new Dialog<>();d.setTitle(r==null?"Add account":"Edit account");d.getDialogPane().setContent(g);d.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL,ButtonType.OK);if(d.showAndWait().orElse(ButtonType.CANCEL)==ButtonType.OK){if(name.getText().isBlank()||email.getText().isBlank()||(r==null&&pass.getText().isBlank())){new Alert(Alert.AlertType.WARNING,"Complete all required fields.").showAndWait();return;}String databaseRole="Librarian".equals(role.getValue())?"librarian":"patron";try{if(r==null)repository.createUser(name.getText().trim(),email.getText().trim(),pass.getText(),databaseRole);else repository.updateUser(r.id(),name.getText().trim(),email.getText().trim(),databaseRole);refresh();}catch(SQLException e){error(e);}}}
    private UserRow selected(){UserRow r=memberTable.getSelectionModel().getSelectedItem();if(r==null)new Alert(Alert.AlertType.INFORMATION,"Select a member first.").showAndWait();return r;}
    private void error(Exception e){new Alert(Alert.AlertType.ERROR,"Database error: "+e.getMessage()).showAndWait();}
}
