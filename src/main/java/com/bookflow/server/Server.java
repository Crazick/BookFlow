package com.bookflow.server;

import com.bookflow.service.LibraryService;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

/**
 * Klasa uruchamiająca serwer bilioteki.
 * <p>
 * Serwer nasłuchuje połączeń klientów na porcie 5000.
 * Dla każdego nowego klienta tworzony jest osobny wątek
 * obsługiwany przez klasę {@code ClientHandler}.
 */
public class Server
{
    /**
     * Główna metoda urchamiająca serwer.
     * @param args argumenty programu (nieużywane)
     */
    public static void main(String[] args)
    {
        try{
            ServerSocket port = new ServerSocket(5000);
            System.out.println("Uruchamiono serwera...");

            LibraryService libraryService = new LibraryService();

            while(true){
                Socket clientSocket = port.accept();
                System.out.println("Nowy klient");

                // nowy wątek
                new Thread(new ClientHandler(clientSocket, libraryService)).start();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}