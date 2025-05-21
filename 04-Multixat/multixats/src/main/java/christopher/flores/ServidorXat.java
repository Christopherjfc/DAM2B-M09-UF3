package christopher.flores;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class ServidorXat {
    public static final int PORT = 9999;
    public static final String HOST = "localhost";
    public static final String MSG_SORTIR = "sortir";

    private Hashtable<String, GestorClients> gestorClients = new Hashtable<>();
    private boolean sortir = false;
    private ServerSocket servidor;
    private Socket clientSocket;

    public void servidorAEscoltar() throws IOException {
        servidor = new ServerSocket(PORT);
        System.out.println("Servidor iniciat a " + HOST + ":" + PORT);

        try {
        while (!sortir) {
            clientSocket = servidor.accept();
            System.out.println("Client connectat: " + clientSocket.getInetAddress());
            GestorClients gestor = new GestorClients(clientSocket, this);
            gestor.start();
        }
    } catch (IOException e) {
        if (!sortir) {
            System.err.println("Error acceptant clients: " + e.getMessage());
        }
    }

    }

    public void pararServidor() throws IOException {
        if (servidor != null && !servidor.isClosed()) {
            sortir = true;
            servidor.close();
        }
    }
    
    public void finalitzarXat() {
        System.out.println("Tancant tots els clients.");
        System.out.println("DEBUG: multicast sortir");
        for (GestorClients client : gestorClients.values()) {
            client.enviarMissatge(Missatge.getMissatgeSortirTots(MSG_SORTIR));
        }
        gestorClients.clear();
        sortir = true;

        try {
            pararServidor();
        } catch (IOException e) {
            System.err.println("Error al parar el servidor: " + e.getMessage());
        }
    }

    public void afegirClient(GestorClients gestorClients) {
        this.gestorClients.put(gestorClients.getNom(), gestorClients);
        System.out.println(gestorClients.getNom() + " connectat.");
        System.out.println("DEBUG: multicast Entra: " + gestorClients.getNom());
    }

    public void eliminarClient(String nom) {
        if (gestorClients.containsKey(nom)) {
            gestorClients.remove(nom);
            System.out.println("Cliente eliminado: " + nom);
        }
    }

    
    public void enviarMissatgeGrup(String missatge) {
        String msgFormatCorrecte = Missatge.getMissatgeGrup(missatge);
        System.out.printf("Enviant missatge grup: %s%n", msgFormatCorrecte);
        for (GestorClients client : gestorClients.values()) {
            client.enviarMissatge(msgFormatCorrecte);
        }
    }

    public void enviarMissatgePersonal(String destinatari, String remitent, String missatge) {
        if (gestorClients.containsKey(destinatari)) {
            String msg = Missatge.getMissatgePersonal(remitent, missatge);
            System.out.printf("Enviant missatge personal per (%s) de (%s): %s%n", destinatari, remitent, missatge);
            gestorClients.get(destinatari).enviarMissatge(msg);
        } else {
            System.out.println("Destinatari inexistent: " + destinatari);
        }
    }

    public static void main(String[] args) {
        ServidorXat servidorXat = new ServidorXat();
        try {
            servidorXat.servidorAEscoltar();
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor: " + e.getMessage());
        } finally {
            try {
                servidorXat.pararServidor();
            } catch (IOException e) {
                System.out.println("Error al parar el servidor: " + e.getMessage());
            }
        }
    }
}