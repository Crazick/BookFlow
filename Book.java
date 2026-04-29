class Book
{
    private int id;
    private String title;
    private String author;
    private String genre;
    private int totalCopies;
    private int availableCopies;

    public Book(int id, String title, String author, String genre, int totalCopies, int availableCopies){
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    // getters
    public String title() {return title;}
    public int id() {return id;}
    public String author() {return author;}
    public String genre() {return genre;}
    public int availableCopies() {return availableCopies;}
    public int totalCopies() {return totalCopies;}

    // setter
    public void setAvailableCopies(int i){
        if(i < totalCopies)
        {
            availableCopies = i;
        }
    }

    // logika
    public boolean isAvailable(){
        return availableCopies > 0;
    }

    public void borrow(){
        if(availableCopies > 0){
            availableCopies--;
            System.out.println("Book borrowed.");
        }
        System.out.println("Book can't be borrowed.");
    }

    public void returnBook(){
        if(availableCopies < totalCopies){
            availableCopies++;
            System.out.println("Book returned.");
        }
    }

    @Override
    public String toString(){
        return id + " | " + title + " | " + author + " | dostępne: " + availableCopies;
    }
}