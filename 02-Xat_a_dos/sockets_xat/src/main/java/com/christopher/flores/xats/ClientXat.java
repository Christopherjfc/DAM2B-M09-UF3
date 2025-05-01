package com.christopher.flores.xats;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat {

    static final int PORT = ServidorXat.PORT;
    static final String HOST = ServidorXat.HOST;

    private Socket socket;
    private ObjectInputStream entrada;
    private ObjectOutputStream sortida;
    
    public void connecta() throws IOException {
        System.out.println("Client connectat a localhost:" + PORT);
        socket = new Socket(HOST, PORT);

        // crear los streams de entrada y salida
        sortida = new ObjectOutputStream(socket.getOutputStream());
        entrada = new ObjectInputStream(socket.getInputStream());
    }

    public void enviarMissatge(String missatge) throws IOException {
        System.out.println("Enviant missatge: " + missatge);
        sortida.writeObject(missatge);
        sortida.flush();
    }

    public void tancaClient() throws IOException{
        System.out.println("Tancant client...");
        if (sortida != null) sortida.close();
        if (entrada != null) entrada.close();
        if (socket != null && !socket.isClosed()) socket.close();
        System.out.println("Cliente tancat.");
    }

    public static void main(String[] args){
        ClientXat client = new ClientXat();
        Scanner scanner = new Scanner(System.in);
        Boolean primerMissatge = true;
        Boolean segonMissatge = false;

        try {
            client.connecta();
            System.out.println("Flux d'entrada i sortida creat");
    
            FilLectorCX filLectorCX = new FilLectorCX(client.entrada, client.sortida);
            filLectorCX.start();

            String msgEnviat;

            while (true) {
                // Se imprime el segundo mensaje con su formato especial
                if (segonMissatge) System.out.print("Missatge ('sortir' per tancar): ");
                msgEnviat = scanner.nextLine();
                
                if (msgEnviat.equalsIgnoreCase(FilLectorCX.MSG_SORTIR)) {
                    client.enviarMissatge(msgEnviat);
                    break;
                }
                
                // Se envía el primer mensaje que contiene el nombre del cliente
                if (primerMissatge) {
                    client.enviarMissatge(msgEnviat);
                    primerMissatge = false;
                    segonMissatge = true;
                } else if (segonMissatge) { 
                    // Se envia el segundo mensaje al servidor
                    client.enviarMissatge(msgEnviat);
                    segonMissatge = false;
                } else {
                    // Se envía el mensaje al servidor  
                    client.enviarMissatge(msgEnviat);
                }
            }

            filLectorCX.join();
            client.tancaClient();
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            scanner.close();
        }
    }
}