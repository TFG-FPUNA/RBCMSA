/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.peticion.model;

/**
 *
 * @author Richard
 */
public class Peticion {
    private String pedido;
    private Integer landa;

    public String getPedido() {
        return pedido;
    }

    public void setPedido(String pedido) {
        this.pedido = pedido;
    }

    public Integer getLanda() {
        return landa;
    }

    public void setLanda(Integer landa) {
        this.landa = landa;
    }

    @Override
    public String toString() {
        return "Peticion{" + "pedido=" + pedido + ", landa=" + landa + '}';
    }
    
    
}
