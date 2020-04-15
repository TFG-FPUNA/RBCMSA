/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package py.com.fp.una.rbcmsa.ilp;

/**
 *
 * @author Ivan
 */
public class Configuracion {
    private int k;
    private int fTotal;
    private int g;
    
    public Configuracion(int k, int fTotal, int g) {
        this.k = k;
        this.fTotal = fTotal;
        this.g = g;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public int getfTotal() {
        return fTotal;
    }

    public void setfTotal(int fTotal) {
        this.fTotal = fTotal;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }
}
