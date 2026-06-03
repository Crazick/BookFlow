package com.bookflow.model;

/**
 * Klasa reprezentująca wypożyczoną książkę.
 * <p>
 * Rozszerza klasę {@link Book} o informacje związane z wypożyczeniem,
 * takie jak daty oraz ewentualna kara.
 */
public class BorrowedBook extends Book // może zmienić na BorrowedRecord
{
    /** Data wypożyczenia książki. */
    private String borrowDate;

    /** Termin zwrotu książki. */
    private String dueDate;

    /** Data faktycznego zwrotu książki. */
    private String returnDate;

    /** Kara za przetrzymanie książki. */
    private double fine;

    /**
     * Tworzy obiekt wypożyczonej książki.
     *
     * @param id identyfikator książki
     * @param title tytuł
     * @param author autor
     * @param genre gatunek
     * @param totalCopies liczba wszystkich egzemplarzy
     * @param availableCopies liczba dostępnych egzemplarzy
     * @param borrowDate data wypożyczenia
     * @param dueDate termin zwrotu
     * @param returnDate data zwrotu
     * @param fine kara za przetrzymanie
     */
    public BorrowedBook(int id, String title, String author, String genre, int totalCopies, int availableCopies,
                        String borrowDate, String dueDate, String returnDate, double fine) {
        super(id, title, author, genre, totalCopies, availableCopies);
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fine = fine;
    }

    /**
     * Konstruktor domyślny.
     */
    public BorrowedBook() {
        super(0, "", "", "", 0, 0);
    }

    /** @return data wypożyczenia */
    public String borrowDate() { return borrowDate; }

    /** @return termin zwrotu */
    public String dueDate() { return dueDate; }

    /** @return data zwrotu */
    public String returnDate() { return returnDate; }

    /** @return kara za przetrzymanie */
    public double fine() { return fine; }

    /** @param borrowDate data wypożyczenia */
    public void setBorrowDate(String borrowDate) { this.borrowDate = borrowDate; }

    /** @param dueDate termin zwrotu */
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    /** @param returnDate data zwrotu */
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }

    /** @param fine kara za przetrzymanie */
    public void setFine(double fine) { this.fine = fine; }

    /**
     * Zwraca tekstową reprezentację wypożyczonej książki.
     * @return opis książki z informacjami o wypożyczeniu
     */
    @Override
    public String toString() {
        return id() + " | " + title() + " | " + author() +
                " | Wypożyczono: " + borrowDate +
                " | Termin: " + dueDate +
                (returnDate != null ? " | Zwrócono: " + returnDate : "") +
                (fine > 0 ? " | Kara: " + fine : "");
    }
}
