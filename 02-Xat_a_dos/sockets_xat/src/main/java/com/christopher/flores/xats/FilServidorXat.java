package com.christopher.flores.xats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;

public class FilServidorXat extends Thread {
    private ObjectInputStream entrada;

    public FilServidorXat(ObjectInputStream entrada, String nombre) {
        super(nombre);
        this.entrada = entrada;
        System.out.println("Fil de xat creat.");
    }

    public boolean comprovaSortida(String msg) throws IOException {
        if(!msg.equalsIgnoreCase(ServidorXat.MSG_SORTIR)) return false;
        
        // Se romperá el bucle si recibe la palabra "sortir"
        System.out.println("Fil de xat finalitzat.");
        System.out.println(msg);
        return true;
    }
    
    @Override
    public void run() {
        String msgrebut = null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.printf("Fil de %s iniciat.\n", getName());
            
            // Rebre missatges del servidor i mostrar-los per pantalla
            while ((msgrebut = (String) entrada.readObject()) != null) {
                if(comprovaSortida(msgrebut)) break;
                System.out.println("Missatge ('Sortir' per tancar): Rebut: " + msgrebut);

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
