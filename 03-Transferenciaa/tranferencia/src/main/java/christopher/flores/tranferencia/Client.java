package christopher.flores.tranferencia;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final int PORT = Servidor.PORT;
    private static final String HOST = Servidor.HOST;
    private String DIR_ARRIBADA = System.getProperty("java.io.tmpdir");
    private ObjectOutputStream objectOutputStream;    
    private ObjectInputStream objectInputStream;
    private Socket socketCliente;

    public void connectar() throws IOException{
        System.out.println("Connectant a -> localhost:" + PORT);
        socketCliente = new Socket(HOST , PORT);
        objectOutputStream = new ObjectOutputStream(socketCliente.getOutputStream());
        objectInputStream = new ObjectInputStream(socketCliente.getInputStream());
    }

    public void rebreFitxers(){
        Scanner sc = new Scanner(System.in);
        try {
            while (true) {
                System.out.print("Nom del fitxer a rebre ('sortir' per sortir): ");
                String rutaFitxer = sc.nextLine();
                
                // Enviar la ruta del fitxer al servidor
                objectOutputStream.writeObject(rutaFitxer);
                objectOutputStream.flush();
                
                if ("sortir".equalsIgnoreCase(rutaFitxer)) {
                    System.out.println("Sortint...");
                    break;
                }
                
                // Rebre el contingut del fitxer com un byte[]
                byte[] contingutRebut = (byte[]) objectInputStream.readObject();
                
                // creo la ruta destino
                String rutaDestino = DIR_ARRIBADA + new File(rutaFitxer).getName();
                
                try (FileOutputStream fos = new FileOutputStream(rutaDestino)) {
                    // Guardar el contingut rebut en el fitxer
                    fos.write(contingutRebut);
                    System.out.println("Fitxer rebut i guardat com: " + new File(rutaDestino).getParent() + File.separator + new File(rutaDestino).getName());
                } catch (IOException e) {
                    System.err.println("Error guardant el fitxer: " + e.getMessage());
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al rebre el fitxer: " + e.getMessage());
        } finally {
            sc.close();
        }
    }

    public void tancarConnexio() throws IOException{
        if (socketCliente != null) socketCliente.close();
        if (objectOutputStream != null) objectOutputStream.close();
        if (objectInputStream != null) objectInputStream.close();
        System.out.println("Connexió tancada.");
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.connectar();
            System.out.println("Connexió acceptada: localhost" + client.socketCliente.getInetAddress());
            client.rebreFitxers();
            client.tancarConnexio();
        } catch (IOException e) {
            System.err.println("Error en la connexió: " + e.getMessage());
        }
    }
}
