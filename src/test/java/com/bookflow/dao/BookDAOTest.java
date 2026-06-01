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
        conn = db.connect();
        conn.setAutoCommit(false); // potrzebne do rollbacku
    }

    @AfterEach
    void rollback() throws SQLException{
        conn.rollback(); // cofnięcie po zmianie
        conn.close();
    }

    // ================== SEARCH ==================
    @Test
    void shouldFindBook(){
        List<Book> books = bookDAO.search("Percy");
        assertNotNull(books);
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
        boolean result = bookDAO.isAvailable(1);
        assertFalse(result);
    }

    @Test
    void shouldReturnTrueWhenAvailableCopies(){
        boolean result = bookDAO.isAvailable(2);
        assertTrue(result);
    }

    // ================= DECREASE =================
    @Test
    void shouldDecreaseCopiesWhenAvailable() throws SQLException{
        int bookId = 2;

        boolean before = bookDAO.isAvailable(bookId);
        assertTrue(before);

        boolean result = bookDAO.decreaseCopies(conn, bookId);
        assertTrue(result);

        Statement stmt = conn.createStatement();
        var rs = stmt.executeQuery("SELECT availableCopies FROM BIBLIOTEKA WHERE id = " + bookId);
        assertTrue(rs.next());
        assertEquals(0, rs.getInt(1));
    }

    @Test
    void shouldNotDecreaseCopiesWhenNoCopiesAvailable() throws SQLException{
        int bookId = 1;
        boolean result = bookDAO.decreaseCopies(conn, bookId);
        assertFalse(result);
    }

    // ================= INCREASE =================
    @Test
    void shouldIncreaseCopies() throws SQLException{
        int bookId = 2;
        int before = getAvailable(bookId);

        boolean result = bookDAO.increaseCopies(conn, bookId);
        assertTrue(result);

        int after = getAvailable(bookId);
        assertEquals(before+1, after);
    }

    @Test
    void shouldNotIncreaseAboveTotalCopies() throws SQLException {
        int bookId = 2;

        // ustawiamy na max (3)
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                UPDATE BIBLIOTEKA 
                SET availableCopies = totalCopies 
                WHERE id = 2
            """);
        }

        boolean result = bookDAO.increaseCopies(conn, bookId);

        assertFalse(result);
    }

    // ================== HELPER ==================
    private int getAvailable(int bookId) throws SQLException{
        var stmt = conn.createStatement();
        var rs = stmt.executeQuery("SELECT availableCopies FROM BIBLIOTEKA WHERE id = " + bookId);
        rs.next();
        return  rs.getInt(1);
    }
}
