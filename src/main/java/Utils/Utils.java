/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

/**
 *
 * @author smart
 */
public class Utils {
    
    public static final String TOPIC = "ssccdd.curso2021.rftsmp";
    public static final String QUEUE_ALMACEN = "ssccdd.curso2021.rftsmp.almacen";
    public static final String QUEUE_CENTRO = "ssccdd.curso2021.rftsmp.centro";
    public static final String BROKER_URL = "tcp://suleiman.ujaen.es:8018";
    
    public static final int VALOR_GENERACION = 101; // Valor para generar un FabricanteVacuna aleatorio
    public static final int MIN_DOSIS_LOTE = 50;  // Numero minimo de dosis por lote
    public static final int MAX_DOSIS_LOTE = 100; // Numero maximo de dosis por lote
    public static final int MIN_TIEMPO_ESPERA_ALMACEN = 5; // Tiempo de espera minimo de un almacen
    public static final int MAX_TIEMPO_ESPERA_ALMACEN = 8; // Tiempo de espera maximmo de un almacen
    public static final int TIEMPO_ESPERA_PUBLICACION = 5; // Tiempo que debe de pasar para volver a realizar una publicacion
    public static final int ID_LOTE_RECHAZADO = -1; // Identidficador que se le da al lote cuando no ha sido entregado a otro centro
    public static final int NUM_ALMACENES = 3;
    public static final int NUM_CENTROS = 3;
    public static final int TIEMPO_ESPERA_HILO_PRINCIPAL = 1;
    public static final int NUM_MAX_LOTES = 10; // Numero maximo de lotes que tiene que recibir cada centro
    public static final int TIEMPO_ESPERA_POR_DOSIS = 100; // Tiempo de espera de 100 milisegundos por dosis
    
    public enum FabricanteVacuna {
        PFINOS(33), ANTIGUA(66), ASTROLUNAR(100);

        private final int valor;

        private FabricanteVacuna(int valor) {
            this.valor = valor;
        }

        /**
         * Obtenemos un tipo de vacuna relacionada con su valor de generación
         *
         * @param valor, entre 0 y 100, de generación
         * @return el laboratorio asociado con el valor de generación
         */
        public static FabricanteVacuna getFabricante(int valor) {
            FabricanteVacuna resultado = null;
            FabricanteVacuna[] laboratorios = FabricanteVacuna.values();
            int i = 0;

            while ((i < laboratorios.length) && (resultado == null)) {
                if (laboratorios[i].valor >= valor) {
                    resultado = laboratorios[i];
                }

                i++;
            }

            return resultado;
        }

        /**
         * Obtenemos un tipo de vacuna relacionada con su valor ordinal
         *
         * @param ordinal, entre 0 y (TOTAL_TIPOS_FABRICANTES - 1)
         * @return el laboratorio asociado con el valor ordinal
         */
        public static FabricanteVacuna getFabricantePorOrdinal(int ordinal) {
            return FabricanteVacuna.values()[ordinal];
        }
    }

}
