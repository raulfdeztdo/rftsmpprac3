/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AlmacenMedico;

import static Utils.Utils.NUM_ALMACENES;
import static Utils.Utils.TIEMPO_ESPERA_HILO_PRINCIPAL;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author smart
 */
public class MainAlmacenMedico {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
                       
        // Variables aplicación
        ExecutorService executor = (ExecutorService) Executors.newCachedThreadPool();

        // Variable atomica para crear IDs unicos
        AtomicInteger contIdLotes = new AtomicInteger();
        
        System.out.println("HILO-Principal[AlmacenMedico] Ha iniciado la ejecución");

        System.out.println("HILO-Principal[AlmacenMedico] Generando almacenes");
        for (int i = 0; i < NUM_ALMACENES; i++) {
            executor.execute(new AlmacenMedico(i,contIdLotes));
        }
        
        // No terminamos la ejecucion hasta pulsar una tecla
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(MainAlmacenMedico.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        executor.shutdownNow();

        try {
            executor.awaitTermination(TIEMPO_ESPERA_HILO_PRINCIPAL, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            //No es necesario tratar la excepción puesto que el hilo principal no se va a interrumpir.
            Logger.getLogger(MainAlmacenMedico.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("HILO-Principal[AlmacenMedico] Ha finalizado la ejecución");
    }
    
}
