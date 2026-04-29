import java.sql.*;

class UserDAO
{
    private DatabaseManager db = new DatabaseManager();

    // === REJESTRACJA ===
    public RegisterStatus register(String username, String password)
    {
        String sql = "INSERT INTO USERS(username, password) VALUES(?, ?)";

        try(Connection conn = db.connect();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            stmt.executeUpdate();
            return RegisterStatus.SUCCESS;
        }
        catch(SQLException e)
        {
            // czy istnieje już taki użytkownik
            if (e.getMessage().contains("UNIQUE")) {
                System.out.println("Username already exists");
            } else {
                System.out.println("DB error: " + e.getMessage());
            }
            return RegisterStatus.ERROR;
        }
    }

    // === LOGOWANIE ===
    public LoginStatus login(String username, String password)
    {
        String sql = "SELECT password FROM USERS WHERE username = ?";

        try (Connection conn = db.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            // 1. user nie istnieje
            if (!rs.next()) {
                return LoginStatus.USER_NOT_FOUND;
            }

            String dbPassword = rs.getString("password");

            // 2. złe hasło
            if (!dbPassword.equals(password)) {
                return LoginStatus.WRONG_PASSWORD;
            }

            // 3. OK
            return LoginStatus.SUCCESS;

        }
        catch (SQLException e) {
            e.printStackTrace();
            return LoginStatus.ERROR;
        }
    }

    public int getUserID(String username){
        String sql = "SELECT id FROM USERS WHERE username = ?";

        try(Connection conn = db.connect();
            PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if(rs.next())  return rs.getInt("id");
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        return -1;
    }
}