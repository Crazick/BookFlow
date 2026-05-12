package com.bookflow.server;

import com.bookflow.enums.LoginStatus;
import com.bookflow.enums.RegisterStatus;
import com.bookflow.model.Book;
import com.bookflow.model.BorrowedBook;
import com.bookflow.service.LibraryService;
import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Klasa obsługująca pojedyńczego klienta serwera.
 * <p>
 * Każdy klient obsługiwany jest w osobnym wątku.
 * Klasa odpowiada za:
 * <ul>
 *     <li>odbieranie komend od klienta,</li>
 *     <li>obsługę logowania i rejestracji,</li>
 *     <li>wyszukiwanie książek,</li>
 *     <li>wypożyczenie i zwracanie książek.</li>
 * </ul>
 */
public class ClientHandler implements Runnable
{
    /** Gniazdo połączenia klienta. */
    private Socket socket;
    /** Strumień wysyłający dane do klienta. */
    private PrintWriter out;
    /** Strumień odbieracjący dane od klienta. */
    private BufferedReader in;
    /** Serwis biblioteki obsługującej logikę aplikacji. */
    private LibraryService libraryService;
    /** Identyfikator aktualnie zalogowanego użytkownika. */
    private int loggedUserId = -1;
    /** Nazwa aktualnie zalogowanego użytkownika. */
    private String loggedUsername = null;
    /** Informacje o stanie logowania użytkownika. */
    private boolean loggedIn = false;

    /**
     * Tworzy nową obsługę klienta.
     * @param socket gniazdo klienta
     * @param libraryService serwis biblioteki
     */
    public ClientHandler(Socket socket, LibraryService libraryService) {
        this.socket = socket;
        this.libraryService = libraryService;
    }

    /**
     * Uruchamia obsługę klienta w osobnym wątku.
     * <p>
     * Metoda nasłuchuje wiadomości od klienta i wykonuje odpowiednie komendy.
     */
    @Override
    public void run()
    {
        System.out.println("Obsługa klienta w wątku: " + Thread.currentThread().getId());

        try{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String message;
            while((message = in.readLine()) != null)
            {
                System.out.println("Otrzymano: " + message);
                String[] parts = message.split(" ");
                String command = parts[0].toUpperCase();

                switch(command){
                    case "REGISTER":
                        handleRegister(parts);
                        break;
                    case "LOGIN":
                        handleLogin(parts);
                        break;
                    case "SEARCH":
                        handleSearch(parts);
                        break;
                    case "BORROWED":
                        handleBorrowed();
                        break;
                    case "BORROW":
                        handleBorrow(parts);
                        break;
                    case "RETURN":
                        handleReturn(parts);
                        break;
                    case "LOGOUT":
                        handleLogout();
                        break;
                    default:
                        out.println("UNKNOWN_COMMAND");
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try{
                System.out.println("Klient się rozłączył");
                socket.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Obsługa rejestracji nowego użytkownika.
     * @param parts dane komendy przesłanej przez klienta
     */
    private void handleRegister(String[] parts) {
        if (parts.length < 3) {
            out.println("REGISTER_FAILED");
            return;
        }

        RegisterStatus status = libraryService.register(parts[1], parts[2]);

        switch (status) {
            case SUCCESS -> out.println("REGISTER_SUCCESS");
            case USER_EXISTS -> out.println("USER_EXISTS");
            case ERROR -> out.println("REGISTER_ERROR");
        }
    }

    /**
     * Obsługa logowania użytkownika
     * @param parts dane komendy przesłanej przez klienta
     */
    private void handleLogin(String[] parts) {
        if (parts.length < 3) {
            out.println("LOGIN_FAILED");
            return;
        }

        String username = parts[1];
        String password = parts[2];

        LoginStatus status = libraryService.login(username, password);

        switch (status) {
            case SUCCESS:
                loggedIn = true;
                loggedUsername = username;
                loggedUserId = libraryService.getUserID(username);
                out.println("LOGIN_SUCCESS");
                break;
            case USER_NOT_FOUND:
                out.println("USER_NOT_FOUND");
                break;
            case WRONG_PASSWORD:
                out.println("WRONG_PASSWORD");
                break;
            case ERROR:
                out.println("LOGIN_ERROR");
                break;
        }
    }

    /**
     * Obsługuje wyszukiwanie książek.
     * @param parts dane komendy przesłanej przez klienta
     */
    private void handleSearch(String[] parts) {
        if (!loggedIn) {
            out.println("LOGIN_REQUIRED");
            return;
        }

        if (parts.length < 2) {
            out.println("SEARCH_FAILED");
            return;
        }

        String phrase = parts[1];

        List<Book> result = libraryService.searchBook(phrase);
        out.println("BEGIN");
        for (Book b : result) {
            out.println(b.toString());
        }
        out.println("END");
    }

    /**
     * Pobiera listę wypożyczonych ksiązek użytkownika.
     */
    private void handleBorrowed(){
        if (!loggedIn) {
            out.println("LOGIN_REQUIRED");
            return;
        }

        List<BorrowedBook> results = libraryService.getBorrowedBooks(loggedUserId);
        if (results.isEmpty()) {
            out.println("BEGIN");
            out.println("Brak wypożyczonych książek.");
            out.println("END");
            return;
        }

        out.println("BEGIN");
        for(BorrowedBook b : results){
            out.println(b.toString());
        }
        out.println("END");
    }

    /**
     * Obsługuje wypożyczanie książek.
     * @param parts dane komendy przesłanej przez klienta
     */
    private void handleBorrow(String[] parts) {
        if (!loggedIn) {
            out.println("LOGIN_REQUIRED");
            return;
        }

        if (parts.length < 2) {
            out.println("BORROW_FAILED");
            return;
        }

        try {
            int bookId = Integer.parseInt(parts[1]);
            boolean success = libraryService.borrowBook(loggedUserId, bookId);
            out.println(success ? "BORROW_SUCCESS" : "BORROW_FAILED");

        } catch (NumberFormatException e) {
            out.println("INVALID_ID");
        }
    }

    /**
     * Obsługuje zwracanie książki.
     * @param parts dane komendy przesłanej przez klienta
     */
    private void handleReturn(String[] parts) {
        if (!loggedIn) {
            out.println("LOGIN_REQUIRED");
            return;
        }

        if (parts.length < 2) {
            out.println("RETURN_FAILED");
            return;
        }

        try {
            int bookId = Integer.parseInt(parts[1]);
            double fine = libraryService.returnBook(loggedUserId, bookId);

            if (fine == -1) {
                out.println("NO_BORROW_FOUND");
            } else if (fine == -2) {
                out.println("ERROR");
            } else {
                out.println("RETURN_SUCCESS " + fine);
            }

        } catch (NumberFormatException e) {
            out.println("INVALID_ID");
        }
    }

    /**
     * Wylogowanie aktualnego użytkownika.
     */
    private void handleLogout() {

        loggedIn = false;
        loggedUserId = -1;
        loggedUsername = null;

        out.println("LOGOUT_SUCCESS");
    }
}