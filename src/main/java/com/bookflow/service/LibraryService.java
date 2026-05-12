package com.bookflow.service;

import com.bookflow.dao.BookDAO;
import com.bookflow.dao.BorrowDAO;
import com.bookflow.dao.UserDAO;
import com.bookflow.enums.LoginStatus;
import com.bookflow.enums.RegisterStatus;
import com.bookflow.model.Book;
import com.bookflow.model.BorrowedBook;
import java.util.List;

/**
 * Serwis odpowiedzialny za logikę biblioteki.
 * <p>
 * Klasa odpowiada za operacje związane z:
 * <ul>
 *      <li>książkami,</li>
 *      <li>użytkownikami,</li>
 *      <li>wypożyczeniami.</li>
 * </ul>
 *  Wykorzystuje klasy DAO do komunikacji z bazą daych.
 */
public class LibraryService
{
    /** Obiekt DAO odpowiedzialny za operacje na książkach. */
    private BookDAO bookDAO = new BookDAO();
    /** Obiekt DAO odpowiedzialny za operacje na użytkownikach. */
    private UserDAO userDAO = new UserDAO();
    /** Obiekt DAO odpowiedzialny za operacje na wypożyczeniach. */
    private BorrowDAO borrowDAO = new BorrowDAO();

    // === BOOKS ===

    /**
     * Wyszukuje książki na podstawie podanej frazy.
     * <p>
     * Fraza może odnosić się do:
     * <ul>
     *     <li>tytułu książki,</li>
     *     <li>autora książki.</li>
     * </ul>
     * @param phrase fraza wyszukiwania
     * @return lista znalezionych książek
     */
    public List<Book> searchBook(String phrase){
        return bookDAO.search(phrase);
    }

    /**
     * Wypożycza książkę dla wskazanego użytkownika.
     * @param userId identyfikator użytkownika
     * @param bookId identyfikator książki
     * @return  {@code true} jeśli wypożyczenie zakończyło się sukcesem,
     *          {@code false} w przeciwnym wypadku
     */
    public boolean borrowBook(int userId, int bookId) {
        return borrowDAO.borrow(userId, bookId);
    }

    /**
     * Zwraca książkę wypożyczoną przez danego użytkownika.
     * @param userId identyfikator użytkownika
     * @param bookId identyfikator książki
     * @return wysokość ewentualnej kary za opóźnienie
     */
    public double returnBook(int userId, int bookId) {
        return borrowDAO.returnBook(userId, bookId);
    }

    /**
     * Pobiera listę książek aktualnie wypożyczonych przez użytkownika.
     * @param userId identyfikator użytkownika
     * @return listę wypożyczonych książek
     */
    public List<BorrowedBook> getBorrowedBooks(int userId){
        return borrowDAO.getBorrowedBooks(userId);
    }

    // === USERS ===

    /**
     * Loguje użytkownika do systemu.
     * @param username nazwa użytkownika
     * @param password hasło użytkownika
     * @return status logowania
     */
    public LoginStatus login(String username, String password){
        return userDAO.login(username, password);
    }

    /**
     * Rejestruje nowego użytkownika do systemu.
     * @param username nazwa użytkownika
     * @param password hasło użytkownika
     * @return status rejestracji
     */
    public RegisterStatus register(String username, String password){
        return userDAO.register(username, password);
    }

    /**
     * Pobiera identyfikator użytkownika na podstawie nazwy użytkownika.
     * @param username nazwa użytkownika
     * @return identyfikator użytkownika
     */
    public int getUserID(String username){
        return userDAO.getUserID(username);
    }

}