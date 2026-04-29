package com.bookflow.dao;

import com.bookflow.config.DatabaseManager;
import java.sql.*;

public class BorrowDAO {
    private DatabaseManager db = new DatabaseManager();
    private BookDAO bookDAO = new BookDAO();

    // ===== BORROW BOOK =====
    public boolean borrow(int userId, int bookId) {
        String insertSql = "INSERT INTO BORROWS(user_id, book_id) VALUES(?, ?)";

        try (Connection conn = db.connect()) {

            conn.setAutoCommit(false);

            boolean updated = bookDAO.decreaseCopies(conn, bookId);

            if (!updated) {
                conn.rollback();
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(insertSql))
            {
                stmt.setInt(1, userId);
                stmt.setInt(2, bookId);
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
    public boolean returnBook(int userId, int bookId) {
        String updateBorrow = "UPDATE BORROWS SET return_date = CURRENT_TIMESTAMP " +
                        "WHERE user_id = ? AND book_id = ? AND return_date IS NULL";

        String updateBook = "UPDATE BIBLIOTEKA SET availableCopies = availableCopies + 1 WHERE id = ?";

        try (Connection conn = db.connect()) {

            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(updateBorrow)) {
                stmt1.setInt(1, userId);
                stmt1.setInt(2, bookId);

                int updated = stmt1.executeUpdate();

                if (updated == 0) {
                    conn.rollback();
                    return false;
                }
            }

            bookDAO.increaseCopies(conn, bookId);
            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // === BORROWED? ===
    public boolean hasBorrowed(int userId, int bookId){
        String sql = "SELECT FROM BORROWS WHERE user_id = ? AND book_id = ? AND return_date IS NULL";

        try(Connection conn = db.connect();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);

            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}