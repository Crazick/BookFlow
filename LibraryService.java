import java.util.*;

class LibraryService{
    private List<Book> books = new ArrayList<>();

    public LibraryService(){
        // przykładowe książki do testów
        books.add(new Book(1, "Wiedźmin: Ostatnie życzenie", "Andrzej Sapkowski", "fantasy", 5, 2));
        books.add(new Book(2, "Harry Potter i Kamień Filozoficzny", "J.K. Rowling", "fantasy", 2, 0));
    }

    // wyszukiwanie przez frazę (autor/tytuł)
    public List<Book> search(String phrase){
        List<Book> result = new ArrayList<>();

        for(Book b : books){
            if(b.title().toLowerCase().contains(phrase.toLowerCase())
                    || b.author().toLowerCase().contains(phrase.toLowerCase())){
                result.add(b);
            }
        }
        return result;
    }

    public boolean borrow(int id){
        for(Book b : books){
            if(id == b.id() && b.isAvailable()){
                b.setAvailableCopies(b.availableCopies() - 1);
                return true;
            }
        }
        return false;
    }

    public boolean returnBook(int id){
        for(Book b : books){
            if(id == b.id()){
                b.setAvailableCopies(b.availableCopies() + 1);
                return true;
            }
        }
        return false;
    }
}