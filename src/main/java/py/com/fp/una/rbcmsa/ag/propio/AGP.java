/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.ag.propio;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import py.com.fp.una.rbcmsa.algoritmos.AlgoritmosAsignacionEspectro;
import py.com.fp.una.rbcmsa.grafos.model.Grafo;
import py.com.fp.una.rbcmsa.peticion.model.PeticionBCM;

/**
 *
 * @author Alexander
 */
public class AGP {

    @Inject
    Operadores operadores;

    @Inject
    AlgoritmosAsignacionEspectro algoritmosAsignacionEspectro;

    public void permute(int[] arr) {
        permuteHelper(arr, 0);
    }

    private void permuteHelper(int[] arr, int index) {
        //int longitud = arr.length;
        //List<int[]> permutaciones = new ArrayList<>();
        if (index >= arr.length - 1) { //If we are at the last element - nothing left to permute
            //System.out.println(Arrays.toString(arr));
            //Print the array
            //int permutacion[]; //declarando un array
            //permutacion = new int[longitud]; // asignando memoria al array
            System.out.print("[");
            for (int i = 0; i < arr.length - 1; i++) {
                System.out.print(arr[i] + ", ");
                //permutacion[i] = arr[i];
            }
            if (arr.length > 0) {
                System.out.print(arr[arr.length - 1]);
            }
            System.out.println("]");
            //permutaciones.add(permutacion);
            return;
        }

        for (int i = index; i < arr.length; i++) { //For each index in the sub array arr[index...end]

            //Swap the elements at indices index and i
            int t = arr[index];
            arr[index] = arr[i];
            arr[i] = t;

            //Recurse on the sub array arr[index+1...end]
            permuteHelper(arr, index + 1);

            //Swap the elements back
            t = arr[index];
            arr[index] = arr[i];
            arr[i] = t;
        }
//        System.out.println("Permutaciones");
//        for (int[] permutacion : permutaciones) {
//            for (int i = 0; i < permutacion.length; i++) {
//                System.out.println(permutacion[i]+",");
//            }
//            System.out.println("\n");
//        }
    }

    public List<Gen> inicializarPoblacion(int cantidadPoblacion, int cantidadComponentes) {
        //System.out.println("estoy inicilizando la poblacion");
        List<Gen> poblacion = new ArrayList<>();

        for (int i = 0; i < cantidadPoblacion; i++) {
            Gen gen = new Gen();
            int[] individuo = new int[cantidadComponentes];
            //llamar a generar permutacion
            gen.setIndividio(individuo);
            poblacion.add(gen);

        }
        return poblacion;
    }

    /*torneo binario*/
    public Gen seleccionarPoblacion(int limite, List<Gen> poblacion) {
        int indicePadre1 = (int) (Math.random() * (limite));
        int indicePadre2 = (int) (Math.random() * (limite));

        Gen padre1 = poblacion.get(indicePadre1);
        Gen padre2 = poblacion.get(indicePadre2);

        if (padre1.getFitness() > padre2.getFitness()) {
            return padre1;
        } else {
            return padre2;
        }

    }

    /*Por si se desee una seleccion de padres totalmente random*/
 /*public int[] seleccionarPadres(int cantidadPoblacion) {
        int[] padres = new int[2];
        padres[0] = (int) (Math.random() * (cantidadPoblacion + 1));
        padres[1] = (int) (Math.random() * (cantidadPoblacion + 1));
        return padres;
    }*/
    public Solucion algoritmoGenetico(int cantidadGeneraciones, int cantidadPoblacion,
            List<PeticionBCM> peticionesFinales, Grafo grafo, int cantidadFS, double tamanhoFS,
            Double limite) throws Exception {
        //System.out.println("empiezo el genetico");

        //Inicializar solucion;
        Solucion solucion = new Solucion();
        solucion.setFitness(Integer.MAX_VALUE);

        //Inicializar población de cromosomas al azar.
        List<Gen> poblacion = inicializarPoblacion(cantidadPoblacion, peticionesFinales.size());

        //Aunque la cantidad de generación <criterio de parada
        for (int i = 0; i < cantidadGeneraciones; i++) {
            //para cada cromosoma en la población, el cálculo de los cromosomas valor de fitness.
            for (int j = 0; j < cantidadPoblacion; j++) {
                /*aplicar conversion del gen para poder usar el algoritmo*/
                Gen individuoDeTurno = poblacion.get(j);

                int resultadoBFMRA = Integer.MAX_VALUE;
                try {
                    resultadoBFMRA = algoritmosAsignacionEspectro.BFMRA(individuoDeTurno.getIndividio(), peticionesFinales, grafo, cantidadFS, tamanhoFS);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    //logger.error("Error:", ex);
                }

                individuoDeTurno.setFitness(resultadoBFMRA);

                // que hago cuando empatan???? decido por que ????
                if (individuoDeTurno.getFitness() < solucion.getFitness()) {
                    solucion.setFitness(individuoDeTurno.getFitness());
                    solucion.setIndividio(individuoDeTurno.getIndividio());
                }

            }

            //Collections.sort(poblacion, (s1, s2) -> Double.compare(s1.getFitness(), s2.getFitness()));
            //reproducirse población entera.
            int numeroDescendientes = 0;
            //mientras que el número total de descendientes <tamaño de la población
            List<Gen> nuevaPoblacion = new ArrayList<>();

            while (numeroDescendientes < cantidadPoblacion) {
                //System.out.println("estoy generando la nueva poblacion");
                //seleccionar dos padres.
                Gen padre1 = seleccionarPoblacion(cantidadPoblacion, poblacion);
                Gen padre2 = seleccionarPoblacion(cantidadPoblacion, poblacion);

                //aplicar operador de cruce de los dos padres con probabilidad, 0.75.
                //Si aplica cruzado
                Gen hijo1 = new Gen();
                Gen hijo2 = new Gen();

                if (Math.random() < 0.75) {
                    //System.out.println("se va a realizar el cruce");
                    List<int[]> descendientes = operadores.orderCrosover(padre1.getIndividio(), padre2.getIndividio());

                    int[] descendiente1 = descendientes.get(0);
                    int[] descendiente2 = descendientes.get(1);

                    hijo1.setIndividio(descendiente1);
                    hijo2.setIndividio(descendiente2);
                } else {
                    //System.out.println("NO se va a realizar el cruce");
                    hijo1.setIndividio(padre1.getIndividio());
                    hijo2.setIndividio(padre2.getIndividio());
                }
                numeroDescendientes += 2;

                //para cada posición de genes en cada descendiente
                operadores.mutarIndividuo(hijo1);
                operadores.mutarIndividuo(hijo2);

                nuevaPoblacion.add(hijo1);
                nuevaPoblacion.add(hijo2);
            }
            //ver si no hay drama con la referenciacion
            poblacion = nuevaPoblacion;
        }
        return solucion;

    }

}
