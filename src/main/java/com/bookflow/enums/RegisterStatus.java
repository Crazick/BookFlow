package com.bookflow.enums;

/**
 * Enum reprezentujący status rejestracji użytkownika.
 */
public enum RegisterStatus
{
    /** Rejestracje zakończyła się sukcesem. */
    SUCCESS,
    /** Użytkownik o podanej nazwie już istnieje. */
    USER_EXISTS,
    /** Hasło nie spełnia wymogów bezpieczeństwa (np. jest za krótkie). */
    INVALID_PASSWORD,
    /** Wystąpił błąd podczas rejestracji. */
    ERROR
}