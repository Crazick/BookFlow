package com.bookflow.model;

/**
 * Klasa reprezentująca książkę w systemie biblioteki.
 * <p>
 * Przechowuje podstawowe informacje o książce oraz jej stan dostępności.
 */
public class Book
{
    /** Identyfikator książki. */
    private int id;
    /** Tytuł książki. */
    private String title;
    /** Autor książki. */
    private String author;
    /** Gatunek książki. */
    private String genre;
    /** Łączna liczba egzemplarzy. */
    private int totalCopies;
    /** Liczba dostępnych egzemplarzy. */
    private int availableCopies;

    /**
     * Tworzy nową książkę.
     *
     * @param id identyfikator książki
     * @param title tytuł
     * @param author autor
     * @param genre gatunek
     * @param totalCopies całkowita liczba egzemplarzy
     * @param availableCopies dostępne egzemplarze
     */
    public Book(int id, String title, String author, String genre,
                int totalCopies, int availableCopies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    /** @return tytuł książki */
    public String title() { return title; }

    /** @return identyfikator książki */
    public int id() { return id; }

    /** @return autor książki */
    public String author() { return author; }

    /** @return gatunek książki */
    public String genre() { return genre; }

    /** @return liczba dostępnych egzemplarzy */
    public int availableCopies() { return availableCopies; }

    /** @return całkowita liczba egzemplarzy */
    public int totalCopies() { return totalCopies; }

    /**
     * Ustawia liczbę dostępnych egzemplarzy.
     * @param i nowa liczba dostępnych książek
     */
    public void setAvailableCopies(int i) {
        if (i < totalCopies) {
            availableCopies = i;
        }
    }

    /**
     * Sprawdza czy książka jest dostępna do wypożyczenia.
     * @return true jeśli dostępna, false jeśli nie
     */
    public boolean isAvailable() {
        return availableCopies > 0;
    }

    /**
     * Wypożycza książkę (zmniejsza liczbę dostępnych egzemplarzy).
     */
    public void borrow() {
        if (availableCopies > 0) {
            availableCopies--;
            System.out.println("Book borrowed.");
        }
        System.out.println("Book can't be borrowed.");
    }

    /**
     * Zwraca książkę (zwiększa liczbę dostępnych egzemplarzy).
     */
    public void returnBook() {
        if (availableCopies < totalCopies) {
            availableCopies++;
            System.out.println("Book returned.");
        }
    }

    /**
     * Zwraca tekstową reprezentację książki.
     * @return opis książki
     */
    @Override
    public String toString() {
        return id + " | " + title + " | " + author + " | dostępne: " + availableCopies;
    }
}