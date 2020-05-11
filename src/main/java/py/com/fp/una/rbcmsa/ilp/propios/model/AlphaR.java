/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.ilp.propios.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Richard
 */
public class AlphaR {
    private List<List<String>> RLista = new ArrayList<>();
    private String alphaR;

    public List<List<String>> getRLista() {
        return RLista;
    }

    public void setRLista(List<List<String>> RLista) {
        this.RLista = RLista;
    }

    public String getAlphaR() {
        return alphaR;
    }

    public void setAlphaR(String alphaR) {
        this.alphaR = alphaR;
    }

    @Override
    public String toString() {
        return "AlphaR{" + "RLista=" + RLista + ", alphaR=" + alphaR + '}';
    }
    
    
    
}
