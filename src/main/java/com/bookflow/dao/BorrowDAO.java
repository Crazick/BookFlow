package com.bookflow.dao;

import com.bookflow.config.DatabaseManager;
import com.bookflow.model.BorrowedBook;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {
    private DatabaseManager db = new DatabaseManager();
    private BookDAO bookDAO = new BookDAO();

    // ===== BORROW BOOK =====
    public boolean borrow(int userId, int bookId) {
        String sql = "INSERT INTO BORROWS(user_id, book_id, borrow_date, due_date, fine) VALUES(?, ?, ?, ?, 0)";

        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusMonths(1);

        try (Connection conn = db.connect()) {

            conn.setAutoCommit(false);

            boolean decreased = bookDAO.decreaseCopies(conn, bookId);

            if (!decreased) {
                conn.rollback();
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql))
            {
                stmt.setInt(1, userId);
                stmt.setInt(2, bookId);
                stmt.setString(3, today.toString());
                stmt.setString(4, dueDate.toString());
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== RETURN BOOK =====
    public double returnBook(int userId, int bookId) {
        String findBorrow = "SELECT id, due_date FROM BORROWS "+
                "WHERE user_id = ? AND book_id = ? AND return_date IS NULL LIMIT 1";
        String updateBorrow = "UPDATE BORROWS SET return_date = ?, fine = ? WHERE id = ?";

        LocalDate today = LocalDate.now();

        try (Connection conn = db.connect()) {

            conn.setAutoCommit(false);

            int borrowId = -1;
            LocalDate dueDate = null;

            try (PreparedStatement stmt = conn.prepareStatement(findBorrow)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, bookId);

                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    conn.rollback();
                    return -1; // brak wypożyczenia
                }
                borrowId = rs.getInt("id");
                dueDate = LocalDate.parse(rs.getString("due_date"));
            }

            double fine = 0;
            if(today.isAfter(dueDate)){
                long daysLate = ChronoUnit.DAYS.between(dueDate, today);
                fine = daysLate * 1.0;
            }

            try(PreparedStatement stmt = conn.prepareStatement(updateBorrow)){
                stmt.setString(1, today.toString());
                stmt.setDouble(2,fine);
                stmt.setInt(3, userId);
                stmt.executeUpdate();
            }

            bookDAO.increaseCopies(conn, bookId);
            conn.commit();
            return fine;

        } catch (Exception e) {
            e.printStackTrace();
            return -2; // błąd
        }
    }

    // === BORROWED LIST ===
    public List<BorrowedBook> getBorrowedBooks(int userId){
        List<BorrowedBook> result = new ArrayList<>();
        String sql = "SELECT b.id, b.title, b.author, br.borrow_date, br.due_date " +
                "FROM BORROWS br JOIN BIBLIOTEKA b ON br.book_id = b.id " +
                "WHERE br.user_id = ? AND br.return_date IS NULL ORDER BY br.borrow_date";

        try(Connection conn = db.connect();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                BorrowedBook book = new BorrowedBook(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        "", 0, 0,
                        rs.getString("borrow_date"),
                        rs.getString("due_date"),
                        null, 0
                );
                result.add(book);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    // === HISTORY ===
    public List<String> getHistory(int userId) {
        List<String> result = new ArrayList<>();

        String sql = "SELECT b.title, br.borrow_date, br.return_date, br.fine " +
                "FROM BORROWS br JOIN BIBLIOTEKA b ON br.book_id = b.id " +
                "WHERE br.user_id = ? ORDER BY br.borrow_date DESC";

        try (Connection conn = db.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String row =
                        rs.getString("title") + " | " +
                                rs.getString("borrow_date") + " | " +
                                rs.getString("return_date") + " | kara: " +
                                rs.getDouble("fine") + " zł";
                result.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}