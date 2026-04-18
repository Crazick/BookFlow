import java.net.*;
import java.io.*;
import java.util.List;

class ClientHandler implements Runnable{
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private LibraryService libraryService;

    // Konstruktor
    public ClientHandler(Socket socket, LibraryService libraryService) {
        this.socket = socket;
        this.libraryService = libraryService;
    }
    private void handleLogin(String[] parts){
        // logowanie
    }

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
                        String phrase = parts[1];
                        List<Book> result = libraryService.search(phrase);

                        for (Book b : result) {
                            out.println(b.toString());
                        }
                        break;
                    case "BORROW":
                        if (parts.length < 2) {
                            out.println("ERROR: Missing book ID");
                            break;
                        }

                        try {
                            int bookId = Integer.parseInt(parts[1]);

                            boolean success = libraryService.borrow(bookId);

                            if (success) {
                                out.println("BORROW_SUCCESS");
                            } else {
                                out.println("BORROW_FAILED");
                            }

                        } catch (NumberFormatException e) {
                            out.println("ERROR: Invalid ID");
                        }
                        break;
                    case "RETURN":
                        if (parts.length < 2) {
                            out.println("ERROR: Missing book ID");
                            break;
                        }

                        try {
                            int bookId = Integer.parseInt(parts[1]);

                            boolean success = libraryService.returnBook(bookId);

                            if (success) {
                                out.println("RETURN_SUCCESS");
                            } else {
                                out.println("RETURN_FAILED");
                            }

                        } catch (NumberFormatException e) {
                            out.println("ERROR: Invalid ID");
                        }
                        break;
                    default:
                        out.println("UNKNOWN_COMMAND");
                        break;
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