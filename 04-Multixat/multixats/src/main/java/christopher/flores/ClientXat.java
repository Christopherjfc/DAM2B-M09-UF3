package christopher.flores;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat {
    private Socket socket;
    private ObjectOutputStream sortida;
    private ObjectInputStream entrada;
    private boolean sortir = false;
    
    public static void main(String[] args) {
        ClientXat clientXat = new ClientXat();
        try {
            clientXat.connecta();
            Scanner sc = new Scanner(System.in);
            Thread tLectura = new Thread(clientXat::executar);
            tLectura.start();
            
            while (!clientXat.sortir) {
                clientXat.ajuda();
                String scLinia = clientXat.getLinea(sc, "", true);
                if (scLinia.isEmpty()) {
                    clientXat.enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                    clientXat.sortir = true;
                    break;
                }
                String missatge = null;
                switch (scLinia) {
                    case "1":
                        String nom = clientXat.getLinea(sc, "Introdueix el nom: ", true);
                        missatge = Missatge.getMissatgeConectar(nom);
                        System.out.println("Enviant missatge: " + missatge);
                        break;
                    case "2":
                        String desti = clientXat.getLinea(sc, "Destinatari: ", true);
                        String msg = clientXat.getLinea(sc, "Missatge a enviar: ", true);
                        missatge = Missatge.getMissatgePersonal(desti, msg);
                        System.out.println("Enviant missatge: " + missatge);
                        break;
                    case "3":
                        String msgGrup = clientXat.getLinea(sc, "Missatge al grup: ", true);
                        missatge = Missatge.getMissatgeGrup(msgGrup);
                        System.out.println("Enviant missatge: " + missatge);
                        break;
                    case "4":
                        missatge = Missatge.getMissatgeSortirClient("Adéu");
                        System.out.println("Enviant missatge: " + missatge);
                        clientXat.sortir = true;
                        break;
                    case "5":
                        missatge = Missatge.getMissatgeSortirTots("Adéu");
                        System.out.println("Enviant missatge: " + missatge);
                        clientXat.sortir = true;
                        break;
                    default:
                        System.out.println("Opció no reconeguda.");
                }

                if (missatge != null) {
                    clientXat.enviarMissatge(missatge);
                }
            }
            sc.close();
            clientXat.tancarClient();
        } catch (IOException e) {
            System.err.println("Error connectant: " + e.getMessage());
        }
    }

    public void connecta() throws IOException {
        socket = new Socket(ServidorXat.HOST, ServidorXat.PORT);
        sortida = new ObjectOutputStream(socket.getOutputStream());
        entrada = new ObjectInputStream(socket.getInputStream());
        System.out.println("Client connectat a " + ServidorXat.HOST + ":" + ServidorXat.PORT);
    }

    public void enviarMissatge(String msg) {
        try {
            sortida.writeObject(msg);
            sortida.flush();
        } catch (IOException e) {
            System.err.println("Error al enviar el missatge: " + e.getMessage());
        }
    }

    public void tancarClient() {
        sortir = true;
        try {
            if (sortida != null) sortida.close();
            if (entrada != null) entrada.close();
            if (socket != null) socket.close();
            System.out.println("Connexió tancada.");
            System.exit(0);
        } catch (IOException e) {
            System.err.println("Error al tancar el client: " + e.getMessage());
        }
    }

    public void executar() {
        try {
            while (!sortir) {
                String msgCru = (String) entrada.readObject();
                if (msgCru == null) continue;

                String codi = Missatge.getCodiMissatge(msgCru);
                String[] parts = Missatge.getPartsMissatge(msgCru);

                if (codi == null) continue;

                switch (codi) {
                    case Missatge.CODI_SORTIR_TOTS:
                        sortir = true;
                        System.out.println("El servidor ha finalitzat el xat.");
                        tancarClient();
                        break;
                    case Missatge.CODI_MSG_PERSONAL:
                        if (parts.length >= 3)
                            System.out.println("Missatge personal de (" + parts[1] + "): " + parts[2]);
                        else
                            System.out.println("Missatge personal incorrecte.");
                        break;
                    case Missatge.CODI_MSG_GRUP:
                        if (parts.length >= 2)
                            System.out.println(parts[1]);
                        else
                            System.out.println("Missatge de grup incorrecte.");
                        break;
                    default:
                        System.out.println("Codi desconegut rebut: " + codi);
                }
            }
        }catch (IOException | ClassNotFoundException e) {
            if (!sortir) {
                System.err.println("Error rebent missatge (" + e.getClass().getSimpleName() + "): " + e.getMessage());
            }
        } finally {
            tancarClient();
        }
    }

    private void ajuda() {
        System.out.println("""
                ---------------------
                Comandes disponibles:
                  1.- Conectar al servidor (primer pas obligatori)
                  2.- Enviar missatge personal
                  3.- Enviar missatge al grup
                  4.- (o línia en blanc) -> Sortir del client
                  5.- Finalitzar tothom
                ---------------------""");
    }

    private String getLinea(Scanner scanner, String msg, boolean obligatori) {
        String sc = "";
        while (sc.isEmpty() && obligatori) {
            System.out.print(msg);
            sc = scanner.nextLine();
            if (sc.isEmpty()) {
                System.out.println("Error: línia buida no acceptada.");
            }
        }
        return sc;
    }

}