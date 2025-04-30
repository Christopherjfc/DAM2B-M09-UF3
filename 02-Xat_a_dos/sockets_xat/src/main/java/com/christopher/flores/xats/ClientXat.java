package com.christopher.flores.xats;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientXat {

    static final int PORT = ServidorXat.PORT;
    static final String HOST = ServidorXat.HOST;

    private Socket socket;
    private InputStream entrada;
    private OutputStream sortida;
    
    public void connecta() {
        try {
            System.out.println("Client connectat a localhost:" + PORT);
            socket = new Socket(HOST, PORT);

            // crear los streams de entrada y salida
            entrada = socket.getInputStream();
            sortida = socket.getOutputStream();
        
        } catch (UnknownHostException e) {
            System.err.println("ERROR: HOST inexistente: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarMissatge(String missatge) {
        try {
            // Convertir el mensaje a bytes y enviarlo al servidor
            sortida.write(missatge.getBytes());
            sortida.flush();
        } catch (IOException e) {
            System.err.println("Error al enviar el missatge: " + e.getMessage());
        }
    }

    public void tancaClient() throws IOException{
        if (entrada != null) entrada.close();
        if (sortida != null) sortida.close();
        if (socket != null && !socket.isClosed()) socket.close();
        System.out.println("Cliente tancat.");
    }

    

    public static void main(String[] args){
        ClientXat client = new ClientXat();
        try {
            client.connecta();
            ObjectOutputStream out = new ObjectOutputStream(client.socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(client.socket.getInputStream());
    
            FilLectorCX filLectorCX = new FilLectorCX(in, out);
            filLectorCX.start();
            filLectorCX.join();
            client.tancaClient();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }
}