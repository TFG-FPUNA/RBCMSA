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
    private Integer distancia;
    private boolean[] SP;
    private int cantidadSP;

    public Arista(int cantidadSP) {
        this.SP = new boolean[cantidadSP];
        this.cantidadSP =  cantidadSP;
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

    public Integer getDistancia() {
        return distancia;
    }

    public void setDistancia(Integer distancia) {
        this.distancia = distancia;
    }

    public boolean[] getSP() {
        return SP;
    }

    public void setSP(boolean[] SP) {
        this.SP = SP;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public int getCantidadSP() {
        return cantidadSP;
    }

    public void setCantidadSP(int cantidadSP) {
        this.cantidadSP = cantidadSP;
    }

    @Override
    public String toString() {
        return "Arista{" + "identificador=" + identificador + ", verticeOrigen=" + verticeOrigen + ", verticeDestino=" + verticeDestino + ", distancia=" + distancia + ", SP=" + SP + ", cantidadSP=" + cantidadSP + '}';
    }

    
    
}
