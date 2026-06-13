package com.bookflow.model;

import java.util.List;

/**
 * Klasa reprezentująca ustrukturyzowaną wiadomość sieciową.
 * <p>
 * Wykorzystywana do przesyłania komend i argumentów pomiędzy klientem a serwerem
 * w formacie JSON, co zapobiega błędom parsowania przy ciągach znaków zawierających spacje.
 */
public class NetworkMessage
{
    /** Główna komenda operacji (np. "LOGIN"). */
    private String command;
    /** Lista argumentów przypiosanych do komend (np login, hasło). */
    private List<String> args;

    /**
     * Domyślny konstruktor dla biblioteki Gson.
     */
    public NetworkMessage() {}

    /**
     * Tworzy nową wiadomość sieciową.
     *
     * @param command nazwa komendy
     * @param args lista argumentów niezbędnych do wykonania komendy
     */
    public NetworkMessage(String command, List<String> args){
        this.command = command;
        this.args = args;
    }

    /**
     * Pobiera nazwę komendy.
     * @return łańcuch znaków reprezentujący komendę
     */
    public String getCommand(){
        return command;
    }
    /**
     * Pobiera listę argumentów.
     * @return lista argumentów w postaci ciągów znaków
     */
    public List<String> getArgs(){
        return args;
    }
}
