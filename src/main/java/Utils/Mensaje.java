/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import static Utils.Utils.ID_LOTE_RECHAZADO;

/**
 *
 * @author smart
 */
public class Mensaje {
    // Atributos de la clase Mensaje
    private final int idLote;
    private final int idBuzon;
    private final Lote lote;

    
    /**
     * @brief Constructor de la clase mensaje que automaticamente crea un mensaje
     * para confirmar que el lote ha sido enviado a otro centro
     * @param idBuzon 
     */
    public Mensaje(int idBuzon) {
        this.idLote = ID_LOTE_RECHAZADO;
        this.idBuzon = idBuzon;
        this.lote = null;
    }
    
    /**
     * @brief Constructor que crea un mensaje con el lote a nulo
     * @param idLote
     * @param idBuzon 
     */
    public Mensaje(int idLote, int idBuzon) {
        this.idLote = idLote;
        this.idBuzon = idBuzon;
        this.lote = null;
    }

    /**
     * @brief Constructor que crea un mensaje con todos los datos
     * @param idLote
     * @param idBuzon
     * @param lote 
     */
    public Mensaje(int idLote, int idBuzon, Lote lote) {
        this.idLote = idLote;
        this.idBuzon = idBuzon;
        this.lote = lote;
    }

    public int getIdLote() {
        return idLote;
    }

    public int getIdBuzon() {
        return idBuzon;
    }

    public Lote getLote() {
        return lote;
    }
}
