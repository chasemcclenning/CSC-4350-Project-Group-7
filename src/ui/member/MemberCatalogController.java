package ui.member;

import database.LibraryRepository;
import database.LibraryRepository.TitleRow;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import session.UserSession;
import ui.librarian.NavigationController;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class MemberCatalogController extends NavigationController {
    @FXML private TextField catalogSearch;
    @FXML private ComboBox<String> genreFilter;
    @FXML private GridPane catalogGrid;
    private final LibraryRepository repository=new LibraryRepository();
    @FXML private void initialize(){try{var genres=repository.findGenres();genres.add(0,"All genres");genreFilter.setItems(FXCollections.observableArrayList(genres));genreFilter.getSelectionModel().selectFirst();refresh();}catch(SQLException e){showError("Catalog data could not be loaded",e);}}
    @FXML private void refresh(){try{var rows=repository.findTitles(catalogSearch.getText(),genreFilter.getValue(),"");catalogGrid.getChildren().clear();if(rows.isEmpty()){Label empty=new Label("No books have been added to the catalog yet.");empty.getStyleClass().add("member-meta");catalogGrid.add(empty,0,0,3,1);return;}for(int i=0;i<rows.size();i++)catalogGrid.add(card(rows.get(i)),i%3,i/3);}catch(SQLException e){showError("Catalog data could not be loaded",e);}}
    private VBox card(TitleRow row){VBox card=new VBox(9);card.getStyleClass().add("catalog-grid-card");Label genre=new Label(row.genre()==null||row.genre().isBlank()?"Uncategorized":row.genre());genre.getStyleClass().add("book-genre");Label title=new Label(row.name());title.setWrapText(true);title.getStyleClass().add("member-book-title");Label author=new Label(row.author());author.getStyleClass().add("member-meta");Pane spacer=new Pane();VBox.setVgrow(spacer,Priority.ALWAYS);boolean available=row.availableCopies()>0;Label status=new Label(available?row.availableCopies()+" available":"Checked out");status.getStyleClass().add(available?"member-status-good":"member-status-warn");Button action=new Button(available?"Borrow":"Place hold");action.setMaxWidth(Double.MAX_VALUE);action.getStyleClass().add(available?"member-primary":"member-secondary");action.setOnAction(event->act(row,available));card.getChildren().addAll(genre,title,author,spacer,status,action);return card;}
    private void act(TitleRow row,boolean available){if(!UserSession.isSignedIn()){new Alert(Alert.AlertType.INFORMATION,"Sign in with a database member account to borrow books or place holds.").showAndWait();return;}try{if(available)repository.checkoutTitle(UserSession.getCurrentUser().getUserID(),row.id(),LocalDateTime.now().plusDays(14));else repository.placeHold(UserSession.getCurrentUser().getUserID(),row.id());refresh();new Alert(Alert.AlertType.INFORMATION,available?"Book borrowed successfully.":"Hold placed successfully.").showAndWait();}catch(SQLException e){showError("The request could not be completed",e);}}
}
