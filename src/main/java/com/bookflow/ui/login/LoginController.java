package com.bookflow.ui.login;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.bookflow.service.LibraryService;
import com.bookflow.ui.view.LibraryController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin() {

        String login = loginField.getText();
        String password = passwordField.getText();

        try (
                Socket socket = new Socket("localhost", 5000);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(
                        socket.getOutputStream(), true)
        ) {

            out.println("LOGIN " + login + " " + password);

            String response = in.readLine();

            switch (response) {

                case "LOGIN_SUCCESS":

                    LibraryService service = new LibraryService();
                    int userId = service.getUserID(login);

                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/com/bookflow/ui/view/view.fxml")
                    );

                    Parent root = loader.load();

                    LibraryController controller = loader.getController();
                    controller.setUserId(userId);

                    Stage stage =
                            (Stage) loginField.getScene().getWindow();

                    stage.setScene(new Scene(root));
                    stage.setTitle("BookFlow - Biblioteka");
                    stage.show();

                    break;

                case "USER_NOT_FOUND":
                    messageLabel.setText("Nie istnieje taki użytkownik");
                    break;

                case "WRONG_PASSWORD":
                    messageLabel.setText("Błędne hasło");
                    break;

                default:
                    messageLabel.setText("Błąd logowania");
            }

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Brak połączenia z serwerem");
        }
    }

    @FXML
    private void handleRegister() {

        String login = loginField.getText();
        String password = passwordField.getText();

        try (
                Socket socket = new Socket("localhost", 5000);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(
                        socket.getOutputStream(), true)
        ) {

            out.println("REGISTER " + login + " " + password);

            String response = in.readLine();

            switch (response) {

                case "REGISTER_SUCCESS":
                    messageLabel.setText("Konto utworzone");
                    break;

                case "USER_EXISTS":
                    messageLabel.setText("Taki login już istnieje");
                    break;

                default:
                    messageLabel.setText("Nie udało się utworzyć konta");
            }

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Brak połączenia z serwerem");
        }
    }
}
