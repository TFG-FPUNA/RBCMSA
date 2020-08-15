/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.peticion.model;

import java.util.ArrayList;
import java.util.List;
import py.com.fp.una.rbcmsa.grafos.model.Camino;

/**
 *
 * @author Richard
 */
public class PeticionBCM implements Cloneable {

    private Peticion peticionOriginal;
    private List<CaminoTR> caminosTR;
    private Integer FSMenor;
    private Integer FSMayor;

    @Override
    public PeticionBCM clone() throws CloneNotSupportedException {
        PeticionBCM peticion = (PeticionBCM) super.clone();
        List<CaminoTR> caminosTRNuevos = new ArrayList<>();
        for (CaminoTR caminoTRExaminado : caminosTR) {
            CaminoTR caminoTRNuevo = new CaminoTR();
            Camino caminoNuevo = new Camino();
            caminoNuevo.setAnchoBandaAsignable(caminoTRExaminado.getCamino().getAnchoBandaAsignable());
            caminoNuevo.setCantidadFsDisponibles(caminoTRExaminado.getCamino().getCantidadFsDisponibles());
            caminoNuevo.setDistancia(caminoTRExaminado.getCamino().getDistancia());
            
            List<Integer> nodosNuevos = new ArrayList<>();
            caminoTRExaminado.getCamino().getNodos().stream().map((nodo) -> {
                Integer nodoNuevo = new Integer(nodo);
                return nodo;
            }).forEachOrdered((nodo) -> {
                nodosNuevos.add(nodo);
            });
            caminoNuevo.setNodos(nodosNuevos);
            
            caminoTRNuevo.setCamino(caminoNuevo);
            
            caminoTRNuevo.setTrfinal(caminoTRExaminado.getTrfinal());
            caminosTRNuevos.add(caminoTRNuevo);

        }
        peticion.setCaminosTR(caminosTRNuevos);
        return peticion;
    }

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

    public Integer getFSMayor() {
        return FSMayor;
    }

    public void setFSMayor(Integer FSMayor) {
        this.FSMayor = FSMayor;
    }

    @Override
    public String toString() {
        return "PeticionBCM{" + "peticionOriginal=" + peticionOriginal + ", caminosTR=" + caminosTR + ", FSMenor=" + FSMenor + ", FSMayor=" + FSMayor + '}';
    }

}
