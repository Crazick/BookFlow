package com.bookflow.server;

import com.bookflow.service.LibraryService;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class Server
{
    public static void main(String[] args)
    {
        try{
            ServerSocket port = new ServerSocket(5000);
            System.out.println("Uruchamianie serwera...");

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