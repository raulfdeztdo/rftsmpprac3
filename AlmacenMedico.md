## Almacén Medico
### Análisis
Para poder tener un contraste de los datos del listener con los del almacen he considerado necesario crear una clase auxiliar Paquete, esta nos permite saber cuando hemos enviado un lote o no, tendra como atributos el lote que ha generado el almacen y dos contadores para saber el numero de solicitudes rechazadas y el numero de lotes que hemos enviado.

>**TDA Paquete**
> - Atributos
>	 - lote: Lote
>	 - solicitudesRechazadas: Entero
>	 - lotesEnviados: Entero
> - Metodos
>	 - incSolicitudesRechazadas()
>	 - incLotesEnviados()

Esta clase tiene los getter de todos sus atributos para poder saber el valor que tienen y ademas tiene el setter del lote para poder ponerlo a nulo cuando el lote ha sido enviado y para asignarle un valor cuando el almacen crea un nuevo lote. Los metodos inc... simplemente lo que hacen es sumar uno al contador de solicitudes y lotes

#### Proceso AlmacenMedico
El almacen tendra los siguientes atributos para poder tener un control sobre los datos que nos especifica la practica
Atributos:
> - iD: Entero
> - contadorIDs: Variable atomica para asignar iD
> - paquete: Paquete
> - lotesGenerados: Lotes\[] // Almacena los lotes generados por el almacen
> - vecesOfertados: Entero\[] // Cuenta las veces que ha sido ofertado el lote i-esimo


Nuestro almacén guardara información sobre su identificador, y sobre el lote que esta ofertando en este momento. El almacén tendrá dos tipos de buzones, para hacer las publicaciones sobre los lotes que ha creado hará uso del buzón TOPIC que funcionara como **Pub/Sub (Public/Subscription)**
> destination: Buzón (ssccdd.curso2021.rftsmp)

También hará uso de otro buzón, de forma asíncrona para saber cuando un centro medico ha solicitado un lote a este almacén. en este caso funcionara como un **PTP (Peer to Peer)**. Cada almacén tendrá un buzón propio de este estilo.
> destinationAsincrono: Buzón (ssccdd.curso2021.rftsmp.almacen(IDAlmacen))

#### Proceso ListenerAlmacen
Esta clase sera la que haga de mediadora entre los almacenes y centros, esta recibira solicitude de los centros y modificara el lote *(Que fue generado por el almacen)* para entregarselo a dicho centro, si el centro le ha pedido un lote que ya no esta en su posesion le enviara un mensaje indicandole que no se le ha podido entegar dicho lote. sus atributos seran los que le pasemos a traves del almacen y son los siguientes:

El almacen tendra los siguientes atributos para poder tener un control sobre los datos que nos especifica la practica
Atributos:
> - iD: Entero
> - Paquete: Paquete

Este proceso tendra creado un numero de buzones **NUM_CENTROS** para poder enviar los mensajes de aceptacion del lote o rechazo y que asi ya, cada centro pueda procesar su mensaje enviado, por lo tanto el destino del buzon es 
> destination\[]: Buzón\[] (ssccdd.curso2021.rftsmp.centro(IDCentro))

### Diseño
#### Proceso AlmacenMedico
Constantes:
> - MAX_TIEMPO_ESPERA_ALMACEN: Entero
> - MIN_TIEMPO_ESPERA_ALMACEN: Entero
> - TIEMPO_ESPERA_PUBLICACION: Entero
> - VALOR_GENERACION: entero

Pseudocodigo del proceso AlmacenMedico

```
ejecucion_proceso()
{

//Variables locales
Bool interrumpido
esperarTiempo(aleatorio(MAX_TIEMPO_ESPERA_ALMACEN - MIN_TIEMPO_ESPERA_ALMACEN))

MIENTRAS NO interrumpido
	SI Lote NO EXISTE ENTONCES
		Lote -> crearLote()
	FIN SI
	
	mensaje -> crearMensaje()
	send(almacen[id],mensaje)
	
	esperarTiempo(TIEMPO_ESPERA_PUBLICACION)
FIN MIENTRAS	
}
```

#### Proceso ListenerAlmacen
Constantes:
> - NUM_CENTROS: CadenaCaracteres
> - QUEUE_CENTRO: CadenaCaracteres

Pseudocodigo del proceso ListenerAlmacen

```
ejecucion_proceso()
{

//Variables locales
Mensaje respuesta


recive(almacen[id], mensajeRecibido)
id -> mensajeRecibido.getIdBuzon

SI paquete NO VACIO Y mensajeRecibido.id IGUAL paquete.id ENTONCES
	respuesta -> mensajeConfirmacion()
	incLotesEnviados()
SI NO ENTONCES
	respuesta -> mensajeRechazo()
	incSolicitudesRechazadas()
FIN SI

send(centro[id], respuesta)

}
```
