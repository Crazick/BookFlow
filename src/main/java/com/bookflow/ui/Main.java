package com.bookflow.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Główna klasa uruchamiająca aplikację BookFlow.
 * <p>
 * Odpowiada za:
 * <ul>
 *     <li>uruchomienie aplikacji JavaFX,</li>
 *     <li>załadowanie początkowego widoku logowania,</li>
 *     <li>utworzenie głównego okna aplikacji,</li>
 *     <li>wyświetlenie interfejsu użytkownika.</li>
 * </ul>
 *
 * Punkt wejścia do aplikacji stanowi metoda main(),
 * natomiast konfiguracja głównego okna wykonywana jest
 * w metodzie start().
 */
public class Main extends Application {

    /**
     * Inicjalizuje i uruchamia główne okno aplikacji.
     * <p>
     * Ładuje widok logowania z pliku login.fxml,
     * tworzy scenę oraz wyświetla główne okno programu.
     *
     * @param stage główne okno aplikacji JavaFX
     * @throws Exception jeśli wystąpi błąd podczas
     *                   ładowania widoku
     */
    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bookflow/ui/login/login.fxml"));

        Scene scene = new Scene(loader.load());

        stage.setTitle("BookFlow");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Punkt wejścia aplikacji.
     * <p>
     * Uruchamia środowisko JavaFX i przekazuje
     * sterowanie do metody start().
     *
     * @param args argumenty przekazane przy uruchomieniu programu
     */
    public static void main(String[] args) {
        launch(args);
    }
}
