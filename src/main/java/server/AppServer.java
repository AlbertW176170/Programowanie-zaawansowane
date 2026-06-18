package server;

import model.Kot;
import model.Pies;
import model.Samochod;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class AppServer {
    public static final int PORT = 12129;
    public static final int MAX_CLIENTS = 3;

    private final Map<String, Object> repositoryMap = new ConcurrentHashMap<>();
    private final AtomicInteger activeClientsCount = new AtomicInteger(0);
    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private volatile boolean isRunning = true;
    private ServerSocket serverSocket;

    public AppServer() {
        initializeData();
    }

    private void initializeData() {
        repositoryMap.put("Kot_1", new Kot("Mruczek", 2));
        repositoryMap.put("Kot_2", new Kot("Filemon", 5));
        repositoryMap.put("Kot_3", new Kot("Bonifacy", 7));
        repositoryMap.put("Kot_4", new Kot("Puszek", 1));

        repositoryMap.put("Pies_1", new Pies("Labrador", 30.5));
        repositoryMap.put("Pies_2", new Pies("Owczarek", 25.0));
        repositoryMap.put("Pies_3", new Pies("Chihuahua", 2.1));
        repositoryMap.put("Pies_4", new Pies("Buldog", 14.3));

        repositoryMap.put("Samochod_1", new Samochod("Toyota", 2020));
        repositoryMap.put("Samochod_2", new Samochod("BMW", 2018));
        repositoryMap.put("Samochod_3", new Samochod("Audi", 2022));
        repositoryMap.put("Samochod_4", new Samochod("Ford", 2015));
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("[SERWER] Uruchomiony na porcie " + PORT + ". Max klientów: " + MAX_CLIENTS);

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();

                if (activeClientsCount.get() >= MAX_CLIENTS) {
                    threadPool.execute(() -> rejectClient(clientSocket));
                } else {
                    activeClientsCount.incrementAndGet();
                    threadPool.execute(() -> handleClient(clientSocket));
                }
            }
        } catch (IOException e) {
            if (!isRunning) {
                System.out.println("[SERWER] Zamknięto gniazdo serwera.");
            } else {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        isRunning = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        threadPool.shutdown();
    }

    private void rejectClient(Socket socket) {
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            int clientId = in.readInt();
            System.out.println("[SERWER] Odrzucono klienta o ID: " + clientId + " (Przekroczono limit MAX_CLIENTS)");
            out.writeUTF("REFUSED");
            out.flush();
        } catch (IOException e) {
            System.err.println("[SERWER] Błąd podczas odrzucania klienta: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    private void handleClient(Socket socket) {
        int clientId = -1;
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            clientId = in.readInt();
            System.out.println("[SERWER] Zaakceptowano klienta o ID: " + clientId + ". Aktywnych: " + activeClientsCount.get());
            out.writeUTF("OK");
            out.flush();

            Random random = new Random();

            while (true) {
                try {
                    String requestedClass = in.readUTF();
                    Thread.sleep(100 + random.nextInt(400)); // Losowe opóźnienie (100-500ms)

                    List<Object> collectionToSend = new ArrayList<>();
                    boolean classExists = false;

                    for (int i = 1; i <= 4; i++) {
                        String key = requestedClass + "_" + i;
                        if (repositoryMap.containsKey(key)) {
                            collectionToSend.add(repositoryMap.get(key));
                            classExists = true;
                        }
                    }

                    // Celowe wymuszenie błędu rzutowania u klienta, jeśli żąda nieistniejącej klasy
                    if (!classExists) {
                        collectionToSend.add(repositoryMap.get("Samochod_1"));
                    }

                    out.writeObject(collectionToSend);
                    out.flush();
                    System.out.println("[SERWER] Wysłano kolekcję dla klienta ID " + clientId + ": " + collectionToSend);

                } catch (IOException e) {
                    break;
                }
            }

        } catch (Exception e) {
            System.err.println("[SERWER] Błąd obsługi klienta ID " + clientId + ": " + e.getMessage());
        } finally {
            if (clientId != -1) {
                activeClientsCount.decrementAndGet();
                System.out.println("[SERWER] Klient ID " + clientId + " rozłączył się. Aktywnych: " + activeClientsCount.get());
            }
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    public Map<String, Object> getRepositoryMap() {
        return repositoryMap;
    }

    public static void main(String[] args) {
        new AppServer().start();
    }
}