import java.net.*;
import java.io.*;
import java.util.*;

// kompilacja javac -cp ".;lib/sqlite-jdbc-3.53.0.0.jar" *.java
// uruchamianie java --enable-native-access=ALL-UNNAMED -cp ".;lib/sqlite-jdbc-3.53.0.0.jar" Server

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