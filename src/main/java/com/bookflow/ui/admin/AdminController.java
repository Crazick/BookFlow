package com.bookflow.ui.admin;

import com.bookflow.model.Book;
import com.bookflow.service.AdminService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AdminController {

    private final AdminService service = new AdminService();

    @FXML
    private TextField idField;

    @FXML
    private TextField titleField;

    @FXML
    private TextField authorField;

    @FXML
    private TextField genreField;

    @FXML
    private TextField totalCopiesField;

    @FXML
    private TextField availableCopiesField;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleAdd() {

        try {

            Book book = new Book(
                    0,
                    titleField.getText(),
                    authorField.getText(),
                    genreField.getText(),
                    Integer.parseInt(totalCopiesField.getText()),
                    Integer.parseInt(availableCopiesField.getText())
            );

            boolean success = service.addBook(book);

            statusLabel.setText(
                    success
                            ? "Książka dodana"
                            : "Nie udało się dodać książki");

        } catch (Exception e) {
            statusLabel.setText("Błędne dane");
        }
    }

    @FXML
    private void handleUpdate() {

        try {

            Book book = new Book(
                    Integer.parseInt(idField.getText()),
                    titleField.getText(),
                    authorField.getText(),
                    genreField.getText(),
                    Integer.parseInt(totalCopiesField.getText()),
                    Integer.parseInt(availableCopiesField.getText())
            );

            boolean success = service.updateBook(book);

            statusLabel.setText(
                    success
                            ? "Zaktualizowano książkę"
                            : "Nie udało się zaktualizować");

        } catch (Exception e) {
            statusLabel.setText("Błędne dane");
        }
    }

    @FXML
    private void handleDelete() {

        try {

            int id = Integer.parseInt(idField.getText());

            boolean success = service.deleteBook(id);

            statusLabel.setText(
                    success
                            ? "Usunięto książkę"
                            : "Nie znaleziono książki");

        } catch (Exception e) {
            statusLabel.setText("Błędne ID");
        }
    }

    @FXML
    private void handleLoad() {

        try {

            int id = Integer.parseInt(idField.getText());

            Book book = service.getBook(id);

            if (book == null) {
                statusLabel.setText("Nie znaleziono książki");
                return;
            }

            titleField.setText(book.title());
            authorField.setText(book.author());
            genreField.setText(book.genre());

            totalCopiesField.setText(
                    String.valueOf(book.totalCopies()));

            availableCopiesField.setText(
                    String.valueOf(book.availableCopies()));

            statusLabel.setText("Wczytano książkę");

        } catch (Exception e) {
            statusLabel.setText("Błędne ID");
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
                    (Stage) statusLabel.getScene().getWindow();

            stage.setScene(scene);
            stage.setTitle("BookFlow - Logowanie");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Nie udało się wylogować");
        }
    }
}
