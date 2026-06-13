package com.bookflow.ui.login;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import com.bookflow.model.NetworkMessage;
import com.bookflow.service.LibraryService;
import com.bookflow.ui.view.LibraryController;
import com.google.gson.Gson;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Kontroler ekranu logowania.
 * <p>
 * Odpowiada za obsługę procesu:
 * <ul>
 *     <li>logowania użytkowników,</li>
 *     <li>rejestracji nowych kont,</li>
 *     <li>przekierowania użytkownika do odpowiedniego widoku
 *     po poprawnym zalogowaniu.</li>
 * </ul>
 *
 * Kontroler komunikuje się z serwerem za pomocą gniazd sieciowych
 * oraz wyświetla użytkownikowi komunikaty o błędach
 * i wynikach wykonywanych operacji.
 */
public class LoginController {

    /** Pole tekstowe zawierające login użytkownika. */
    @FXML
    private TextField loginField;

    /** Pole tekstowe zawierające hasło użytkownika. */
    @FXML
    private PasswordField passwordField;

    /** Etykieta wyświetlająca komunikaty dla użytkownika. */
    @FXML
    private Label messageLabel;

    /**
     * Obsługuje proces logowania użytkownika.
     * <p>
     * Pobiera login i hasło z formularza,
     * wysyła żądanie logowania do serwera
     * oraz oczekuje na odpowiedź.
     * <p>
     * W przypadku poprawnego logowania:
     * <ul>
     *     <li>administrator zostaje przekierowany
     *     do panelu administratora,</li>
     *     <li>zwykły użytkownik zostaje przekierowany
     *     do widoku biblioteki.</li>
     * </ul>
     *
     * W przypadku błędu wyświetlany jest
     * odpowiedni komunikat.
     */
    @FXML
    private void handleLogin() {

        String login = loginField.getText();
        String password = passwordField.getText();

        try (Socket socket = new Socket("localhost", 5000); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out = new PrintWriter( socket.getOutputStream(), true)) {

            NetworkMessage msg = new NetworkMessage("LOGIN",List.of(login, password));

            String jsonMessage = new Gson().toJson(msg);

            out.println(jsonMessage);

            String response = in.readLine();

            // ===== POPRAWNE LOGOWANIE =====

            if (response != null && response.startsWith("LOGIN_SUCCESS")) {

                String[] parts = response.split(" ");
                String role = "USER";

                if (parts.length >= 2) {
                    role = parts[1];
                }

                Stage stage = (Stage) loginField.getScene().getWindow();

                // ===== ADMIN =====

                if (role.equalsIgnoreCase("ADMIN")) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bookflow/ui/admin/admin.fxml"));

                    Parent root = loader.load();

                    stage.setScene(new Scene(root));
                    stage.setTitle("BookFlow - Panel administratora");

                    stage.show();

                    return;
                }

                // ===== USER =====

                LibraryService service = new LibraryService();

                int userId = service.getUserID(login);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bookflow/ui/view/view.fxml"));

                Parent root = loader.load();

                LibraryController controller = loader.getController();

                controller.setUserId(userId);

                stage.setScene(new Scene(root));
                stage.setTitle( "BookFlow - Biblioteka");

                stage.show();

                return;
            }

            // ===== BŁĘDY =====

            switch (response) {
                case "USER_NOT_FOUND":
                    messageLabel.setText("Nie istnieje taki użytkownik");
                    break;

                case "WRONG_PASSWORD":
                    messageLabel.setText("Błędne hasło");
                    break;

                case "LOGIN_ERROR":
                    messageLabel.setText("Błąd serwera");
                    break;

                default:
                    messageLabel.setText("Błąd logowania. Odpowiedź: " + response);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText( "Brak połączenia z serwerem");
        }
    }

    /**
     * Obsługuje proces rejestracji nowego użytkownika.
     * <p>
     * Pobiera login i hasło z formularza,
     * wysyła żądanie rejestracji do serwera
     * i wyświetla wynik operacji.
     * <p>
     * Informuje użytkownika o:
     * <ul>
     *     <li>poprawnym utworzeniu konta,</li>
     *     <li>istniejącym już loginie,</li>
     *     <li>niepoprawnym haśle,</li>
     *     <li>błędzie połączenia z serwerem.</li>
     * </ul>
     */
    @FXML
    private void handleRegister() {

        String login = loginField.getText();
        String password = passwordField.getText();

        try (Socket socket = new Socket("localhost", 5000); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) 
        {

            NetworkMessage msg = new NetworkMessage("REGISTER", List.of(login, password));

            String jsonMessage = new Gson().toJson(msg);

            out.println(jsonMessage);

            String response = in.readLine();

            switch (response) {
                case "REGISTER_SUCCESS":
                    messageLabel.setText("Konto utworzone");
                    break;

                case "USER_EXISTS":
                    messageLabel.setText("Taki login już istnieje");
                    break;

                case "INVALID_PASSWORD":
                    messageLabel.setText("Hasło musi składać się z min. 4 znaków");
                    break;

                default:
                    messageLabel.setText("Nie udało się utworzyć konta. Odpowiedź: " + response);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Brak połączenia z serwerem");
        }
    }
}
