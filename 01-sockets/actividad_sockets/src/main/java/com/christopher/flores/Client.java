package com.christopher.flores;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    static final int PORT = 7777;
    final String HOST = "127.0.0.1";
    Socket socket;
    PrintWriter out;
    
    public void connecta() {
        try {
            System.out.println("Connectat a servidor en localhost:" + PORT);
            socket = new Socket(HOST, PORT);
        } catch (UnknownHostException e) {
            System.err.println("ERROR: HOST inexistente: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void envia(String msg){
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(msg);
            System.out.println("Enviat al servidor: " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void tanca(){
        boolean clientClosed = false;
        try{
            // se cierra el cliente
            if (socket != null && !socket.isClosed()) {
                clientClosed = true;
                System.out.println("Client tancat.");
                socket.close();
            }

            // se cierra el PrintWriter
            if (out != null) {
                out.close();
            }
            
        } catch (IOException e) {
            if(!clientClosed) System.err.println("Error cuando se intenta cerrar el Cliente");

            System.err.println("Error cuando se intenta cerrar el PrintWriter");
        }
    }

    

    public static void main(String[] args) {
        Client client = new Client();

        client.connecta();
        client.envia("Prova d'enviament 1");
        client.envia("Prova d'enviament 2");
        client.envia("Adéu!");
        try (BufferedReader entrada = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Prem ENTER per tancar el client...");
            while (!entrada.readLine().isBlank()) {
                System.out.println("Prem ENTER per tancar el client...");
            }
            client.tanca();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}