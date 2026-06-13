package com.bookflow.service;

import com.bookflow.dao.AdminDAO;
import com.bookflow.model.Book;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminServiceTest
{
    @Test
    void shouldRejectBookWithNegativeCopies() {
        AdminDAO adminDAO = mock(AdminDAO.class);
        AdminService service = new AdminService(adminDAO);

        // Książka z ujemnymi wartościami
        Book book = new Book(1, "Tytuł", "Autor", "Gatunek", -5, -2);

        boolean result = service.addBook(book);

        assertFalse(result);
        verify(adminDAO, never()).addBook(any()); // Upewniamy się, że DAO nie zostało wywołane
    }

    @Test
    void shouldRejectBookWhenAvailableExceedsTotal() {
        AdminDAO adminDAO = mock(AdminDAO.class);
        AdminService service = new AdminService(adminDAO);

        // Dostępnych egzemplarzy (10) jest więcej niż całkowitych (5)
        Book book = new Book(1, "Tytuł", "Autor", "Gatunek", 5, 10);

        boolean result = service.addBook(book);

        assertFalse(result);
        verify(adminDAO, never()).addBook(any());
    }

    @Test
    void shouldCallDaoWhenBookIsValid() {
        AdminDAO adminDAO = mock(AdminDAO.class);
        when(adminDAO.addBook(any(Book.class))).thenReturn(true);
        AdminService service = new AdminService(adminDAO);

        // Prawidłowa książka
        Book book = new Book(1, "Tytuł", "Autor", "Gatunek", 10, 5);

        boolean result = service.addBook(book);

        assertTrue(result);
        verify(adminDAO, times(1)).addBook(book);
    }
}
