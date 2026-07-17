package ui.librarian;

import database.LibraryRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import model.DisplayIds;

public class ReportsController extends NavigationController {
 @FXML private ComboBox<String> reportType, reportFormat;
 @FXML private DatePicker reportStart, reportEnd;
 @FXML private Label reportResult;
 private final LibraryRepository repository=new LibraryRepository();
 @FXML private void initialize(){reportType.setItems(FXCollections.observableArrayList("Book inventory","Borrowing records","Overdue books","Fine records"));reportType.getSelectionModel().selectFirst();reportFormat.setItems(FXCollections.observableArrayList("On-screen summary","CSV"));reportFormat.getSelectionModel().selectFirst();reportResult.setText("No reports have been generated in this session.");}
 @FXML private void clearReport(){reportType.getSelectionModel().clearSelection();reportFormat.getSelectionModel().selectFirst();reportStart.setValue(null);reportEnd.setValue(null);}
 @FXML private void generateReport(){if(reportType.getValue()==null){new Alert(Alert.AlertType.WARNING,"Choose a report type.").showAndWait();return;}if(reportStart.getValue()!=null&&reportEnd.getValue()!=null&&reportEnd.getValue().isBefore(reportStart.getValue())){new Alert(Alert.AlertType.WARNING,"The end date cannot be before the start date.").showAndWait();return;}try{List<String> lines=reportLines();int records=Math.max(0,lines.size()-1);String result=records==0?"No matching records were found.":records+" record"+(records==1?"":"s")+" found.";if("CSV".equals(reportFormat.getValue())){FileChooser chooser=new FileChooser();chooser.setTitle("Save library report");chooser.setInitialFileName(reportType.getValue().toLowerCase().replace(' ','-')+"-"+LocalDate.now()+".csv");chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files","*.csv"));File file=chooser.showSaveDialog(reportResult.getScene().getWindow());if(file==null)return;Files.writeString(file.toPath(),String.join(System.lineSeparator(),lines));result+=" Saved to "+file.getName()+".";}reportResult.setText(reportType.getValue()+" · "+LocalDate.now()+" · "+result);}catch(SQLException|IOException e){showError("The report could not be generated",e);}}
 private List<String> reportLines() throws SQLException{return switch(reportType.getValue()){case "Book inventory"->{var rows=repository.findTitles("","","");var out=new java.util.ArrayList<String>();out.add("Title ID,Title,Author,Genre,ISBN,Total Copies,Available Copies");rows.forEach(r->out.add(csv(DisplayIds.shortId("t",r.id()),r.name(),r.author(),r.genre(),r.isbn(),r.totalCopies(),r.availableCopies())));yield out;}case "Borrowing records"->{var rows=repository.findCheckouts("",false).stream().filter(r->inRange(r.checkedOutAt()==null?null:r.checkedOutAt().toLocalDate())).toList();var out=new java.util.ArrayList<String>();out.add("Checkout ID,Member,Copy ID,Title,Checked Out,Due,Returned");rows.forEach(r->out.add(csv(DisplayIds.shortId("co",r.id()),r.member(),DisplayIds.shortId("c",r.copyId()),r.title(),r.checkedOutAt(),r.dueAt(),r.returnedAt())));yield out;}case "Overdue books"->{var rows=repository.findOverdueCheckouts().stream().filter(r->inRange(r.dueAt()==null?null:r.dueAt().toLocalDate())).toList();var out=new java.util.ArrayList<String>();out.add("Checkout ID,Member,Copy ID,Title,Due Date");rows.forEach(r->out.add(csv(DisplayIds.shortId("co",r.id()),r.member(),DisplayIds.shortId("c",r.copyId()),r.title(),r.dueAt())));yield out;}case "Fine records"->{var rows=repository.findFines("");var out=new java.util.ArrayList<String>();out.add("Fine ID,Checkout ID,Member,Amount,Status");rows.forEach(r->out.add(csv(DisplayIds.shortId("f",r.id()),DisplayIds.shortId("co",r.checkoutId()),r.member(),r.amount(),r.status())));yield out;}default->List.of("No records");};}
 private boolean inRange(LocalDate date){if(date==null)return false;return(reportStart.getValue()==null||!date.isBefore(reportStart.getValue()))&&(reportEnd.getValue()==null||!date.isAfter(reportEnd.getValue()));}
 private String csv(Object... values){return java.util.Arrays.stream(values).map(v->"\""+String.valueOf(v==null?"":v).replace("\"","\"\"")+"\"").collect(java.util.stream.Collectors.joining(","));}
}
