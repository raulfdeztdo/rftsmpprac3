/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AlmacenMedico;

import Utils.GsonUtil;
import Utils.Lote;
import Utils.Mensaje;
import static Utils.Utils.BROKER_URL;
import Utils.Utils.FabricanteVacuna;
import static Utils.Utils.MAX_TIEMPO_ESPERA_ALMACEN;
import static Utils.Utils.MIN_TIEMPO_ESPERA_ALMACEN;
import static Utils.Utils.QUEUE_ALMACEN;
import static Utils.Utils.TIEMPO_ESPERA_PUBLICACION;
import static Utils.Utils.TOPIC;
import static Utils.Utils.VALOR_GENERACION;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 *
 * @author smart
 */
public class AlmacenMedico implements Runnable {
    // Atributos del AlmacenMedico
    private final int iD;
    private final AtomicInteger contadorIDs;
    private final Paquete paquete;
    
    // Atributos necesarios para imprimir informacion del almacen
    private ArrayList<Lote> lotesGenerados;
    private ArrayList<Integer> vecesOfertado;
    
    // Cerrojo para garantizar la exclusion mutua
    private final ReentrantLock exm;
    
    // Atributos referente a la conexion JMS 
    private ActiveMQConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Destination destination;
    private Destination destinationAsincrono;
    private MessageConsumer consumerAsincrono;

    public AlmacenMedico(int iD, AtomicInteger contadorIDs) {
        this.iD = iD;
        this.contadorIDs = contadorIDs;
        this.paquete = new Paquete();
        this.exm = new ReentrantLock();
        this.lotesGenerados = new ArrayList<>();
        this.vecesOfertado = new ArrayList<>();
        ThreadLocalRandom.current();
    }
    
    private int getSiguienteID(){
        return this.contadorIDs.getAndIncrement();
    }
    
    @Override
    public void run() {
        System.out.println("Almacen con id "+iD+" ha comenzado la fabricacion de lotes");
        try {
            before();
            task();
        } catch (JMSException ex) {
            System.err.println("Almacen "+iD+" JMSException");
        } finally {
            after();
            System.out.println("Almacen "+iD+" ha finalizado la ejecucion");
        }
        imprimirInfo();
    }

    private void before() throws JMSException{
        connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // Creamos un buzon para la publicacion de lotes
        destination = session.createTopic(TOPIC);
        // Creamos un buzon unico para la comunicacion entre almacenes
        destinationAsincrono = session.createQueue(QUEUE_ALMACEN + iD);
        // Creamos una comunicacion asincrona
        consumerAsincrono = session.createConsumer(destinationAsincrono);
        consumerAsincrono.setMessageListener(new ListenerAlmacen(this.iD, paquete, session, exm));
    }
    
    private void after(){
        try {
            if (consumerAsincrono != null){
                consumerAsincrono.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception ex) {
            // No hacer nada
        }
    }
    
    private void task() throws JMSException{
        boolean interrumpido = false;
        MessageProducer producer = session.createProducer(destination);
        GsonUtil<Mensaje> gson = new GsonUtil();
        
        // Esperamos un tiempo aleatorio que como minimo son 5 segundos
        try {
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(MAX_TIEMPO_ESPERA_ALMACEN - MIN_TIEMPO_ESPERA_ALMACEN) + MIN_TIEMPO_ESPERA_ALMACEN);
        } catch (InterruptedException ex) {
            System.out.println();
        }
        
        while(!interrumpido){
            try{
                // Creamos un paquete con un fabricante aleatorio
                if(paquete.getLote() == null){
                    FabricanteVacuna fabricante = FabricanteVacuna.getFabricante(ThreadLocalRandom.current().nextInt(VALOR_GENERACION));
                    Lote nuevoLote = new Lote(getSiguienteID(), fabricante);
                    paquete.setLote(nuevoLote);
                    lotesGenerados.add(nuevoLote);
                    vecesOfertado.add(0);
                }
                
                // Publicamos el mensaje en el buzon comun a todos
                Mensaje mensaje = new Mensaje(paquete.getLote().getiD(), this.iD);
                String msg = gson.encode(mensaje, Mensaje.class);
                TextMessage textMessage = session.createTextMessage(msg);
                producer.send(textMessage);
                
                //Cogemos el ultimo paquete que estamos procesando y le incrementamos el numero de veces ofertado
                incVecesOfertado();
                System.out.println("Almacen "+ iD +" ha publicado:" +msg);
                
                
                // Una vez enviado el lote esperaremos TIEMPO_ESPERA_PUBLICACION para volver a enviar el paquete o crear uno
                TimeUnit.SECONDS.sleep(TIEMPO_ESPERA_PUBLICACION);
            } catch(InterruptedException ex){
                interrumpido = true;
            }
        }
        
        producer.close();
    }
    
    private void imprimirInfo(){
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("\nInformacion referente a Almacen Medico con id ").append(iD);
        mensaje.append("\t\nNumero de solicitudes rechazas: ").append(paquete.getSolicitudesRechazadas());
        mensaje.append("\t\nNumero de lotes enviados: ").append(paquete.getLotesEnviados());
        mensaje.append("\t\nNumero de lotes generados: ").append(lotesGenerados.size());
        for (int i = 0; i < lotesGenerados.size(); i++) {
            mensaje.append("\n\t").append(lotesGenerados.get(i)).append(" ha sido publicado ").append(vecesOfertado.get(i)).append(" veces");
        }
        System.out.println(mensaje);
    }
    
    private void incVecesOfertado(){
        Integer index = lotesGenerados.size() - 1;
        Integer numVeces = this.vecesOfertado.get(index);
        this.vecesOfertado.set(index, numVeces + 1);
    }
}
