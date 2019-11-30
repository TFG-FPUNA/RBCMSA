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
public class CantidadDeExpanciones {
    private Integer cantidadExpandidas;

    public Integer getCantidadExpandidas() {
        return cantidadExpandidas;
    }

    public void setCantidadExpandidas(Integer cantidadExpandidas) {
        this.cantidadExpandidas = cantidadExpandidas;
    }

    @Override
    public String toString() {
        return "RespuestaGenerarExpanciones{" + ", cantidadExpandidas=" + cantidadExpandidas + '}';
    }
    
    
    
}
