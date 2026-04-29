package com.bookflow.service;

import com.bookflow.dao.BookDAO;
import com.bookflow.dao.BorrowDAO;
import com.bookflow.dao.UserDAO;

import com.bookflow.enums.LoginStatus;
import com.bookflow.enums.RegisterStatus;
import com.bookflow.model.Book;

import java.util.List;

public class LibraryService{
    private BookDAO bookDAO = new BookDAO();
    private UserDAO userDAO = new UserDAO();
    private BorrowDAO borrowDAO = new BorrowDAO();

    // === BOOKS ===
    // wyszukiwanie przez frazę (autor/tytuł)
    public List<Book> searchBook(String phrase){
        return bookDAO.search(phrase);
    }
    // wypożyczanie książek
    public boolean borrowBook(int userId, int bookId) {
        return borrowDAO.borrow(userId, bookId);
    }
    // zwrot książek
    public boolean returnBook(int userId, int bookId) {
        return borrowDAO.returnBook(userId, bookId);
    }

    // === USERS ===
    public LoginStatus login(String username, String password){
        return userDAO.login(username, password);
    }
    public RegisterStatus register(String username, String password){
        return userDAO.register(username, password);
    }
    public int getUserID(String username){
        return userDAO.getUserID(username);
    }

}