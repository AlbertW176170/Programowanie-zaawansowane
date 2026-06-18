package test;

import client.AppClient;
import model.Kot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.AppServer;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectApplicationTest {

    private AppServer server;

    @BeforeEach
    public void setUp() {
        server = new AppServer();
    }

    @AfterEach
    public void tearDown() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    public void testEqualsAndHashCodeForKot() {
        Kot kot1 = new Kot("Mruczek", 2);
        Kot kot2 = new Kot("Mruczek", 2);
        Kot kot3 = new Kot("Filemon", 5);

        assertEquals(kot1, kot2);
        assertNotEquals(kot1, kot3);
        assertEquals(kot1.hashCode(), kot2.hashCode());
    }

    @Test
    public void testServerDataInitialization() {
        Map<String, Object> map = server.getRepositoryMap();
        assertNotNull(map);
        assertEquals(12, map.size());
        assertTrue(map.containsKey("Kot_1"));
        assertTrue(map.containsKey("Pies_4"));
        assertTrue(map.containsKey("Samochod_2"));
    }

    @Test
    public void testSerializationAndDeserializationOfCollection() throws IOException, ClassNotFoundException {
        Kot oryginalnyKot = new Kot("Bastek", 3);
        List<Kot> listaDoWyslania = List.of(oryginalnyKot);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(listaDoWyslania);
        oos.flush();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object odebranyObiekt = ois.readObject();

        assertTrue(odebranyObiekt instanceof List);
        List<?> odebranaLista = (List<?>) odebranyObiekt;
        assertEquals(1, odebranaLista.size());
        assertEquals(oryginalnyKot, odebranaLista.get(0));
    }

    @Test
    public void testE2E_AcceptAndRejectClientsAndCastException() throws InterruptedException {
        Thread serverThread = new Thread(() -> server.start());
        serverThread.start();
        Thread.sleep(300);

        String[] classes = {"Kot"};
        Thread c1 = new Thread(new AppClient(1, "localhost", AppServer.PORT, classes));
        Thread c2 = new Thread(new AppClient(2, "localhost", AppServer.PORT, classes));
        Thread c3 = new Thread(new AppClient(3, "localhost", AppServer.PORT, classes));

        c1.start();
        c2.start();
        c3.start();
        Thread.sleep(100);

        assertDoesNotThrow(() -> {
            try (Socket socket = new Socket("localhost", AppServer.PORT);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                out.writeInt(4);
                out.flush();
                String status = in.readUTF();
                assertEquals("REFUSED", status);
            }
        });

        String[] wrongClass = {"Nieznana"};
        Thread cWrong = new Thread(new AppClient(5, "localhost", AppServer.PORT, wrongClass));

        c1.join();
        c2.join();
        c3.join();

        assertDoesNotThrow(() -> {
            cWrong.start();
            cWrong.join();
        });
    }
}