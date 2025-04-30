package com.christopher.flores.xats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorXat {
    
    static final int PORT = 9999;
    final static String HOST = "127.0.0.1";
    final static String MSG_SORTIR = "sortir"; 
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectOutputStream objectOut;
    private ObjectInputStream objectIn;

    public void iniciarServidor() {
        try{
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciat a localhost:" + PORT);

        } catch (IOException ioEx) {
            System.err.println("Error al instanciar el servidor con el puerto: " + PORT);
        }
    }

    // método getNom que reciba los stream i obtenga el nombre del cliente
    public String getNom(ObjectInputStream entrada) throws IOException, ClassNotFoundException {
        objectOut.writeObject("Escriu el teu nom:");
        objectOut.flush();
        String msgRebut = (String) entrada.readObject();
        System.out.println("Nom rebut: " + msgRebut);     
        return msgRebut;     
    }
    
    public void paraServidor() throws IOException {
        // se cierra el servidor
        if (objectIn != null) objectIn.close();
        if (objectOut != null) objectOut.close();
        if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
        if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
        System.out.println("Servidor aturat.");
    }

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        try {
            servidor.iniciarServidor();

            // se acepta la conexión
            servidor.clientSocket = servidor.serverSocket.accept();
            System.out.println("Client connectat: " + servidor.clientSocket.getInetAddress());

            // se crean los streams
            servidor.objectOut = new ObjectOutputStream(servidor.clientSocket.getOutputStream());
            servidor.objectIn = new ObjectInputStream(servidor.clientSocket.getInputStream());

            // Obtengo el nombre del cliente
            String nomFil = servidor.getNom(servidor.objectIn);

            // instancio el hilo del servidor y lo inicio
            FilServidorXat filServidor = new FilServidorXat(servidor.objectIn, nomFil);
            filServidor.start();

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String msg;

            while (true) {
                msg = br.readLine();

                servidor.objectOut.writeObject(msg);
                servidor.objectOut.flush();

                if (msg.equalsIgnoreCase(MSG_SORTIR)) break;
            }
            
            // espera aque el hilo del servidor termine
            filServidor.join();
            
            // cierra todo (streams, socket y servidor)
            servidor.paraServidor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }        
    }

}
