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

/**
 * Kontroler panelu administratora.
 * <p>
 * Odpowiada za obsługę interfejsu administratora,
 * umożliwiając zarządzanie książkami w systemie.
 * Administrator może:
 * <ul>
 *     <li>dodawać nowe książki,</li>
 *     <li>edytować istniejące książki,</li>
 *     <li>usuwać książki,</li>
 *     <li>przeglądać listę wszystkich książek,</li>
 *     <li>wylogować się z aplikacji.</li>
 * </ul>
 *
 * Kontroler komunikuje się z serwerem poprzez Socket
 * oraz korzysta z warstw serwisowych AdminService
 * i LibraryService.
 */

public class AdminController {


    /** Pole tekstowe zawierające identyfikator książki. */
    @FXML
    private TextField idField;

    /** Pole tekstowe zawierające tytuł książki. */
    @FXML
    private TextField titleField;

    /** Pole tekstowe zawierające autora książki. */
    @FXML
    private TextField authorField;

    /** Pole tekstowe zawierające gatunek książki. */
    @FXML
    private TextField genreField;

    /** Pole tekstowe zawierające całkowitą liczbę egzemplarzy. */
    @FXML
    private TextField totalCopiesField;

    /** Pole tekstowe zawierające liczbę dostępnych egzemplarzy. */
    @FXML
    private TextField availableCopiesField;

    /** Etykieta wyświetlająca komunikaty dla administratora. */
    @FXML
    private Label statusLabel;

    /** Tabela wyświetlająca listę wszystkich książek. */
    @FXML
    private TableView<Book> booksTable;

    /** Kolumna identyfikatora książki. */
    @FXML
    private TableColumn<Book, Integer> idColumn;

    /** Kolumna tytułu książki. */
    @FXML
    private TableColumn<Book, String> titleColumn;

    /** Kolumna autora książki. */
    @FXML
    private TableColumn<Book, String> authorColumn;

    /** Kolumna gatunku książki. */
    @FXML
    private TableColumn<Book, String> genreColumn;

    /** Kolumna całkowitej liczby egzemplarzy. */
    @FXML
    private TableColumn<Book, Integer> totalColumn;

    /** Kolumna liczby dostępnych egzemplarzy. */
    @FXML
    private TableColumn<Book, Integer> availableColumn;

    /** Obiekt służący do serializacji i deserializacji JSON. */
    private final Gson gson = new Gson();

    /** Serwis administracyjny obsługujący operacje na książkach. */
    private final AdminService adminService = new AdminService();

    /** Serwis biblioteczny udostępniający listę książek. */
    private final LibraryService libraryService = new LibraryService();

