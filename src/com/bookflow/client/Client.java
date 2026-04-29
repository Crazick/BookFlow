package com.bookflow.client;

import java.io.*;
import java.util.Scanner;
import java.net.Socket;

public class Client
{
    public static void main(String[] args)
    {
        try(Socket socket = new Socket("localhost", 5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in))
        {

            System.out.println("Połączono z serwerem.");
            boolean loggedIn = false;

            // === LOGOWANIE ===
            while(!loggedIn)
            {
                System.out.println("\n=== Menu ===");
                System.out.println("1. Logowanie");
                System.out.println("2. Rejestracja");
                System.out.println("0. Wyjście");
                System.out.print("> ");

                String choice = scanner.nextLine();

                switch(choice){
                    case "1":
                        System.out.println("Login: ");
                        String login = scanner.nextLine();
                        System.out.println("Hasło: ");
                        String password = scanner.nextLine();

                        out.println("LOGIN " + login + " " + password);
                        String loginResponse = in.readLine();
                        switch (loginResponse) {
                            case "LOGIN_SUCCESS":
                                System.out.println("Zalogowano pomyślnie.");
                                loggedIn = true;
                                break;
                            case "USER_NOT_FOUND":
                                System.out.println("Nie istnieje taki użytkownik.");
                                break;
                            case "WRONG_PASSWORD":
                                System.out.println("Błędne hasło.");
                                break;
                            default:
                                System.out.println("Błąd logowania.");
                        }
                        break;
                    case "2":
                        System.out.println("Nowy login: ");
                        String newLogin = scanner.nextLine();
                        System.out.println("Nowe hasło: ");
                        String newPassword = scanner.nextLine();

                        out.println("REGISTER " + newLogin + " " + newPassword);
                        String regResponse = in.readLine();
                        switch (regResponse) {
                            case "REGISTER_SUCCESS":
                                System.out.println("Konto utworzone.");
                                break;
                            case "USER_EXISTS":
                                System.out.println("Taki login już istnieje.");
                                break;
                            default:
                                System.out.println("Nie udało się utworzyć konta.");
                        }
                        break;
                    case "0":
                        System.out.println("Zamknięto klienta.");
                        return;
                    default:
                        System.out.println("Niepoprawny wybór");
                }


            }

            // === ZALOGOWANO ===
            while(loggedIn) {
                System.out.println("\n=== PANEL BIBLIOTEKI ===");
                System.out.println("1. Szukaj książki");
                System.out.println("2. Wypożycz książkę");
                System.out.println("3. Oddaj książkę");
                System.out.println("4. Wyloguj");
                System.out.println("0. Zamknij");

                System.out.print("Wybór: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    // === SEARCH ===
                    case "1":
                        System.out.print("Fraza: ");
                        String phrase = scanner.nextLine();

                        out.println("SEARCH " + phrase);

                        String line;
                        while (!(line = in.readLine()).equals("END")) {
                            if (!line.equals("BEGIN")) {
                                System.out.println(line);
                            }
                        }
                        break;
                    // === BORROW ===
                    case "2":
                        System.out.print("Podaj ID książki: ");
                        String borrowId = scanner.nextLine();

                        out.println("BORROW " + borrowId);

                        System.out.println(in.readLine());
                        break;
                    // === RETURN ===
                    case "3":
                        System.out.print("Podaj ID książki: ");
                        String returnId = scanner.nextLine();

                        out.println("RETURN " + returnId);

                        System.out.println(in.readLine());
                        break;
                    // === LOGOUT ===
                    case "4":
                        out.println("LOGOUT");

                        String logoutResponse = in.readLine();

                        if (logoutResponse.equals("LOGOUT_SUCCESS")) {
                            loggedIn = false;
                            System.out.println("Wylogowano.");
                        }
                        break;
                    case "0":
                        System.out.println("Zamknięto klienta.");
                        return;
                    default:
                        System.out.println("Niepoprawny wybór.");
                }
            }

        }
        catch(Exception e){
            System.out.println("Błąd klienta: " + e.getMessage());
        }
    }
}