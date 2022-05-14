/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CentroMedico;

import static Utils.Utils.NUM_CENTROS;
import static Utils.Utils.TIEMPO_ESPERA_HILO_PRINCIPAL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author raul
 */
public class MainCentroMedico {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ExecutorService executor = (ExecutorService) Executors.newCachedThreadPool();
        
        System.out.println("HILO-Principal[CentroMedico] Ha iniciado la ejecución");

        System.out.println("HILO-Principal[CentroMedico] Generando centros");
        
        ArrayList<CentroMedico> centros = new ArrayList();
        for (int i = 0; i < NUM_CENTROS; i++) {
            centros.add(new CentroMedico(i));
            executor.execute(centros.get(i));
        }
        
        executor.shutdown();

        try {
            executor.awaitTermination(TIEMPO_ESPERA_HILO_PRINCIPAL, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            Logger.getLogger(MainCentroMedico.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("HILO-Principal[CentroMedico] Ha finalizado la ejecución");
        
        for (int i = 0; i < NUM_CENTROS; i++) {
            centros.get(i).mostrarInformacion();
        }
        
        
    }
    
}
