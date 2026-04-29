import java.sql.*;

class BorrowDAO {
    private DatabaseManager db = new DatabaseManager();

    // ===== BORROW BOOK =====
    public boolean borrow(int userId, int bookId) {

        String insertBorrow =
                "INSERT INTO BORROWS(user_id, book_id) VALUES(?, ?)";

        String updateBook =
                "UPDATE BIBLIOTEKA SET availableCopies = availableCopies - 1 " +
                        "WHERE id = ? AND availableCopies > 0";

        try (Connection conn = db.connect()) {

            conn.setAutoCommit(false); // 🔥 TRANSAKCJA

            try (PreparedStatement stmt1 = conn.prepareStatement(updateBook)) {
                stmt1.setInt(1, bookId);

                int updated = stmt1.executeUpdate();

                if (updated == 0) {
                    conn.rollback();
                    return false;
                }
            }

            try (PreparedStatement stmt2 = conn.prepareStatement(insertBorrow)) {
                stmt2.setInt(1, userId);
                stmt2.setInt(2, bookId);

                stmt2.executeUpdate();
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

        String updateBorrow =
                "UPDATE BORROWS SET return_date = CURRENT_TIMESTAMP " +
                        "WHERE user_id = ? AND book_id = ? AND return_date IS NULL";

        String updateBook =
                "UPDATE BIBLIOTEKA SET availableCopies = availableCopies + 1 WHERE id = ?";

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

            try (PreparedStatement stmt2 = conn.prepareStatement(updateBook)) {
                stmt2.setInt(1, bookId);
                stmt2.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}