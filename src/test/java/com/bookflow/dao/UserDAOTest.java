package com.bookflow.dao;

import com.bookflow.enums.LoginStatus;
import com.bookflow.enums.RegisterStatus;
import com.bookflow.config.DatabaseManager;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest
{
    private UserDAO userDAO;
    private DatabaseManager db;

    @BeforeEach
    void setup() {
        userDAO = new UserDAO();
        db = new DatabaseManager();
    }

    // ================== LOGIN ===================
    @Test
    void shouldLoginSuccesfully(){
        LoginStatus result = userDAO.login("test", "test");
        assertEquals(LoginStatus.SUCCESS, result);
    }

    @Test
    void shouldReturnWrongPassword() {
        LoginStatus result = userDAO.login("test", "zlehaslo");
        assertEquals(LoginStatus.WRONG_PASSWORD, result);
    }

    @Test
    void shouldReturnUserNotFound() {
        LoginStatus result = userDAO.login("nieistnieje", "1234");
        assertEquals(LoginStatus.USER_NOT_FOUND, result);
    }

    // ================= REGISTER =================

    @Test
    void shouldNotRegisterExistingUser() {
        RegisterStatus result = userDAO.register("test", "abc");
        assertEquals(RegisterStatus.ERROR, result);
    }

    @Test
    void shouldRegisterUser() throws SQLException {
        try(Connection conn = db.connect()) {
            conn.setAutoCommit(false);

            RegisterStatus result = userDAO.register(conn, "test123", "1234");

            assertEquals(RegisterStatus.SUCCESS, result);
            conn.rollback();
        }
    }

    // ================= GET USER ID =================

    @Test
    void shouldReturnUserId() {
        int id = userDAO.getUserID("test");
        assertEquals(0, id);
    }

    @Test
    void shouldReturnMinusOneWhenUserNotFound() {
        int id = userDAO.getUserID("nie_ma_takiego_uzytkownika");
        assertEquals(-1, id);
    }
}
