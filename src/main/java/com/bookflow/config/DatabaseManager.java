package com.bookflow.config;

import java.sql.*;

/**
 * Klasa odpowiedzialna za zarządzanie połączeniem z bazą danych SQLite.
 * Umożliwia nawiązanie połączenia z bazą biblioteki.
 */
public class DatabaseManager
{
    /**
     * Adres bazy danych SQLite używanej w aplikacji.
     */
    private static final String URL = "jdbc:sqlite:BIBLIOTEKA.db";


    /**
     * Tworzy obiekt klasy DatabaseManager.
     */
    public DatabaseManager() {}

    /**
     * Nawiązuje połączenie z bazą danych.
     * @return obiekt Connection reprezentujący aktywne połączenie z bazą
     * @throws SQLException jeśli wystąpi błąd podczas łączenia z bazą danych
     */
    public Connection connect() throws SQLException{
        return DriverManager.getConnection(URL);
    }
}