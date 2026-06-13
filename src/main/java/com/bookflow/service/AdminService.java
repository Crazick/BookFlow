package com.bookflow.service;

import com.bookflow.dao.AdminDAO;
import com.bookflow.model.Book;

/**
 * Warstwa serwisowa dla operacji administracyjnych na książkach.
 * <p>
 * Odpowiada za logikę biznesową związaną z:
 * <ul>
 *      <li>dodawanie książek,</li>
 *      <li>edytowanie książek,</li>
 *      <li>usuwanie książek.</li>
 * </ul>
 * 
 * AdminService nie wykonuje zapytań SQL bezpośrednio - korzysta z AdminDAO.
 */
public class AdminService {
    /** DAO odpowiedzialne za operacje administracyjne na książkach. */
    private final AdminDAO adminDAO;

    /**
     * Tworzy serwis administracyjny z domyślnym DAO.
     */
    public AdminService(){
        this.adminDAO = new AdminDAO();
    }

    /**
     * Konstruktor z konkretnym DAO (do testów).
     * 
     * @param adminDAO DAO administracyjne
     */
    public AdminService(AdminDAO adminDAO){
        this.adminDAO = adminDAO;
    }

    /**
     * Dodaje nową książkę do systemu.
     * 
     * @param book książka do dodanie
     * @return true jeśli operacja się powiodła
     */
    public synchronized boolean addBook(Book book){
        if(book == null) return false;

        if(book.title() == null || book.title().isBlank()) return false;

        if(book.author() == null || book.author().isBlank()) return false;

        if(book.totalCopies() < 0) return false;

        return adminDAO.addBook(book);
    }

    /**
     * Edytuje dane książki.
     * 
     * @param book książka do edycji
     * @return tru jeśli aktualizacja się powiodła
     */
    public synchronized boolean updateBook(Book book){
        if(book == null) return false;

        if(book.id() < 0) return false;

        return adminDAO.uptadeBook(book);
    }

    /**
     * Usuwa książkę z systemu.
     * 
     * @param bookId ID książki
     * @return true jeśli usunięcie się powiodło
     */
    public synchronized boolean deleteBook(int bookId){
        if(bookId < 0) return false;

        return adminDAO.deleteBook(bookId);
    }

    /**
     * Pobiera książkę po ID.
     * 
     * @param bookId ID książki
     * @return Book lub null jeśli nie istnieje
     */
    public Book getBook(int bookId){
        if(bookId < 0) return null;

        return adminDAO.getById(bookId);
    }
}
