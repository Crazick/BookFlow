package com.bookflow.ui.view;

import com.bookflow.model.Book;
import com.bookflow.model.BorrowedBook;
import com.bookflow.service.LibraryService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class LibraryController {

    private LibraryService service = new LibraryService();

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
    private ListView<String> borrowedList;

    public void setUserId(int userId) {
        this.userId = userId;
        refreshBorrowed();
    }

    @FXML
    private void initialize() {

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id"));

        titleColumn.setCellValueFactory(
                new PropertyValueFactory<>("title"));

        authorColumn.setCellValueFactory(
                new PropertyValueFactory<>("author"));
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

            int bookId = Integer.parseInt(bookIdField.getText());

            boolean success =
                    service.borrowBook(userId, bookId);

            if (success) {
                refreshBorrowed();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReturn() {

        try {

            int bookId = Integer.parseInt(bookIdField.getText());

            double fine =
                    service.returnBook(userId, bookId);

            if (fine >= 0) {
                refreshBorrowed();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource(
                            "/com/bookflow/ui/login/login.fxml"));

            Scene scene =
                    new Scene(loader.load());

            Stage stage =
                    (Stage) booksTable.getScene().getWindow();

            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
