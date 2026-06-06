package com.bookflow.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.bookflow.config.DatabaseManager;
import com.bookflow.model.Book;;

/**
 * DAO odpowiedzialne za operacje administracyjne na książkach:
 * dodawanie, edycja i usuwanie książek z systemu.
 */
public class AdminDAO
{
    /** Zarządca połączenia z bazą danych. */
    private final DatabaseManager db = new DatabaseManager();

    /**
     * Dodaje nową książkę do bazy danych.
     * 
     * @param book obiekt książki
     * @return true jeśli dodanie się powiodło
     */
    public boolean addBook(Book book){
        String sql = """
            INSERT INTO BIBLIOTEKA
            (title, author, genre, totalCopies, availableCopies)
            VALUES (?, ?, ?, ?, ?)
            """;
        try(Connection conn = db.connect();
            PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, book.title());
            stmt.setString(2, book.author());
            stmt.setString(3, book.genre());
            stmt.setInt(4, book.totalCopies());
            stmt.setInt(5, book.availableCopies());

            return stmt.executeUpdate() > 0;
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Aktualizuje dane książki (bez zmiany ID).
     * 
     * @param book obiekt książki z nowymi danymi
     * @return true jeśli aktualizacja się powiodła
     */
    public boolean uptadeBook(Book book){
        String sql = """
                UPDATE BIBLIOTEKA
                SET title = ?,
                    author = ?,
                    genre = ?,
                    totalCopies = ?,
                    availableCopies = ?
                WHERE id = ?
                """;

        try(Connection conn = db.connect();
            PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, book.title());
            stmt.setString(2, book.author());
            stmt.setString(3, book.genre());
            stmt.setInt(4, book.totalCopies());
            stmt.setInt(5, book.availableCopies());
            stmt.setInt(6, book.id());        

            return stmt.executeUpdate() > 0;
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Usuwa książkę z bazy danych.
     * 
     * @param bookId identyfikator książki
     * @return tru jeśli usunięcie się powiodło
     */
    public boolean deleteBook(int bookId){
        String sql = "DELETE FROM BIBLIOTEKA WHERE id = ?";

        try(Connection conn = db.connect();
            PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, bookId);
            return stmt.executeUpdate() > 0;
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Pobiera książkę po ID (pomocznicza do edycji w panelu administracji).
     */
    public Book getById(int id){
        String sql = "SELECT * FROM BIBLIOTEKA WHERE id = ?";

        try(Connection conn = db.connect();
            PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                return new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre"),
                        rs.getInt("totalCopies"),
                        rs.getInt("availableCopies")
                );
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }
}
