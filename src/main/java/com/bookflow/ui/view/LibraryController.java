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

/**
 * Kontroler widoku biblioteki użytkownika.
 * <p>
 * Odpowiada za obsługę funkcjonalności dostępnych
 * dla zalogowanego użytkownika:
 * <ul>
 *     <li>wyszukiwanie książek,</li>
 *     <li>wypożyczanie książek,</li>
 *     <li>zwracanie książek,</li>
 *     <li>przeglądanie aktualnie wypożyczonych książek,</li>
 *     <li>wylogowanie z systemu.</li>
 * </ul>
 *
 * Kontroler korzysta z warstwy serwisowej LibraryService
 * do wykonywania operacji na książkach i wypożyczeniach.
 */
public class LibraryController {

    /** Serwis biblioteczny obsługujący operacje użytkownika. */
    private final LibraryService service = new LibraryService();

    /** Identyfikator aktualnie zalogowanego użytkownika. */
    private int userId;

    /** Pole tekstowe służące do wyszukiwania książek. */
    @FXML
    private TextField searchField;

    /** Pole tekstowe zawierające identyfikator książki. */
    @FXML
    private TextField bookIdField;

    /** Tabela wyświetlająca wyniki wyszukiwania książek. */
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

    /** Kolumna liczby dostępnych egzemplarzy. */
    @FXML
    private TableColumn<Book, Integer> availableColumn;

    /** Lista aktualnie wypożyczonych książek. */
    @FXML
    private ListView<String> borrowedList;

    /**
     * Ustawia identyfikator zalogowanego użytkownika.
     * <p>
     * Po ustawieniu identyfikatora odświeżana jest
     * lista wypożyczonych książek.
     *
     * @param userId identyfikator użytkownika
     */
    public void setUserId(int userId) {
        this.userId = userId;
        refreshBorrowed();
    }

    /**
     * Inicjalizuje kontroler po załadowaniu widoku.
     * <p>
     * Konfiguruje kolumny tabeli książek oraz
     * ustawia właściwości tabeli.
     */
    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().id()).asObject());

        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().title()));

        authorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().author()));

        availableColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().availableCopies()).asObject());

        booksTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        idColumn.setResizable(false);
        titleColumn.setResizable(false);
        authorColumn.setResizable(false);
        availableColumn.setResizable(false);
    }

    /**
     * Wyszukuje książki na podstawie podanej frazy.
     * <p>
     * Wyniki wyszukiwania są wyświetlane
     * w tabeli książek.
     */
    @FXML
    private void handleSearch() {
        String phrase = searchField.getText();
        booksTable.setItems(FXCollections.observableArrayList(service.searchBook(phrase)));
    }

    /**
     * Wypożycza książkę o podanym identyfikatorze.
     * <p>
     * Po pomyślnym wypożyczeniu odświeżana jest
     * lista wypożyczonych książek oraz wyniki wyszukiwania.
     */
    @FXML
    private void handleBorrow() {
        try {
            int bookId = Integer.parseInt( bookIdField.getText());

            boolean success = service.borrowBook(userId, bookId);

            if (success) {
                refreshBorrowed();
                refreshSearchResults();
                System.out.println("Książka wypożyczona");
            } 
            else {
                System.out.println("Nie można wypożyczyć książki");
            }
        } 
        catch (NumberFormatException e) {
            System.out.println( "Niepoprawne ID książki");
        }
    }

    /**
     * Zwraca książkę o podanym identyfikatorze.
     * <p>
     * Po pomyślnym zwrocie odświeżana jest lista
     * wypożyczonych książek oraz wyniki wyszukiwania.
     * Dodatkowo wyświetlana jest ewentualna kara
     * za przetrzymanie książki.
     */
    @FXML
    private void handleReturn() {
        try {
            int bookId = Integer.parseInt(bookIdField.getText());

            double fine = service.returnBook(userId, bookId);

            if (fine >= 0) {
                refreshBorrowed();
                refreshSearchResults();
                System.out.println( "Książka zwrócona. Kara: " + fine + " zł");
            } 
            else {
                System.out.println("Błąd zwrotu książki");
            }
        } 
        catch (NumberFormatException e) {
            System.out.println( "Niepoprawne ID książki");
        }
    }

    /**
     * Wylogowuje użytkownika z systemu.
     * <p>
     * Przełącza aktualny widok na ekran logowania.
     */
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bookflow/ui/login/login.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) booksTable.getScene().getWindow();
            stage.setScene(scene);

        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Odświeża wyniki wyszukiwania książek.
     * <p>
     * Ponownie wykonuje wyszukiwanie dla aktualnej
     * frazy znajdującej się w polu wyszukiwania.
     */
    private void refreshSearchResults() {
        String phrase = searchField.getText();

        booksTable.setItems(FXCollections.observableArrayList(service.searchBook(phrase)));
    }

    /**
     * Odświeża listę wypożyczonych książek.
     * <p>
     * Pobiera aktualną listę wypożyczeń użytkownika
     * i wyświetla ją w komponencie ListView.
     */
    private void refreshBorrowed() {
        borrowedList.getItems().clear();

        for (BorrowedBook book : service.getBorrowedBooks(userId)) {
            borrowedList.getItems().add(book.toString());
        }
    }
}
