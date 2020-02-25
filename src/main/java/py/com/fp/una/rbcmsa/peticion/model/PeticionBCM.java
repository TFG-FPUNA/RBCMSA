/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.peticion.model;

import java.util.List;
import py.com.fp.una.rbcmsa.grafos.model.Camino;
import py.com.fp.una.rbcmsa.tr.model.TRBCM;

/**
 *
 * @author Richard
 */
public class PeticionBCM {
    private Peticion peticionOriginal;
    private List<Camino> caminos;
    private List<TRBCM> trfinal;
    private Integer FSMayor;

    public Peticion getPeticionOriginal() {
        return peticionOriginal;
    }

    public void setPeticionOriginal(Peticion peticionOriginal) {
        this.peticionOriginal = peticionOriginal;
    }

    public List<Camino> getCaminos() {
        return caminos;
    }

    public void setCaminos(List<Camino> caminos) {
        this.caminos = caminos;
    }

    public List<TRBCM> getTrfinal() {
        return trfinal;
    }

    public void setTrfinal(List<TRBCM> trfinal) {
        this.trfinal = trfinal;
    }

    public Integer getFSMayor() {
        return FSMayor;
    }

    public void setFSMayor(Integer FSMayor) {
        this.FSMayor = FSMayor;
    }

    @Override
    public String toString() {
        return "PeticionBCM{" + "peticionOriginal=" + peticionOriginal + ", caminos=" + caminos + ", trfinal=" + trfinal + ", FSMayor=" + FSMayor + '}';
    }
    
}
