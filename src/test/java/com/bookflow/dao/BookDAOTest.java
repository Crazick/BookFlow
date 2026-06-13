package com.bookflow.dao;

import com.bookflow.config.DatabaseManager;
import com.bookflow.model.Book;

import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class BookDAOTest
{
    private BookDAO bookDAO;
    private DatabaseManager db;
    private Connection conn;

    @BeforeEach
    void setup() throws SQLException
    {
        bookDAO = new BookDAO();
        db = new DatabaseManager();

        try (Connection setupConn = db.connect();
             Statement stmt = setupConn.createStatement()) {

            stmt.executeUpdate("DELETE FROM BIBLIOTEKA WHERE id IN (9991, 9992)");

            // Książka 9992: dostępna (szukamy też po frazie "Percy" dla testu wyszukiwania)
            stmt.executeUpdate("INSERT INTO BIBLIOTEKA (id, title, author, genre, totalCopies, availableCopies) " +
                    "VALUES (9992, 'Percy Jackson', 'Autor Testowy', 'IT', 5, 1)");

            // Książka 9991: niedostępna
            stmt.executeUpdate("INSERT INTO BIBLIOTEKA (id, title, author, genre, totalCopies, availableCopies) " +
                    "VALUES (9991, 'Atrapa Niedostępna', 'Autor Testowy', 'IT', 5, 0)");
        }

        conn = db.connect();
        conn.setAutoCommit(false); // potrzebne do rollbacku
    }

    @AfterEach
    void teardown() throws SQLException {
        conn.rollback();
        conn.close();
        try (Connection cleanupConn = db.connect();
             Statement stmt = cleanupConn.createStatement()) {
            stmt.executeUpdate("DELETE FROM BIBLIOTEKA WHERE id IN (9991, 9992)");
        }
    }

    // ================== SEARCH ==================
    @Test
    void shouldFindBook(){
        List<Book> books = bookDAO.search("Percy");
        assertNotNull(books);
        assertFalse(books.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenBookNotFound(){
        List<Book> books = bookDAO.search("xxxxxx");
        assertNotNull(books);
        assertTrue(books.isEmpty());
    }

    // =============== AVAILABILITY ===============
    @Test
    void shouldReturnFalseWhenNotAvailableCopies(){
        boolean result = bookDAO.isAvailable(9991);
        assertFalse(result);
    }

    @Test
    void shouldReturnTrueWhenAvailableCopies(){
        boolean result = bookDAO.isAvailable(9992);
        assertTrue(result);
    }

    // ================= DECREASE =================
    @Test
    void shouldDecreaseCopiesWhenAvailable() throws SQLException{
        int bookId = 9992;
        boolean before = bookDAO.isAvailable(bookId);
        assertTrue(before);

        boolean result = bookDAO.decreaseCopies(conn, bookId);
        assertTrue(result);

        Statement stmt = conn.createStatement();
        var rs = stmt.executeQuery("SELECT availableCopies FROM BIBLIOTEKA WHERE id = " + bookId);
        assertTrue(rs.next());
        assertEquals(0, rs.getInt(1)); // 1 początkowe - 1 zabrane = 0
    }

    @Test
    void shouldNotDecreaseCopiesWhenNoCopiesAvailable() throws SQLException{
        int bookId = 9991;
        boolean result = bookDAO.decreaseCopies(conn, bookId);
        assertFalse(result);
    }

    // ================= INCREASE =================
    @Test
    void shouldIncreaseCopies() throws SQLException{
        int bookId = 9992;
        int before = getAvailable(bookId);

        boolean result = bookDAO.increaseCopies(conn, bookId);
        assertTrue(result);

        int after = getAvailable(bookId);
        assertEquals(before+1, after);
    }

    @Test
    void shouldNotIncreaseAboveTotalCopies() throws SQLException {
        int bookId = 9992;
        // ustawiamy na max (5)
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("UPDATE BIBLIOTEKA SET availableCopies = totalCopies WHERE id = " + bookId);
        }

        boolean result = bookDAO.increaseCopies(conn, bookId);
        assertFalse(result);
    }

    // ================== HELPER ==================
    private int getAvailable(int bookId) throws SQLException{
        var stmt = conn.createStatement();
        var rs = stmt.executeQuery("SELECT availableCopies FROM BIBLIOTEKA WHERE id = " + bookId);
        rs.next();
        return rs.getInt(1);
    }
}