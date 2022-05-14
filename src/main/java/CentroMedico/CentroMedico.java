/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CentroMedico;

import Utils.GsonUtil;
import Utils.Lote;
import Utils.Mensaje;
import static Utils.Utils.BROKER_URL;
import static Utils.Utils.NUM_MAX_LOTES;
import static Utils.Utils.QUEUE_ALMACEN;
import static Utils.Utils.QUEUE_CENTRO;
import static Utils.Utils.TIEMPO_ESPERA_POR_DOSIS;
import static Utils.Utils.TOPIC;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
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
 * @author raul
 */
public class CentroMedico implements Runnable {
    
    private final int iD;
    private ArrayList<Lote> lote;
    private int numLotes;
    private int numLotesPerdidos;
    
    private ActiveMQConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Destination destination;
    private Destination destinationCentro;

    public int getiD() {
        return iD;
    }

    public CentroMedico(int iD) {
        this.iD = iD;
        this.lote = new ArrayList();
        this.numLotes = 0;
        this.numLotesPerdidos = 0;
    }
    
    public void mostrarInformacion() {
        System.out.println("---------------------------------------------------------");
        System.out.println("- iD del centro: "+ iD);
        System.out.println("- Num Lotes totales: "+ (numLotes+numLotesPerdidos));
        System.out.println("- Num Lotes perdidos: "+ numLotesPerdidos);
        System.out.println("********* Num Lotes recibidos: "+ numLotes);
        if ( numLotes > 0) {
            for (int i = 0; i < numLotes; i++) {
                System.out.println("- iD Lote: "+ lote.get(i).getiD());
                System.out.println("- Num dosis: "+ lote.get(i).getNumDosis());
                System.out.println("- Fabricante: "+ lote.get(i).getFabricante());
                System.out.println("*********");
            }
        }
        System.out.println("---------------------------------------------------------");
    }
    
    @Override
    public void run() {
        System.out.println("[CentroMedico][" + getiD() + "] empezando su ejecucion ...");
        
        try {
            before();
            try {
                execution();
            } catch (InterruptedException ex) {
                Logger.getLogger(CentroMedico.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (JMSException e) {
            System.err.println("[CentroMedico][" + getiD() + "] ha sido detenido: " + e.getMessage());
        } finally {
            after();
            System.out.println("[CentroMedico][" + getiD() + "] terminando su ejecucion");
        }

    }
    
    public void before() throws JMSException {
        connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        destination = session.createTopic(TOPIC);
        destinationCentro = session.createQueue(QUEUE_CENTRO + iD);
        
    }
    
    public void after() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception ex) {
            // No hacer nada
        }    
    }

    
    public void execution() throws JMSException, InterruptedException {
        MessageConsumer consumer = session.createConsumer(destination);
        MessageConsumer consumerListener = session.createConsumer(destinationCentro);
        Destination destinoContestacion; 
        
        GsonUtil<Mensaje> gson = new GsonUtil();
        Mensaje msgRecibido, msgDevuelto, loteRecibido;
        
        
        while (numLotes < NUM_MAX_LOTES) { 
            // Recibimos el mensaje de forma síncrona
            TextMessage msg = (TextMessage) consumer.receive();
            msgRecibido = gson.decode(msg.getText(), Mensaje.class);
            
            // Devolvemos el mensaje con el id del lote ofertado y el buzon propio
            destinoContestacion = session.createQueue(QUEUE_ALMACEN + msgRecibido.getIdBuzon());
            MessageProducer producer = session.createProducer(destinoContestacion);
            
            msgDevuelto = new Mensaje(msgRecibido.getIdLote(), this.iD);
            String msgDevueltoString = gson.encode(msgDevuelto, Mensaje.class);
            TextMessage textMessage = session.createTextMessage(msgDevueltoString);
            producer.send(textMessage);
             
            producer.close();
            
            // Recibimos el mensaje de forma síncrona
            TextMessage msg2 = (TextMessage) consumerListener.receive();
            loteRecibido = gson.decode(msg2.getText(), Mensaje.class);

            if (loteRecibido.getLote() != null && loteRecibido.getIdLote() > -1) {
                System.out.println("CentroMedico["+ iD +"], ID lote de: " + loteRecibido.getIdLote()+ " recibido");
                lote.add(loteRecibido.getLote());
                int numDosis = lote.get(numLotes).getNumDosis();
                numLotes++;
                for (int i = 0; i < numDosis; i++) {
                    TimeUnit.MILLISECONDS.sleep(TIEMPO_ESPERA_POR_DOSIS);
                }
                
            } else {
                System.out.println("CentroMedico["+ iD +"], ID lote de: " + loteRecibido.getIdLote() + " perdido");
                numLotesPerdidos++;
            }
            System.out.println("CentroMedico["+ iD +"] Num lotes perdidos: " + numLotesPerdidos);
            System.out.println("CentroMedico["+ iD +"] Num lotes recibidos: " + numLotes);
            
        }
        
        consumerListener.close();
        consumer.close();
    }
    
    
}
