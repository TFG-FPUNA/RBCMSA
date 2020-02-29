/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.peticion.model;

import py.com.fp.una.rbcmsa.grafos.model.Camino;
import py.com.fp.una.rbcmsa.tr.model.TRBCM;

/**
 *
 * @author Richard
 */
public class CaminoTR {
    private Camino camino;
    private TRBCM trfinal;

    public Camino getCamino() {
        return camino;
    }

    public void setCamino(Camino camino) {
        this.camino = camino;
    }

    public TRBCM getTrfinal() {
        return trfinal;
    }

    public void setTrfinal(TRBCM trfinal) {
        this.trfinal = trfinal;
    }

    @Override
    public String toString() {
        return "CaminoTR{" + "camino=" + camino + ", trfinal=" + trfinal + '}';
    }
    
}
