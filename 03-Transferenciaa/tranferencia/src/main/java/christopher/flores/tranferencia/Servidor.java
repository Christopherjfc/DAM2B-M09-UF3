package christopher.flores.tranferencia;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    static final int PORT = 9999;
    static final String HOST = "127.0.0.1";
    private ServerSocket serverSocket;
    private Socket socketClient;
    private ObjectOutputStream sortida;
    private ObjectInputStream entrada;

    public Socket connectar() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Acceptant connexions en -> localhost:" + PORT);
        System.out.println("Esperant connexió...");
        return serverSocket.accept();
    }

    public void tancarConnexio(Socket socket) throws IOException{
        if (socket != null) socket.close();
        if (serverSocket != null) serverSocket.close();
        if (sortida != null) sortida.close();
        if (entrada != null) entrada.close();
        System.out.println("Tancant connexió amb el client: " + socket.getInetAddress());
    }

    public void enviarFitxers() throws IOException, ClassNotFoundException{
        entrada = new ObjectInputStream(socketClient.getInputStream());
        sortida = new ObjectOutputStream(socketClient.getOutputStream());
        
        while (true) {
            // Rebre el nom del fitxer del client
            String nomFitxer = (String) entrada.readObject();
            
            if (!nomFitxer.equalsIgnoreCase("sortir") || nomFitxer != null || !nomFitxer.isEmpty()) {
                System.out.println("Nom del fitxer rebut: " + nomFitxer);
            }

            // Crear l'objecte Fitxer i obté el contingut en bytes
            Fitxer fitxer = new Fitxer(nomFitxer);
            byte[] contingut = fitxer.getContingut();

            if (contingut == null) {
                System.out.println("Nom del fitxer buit o nul. Sortint...");
                return;
            }

            System.out.println("Contingut del fitxer a enviar: " + contingut.length + " bytes");

            // Enviar el contingut del fitxer al client
            sortida.writeObject(contingut);
            sortida.flush();
            System.out.println("Fitxer enviat al client: " + nomFitxer);
        }        
    }    

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        try {
            servidor.socketClient = servidor.connectar();
            System.out.println("Connexió acceptada: " + servidor.socketClient.getInetAddress());
            System.out.println("Esperant el nom del fitxer del client...");
            servidor.enviarFitxers();
            servidor.tancarConnexio(servidor.socketClient);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error en la connexió: " + e.getMessage());
        }
    }
}