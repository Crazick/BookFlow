package com.bookflow.client;

import java.net.Socket;
import java.io.*;

public class TestClient {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 5000);
        System.out.println("Połączono z serwerem!");

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // wysyłanie
        out.println("BORROW 1");

        // odbieranie
        String response = in.readLine();
        System.out.println("Serwer: " + response);
    }
}