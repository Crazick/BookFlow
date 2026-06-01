package com.bookflow.dao;

import com.bookflow.config.DatabaseManager;
import com.bookflow.enums.LoginStatus;
import com.bookflow.enums.RegisterStatus;

import java.sql.*;

/**
 * DAO odpowiedzialne za operacje na użytkownikach w bazie danych.
 * <p>
 * Obsługuje:
 * <ul>
 *     <li>rejestrację użytkowników,</li>
 *     <li>logowanie użytkowników,</li>
 *     <li>pobieranie ID użytkownika.</li>
 * </ul>
 */
public class UserDAO
{
    /** Zarządca połączenia z bazą danych. */
    private DatabaseManager db = new DatabaseManager();

    /**
     * Rejestruje nowego użytkownika w systemie.
     *
     * @param username nazwa użytkownika
     * @param password hasło użytkownika
     * @return status rejestracji
     */
    public RegisterStatus register(String username, String password) {
        String sql = "INSERT INTO USERS(username, password) VALUES(?, ?)";

        try (Connection conn = db.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            stmt.executeUpdate();
            return RegisterStatus.SUCCESS;
        } catch (SQLException e) {

            if (e.getMessage().contains("UNIQUE")) {
                System.out.println("Username already exists");
            } else {
                System.out.println("DB error: " + e.getMessage());
            }

            return RegisterStatus.ERROR;
        }
    }

    /**
     * Rejestruje nowego użytkownika w systemie.
     * Potrzebna do testowania. Umożliwia wykonanie rollback'u.
     *
     * @param conn aktywne połączenie z bazą (transakcja)
     * @param username nazwa użytkownika
     * @param password hasło użytkownika
     * @return status rejestracji
     */
    public RegisterStatus register(Connection conn, String username, String password)
    {
        String sql = "INSERT INTO USERS(username, password) VALUES(?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            stmt.executeUpdate();
            return RegisterStatus.SUCCESS;

        } catch (SQLException e) {
            return RegisterStatus.ERROR;
        }
    }

    /**
     * Loguje użytkownika do systemu.
     *
     * @param username nazwa użytkownika
     * @param password hasło użytkownika
     * @return status logowania
     */
    public LoginStatus login(String username, String password) {
        String sql = "SELECT password FROM USERS WHERE username = ?";

        try (Connection conn = db.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                return LoginStatus.USER_NOT_FOUND;
            }

            String dbPassword = rs.getString("password");

            if (!dbPassword.equals(password)) {
                return LoginStatus.WRONG_PASSWORD;
            }

            return LoginStatus.SUCCESS;

        } catch (SQLException e) {
            e.printStackTrace();
            return LoginStatus.ERROR;
        }
    }

    /**
     * Pobiera ID użytkownika na podstawie nazwy.
     *
     * @param username nazwa użytkownika
     * @return ID użytkownika lub -1 jeśli nie znaleziono
     */
    public int getUserID(String username) {
        String sql = "SELECT id FROM USERS WHERE username = ?";

        try (Connection conn = db.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}