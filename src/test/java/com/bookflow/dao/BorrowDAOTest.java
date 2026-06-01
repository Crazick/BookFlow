package com.bookflow.dao;

import com.bookflow.config.DatabaseManager;
import com.bookflow.model.BorrowedBook;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BorrowDAOTest {

    private BorrowDAO borrowDAO;
    private DatabaseManager db;
    private Connection conn;

    @BeforeAll
    void setup() throws SQLException {
        borrowDAO = new BorrowDAO();
        db = new DatabaseManager();
        conn = db.connect();
        conn.setAutoCommit(false);
    }

    @AfterEach
    void rollback() throws SQLException {
        conn.rollback();
    }

    @AfterAll
    void close() throws SQLException {
        conn.close();
    }

    @Test
    void shouldBorrowBook() {
        boolean result = borrowDAO.borrow(conn, 1, 2);
        assertTrue(result);
    }

    @Test
    void shouldNotBorrowUnavailableBook() {
        boolean result = borrowDAO.borrow(conn, 0, 1);
        assertFalse(result);
    }

    @Test
    void shouldReturnMinusOneWhenBookNotBorrowed() {
        double result = borrowDAO.returnBook(conn, 999, 999);
        assertEquals(-1, result);
    }

    @Test
    void shouldReturnBorrowedBook() {
        boolean borrowed = borrowDAO.borrow(conn, 1, 2);
        assertTrue(borrowed);

        double fine = borrowDAO.returnBook(conn, 1, 2);
        assertEquals(0.0, fine);
    }

    @Test
    void shouldGetBorrowedBooks() {
        borrowDAO.borrow(conn, 0, 0);

        List<BorrowedBook> books = borrowDAO.getBorrowedBooks(1);

        assertNotNull(books);
        assertFalse(books.isEmpty());
    }

    @Test
    void shouldGetHistory() {
        borrowDAO.borrow(conn, 0, 0);
        List<String> history = borrowDAO.getHistory(0);
        assertNotNull(history);
    }
}