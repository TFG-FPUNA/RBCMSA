/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.ag.propio;

/**
 *
 * @author Alexander
 */
public class Solucion extends Gen{
    int generacion;

    public int getGeneracion() {
        return generacion;
    }

    public void setGeneracion(int generacion) {
        this.generacion = generacion;
    }

    @Override
    public String toString() {
        return "Solucion{" + "generacion=" + generacion + '}';
    }
    
}
