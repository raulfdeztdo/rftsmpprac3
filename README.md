# Practica 3: Paso de mensajes JMS
### Practica a realizar
En esta practica vamos a realizar la simulación del envió y recepción de vacunas entre los almacenes médicos, que son los encargados de generar las vacunas *(“Pfinos”, “Antígua” o “AstroLunar”)* y los centros médicos que son las que van a recibir estos lotes de vacunas. Para ello utilizaremos JMS, como nos indican, el almacén tendrá que estar generando lotes continuamente hasta que deseemos finalizar la ejecución, cuando un centro pida un lote a un almacén, usaremos una clase *oyente*, que se hará cargo de enviar el lote generado por el almacén a aquel centro que primero lo consiga, o en su defecto enviar mensajes de rechazo para que sepan que no han conseguido ese lote.  
Nustro almacen estara publicando lotes cada 5 segundos, si el lote que ha generado nuestro almacen no ha conseguido enviarlo  a ningun centro, volvera a publicar el lote anterior, en caso de que si lo haya hecho creara un nuevo lote y este sera el que se publique, todo ello indefinidamente hasta que se pulse una tecla

### Análisis
#### Tipos de datos abstractos
Para la resolución de la practica necesitaremos diferenciar cual es el fabricante de la vacuna para ello haremos uso de un enumerado el cual nos indique los tres tipos de fabricantes posibles.
>  TDA Enum FabricanteVacuna { Pfinos, Antígua,  AstroLunar}

De esta forma ya podemos identificar quien es el fabricante de la vacuna, cosa que nos será de utilidad a la hora de crear un lote de vacunas, los cuales pertenecerán a un fabricante y tendrán una cantidad de dosis asignadas.

>**TDA Lote**
> - Atributos
>	 - iD: Entero
>	 - fabricante: FabricanteVacuna
>	 - numDosis: entero
 	
Para poder comunicarnos entre los centros y los almacenes tendremos que tener una estructura que soporte el intercambio de información, para ello nuestra estructura será un mensaje con los parámetros necesarios para que desde el centro y almacén podamos identificar los destinos *(Buzones)* de nuestros mensajes

>**TDA Mensaje**
> - Atributos
>	 - idLote: Entero
>	 - idBuzon: Entero
>	 - lote: Lote

#### Buzones
- Tendremos un buzon general, que sera donde el Almacen publique sus dosis y de donde las recogera el centro con la direccion TOPIC, este buzon sera un topico **Pub/Sub** que tiene la direccion *ssccdd.curso2021.rftsmp*
- Cada almacen tendra su propio buzon, por lo tanto tendremos tantos buzones como NUM_ALMACENES, estos seran de tipo **P2P** y tendran el valor QUEUE_ALMACEN *(ssccdd.curso2021.rftsmp.almacenID)*
- Cada centro tendra su propio buzon, por lo tanto tendremos tantos buzones como NUM_CENTROS, estos seran de tipo **P2P** y tendran el valor QUEUE_CENTRO *(ssccdd.curso2021.rftsmp.centroID)*

### Diseño
En cuanto al diseño de las TDA comunes podemos comentar que la clase **Lote** tiene getters de todos sus atributos, al igual que ocurre con la clase **Mensaje**

#### Lote
Constantes:
> - MAX_DOSIS_LOTE: Entero
> - MIN_DOSIS_LOTE: Entero

El constructor del lote ya lleva integrado la creacion de un numero de dosis entre 50 y 100

#### Mensaje
Constantes:
> - ID_LOTE_RECHAZADO: Entero

En cuanto al mensaje podemos hablar de sus constructores, que tenemos una sobrecarga de los mismos para usar uno u otro en funcion del tipo de mensaje que queramos generar

```
constructorMensajeRechazo(id)
    idLote -> ID_LOTE_RECHAZADO
    idBuzon -> id
    lote -> null
```

```
constructorMensajeSolicitud(idL, idB)
    idLote -> idL
    idBuzon -> idB
    lote -> null
```

```
constructorMensajeConfirmacion(idL, idB, l)
    idLote -> idL
    idBuzon -> idB
    lote -> l
```

Una vez definidos los elementos comunes tendremos que darle soporte a nuestro proceso para el [almacén médico](http://suleiman.ujaen.es:8011/rftsmp/rftsmpprac3/-/blob/master/AlmacenMedico.md) y para nuestro [centro médico](http://suleiman.ujaen.es:8011/rftsmp/rftsmpprac3/-/blob/master/CentroMedico.md)
