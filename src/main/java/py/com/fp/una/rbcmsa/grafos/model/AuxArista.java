/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.grafos.model;

import java.util.List;

/**
 *
 * @author Richard
 */
public class AuxArista {
    List<Integer> aristasInvolucradas;
    List<Integer> posicionFS;

    public List<Integer> getAristasInvolucradas() {
        return aristasInvolucradas;
    }

    public void setAristasInvolucradas(List<Integer> aristasInvolucradas) {
        this.aristasInvolucradas = aristasInvolucradas;
    }

    public List<Integer> getPosicionFS() {
        return posicionFS;
    }

    public void setPosicionFS(List<Integer> posicionFS) {
        this.posicionFS = posicionFS;
    }

    @Override
    public String toString() {
        return "AuxArista{" + "aristasInvolucradas=" + aristasInvolucradas + ", posicionFS=" + posicionFS + '}';
    }
    
}
