package com.bookflow.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BookTest
{
    // test isAvailable()
    @Test
    void shouldReturnTrueWhenBookAvailable()
    {
        Book book = new Book(1,
                "title",
                "author",
                "genre",
                5,
                2
        );

        // when
        boolean result = book.isAvailable();
        // then
        assertTrue(result);
    }

    // test borrow()
    @Test
    void shouldDecreaseAvailableCopiesAfterBorrow()
    {
        Book book = new Book(1,
                "title",
                "author",
                "genre",
                5,
                2
        );

        book.borrow();

        assertEquals(1, book.availableCopies());
    }

    // test returnBook()
    @Test
    void shouldIncreaseAvailableCopiesAfterReturn()
    {
        Book book = new Book(1,
                "title",
                "author",
                "genre",
                5,
                1
        );

        book.returnBook();

        assertEquals(2, book.availableCopies());
    }

    // test "book not available"
    @Test
    void shouldReturnFalseWhenBookUnavailable()
    {
        Book book = new Book(1,
                "title",
                "author",
                "genre",
                5,
                0
        );

        boolean result = book.isAvailable();
        assertFalse(result);
    }

    // test setAvailableCopies() negative bounds
    @Test
    void shouldNotSetNegativeAvailableCopies()
    {
        Book book = new Book(1, "title", "author", "genre", 5, 2);

        // Próbujemy ustawić na -1
        book.setAvailableCopies(-1);

        // Wartość powinna pozostać niezmieniona (2)
        assertEquals(2, book.availableCopies());
    }

    // test setAvailableCopies() upper bounds
    @Test
    void shouldNotSetAvailableCopiesAboveTotal()
    {
        Book book = new Book(1, "title", "author", "genre", 5, 2);

        // Próbujemy ustawić 10, gdy totalCopies to 5
        book.setAvailableCopies(10);

        // Wartość powinna pozostać niezmieniona (2)
        assertEquals(2, book.availableCopies());
    }
}
