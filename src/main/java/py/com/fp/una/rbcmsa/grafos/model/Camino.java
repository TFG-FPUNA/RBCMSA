/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.grafos.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Richard
 */
public class Camino {
    List<Integer> nodos;
    Integer distancia;
    Integer cantidadFsDisponibles;
    Integer anchoBandaAsignable;

    public Camino() {
        this.nodos = new ArrayList<>();
    }

    public List<Integer> getNodos() {
        return nodos;
    }

    public void setNodos(List<Integer> nodos) {
        this.nodos = nodos;
    }

    public void setDistancia(Integer distancia) {
        this.distancia = distancia;
    }

    public Integer getDistancia() {
        return distancia;
    }

    public Integer getCantidadFsDisponibles() {
        return cantidadFsDisponibles;
    }

    public void setCantidadFsDisponibles(Integer cantidadFsDisponibles) {
        this.cantidadFsDisponibles = cantidadFsDisponibles;
    }

    public Integer getAnchoBandaAsignable() {
        return anchoBandaAsignable;
    }

    public void setAnchoBandaAsignable(Integer anchoBandaAsignable) {
        this.anchoBandaAsignable = anchoBandaAsignable;
    }

    @Override
    public String toString() {
        return "Camino{" + "nodos=" + nodos + ", distancia=" + distancia + ", cantidadFsDisponibles=" + cantidadFsDisponibles + ", anchoBandaAsignable=" + anchoBandaAsignable + '}';
    }
    
}
