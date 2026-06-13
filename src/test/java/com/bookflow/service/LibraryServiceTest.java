package com.bookflow.service;

import com.bookflow.dao.BookDAO;
import com.bookflow.dao.BorrowDAO;
import com.bookflow.dao.UserDAO;
import com.bookflow.enums.LoginStatus;
import com.bookflow.enums.RegisterStatus;
import com.bookflow.model.Book;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LibraryServiceTest
{
    // === BOOKS ===
    // test searchBook()
    @Test
    void shouldReturnBookList()
    {
        BookDAO bookDAO = mock(BookDAO.class);
        List<Book> books = List.of(new Book(1, "title", "author", "genre", 5, 2));

        when(bookDAO.search("title")).thenReturn(books);

        LibraryService libraryService = new LibraryService(bookDAO, null, null);

        List<Book> result = libraryService.searchBook("title");

        assertEquals(1, result.size());
    }

    // test borrowBook()
    @Test
    void shouldBorrowBook()
    {
        BorrowDAO borrowDAO = mock(BorrowDAO.class);
        when(borrowDAO.borrow(1,1)).thenReturn(true);

        LibraryService libraryService = new LibraryService(null, null, borrowDAO);

        boolean result = libraryService.borrowBook(1,1);

        assertTrue(result);
    }

    // test returnBook()
    @Test
    void shouldReturnFine()
    {
        BorrowDAO borrowDAO = mock(BorrowDAO.class);
        when(borrowDAO.returnBook(1,1)).thenReturn(5.0);

        LibraryService service = new LibraryService(null, null, borrowDAO);

        double result = service.returnBook(1,1);

        assertEquals(5.0, result);
    }

    // === USERS==
    // test login()
    @Test
    void shouldReturnSuccessWhenLoginCorrect()
    {
        UserDAO userDAO = mock(UserDAO.class);
        when(userDAO.login("admin", "123")).thenReturn(LoginStatus.SUCCESS);

        LibraryService libraryService = new LibraryService(null, userDAO, null);

        LoginStatus result = libraryService.login("admin", "123");

        assertEquals(LoginStatus.SUCCESS, result);
    }

    // test "wrong password"
    @Test
    void shouldReturnWrongPassword()
    {
        UserDAO userDAO = mock(UserDAO.class);
        when(userDAO.login("admin", "bad")).thenReturn(LoginStatus.WRONG_PASSWORD);

        LibraryService libraryService = new LibraryService(null, userDAO, null);

        LoginStatus result = libraryService.login("admin", "bad");

        assertEquals(LoginStatus.WRONG_PASSWORD, result);
    }

    // test register()
    @Test
    void shouldRegisterUser()
    {
        UserDAO userDAO = mock(UserDAO.class);
        when(userDAO.register("user", "password")).thenReturn(RegisterStatus.SUCCESS);

        LibraryService libraryService = new LibraryService(null, userDAO, null);

        RegisterStatus result = libraryService.register("user", "password");

        assertEquals(RegisterStatus.SUCCESS, result);
    }

    // test register() with invalid password
    @Test
    void shouldReturnInvalidPasswordWhenTooShort()
    {
        LibraryService libraryService = new LibraryService(null, null, null);

        RegisterStatus result = libraryService.register("user", "123");

        assertEquals(RegisterStatus.INVALID_PASSWORD, result);
    }
}
