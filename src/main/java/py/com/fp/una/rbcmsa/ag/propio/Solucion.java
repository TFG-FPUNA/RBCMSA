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
    int indice;
    int rechazados;
    double defracmencion;

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public int getGeneracion() {
        return generacion;
    }

    public void setGeneracion(int generacion) {
        this.generacion = generacion;
    }

    public int getRechazados() {
        return rechazados;
    }

    public void setRechazados(int rechazados) {
        this.rechazados = rechazados;
    }

    public double getDefracmencion() {
        return defracmencion;
    }

    public void setDefracmencion(double defracmencion) {
        this.defracmencion = defracmencion;
    }

    @Override
    public String toString() {
        return "Solucion{" + "generacion=" + generacion + ", indice=" + indice + ", rechazados=" + rechazados + ", defracmencion=" + defracmencion + '}';
    }
        
}
