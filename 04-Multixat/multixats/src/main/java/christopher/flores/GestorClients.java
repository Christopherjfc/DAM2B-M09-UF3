package christopher.flores;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GestorClients extends Thread {
    private Socket clientSocket;
    private ObjectOutputStream sortida;
    private ObjectInputStream entrada;
    private ServidorXat servidorXat;
    private String nom;
    private boolean sortir = false;

    public GestorClients(Socket clientSocket, ServidorXat servidorXat) throws IOException {
        this.clientSocket = clientSocket;
        this.servidorXat = servidorXat;
        sortida = new ObjectOutputStream(clientSocket.getOutputStream());
        entrada = new ObjectInputStream(clientSocket.getInputStream());
    }
    
    public String getNom() {
        return nom;
    }

    @Override
    public void run() {
        try {
            // mensaje tal cual llega desde el cliente, sin procesar ("1000#...")
            String msgCru;
            while (!sortir && (msgCru = (String) entrada.readObject()) != null) {
                processaMissatge(msgCru);
            }
        } catch (IOException | ClassNotFoundException e) {
            if (!sortir) {
                System.err.println("Error llegint missatge: " + e.getMessage());
            }
        } finally {
            if (nom != null) {
                servidorXat.eliminarClient(nom);
            }
            try {
                clientSocket.close();
            } catch (IOException ex) {
                System.err.println("Error al tancar el client: " + ex.getMessage());
            }
        }
    }

    public void enviarMissatge(String msg) {
        try {
            sortida.writeObject(msg);
            sortida.flush();
        } catch (IOException e) {
            System.err.println("Error enviant missatge: " + e.getMessage());
        }
    }

    private void processaMissatge(String msgCru) {
        // extrec el codi del missatge cru
        String codiMsg = Missatge.getCodiMissatge(msgCru);
        if (codiMsg == null) return;

        // array que separa el missatge cru per parts
        String[] msgPerParts = Missatge.getPartsMissatge(msgCru);
        
        // si no es null, el comparo amb els codis de la classe Missatge 
        switch (codiMsg) {
            case Missatge.CODI_SORTIR_TOTS: // 0000
                sortir = true;
                servidorXat.finalitzarXat();
                break;

            case Missatge.CODI_CONECTAR: // 1000
                if (msgPerParts.length >= 2) {
                    nom = msgPerParts[1];
                    servidorXat.afegirClient(this);
                }
                break;
                
            case Missatge.CODI_MSG_PERSONAL: // 1001
                if (msgPerParts.length >= 3) {
                    String destinatari = msgPerParts[1];
                    String missatge = msgPerParts[2];
                    servidorXat.enviarMissatgePersonal(destinatari, nom, missatge);
                }
                break;

            case Missatge.CODI_MSG_GRUP: // 1002
                if (msgPerParts.length >= 2) {
                    String missatge = msgPerParts[1];
                    servidorXat.enviarMissatgeGrup(missatge);
                }
                break;

            case Missatge.CODI_SORTIR_CLIENT: // 1003
                servidorXat.eliminarClient(nom);
                sortir = true;
                break;

            default:
                System.err.println("Codí desconegut rebut: " + codiMsg);
        }
    }
}
