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

    public int[] individuo;
    public double fitness;

    public Gen() {
    }

    public int[] getIndividuo() {
        return individuo;
    }

    public void setIndividuo(int[] individuo) {
        this.individuo = individuo;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    @Override
    public String toString() {
        return "Gen{" + "individio=" + individuo + ", fitness=" + fitness + '}';
    }

}
