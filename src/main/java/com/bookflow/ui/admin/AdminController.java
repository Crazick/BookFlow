package com.bookflow.ui.admin;

import com.bookflow.model.NetworkMessage;
import com.google.gson.Gson;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class AdminController {

    @FXML
    private TextField idField;

    @FXML
    private TextField titleField;

    @FXML
    private TextField authorField;

    @FXML
    private TextField genreField;

    @FXML
    private TextField totalCopiesField;

    @FXML
    private TextField availableCopiesField;

    @FXML
    private Label statusLabel;

    /** Narzędzie do konwersji obiektów na format JSON. */
    private final Gson gson = new Gson();

    @FXML
    private void handleAdd() {
        try (
                Socket socket = new Socket("localhost", 5000);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // Serwer oczekuje: title, author, genre, totalCopies, availableCopies
            NetworkMessage msg = new NetworkMessage("ADD_BOOK", List.of(
                    titleField.getText(),
                    authorField.getText(),
                    genreField.getText(),
                    totalCopiesField.getText(),
                    availableCopiesField.getText()
            ));

            out.println(gson.toJson(msg));
            String response = in.readLine();

            if ("ADD_BOOK_SUCCESS".equals(response)) {
                statusLabel.setText("Książka dodana");
            } else {
                statusLabel.setText("Nie udało się dodać książki");
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Błąd: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        try (
                Socket socket = new Socket("localhost", 5000);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // Serwer oczekuje: id, title, author, genre, totalCopies, availableCopies
            NetworkMessage msg = new NetworkMessage("UPDATE_BOOK", List.of(
                    idField.getText(),
                    titleField.getText(),
                    authorField.getText(),
                    genreField.getText(),
                    totalCopiesField.getText(),
                    availableCopiesField.getText()
            ));

            out.println(gson.toJson(msg));
            String response = in.readLine();

            if ("UPDATE_BOOK_SUCCESS".equals(response)) {
                statusLabel.setText("Zaktualizowano książkę");
            } else {
                statusLabel.setText("Nie udało się zaktualizować");
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Błąd: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        try (
                Socket socket = new Socket("localhost", 5000);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            NetworkMessage msg = new NetworkMessage("DELETE_BOOK", List.of(
                    idField.getText()
            ));

            out.println(gson.toJson(msg));
            String response = in.readLine();

            if ("DELETE_BOOK_SUCCESS".equals(response)) {
                statusLabel.setText("Usunięto książkę");
            } else {
                statusLabel.setText("Nie znaleziono książki");
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Błąd: " + e.getMessage());
        }
    }

    @FXML
    private void handleLoad() {
        try (
                Socket socket = new Socket("localhost", 5000);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            NetworkMessage msg = new NetworkMessage("GET_BOOK", List.of(idField.getText()));
            out.println(gson.toJson(msg));

            String response = in.readLine();

            // Zakładamy, że jeśli się uda, serwer zwróci dane książki rozdzielone średnikami
            if (response != null && response.startsWith("GET_BOOK_SUCCESS")) {
                String[] parts = response.split(";");
                if (parts.length >= 6) {
                    titleField.setText(parts[1]);
                    authorField.setText(parts[2]);
                    genreField.setText(parts[3]);
                    totalCopiesField.setText(parts[4]);
                    availableCopiesField.setText(parts[5]);
                    statusLabel.setText("Wczytano książkę");
                }
            } else {
                statusLabel.setText("Nie znaleziono książki");
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Błąd połączenia / Błędne ID");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bookflow/ui/login/login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) statusLabel.getScene().getWindow();

            stage.setScene(scene);
            stage.setTitle("BookFlow - Logowanie");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Nie udało się wylogować");
        }
    }
}