/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import Utils.Utils.FabricanteVacuna;
import static Utils.Utils.MAX_DOSIS_LOTE;
import static Utils.Utils.MIN_DOSIS_LOTE;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author smart
 */
public class Lote {
    // Atributos de la clase Lote
    private final int iD;
    private final FabricanteVacuna fabricante;
    private final int numDosis;

    public Lote(int iD, FabricanteVacuna fabricante) {
        this.iD = iD;
        this.fabricante = fabricante;
        // Generacion de un valor aleatorio de dosis
        ThreadLocalRandom.current();
        this.numDosis = ThreadLocalRandom.current().nextInt(MAX_DOSIS_LOTE - MIN_DOSIS_LOTE) + MIN_DOSIS_LOTE;
    }

    public int getiD() {
        return iD;
    }

    public FabricanteVacuna getFabricante() {
        return fabricante;
    }

    public int getNumDosis() {
        return numDosis;
    }

    /**
     * Devuelve una cadena con la información de la clase
     * @return Cadena de texto con la información
     */
    @Override
    public String toString() {
        return "Lote{" + "iD=" + iD + ", fabricante=" + fabricante + ", numDosis=" + numDosis + '}';
    }
    
}
