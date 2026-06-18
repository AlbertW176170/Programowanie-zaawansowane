package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class AppClient implements Runnable {
    private final int clientId;
    private final String host;
    private final int port;
    private final String[] classesToRequest;

    public AppClient(int clientId, String host, int port, String[] classesToRequest) {
        this.clientId = clientId;
        this.host = host;
        this.port = port;
        this.classesToRequest = classesToRequest;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeInt(clientId);
            out.flush();

            String status = in.readUTF();
            System.out.println("[KLIENT " + clientId + "] Status połączenia: " + status);

            if ("REFUSED".equals(status)) {
                System.out.println("[KLIENT " + clientId + "] Odmowa obsługi. Kończę działanie.");
                return;
            }

            for (String className : classesToRequest) {
                System.out.println("[KLIENT " + clientId + "] Proszę o klasę: " + className);
                out.writeUTF(className);
                out.flush();

                try {
                    Object response = in.readObject();
                    if (response instanceof List<?>) {
                        List<?> rawList = (List<?>) response;

                        // Wykorzystanie Stream API do wypisania obiektów i symulacji rzutowania
                        rawList.stream().forEach(obj -> {
                            try {
                                Object castedObj = castToTargetClass(obj, className);
                                System.out.println("[KLIENT " + clientId + " SUCCESS] Odebrano: " + castedObj);
                            } catch (ClassCastException e) {
                                System.out.println("[KLIENT " + clientId + " ERROR] Przechwycono błąd rzutowania! Detale: " + e.getMessage());
                            }
                        });
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("[KLIENT " + clientId + "] Nieznana klasa obiektu: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("[KLIENT " + clientId + "] Błąd komunikacji sieciowej: " + e.getMessage());
        }
    }

    private Object castToTargetClass(Object obj, String className) throws ClassCastException {
        if ("Kot".equals(className)) {
            return (model.Kot) obj;
        } else if ("Pies".equals(className)) {
            return (model.Pies) obj;
        } else if ("Samochod".equals(className)) {
            return (model.Samochod) obj;
        } else {
            throw new ClassCastException("Nie można rzutować " + obj.getClass().getName() + " na klasę " + className);
        }
    }

    public static void main(String[] args) {
        String[] targetClasses = {"Kot", "Pies", "NieznanaKlasa", "Samochod"};
        new Thread(new AppClient(101, "localhost", 12129, targetClasses)).start();
    }
}