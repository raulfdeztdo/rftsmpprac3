/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AlmacenMedico;

import Utils.GsonUtil;
import Utils.Mensaje;
import static Utils.Utils.NUM_ALMACENES;
import static Utils.Utils.NUM_CENTROS;
import static Utils.Utils.QUEUE_CENTRO;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 *
 * @author smart
 */
public class ListenerAlmacen implements MessageListener {
    private final int iD;
    private final Paquete paquete;
    
    private final Session session;
    private final Destination[] destination;
    private final ReentrantLock exm;

    public ListenerAlmacen(int iD, Paquete paquete, Session session, ReentrantLock exm) {
        this.iD = iD;
        this.paquete = paquete;
        this.session = session;
        this.exm = exm;
        this.destination = new Destination[NUM_CENTROS]; 
        
        for (int i = 0; i < NUM_CENTROS; i++) {
            try {
                destination[i] = this.session.createQueue(QUEUE_CENTRO + i);
            } catch (JMSException ex) {
               System.err.println("ListenerAlmacen: Fallo al crear el buzon");
            }
        }
    }
    
    
    @Override
    public void onMessage(Message msg) {
        GsonUtil<Mensaje> gson = new GsonUtil();
        Mensaje respuesta;
        
        try {			
            if (msg instanceof TextMessage) {
                // Recogemos los datos del mensaje, que nos lo enviara el CentroMedico
                TextMessage contenido = (TextMessage) msg;
                Mensaje mensaje = gson.decode(contenido.getText(), Mensaje.class);
		System.out.println("\nCentro Medico " +mensaje.getIdBuzon()+ " ha enviado solicitud");
                System.out.println("\tLa solicitud ha sido enviada al Almacen Medico: " +this.iD);
                System.out.println("\tEl lote solicitado es: " +mensaje.getIdLote());
                
                // Comprobamos en exclusion mutua si el paquete ha sido enviado o no
                exm.lock();
                if(paquete.getLote() != null && mensaje.getIdLote() == paquete.getLote().getiD()){
                    // Si todavia el paquete no se ha enviado, asignamos el paquete
                    respuesta = new Mensaje(paquete.getLote().getiD(), this.iD , paquete.getLote());
                    // Al ser el paquete asignado lo dejamos a null
                    paquete.setLote(null);
                    paquete.incLotesEnviados(); //Incrementamos en uno el numero de lotes enviados
                    exm.unlock();
                    System.out.println("\nEl lote: "+mensaje.getIdLote()+" ha sido asignado al Centro Medico "+mensaje.getIdBuzon());
                }else{
                    // Si el paquete ya ha sido asignado
                    paquete.incSolicitudesRechazadas(); //Incrementamos en uno el numero de solicitudes rechazadas
                    exm.unlock();
                    // Generamos una respuesta de rechazo
                    respuesta = new Mensaje(this.iD);
                    System.out.println("\nEnviando respuesta de rechazo al Centro Medico: "+mensaje.getIdBuzon());
                }
                
                // Creamos un buzon de destino para ese CentroMedico y un productor para enviarle un mensaje
                MessageProducer producer = session.createProducer(destination[mensaje.getIdBuzon()]);
                // Enviamos el mensaje al CentroMedico con la respuesta correspondiente
                String msgRespuesta = gson.encode(respuesta, Mensaje.class);
                TextMessage textMessage = session.createTextMessage(msgRespuesta);
                producer.send(textMessage);
                producer.close();
                
            } else {
                System.out.println("Almacen: "+ iD + " Unknown message");
            }
	} catch (JMSException e) {			
            // No hacer nada
	}
    }
    
}
