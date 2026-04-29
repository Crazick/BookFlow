import java.sql.*;

class DatabaseManager
{
    private static final String URL = "jdbc:sqlite:BIBLIOTEKA.db";

    public Connection connect() throws SQLException{
        return DriverManager.getConnection(URL);
    }
}
