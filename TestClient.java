import java.net.*;
import java.io.*;

public class TestClient {
    private PrintWriter out;
    private BufferedReader in;

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 5000);
        System.out.println("Połączono z serwerem!");

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // wysyłanie
        out.println("Hi Server :3");

        // odbieranie
        String response = in.readLine();
        System.out.println("Serwer: " + response);
    }
}