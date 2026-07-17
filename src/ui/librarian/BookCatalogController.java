package ui.librarian;

import database.LibraryRepository;
import database.LibraryRepository.TitleRow;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;
import java.util.List;

public class BookCatalogController extends NavigationController {
    @FXML private TextField bookSearch;
    @FXML private ComboBox<String> genreFilter;
    @FXML private ComboBox<String> availabilityFilter;
    @FXML private TableView<TitleRow> bookTable;
    @FXML private TableColumn<TitleRow,String> titleColumn, authorColumn, genreColumn, isbnColumn, statusColumn;
    @FXML private TableColumn<TitleRow,Number> copiesColumn;
    @FXML private Label bookCount;
    private final LibraryRepository repository=new LibraryRepository();

    @FXML private void initialize() {
        bookTable.setRowFactory(view->{TableRow<TitleRow> row=new TableRow<>();row.setOnMouseClicked(event->{if(event.getClickCount()==2&&!row.isEmpty())try{showBookProfile(new javafx.event.ActionEvent(row,row),row.getItem().id());}catch(Exception error){showError("Book profile could not be opened",error);}});return row;});
        titleColumn.setCellValueFactory(v->new ReadOnlyStringWrapper(v.getValue().name()));
        authorColumn.setCellValueFactory(v->new ReadOnlyStringWrapper(v.getValue().author()));
        genreColumn.setCellValueFactory(v->new ReadOnlyStringWrapper(v.getValue().genre()));
        isbnColumn.setCellValueFactory(v->new ReadOnlyStringWrapper(v.getValue().isbn()));
        copiesColumn.setCellValueFactory(v->new ReadOnlyIntegerWrapper(v.getValue().totalCopies()));
        statusColumn.setCellValueFactory(v->new ReadOnlyStringWrapper(
                v.getValue().availableCopies()+" of "+v.getValue().totalCopies()+" available"));
        availabilityFilter.setItems(FXCollections.observableArrayList("All availability","Available","Unavailable"));
        availabilityFilter.getSelectionModel().selectFirst();
        try { List<String> genres=repository.findGenres(); genres.add(0,"All genres"); genreFilter.setItems(FXCollections.observableArrayList(genres)); genreFilter.getSelectionModel().selectFirst(); }
        catch(SQLException e){ error(e); }
        refresh();
    }
    @FXML private void refresh(){
        try { var rows=repository.findTitles(bookSearch.getText(),genreFilter.getValue(),availabilityFilter.getValue()); bookTable.setItems(FXCollections.observableArrayList(rows)); bookCount.setText(rows.size()+" titles"); }
        catch(SQLException e){ error(e); }
    }
    @FXML private void clearFilters(){ bookSearch.clear(); genreFilter.getSelectionModel().selectFirst(); availabilityFilter.getSelectionModel().selectFirst(); refresh(); }
    @FXML private void addBook(){ editDialog(null); }
    @FXML private void editBook(){ TitleRow row=selected(); if(row!=null) editDialog(row); }
    @FXML private void viewBook(){TitleRow row=selected();if(row!=null)try{showBookProfile(new javafx.event.ActionEvent(bookTable,bookTable),row.id());}catch(Exception error){showError("Book profile could not be opened",error);}}
    @FXML private void deleteBook(){ TitleRow row=selected(); if(row==null)return; Alert a=new Alert(Alert.AlertType.CONFIRMATION,"Delete “"+row.name()+"”?",ButtonType.CANCEL,ButtonType.OK); if(a.showAndWait().orElse(ButtonType.CANCEL)==ButtonType.OK) try{repository.deleteTitle(row.id());refresh();}catch(SQLException e){error(e);} }
    private void editDialog(TitleRow row){
        TextField name=new TextField(row==null?"":row.name()), author=new TextField(row==null?"":row.author()), genre=new TextField(row==null?"":row.genre()), isbn=new TextField(row==null?"":row.isbn());
        Spinner<Integer> copies=new Spinner<>(0,10000,row==null?1:row.totalCopies());copies.setEditable(true);
        GridPane grid=new GridPane(); grid.setHgap(10);grid.setVgap(10); String[] labels={"Title","Author","Genre","ISBN"}; TextField[] fields={name,author,genre,isbn}; for(int i=0;i<4;i++){grid.add(new Label(labels[i]),0,i);grid.add(fields[i],1,i);}grid.add(new Label("Number of copies"),0,4);grid.add(copies,1,4);
        Dialog<ButtonType> d=new Dialog<>();d.setTitle(row==null?"Add book":"Edit book");d.getDialogPane().setContent(grid);d.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL,ButtonType.OK);
        if(d.showAndWait().orElse(ButtonType.CANCEL)==ButtonType.OK){ if(name.getText().isBlank()||author.getText().isBlank()){new Alert(Alert.AlertType.WARNING,"Title and author are required.").showAndWait();return;} try{int quantity=Integer.parseInt(copies.getEditor().getText().trim());if(quantity<0)throw new NumberFormatException("Copy count cannot be negative.");if(row==null)repository.createTitle(name.getText().trim(),author.getText().trim(),genre.getText().trim(),isbn.getText().trim(),quantity);else{repository.updateTitle(row.id(),name.getText().trim(),author.getText().trim(),genre.getText().trim(),isbn.getText().trim());repository.setTitleCopyCount(row.id(),quantity);}refresh();}catch(SQLException|NumberFormatException e){error(e);} }
    }
    private TitleRow selected(){TitleRow r=bookTable.getSelectionModel().getSelectedItem();if(r==null)new Alert(Alert.AlertType.INFORMATION,"Select a book first.").showAndWait();return r;}
    private void error(Exception e){new Alert(Alert.AlertType.ERROR,"Database error: "+e.getMessage()).showAndWait();}
}
