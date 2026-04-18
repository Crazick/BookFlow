import java.net.*;
import java.io.*;

class ClientHandler implements Runnable{
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    // Konstruktor
    public ClientHandler(Socket socket){
        this.socket = socket;
    }
    private void handleLogin(String[] parts){
        // logowanie
    }
    private void handleSearch(String[] parts){
        // wyszukiwanie
    }
    private void handleBorrow(String[] parts){
        // wypożyczanie
    }
    // nie wiem jakie jeszcze komendy na ten moment

    @Override
    public void run()
    {
        System.out.println("Obsługa klienta w wątku: " + Thread.currentThread().getId());

        try{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String message;
            while((message = in.readLine()) != null)
            {
                System.out.println("Otrzymano: " + message);
                String[] parts = message.split(" ");
                String command = parts[0]; // komenda z bazy danych

                switch(command){
                    case "LOGIN":
                        handleLogin(parts);
                        break;
                    case "SEARCH":
                        handleSearch(parts);
                        break;
                    case "BORROW":
                        handleBorrow(parts);
                        break;
                    default:
                        out.println("UNKNOWN_COMMAND");

                }
            }
        }
        catch (Exception e){
            System.out.println("Klient się rozłączył");
        }
        finally {
            try{
                socket.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}