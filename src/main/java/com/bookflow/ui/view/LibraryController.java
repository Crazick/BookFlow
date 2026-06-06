package com.bookflow.ui.view;

import com.bookflow.model.Book;
import com.bookflow.model.BorrowedBook;
import com.bookflow.service.LibraryService;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LibraryController {

    private final LibraryService service = new LibraryService();

    private int userId;

    @FXML
    private TextField searchField;

    @FXML
    private TextField bookIdField;

    @FXML
    private TableView<Book> booksTable;

    @FXML
    private TableColumn<Book, Integer> idColumn;

    @FXML
    private TableColumn<Book, String> titleColumn;

    @FXML
    private TableColumn<Book, String> authorColumn;

    @FXML
    private TableColumn<Book, Integer> availableColumn;

    @FXML
    private ListView<String> borrowedList;

    public void setUserId(int userId) {
        this.userId = userId;
        refreshBorrowed();
    }

    @FXML
    private void initialize() {

        idColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(
                        cellData.getValue().id()
                ).asObject());

        titleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().title()
                ));

        authorColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().author()
                ));

        availableColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(
                        cellData.getValue().availableCopies()
                ).asObject());
    }

    @FXML
    private void handleSearch() {

        String phrase = searchField.getText();

        booksTable.setItems(
                FXCollections.observableArrayList(
                        service.searchBook(phrase)
                )
        );
    }

    @FXML
    private void handleBorrow() {

        try {

            int bookId = Integer.parseInt(
                    bookIdField.getText());

            boolean success =
                    service.borrowBook(userId, bookId);

            if (success) {

                refreshBorrowed();
                refreshSearchResults();

                System.out.println(
                        "Książka wypożyczona");

            } else {

                System.out.println(
                        "Nie można wypożyczyć książki");
            }

        } catch (NumberFormatException e) {

            System.out.println(
                    "Niepoprawne ID książki");
        }
    }

    @FXML
    private void handleReturn() {

        try {

            int bookId = Integer.parseInt(
                    bookIdField.getText());

            double fine =
                    service.returnBook(userId, bookId);

            if (fine >= 0) {

                refreshBorrowed();
                refreshSearchResults();

                System.out.println(
                        "Książka zwrócona. Kara: "
                                + fine + " zł");

            } else {

                System.out.println(
                        "Błąd zwrotu książki");
            }

        } catch (NumberFormatException e) {

            System.out.println(
                    "Niepoprawne ID książki");
        }
    }

    @FXML
    private void handleLogout() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/com/bookflow/ui/login/login.fxml"));

            Scene scene =
                    new Scene(loader.load());

            Stage stage =
                    (Stage) booksTable
                            .getScene()
                            .getWindow();

            stage.setScene(scene);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void refreshSearchResults() {

        String phrase = searchField.getText();

        booksTable.setItems(
                FXCollections.observableArrayList(
                        service.searchBook(phrase)
                )
        );
    }

    private void refreshBorrowed() {

        borrowedList.getItems().clear();

        for (BorrowedBook book :
                service.getBorrowedBooks(userId)) {

            borrowedList.getItems().add(
                    book.toString());
        }
    }
}
