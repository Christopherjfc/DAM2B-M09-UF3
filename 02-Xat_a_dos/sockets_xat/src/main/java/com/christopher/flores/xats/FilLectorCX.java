package com.christopher.flores.xats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FilLectorCX extends Thread {
    private ObjectInputStream entrada;
    private ObjectOutputStream sortida;
    static final String MSG_SORTIR = ServidorXat.MSG_SORTIR;

    public FilLectorCX(ObjectInputStream entrada, ObjectOutputStream sortida) {
        this.entrada = entrada;
        this.sortida = sortida;
    }

    public boolean getMessageServer(String msg) throws IOException {
        // No se romperá el bucle hasta que no reciba "sortir"
        if(msg.equalsIgnoreCase(MSG_SORTIR)) {
            System.out.println("Tancant client...");
            System.out.println(msg);
            entrada.close();
            return true;
        }

        System.out.printf("Rebut: %s\n", msg);
        return false;
    }

    @Override
    public void run() {
        String msg = null;
        String msgEnviat;
        boolean primerMissatge = true;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Flux d'entrada i sortida creat");
            System.out.println("Missatge ('sortir' per tancar): Fil de lectura iniciat");
            
            // Rebre missatges del servidor i mostrar-los per pantalla
            while ((msg = (String) entrada.readObject()) != null) {
                if(getMessageServer(msg)) break;
            
                // Primer missatge = el nom del client
                if (primerMissatge) {
                    msgEnviat = br.readLine();
                    primerMissatge = false;
                } else {
                    System.out.print("Missatge ('sortir' per tancar): ");
                    msgEnviat = br.readLine();
                }

                // Envia el missatge
                System.out.println("Enviant missatge: " + msgEnviat);
                sortida.writeObject(msgEnviat);
                sortida.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
