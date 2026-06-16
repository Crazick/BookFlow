package com.bookflow.dao;

import com.bookflow.config.DatabaseManager;
import com.bookflow.enums.LoginStatus;
import com.bookflow.enums.RegisterStatus;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
    private UserDAO userDAO;
    private DatabaseManager db;

    @BeforeEach
    void setup() throws SQLException {
        userDAO = new UserDAO();
        db = new DatabaseManager();

        try (Connection conn = db.connect();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM USERS WHERE username LIKE 'testuser_%'");
        }

        userDAO.register("testuser_existing", "haslo1234");
    }

    @AfterEach
    void teardown() throws SQLException {
        // Sprzątamy testowe konta z bazy
        try (Connection conn = db.connect();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM USERS WHERE username LIKE 'testuser_%'");
        }
    }

    @Test
    void shouldLoginSuccesfully() {
        assertEquals(LoginStatus.SUCCESS, userDAO.login("testuser_existing", "haslo1234"));
    }

    @Test
    void shouldReturnWrongPassword() {
        assertEquals(LoginStatus.WRONG_PASSWORD, userDAO.login("testuser_existing", "zlehaslo"));
    }

    @Test
    void shouldNotRegisterExistingUser() {
        assertEquals(RegisterStatus.ERROR, userDAO.register("testuser_existing", "nowehaslo"));
    }

    @Test
    void shouldReturnUserId() {
        int id = userDAO.getUserID("testuser_existing");
        assertTrue(id > 0);
    }
}