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
public class Gen {

    public int[] individio;
    public double fitness;

    public Gen() {
    }

    public int[] getIndividio() {
        return individio;
    }

    public void setIndividio(int[] individio) {
        this.individio = individio;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    @Override
    public String toString() {
        return "Gen{" + "individio=" + individio + ", fitness=" + fitness + '}';
    }

}
