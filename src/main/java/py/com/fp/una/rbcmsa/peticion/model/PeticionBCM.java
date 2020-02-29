/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.peticion.model;

import java.util.List;

/**
 *
 * @author Richard
 */
public class PeticionBCM {
    private Peticion peticionOriginal;
    private List<CaminoTR> caminosTR;
    private Integer FSMenor;

    public Peticion getPeticionOriginal() {
        return peticionOriginal;
    }

    public void setPeticionOriginal(Peticion peticionOriginal) {
        this.peticionOriginal = peticionOriginal;
    }

    public List<CaminoTR> getCaminosTR() {
        return caminosTR;
    }

    public void setCaminosTR(List<CaminoTR> caminosTR) {
        this.caminosTR = caminosTR;
    }

    public Integer getFSMenor() {
        return FSMenor;
    }

    public void setFSMenor(Integer FSMenor) {
        this.FSMenor = FSMenor;
    }

    @Override
    public String toString() {
        return "PeticionBCM{" + "peticionOriginal=" + peticionOriginal + ", caminosTR=" + caminosTR + ", FSMenor=" + FSMenor + '}';
    }

    
    
}
