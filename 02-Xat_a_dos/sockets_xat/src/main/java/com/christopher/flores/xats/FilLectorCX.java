package com.christopher.flores.xats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FilLectorCX extends Thread {
    private ObjectInputStream entrada;
    private boolean primerMissatgeRebut = true;
    static final String MSG_SORTIR = ServidorXat.MSG_SORTIR;

    public FilLectorCX(ObjectInputStream entrada, ObjectOutputStream sortida) {
        this.entrada = entrada;
    }

    public boolean getMessageServer(String msg) throws IOException {
        // No se romperá el bucle hasta que no reciba "sortir"
        if(msg.equalsIgnoreCase(MSG_SORTIR)) {
            return true;
        }

        if (primerMissatgeRebut) {
            System.out.printf("Rebut: %s\n", msg);
            primerMissatgeRebut = false;
        } else{
            System.out.printf("Missatge ('sortir' per tancar): Rebut: %s\n", msg);
        }

        return false;
    }

    @Override
    public void run() {
        String msg = null;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Missatge ('sortir' per tancar): Fil de lectura iniciat");
            
            // Rebre missatges del servidor i mostrar-los per pantalla
            while ((msg = (String) entrada.readObject()) != null) {
                if(getMessageServer(msg)) break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
