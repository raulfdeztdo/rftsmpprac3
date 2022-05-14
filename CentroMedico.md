## Centro Medico

### Análisis

Nuestro centro medico recibirás los lotes del almacén medico con un numero determinado de dosis por lote, para la comunicación con el almacén hemos utilizado dos buzones, primero recibiremos la oferta de forma síncrona en el buzón común, luego contestaremos a ella y por ultimo recibiremos el lote y se adminsitraran las dosis dependiendo de si el id del lote es valido o no.

#### Proceso CentroMedico

El centro tendrá los siguientes atributos para poder tener un control sobre los datos que nos especifica la practica

Atributos:

>- iD: Entero
>- lote: ArrayList de lote // Almacena los lotes recibidos del almacén
>- numLotes: Entero para guardar el numero total de lotes recibidos
>- numLotesPerdidos: Entero para guardar los numero de lotes perdidos


Como hemos dicho el centro medico tendrá tres buzones, uno para recibir las ofertas de lotes del almacén medico que usara un buzón TOPIC que funcionará como **Pub/Sub (Public/Subscription)**.

> destination: Buzón (ssccdd.curso2021.rftsmp)

También hará uso de dos buzones, uno para  enviar la contestación a la oferta recibida en la que se enviará en el mensaje el ID del lote ofertado y el ID de nuestro buzón y otro para recibir un mensaje que incluirá el lote y dependiendo del ID del lote recibido se tramitará de un forma u otra, estos funcionaran como un **PTP (Peer to Peer)**.

> destinationContestacion: Buzón (ssccdd.curso2021.rftsmp.almacen(IDAlmacen))
> 
> destinationCentro: Buzón (ssccdd.curso2021.rftsmp.centro(IDCentro))
  

### Diseño

#### Proceso CentroMedico

Constantes:

> - NUM_MAX_LOTES: Entero
> - TIEMPO_ESPERA_POR_DOSIS: Entero

Pseudocodigo del proceso CentroMedico
```
ejecucion_proceso() {

while (numLotes < NUM_MAX_LOTES)

	mensajeRecibido = recive(centro[id], msg)

	send(almacen[id], msgDevuelto)

	loteRecibido = recive(centro[id], lote)

	if ( LoreRecibido != null AND loteRecibido.ID > -1)
		lote[].add(loteRecibido)
		numDosis = lote[numLotes].Dosis
		numLotes++
		for 0 to numDosis
			sleep(TIEMPO_ESPERA_POR_DOSIS)
		Fin for
	else
		numLotesPerdidos++
	Fin if
Fin while

}

```
