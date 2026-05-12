package com.bookflow.model;

public class BorrowedBook extends Book{
    private String borrowDate;
    private String dueDate;
    private String returnDate;
    private double fine;

    public BorrowedBook(int id, String title, String author, String genre, int totalCopies, int availableCopies,
                        String borrowDate, String dueDate, String returnDate, double fine) {
        super(id, title, author, genre, totalCopies, availableCopies);
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fine = fine;
    }

    public BorrowedBook() {
        super(0, "", "", "", 0, 0);
    }

    // getters
    public String borrowDate() { return borrowDate; }
    public String dueDate() { return dueDate; }
    public String returnDate() { return returnDate; }
    public double fine() { return fine; }
    // setters
    public void setBorrowDate(String borrowDate) { this.borrowDate = borrowDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }
    public void setFine(double fine) { this.fine = fine; }

    @Override
    public String toString() {
        return id() + " | " + title() + " | " + author() + " | Wypożyczono: " + borrowDate + " | Termin: " + dueDate +
                (returnDate != null ? " | Zwrócono: " + returnDate : "") +
                (fine > 0 ? " | Kara: " + fine : "");
    }
}
