/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.grafos.model;

/**
 *
 * @author Richard
 */
public class Arista {
    private String identificador;
    private String verticeOrigen;
    private String verticeDestino;
    private String distancia;

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getVerticeOrigen() {
        return verticeOrigen;
    }

    public void setVerticeOrigen(String verticeOrigen) {
        this.verticeOrigen = verticeOrigen;
    }

    public String getVerticeDestino() {
        return verticeDestino;
    }

    public void setVerticeDestino(String verticeDestino) {
        this.verticeDestino = verticeDestino;
    }

    public String getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    @Override
    public String toString() {
        return "Arista{" + "identificador=" + identificador + ", verticeOrigen=" + verticeOrigen + ", verticeDestino=" + verticeDestino + ", distancia=" + distancia + '}';
    }
    
}
