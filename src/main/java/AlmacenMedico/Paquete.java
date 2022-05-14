/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AlmacenMedico;

import Utils.Lote;

/**
 *
 * @author smart
 */
public class Paquete {
    // Atributos de la clase paquete
    private Lote lote;
    private int solicitudesRechazadas;
    private int lotesEnviados;
    
    /**
     * @brief Constructor por defecto de la clase paquete
     */
    public Paquete() {
        this.lote = null;
        this.solicitudesRechazadas = 0;
        this.lotesEnviados = 0;
    }

    /**
     * Getter del lote
     * @return Devuelve el lote que contiene la clase paquete
     */
    public Lote getLote() {
        return lote;
    }

    public int getSolicitudesRechazadas() {
        return solicitudesRechazadas;
    }

    public int getLotesEnviados() {
        return lotesEnviados;
    }

    
    /**
     * @brief Setter del lote
     * @param lote Lote que le queremos asignar al paquete
     */
    public void setLote(Lote lote) {
        this.lote = lote;
    }
    
    public void incSolicitudesRechazadas(){
        this.solicitudesRechazadas++;
    }

    public void incLotesEnviados(){
        this.lotesEnviados++;
    }
    
    
}
