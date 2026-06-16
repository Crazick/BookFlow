package com.bookflow.dao;

import com.bookflow.config.DatabaseManager;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import static org.junit.jupiter.api.Assertions.*;

public class BorrowDAOTest {
    private BorrowDAO borrowDAO;
    private DatabaseManager db;

    @BeforeEach
    void setup() throws SQLException {
        borrowDAO = new BorrowDAO();
        db = new DatabaseManager();

        try (Connection conn = db.connect();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("DELETE FROM BORROWS WHERE user_id = 9991");
            stmt.executeUpdate("DELETE FROM BIBLIOTEKA WHERE id IN (9991, 9992)");
            stmt.executeUpdate("DELETE FROM USERS WHERE id = 9991");

            stmt.executeUpdate("INSERT INTO USERS (id, username, password) VALUES (9991, 'borrow_user', 'hash')");
            stmt.executeUpdate("INSERT INTO BIBLIOTEKA (id, title, author, genre, totalCopies, availableCopies) VALUES (9991, 'Dostępna', 'A', 'IT', 5, 5)");
            stmt.executeUpdate("INSERT INTO BIBLIOTEKA (id, title, author, genre, totalCopies, availableCopies) VALUES (9992, 'Niedostępna', 'A', 'IT', 5, 0)");
        }
    }

    @AfterEach
    void teardown() throws SQLException {
        try (Connection conn = db.connect();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM BORROWS WHERE user_id = 9991");
            stmt.executeUpdate("DELETE FROM BIBLIOTEKA WHERE id IN (9991, 9992)");
            stmt.executeUpdate("DELETE FROM USERS WHERE id = 9991");
        }
    }

    @Test
    void shouldBorrowBook() {
        boolean result = borrowDAO.borrow(9991, 9991);
        assertTrue(result);
    }

    @Test
    void shouldNotBorrowUnavailableBook() {
        boolean result = borrowDAO.borrow(9991, 9992);
        assertFalse(result);
    }

    @Test
    void shouldReturnBorrowedBook() {
        // Najpierw pomyślnie wypożyczamy
        borrowDAO.borrow(9991, 9991);

        double fine = borrowDAO.returnBook(9991, 9991);
        assertTrue(fine >= 0.0);
    }

    @Test
    void shouldGetBorrowedBooks() {
        borrowDAO.borrow(9991, 9991);

        boolean isEmpty = borrowDAO.getBorrowedBooks(9991).isEmpty();
        assertFalse(isEmpty);
    }
}