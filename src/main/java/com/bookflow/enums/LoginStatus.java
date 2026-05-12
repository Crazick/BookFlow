package com.bookflow.enums;

/**
 * Enum reperezentujący status logowania użytkownika.
 */
public enum LoginStatus
{
    /** Logowanie zakończyło się sukcesem. */
    SUCCESS,
    /** Użytkownik o podanej nazwie nie istnieje. */
    USER_NOT_FOUND,
    /** Podano niepoprawne hasło. */
    WRONG_PASSWORD,
    /** Wystąpił błąd podczas logowania. */
    ERROR
}