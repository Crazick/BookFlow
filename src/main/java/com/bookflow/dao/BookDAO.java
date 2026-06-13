package com.bookflow.dao;

import com.bookflow.config.DatabaseManager;
import com.bookflow.model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

/**
 * DAO odpowiedzialne za operacje na książkach w bazie danych.
 * <p>
 * Obsługuje:
 * <ul>
 *     <li>wyszukiwanie książek,</li>
 *     <li>sprawdzanie dostępności,</li>
 *     <li>aktualizację liczby egzemplarzy.</li>
 * </ul>
 */
public class BookDAO
{
    /** Zarządca połączenia z bazą danych. */
    private DatabaseManager db = new DatabaseManager();

    /**
     * Mapuje rekord z bazy danych na obiekt Book.
     * @param rs wynik zapytania SQL
     * @return obiekt Book
     * @throws SQLException gdy wystąpi błąd odczytu danych
     */
    public static Book mapBook(ResultSet rs) throws SQLException{
        return new Book(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("genre"),
                rs.getInt("totalCopies"),
                rs.getInt("availableCopies")
        );
    }

    // === SEARCH ===
    /**
     * Wyszukuje książki po tytule lub autorze.
     * @param phrase fraza wyszukiwania
     * @return lista znalezionych książek
     */
    public List<Book> search(String phrase) {
        List<Book> result = new ArrayList<>();
        String sql = "SELECT id, title, author, genre, totalCopies, availableCopies FROM BIBLIOTEKA WHERE LOWER(title) LIKE ? OR LOWER(author) LIKE ?";

        try(Connection conn = db.connect();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            String query = "%" + phrase.toLowerCase() + "%";
            stmt.setString(1, query);
            stmt.setString(2, query);

            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                result.add(mapBook(rs));
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    // === AVAILABILITY ===
    /**
     * Sprawdza czy książka jest dostępna.
     * @param bookId ID książki
     * @return true jeśli dostępna, false jeśli nie
     */
    public boolean isAvailable(int bookId){
        String sql = "SELECT * FROM BIBLIOTEKA WHERE id = ?";

        try(Connection conn = db.connect();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getInt("availableCopies") > 0;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    // === DECREASE ===
    /**
     * Zmniejsza liczbę dostępnych egzemplarzy książki.
     * @param conn aktywne połączenie z bazą (transakcja)
     * @param bookId ID książki
     * @return true jeśli operacja się powiodła
     */
    public boolean decreaseCopies(Connection conn, int bookId){
        String sql = "UPDATE BIBLIOTEKA SET availableCopies = availableCopies - 1 WHERE id = ? AND availableCopies > 0";

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);
            return stmt.executeUpdate() > 0;
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    // === INCREASE ===
    /**
     * Zwiększa liczbę dostępnych egzemplarzy książki.
     * @param conn aktywne połączenie z bazą (transakcja)
     * @param bookId ID książki
     * @return true jeśli operacja się powiodła
     */
    public boolean increaseCopies(Connection conn, int bookId){
        String sql = "UPDATE BIBLIOTEKA SET availableCopies = availableCopies + 1 WHERE id = ? AND totalCopies > availableCopies";

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);
            return stmt.executeUpdate() > 0;
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Pobiera wszystkie książki zarejestrowane w bazie danych biblioteki.
     * @return lista wszystkich obiektów {@link Book} znajdujących się w bazie danych
     */
    public List<Book> getAllBooks(){
        List<Book> result = new ArrayList<>();
        String sql = "SELECT id, title, author, genre, totalCopies, availableCopies FROM BIBLIOTEKA";

        try(Connection conn = db.connect();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery())
        {
            while(rs.next()){
                result.add(mapBook(rs));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return result;
    }
}
