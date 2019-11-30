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
public class Rutas implements Cloneable{
    private Integer origen;
    private Integer destino;
    private List<Camino> caminos;
    private boolean expandido;
    
    @Override
    public Rutas clone() throws CloneNotSupportedException{
        List<Camino> caminosNuevos = new ArrayList<>();
        caminos.forEach((camino) -> {
            
            List<Integer> nuevoNodo = new ArrayList<>();
            camino.getNodos().forEach((nodo) -> {
                nuevoNodo.add(nodo);
            });
            Camino caminoNuevo = new Camino();
            caminoNuevo.setDistancia(camino.getDistancia());
            caminoNuevo.setNodos(nuevoNodo);
            caminosNuevos.add(caminoNuevo);
        });
        Rutas rutaNueva = (Rutas)super.clone();
        
        rutaNueva.setCaminos(caminosNuevos);
        return rutaNueva;
    }

    public Integer getOrigen() {
        return origen;
    }

    public void setOrigen(Integer origen) {
        this.origen = origen;
    }

    public Integer getDestino() {
        return destino;
    }

    public void setDestino(Integer destino) {
        this.destino = destino;
    }

    public List<Camino> getCaminos() {
        return caminos;
    }

    public void setCaminos(List<Camino> caminos) {
        this.caminos = caminos;
    }

    public boolean isExpandido() {
        return expandido;
    }

    public void setExpandido(boolean expandido) {
        this.expandido = expandido;
    }

    @Override
    public String toString() {
        return "Rutas{" + "origen=" + origen + ", destino=" + destino + ", caminos=" + caminos + ", expandido=" + expandido + '}';
    }
}