    /**
     * Inicjalizuje kontroler po załadowaniu widoku.
     * <p>
     * Konfiguruje kolumny tabeli, pobiera listę książek
     * oraz ustawia obsługę zaznaczenia książki w tabeli.
     */
    @FXML
    private void initialize() {

        idColumn.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().id()).asObject());

        titleColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().title()));

        authorColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().author()));

        genreColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().genre()));

        totalColumn.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().totalCopies()).asObject());

        availableColumn.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().availableCopies()).asObject());

        refreshBooks();

        booksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldBook, book) -> {
                    if (book == null) {
                        return;
                    }

                    idField.setText(String.valueOf(book.id()));

                    titleField.setText(book.title());
                    authorField.setText(book.author());
                    genreField.setText(book.genre());

                    totalCopiesField.setText( String.valueOf(book.totalCopies()));

                    availableCopiesField.setText(String.valueOf(book.availableCopies()));
                });
    }

    /**
     * Dodaje nową książkę do systemu.
     * <p>
     * Tworzy komunikat ADD_BOOK, wysyła go do serwera
     * i odświeża tabelę po pomyślnym dodaniu książki.
     */
    @FXML
    private void handleAdd() {
        try (Socket socket = new Socket("localhost", 5000); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out = new PrintWriter( socket.getOutputStream(), true)) {
            NetworkMessage msg = new NetworkMessage( "ADD_BOOK", List.of( titleField.getText(), authorField.getText(), genreField.getText(), totalCopiesField.getText(), availableCopiesField.getText()));

            out.println(gson.toJson(msg));

            String response = in.readLine();

            if ("ADD_BOOK_SUCCESS".equals(response)) {
                statusLabel.setText( "Książka dodana");
                refreshBooks();
            } 
            else {
                statusLabel.setText("Nie udało się dodać książki");
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Błąd: " + e.getMessage());
        }
    }

    /**
     * Aktualizuje dane istniejącej książki.
     * <p>
     * Tworzy komunikat UPDATE_BOOK, wysyła go do serwera
     * i odświeża tabelę po pomyślnej aktualizacji.
     */
    @FXML
    private void handleUpdate() {

        try (Socket socket = new Socket("localhost", 5000); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out = new PrintWriter( socket.getOutputStream(), true)) {
            NetworkMessage msg = new NetworkMessage( "UPDATE_BOOK", List.of(idField.getText(), titleField.getText(), authorField.getText(), genreField.getText(), totalCopiesField.getText(), availableCopiesField.getText()));

            out.println(gson.toJson(msg));

            String response = in.readLine();

            if ("UPDATE_BOOK_SUCCESS".equals(response)) {
                statusLabel.setText("Zaktualizowano książkę");
                refreshBooks();
            } 
            else {
                statusLabel.setText( "Nie udało się zaktualizować");
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Błąd: " + e.getMessage());
        }
    }

    /**
     * Usuwa książkę z systemu.
     * <p>
     * Tworzy komunikat DELETE_BOOK, wysyła go do serwera
     * i odświeża listę książek po pomyślnym usunięciu.
     */
    @FXML
    private void handleDelete() {
        try (Socket socket = new Socket("localhost", 5000); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out = new PrintWriter(socket.getOutputStream(), true)){
            NetworkMessage msg = new NetworkMessage("DELETE_BOOK", List.of(idField.getText()));

            out.println(gson.toJson(msg));

            String response = in.readLine();

            if ("DELETE_BOOK_SUCCESS".equals(response)) {
                statusLabel.setText("Usunięto książkę");
                refreshBooks();
            } 
            else {
                statusLabel.setText("Nie znaleziono książki");
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Błąd: " + e.getMessage());
        }
    }

    /**
     * Wczytuje dane książki o podanym identyfikatorze.
     * <p>
     * Pobiera książkę z warstwy serwisowej i uzupełnia
     * formularz jej danymi.
     */
    @FXML
    private void handleLoad() {
        try {
            int id = Integer.parseInt( idField.getText());

            Book book = adminService.getBook(id);

            if (book == null) {
                statusLabel.setText("Nie znaleziono książki");
                return;
            }

            titleField.setText(book.title());
            authorField.setText(book.author());
            genreField.setText(book.genre());

            totalCopiesField.setText(String.valueOf(book.totalCopies()));

            availableCopiesField.setText(String.valueOf(book.availableCopies()));

            statusLabel.setText("Wczytano książkę");
        } 
        catch (Exception e) {
            statusLabel.setText("Błędne ID");
        }
    }

    /**
     * Wylogowuje administratora.
     * <p>
     * Przełącza aktualny widok na ekran logowania.
     */
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader =new FXMLLoader(getClass().getResource("/com/bookflow/ui/login/login.fxml"));

            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) statusLabel.getScene().getWindow();

            stage.setScene(scene);
            stage.setTitle("BookFlow - Logowanie");

            stage.show();
        } 
        catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Nie udało się wylogować");
        }
    }

    /**
     * Odświeża zawartość tabeli książek.
     * <p>
     * Pobiera aktualną listę książek z LibraryService
     * i wyświetla ją w komponencie TableView.
     */
    private void refreshBooks() {
        booksTable.setItems(FXCollections.observableArrayList(libraryService.getAllBooks()));
    }
}
