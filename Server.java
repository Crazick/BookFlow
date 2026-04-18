import java.net.*;
import java.io.*;

public class Server
{
    public static void main(String[] args)
    {
        try{
            ServerSocket port = new ServerSocket(5000);
            System.out.println("Uruchamianie serwera...");

            while(true){
                Socket clientSocket = port.accept();
                System.out.println("Nowy klient");

                // nowy wątek
                new Thread(new ClientHandler(clientSocket)).start();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}