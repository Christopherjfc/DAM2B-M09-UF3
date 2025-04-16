package com.christopher.flores;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    
    static final int PORT = 7777;
    ServerSocket serverSocket;
    Socket clientSocket;

    public void connecta() {
        try{
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor en marxa a localhost:" + PORT);
            System.out.println("Esperant connexions a localhost:" + PORT);

            // El servidor estará siempre atento a las peticiones del cliente
            while (true) {
                clientSocket = serverSocket.accept();
                repDades(clientSocket);
                break;
            }

            
        } catch (IOException ioEx) {
            System.err.println("Error al instanciar el servidor con el puerto: " + PORT);
        }
    }

    public void repDades(Socket dadesClient){
        try (BufferedReader br = new BufferedReader(new InputStreamReader(dadesClient.getInputStream()))){
            System.out.println("Client connectat: " + dadesClient.getInetAddress());
            
            String msgRebut;
            while ((msgRebut = br.readLine()) != null) {
                System.out.printf("Rebut: %s%n", msgRebut);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void tanca(){
        boolean serverClosed = false;
        try{
            // se cierra el servidor
            if (serverSocket != null && !serverSocket.isClosed()) {
                System.out.println("Servidor tancat.");
                serverSocket.close();
                serverClosed = true;
            }
            
            // se cierra el cliente
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            if(!serverClosed) System.err.println("Error cuando se intenta cerrar el servidor");

            System.err.println("Error cuando se intenta cerrar el cliente");
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();

        servidor.connecta();
        servidor.tanca();
    }

}
