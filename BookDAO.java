import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

// dostęp do bazy danych
class BookDAO
{
    private DatabaseManager db = new DatabaseManager();

    //helper mapowania
    private Book mapBook(ResultSet rs) throws SQLException{
        return new Book(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("genre"),
                rs.getInt("totalCopies"),
                rs.getInt("availableCopies")
        );
    }

    public List<Book> search(String phrase)
    {
        List<Book> result = new ArrayList<>();
        String sql = "SELECT id, title, author, genre, totalCopies, availableCopies FROM BIBLIOTEKA WHERE LOWER(title) LIKE ? OR LOWER(author) LIKE ?";

        try(Connection conn = db.connect();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            String query = "%" + phrase + "%";
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

    public boolean borrow(int id){
        String sql = "UPDATE BIBLIOTEKA "
                + "SET availableCopies = availableCopies - 1 "
                + "WHERE id = ? AND availableCopies > 0";
        try(Connection conn = db.connect();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1,id);
            return stmt.executeUpdate() > 0;

        }
        catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean returnBook(int id){
        String sql = "UPDATE BIBLIOTEKA "
                + "SET availableCopies = availableCopies + 1 "
                + "WHERE id = ? AND availableCopies < totalCopies";
        try(Connection conn = db.connect();
            PreparedStatement stmt = conn.prepareStatement(sql)){


            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
        catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}
