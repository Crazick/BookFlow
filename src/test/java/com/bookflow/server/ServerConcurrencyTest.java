package com.bookflow.server;

import com.bookflow.model.NetworkMessage;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

// dla poprawności testu musi być włączony serwer
public class ServerConcurrencyTest {

    @Test
    void shouldHandle13ConcurrentConnections() throws InterruptedException {
        int numberOfClients = 13;

        ExecutorService executor = Executors.newFixedThreadPool(numberOfClients);

        CountDownLatch latch = new CountDownLatch(numberOfClients);

        AtomicInteger successfulConnections = new AtomicInteger(0);
        Gson gson = new Gson();

        System.out.println("Uruchamiam atak " + numberOfClients + " klientów na serwer...");

        for (int i = 0; i < numberOfClients; i++) {
            final int clientId = i + 1;

            executor.submit(() -> {
                try (Socket socket = new Socket("localhost", 5000);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    NetworkMessage msg = new NetworkMessage("GET_ALL_BOOKS", List.of());
                    out.println(gson.toJson(msg));

                    String response = in.readLine();

                    if (response != null && !response.isEmpty()) {
                        successfulConnections.incrementAndGet();
                        System.out.println("Klient " + clientId + " otrzymał odpowiedź od serwera.");
                    }

                } catch (Exception e) {
                    System.err.println("Klient " + clientId + " natrafił na błąd: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(15, TimeUnit.SECONDS);
        executor.shutdown();


        assertEquals(numberOfClients, successfulConnections.get(),
                "Nie wszyscy klienci otrzymali odpowiedź. Serwer mógł się zablokować.");
    }
}