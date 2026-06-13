package com.bookflow.ui.admin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import com.bookflow.model.Book;
import com.bookflow.model.NetworkMessage;
import com.bookflow.service.AdminService;
import com.bookflow.service.LibraryService;
import com.google.gson.Gson;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AdminController {

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
    private TableView<Book> booksTable;

    @FXML
    private TableColumn<Book, Integer> idColumn;

    @FXML
    private TableColumn<Book, String> titleColumn;

    @FXML
    private TableColumn<Book, String> authorColumn;

    @FXML
    private TableColumn<Book, String> genreColumn;

    @FXML
    private TableColumn<Book, Integer> totalColumn;

    @FXML
    private TableColumn<Book, Integer> availableColumn;

    private final Gson gson = new Gson();

    private final AdminService adminService =
            new AdminService();

    private final LibraryService libraryService =
            new LibraryService();

    @FXML
    private void initialize() {

        idColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(
                        cell.getValue().id()
                ).asObject());

        titleColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().title()));

        authorColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().author()));

        genreColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().genre()));

        totalColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(
                        cell.getValue().totalCopies()
                ).asObject());

        availableColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(
                        cell.getValue().availableCopies()
                ).asObject());

        refreshBooks();

        booksTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldBook, book) -> {

                    if (book == null) {
                        return;
                    }

                    idField.setText(
                            String.valueOf(book.id()));

                    titleField.setText(book.title());
                    authorField.setText(book.author());
                    genreField.setText(book.genre());

                    totalCopiesField.setText(
                            String.valueOf(
                                    book.totalCopies()));

                    availableCopiesField.setText(
                            String.valueOf(
                                    book.availableCopies()));
                });
    }

    @FXML
    private void handleAdd() {

        try (
                Socket socket = new Socket("localhost", 5000);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
                PrintWriter out = new PrintWriter(
                        socket.getOutputStream(), true)
        ) {

            NetworkMessage msg =
                    new NetworkMessage(
                            "ADD_BOOK",
                            List.of(
                                    titleField.getText(),
                                    authorField.getText(),
                                    genreField.getText(),
                                    totalCopiesField.getText(),
                                    availableCopiesField.getText()
                            )
                    );

            out.println(gson.toJson(msg));

            String response = in.readLine();

            if ("ADD_BOOK_SUCCESS".equals(response)) {

                statusLabel.setText(
                        "Książka dodana");

                refreshBooks();

            } else {

                statusLabel.setText(
                        "Nie udało się dodać książki");
            }

        } catch (Exception e) {

            e.printStackTrace();

            statusLabel.setText(
                    "Błąd: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {

        try (
                Socket socket = new Socket("localhost", 5000);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
                PrintWriter out = new PrintWriter(
                        socket.getOutputStream(), true)
        ) {

            NetworkMessage msg =
                    new NetworkMessage(
                            "UPDATE_BOOK",
                            List.of(
                                    idField.getText(),
                                    titleField.getText(),
                                    authorField.getText(),
                                    genreField.getText(),
                                    totalCopiesField.getText(),
                                    availableCopiesField.getText()
                            )
                    );

            out.println(gson.toJson(msg));

            String response = in.readLine();

            if ("UPDATE_BOOK_SUCCESS".equals(response)) {

                statusLabel.setText(
                        "Zaktualizowano książkę");

                refreshBooks();

            } else {

                statusLabel.setText(
                        "Nie udało się zaktualizować");
            }

        } catch (Exception e) {

            e.printStackTrace();

            statusLabel.setText(
                    "Błąd: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {

        try (
                Socket socket = new Socket("localhost", 5000);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
                PrintWriter out = new PrintWriter(
                        socket.getOutputStream(), true)
        ) {

            NetworkMessage msg =
                    new NetworkMessage(
                            "DELETE_BOOK",
                            List.of(idField.getText())
                    );

            out.println(gson.toJson(msg));

            String response = in.readLine();

            if ("DELETE_BOOK_SUCCESS".equals(response)) {

                statusLabel.setText(
                        "Usunięto książkę");

                refreshBooks();

            } else {

                statusLabel.setText(
                        "Nie znaleziono książki");
            }

        } catch (Exception e) {

            e.printStackTrace();

            statusLabel.setText(
                    "Błąd: " + e.getMessage());
        }
    }

    @FXML
    private void handleLoad() {

        try {

            int id =
                    Integer.parseInt(
                            idField.getText());

            Book book =
                    adminService.getBook(id);

            if (book == null) {

                statusLabel.setText(
                        "Nie znaleziono książki");

                return;
            }

            titleField.setText(book.title());
            authorField.setText(book.author());
            genreField.setText(book.genre());

            totalCopiesField.setText(
                    String.valueOf(
                            book.totalCopies()));

            availableCopiesField.setText(
                    String.valueOf(
                            book.availableCopies()));

            statusLabel.setText(
                    "Wczytano książkę");

        } catch (Exception e) {

            statusLabel.setText(
                    "Błędne ID");
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
                    (Stage) statusLabel
                            .getScene()
                            .getWindow();

            stage.setScene(scene);
            stage.setTitle(
                    "BookFlow - Logowanie");

            stage.show();

        } catch (Exception e) {

            e.printStackTrace();

            statusLabel.setText(
                    "Nie udało się wylogować");
        }
    }

    private void refreshBooks() {

        booksTable.setItems(
                FXCollections.observableArrayList(
                        libraryService.getAllBooks()
                )
        );
    }
}
