import java.util.*;

class LibraryService{
    private BookDAO bookDAO = new BookDAO();
    private UserDAO userDAO = new UserDAO();
    private BorrowDAO borrowDAO = new BorrowDAO();

    // === BOOKS ===
    // wyszukiwanie przez frazę (autor/tytuł)
    public List<Book> searchBook(String phrase){
        return bookDAO.search(phrase);
    }
    // wypożyczanie książek
    public boolean borrowBook(int userId, int bookId) {
        return borrowDAO.borrow(userId, bookId);
    }
    // zwrot książek
    public boolean returnBook(int userId, int bookId) {
        return borrowDAO.returnBook(userId, bookId);
    }

    // === USERS ===
    public LoginStatus login(String username, String password){
        return userDAO.login(username, password);
    }
    public RegisterStatus register(String username, String password){
        return userDAO.register(username, password);
    }
    public int getUserID(String username){
        return userDAO.getUserID(username);
    }

}