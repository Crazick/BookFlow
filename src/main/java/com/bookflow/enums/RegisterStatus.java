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
    /** Wystąpił błąd podczas rejestracji. */
    ERROR
}