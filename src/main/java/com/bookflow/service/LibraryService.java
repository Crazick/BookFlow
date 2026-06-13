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

    /**
     * Tworzy instancję serwisu biblioteki.
     * <p>
     * Konstruktor domyślny inicjalizuje wewnętrzne obiekty DAO:
     * {@link BookDAO}, {@link UserDAO} oraz {@link BorrowDAO}.
     * Są one wykorzystywane do komunikacji z warstwą bazy danych.
     */
    public LibraryService() {}

    /**
     * Tworzy instancję serwisu biblioteki z wstrzykniętymi zależnościami DAO.
     * <p>
     * Konstruktor umożliwia przekazanie własnych implementacji DAO,
     * co jest szczególnie przydatne w testach jednostkowych
     * lub przy alternatywnych źródłach danych.
     *
     * @param bookDAO obiekt DAO odpowiedzialny za operacje na książkach
     * @param userDAO obiekt DAO odpowiedzialny za operacje na użytkownikach
     * @param borrowDAO obiekt DAO odpowiedzialny za operacje na wypożyczeniach
     */
    public LibraryService(BookDAO bookDAO, UserDAO userDAO, BorrowDAO borrowDAO)
    {
        this.bookDAO = bookDAO;
        this.userDAO = userDAO;
        this.borrowDAO = borrowDAO;
    }

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
    public synchronized boolean borrowBook(int userId, int bookId) {
        return borrowDAO.borrow(userId, bookId);
    }

    /**
     * Zwraca książkę wypożyczoną przez danego użytkownika.
     * @param userId identyfikator użytkownika
     * @param bookId identyfikator książki
     * @return wysokość ewentualnej kary za opóźnienie
     */
    public synchronized double returnBook(int userId, int bookId) {
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
     * Wykonuje walidację długości hasła przed próbą zapisu do bazy danych (min. 4 znaki).
     *
     * @param username nazwa użytkownika
     * @param password hasło użytkownika
     * @return status rejestracji
     */
    public synchronized RegisterStatus register(String username, String password){
        if(password == null || password.trim().length() < 4){
            return RegisterStatus.INVALID_PASSWORD;
        }
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

    /**
     * Pobiera role użytkownika na podstawie nazwy użytkownika.
     * @param username nazwa użytkownika
     * @return rola użytkownika
     */
    public String getUserRole(String username){
        return userDAO.getRole(username);
    }

    /**
     * Pobiera pełną listę książek dostępnych w systemie biblioteki.
     * @return lista wszystkich książek w systemie
     */
    public List<Book> getAllBooks(){
        return bookDAO.getAllBooks();
    }
}