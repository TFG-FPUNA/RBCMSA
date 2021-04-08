/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.ag.propio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javax.inject.Inject;
import py.com.fp.una.rbcmsa.algoritmos.AlgoritmosAsignacionEspectro;
import py.com.fp.una.rbcmsa.grafos.model.Grafo;
import py.com.fp.una.rbcmsa.peticion.model.PeticionBCM;
import static py.com.fp.una.rbcmsa.tr.model.Constantes.CRITERIO_PARADA;

/**
 *
 * @author Alexander
 */
public class AGP {

    @Inject
    Operadores operadores;

    @Inject
    AlgoritmosAsignacionEspectro algoritmosAsignacionEspectro;
    
//    @Inject
//    Logger log;
    public void permute(int[] arr, List<int[]> permutaciones, int cantidadElementos, int limite) {
        permuteHelper(arr, 0, permutaciones, cantidadElementos, limite);
    }

    private void permuteHelper(int[] arr, int index, List<int[]> permutaciones, int cantidadElemento, int limite) {
        if (index >= arr.length - 1) { //If we are at the last element - nothing left to permute
            int[] permutacion = new int[cantidadElemento]; //declarando un array

            for (int i = 0; i < arr.length - 1; i++) {
                permutacion[i] = arr[i];
            }
            if (arr.length > 0) {
                permutacion[arr.length - 1] = arr[arr.length - 1];
            }
            if (permutaciones.size() < limite) {
                permutaciones.add(permutacion);

            } else {
                return;// si hay kilombo con la permutacion quitar el else
            }

            return;
        }

        for (int i = index; i < arr.length; i++) { //For each index in the sub array arr[index...end]

            //Swap the elements at indices index and i
            int t = arr[index];
            arr[index] = arr[i];
            arr[i] = t;

            //Recurse on the sub array arr[index+1...end]
            permuteHelper(arr, index + 1, permutaciones, cantidadElemento, limite);

            //Swap the elements back
            t = arr[index];
            arr[index] = arr[i];
            arr[i] = t;
        }
    }
    private int[] generarPermutacionIdentidad(int cantidadPeticiones) {
        int[] identidad = new int[cantidadPeticiones];
        for (int i = 0; i < cantidadPeticiones; i++) {
            identidad[i] = i;
        }
        return identidad;
    }

    public List<Gen> inicializarPoblacion(int cantidadPoblacion, int cantidadComponentes) {
        //System.out.println("estoy inicilizando la poblacion");
        int[] identidad = generarPermutacionIdentidad(cantidadComponentes);

        List<Gen> poblacion = new ArrayList<>();
        for (int i = 0; i < cantidadPoblacion; i++) {
            int[] mutarContinuamente = identidad;
            for (int j = 0; j < cantidadComponentes; j++) {
                mutarContinuamente = operadores.mutacion(mutarContinuamente, j, true);
            }
            Gen gen = new Gen();
            gen.setIndividuo(mutarContinuamente);
            poblacion.add(gen);
        }
        return poblacion;
    }
    public void imprimirPoblacion(List<Gen> poblacion){
        for (Gen gen : poblacion) {
            System.out.print("");
            for (int i : gen.getIndividuo()) {
                System.out.print(i + ", ");
            }
            System.out.println("");
            System.out.println("");
            System.out.println("");
            System.out.println("");
        }
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
            List<PeticionBCM> peticionesFinales, Grafo grafo, int cantidadFS, double tamanhoFS, double mutacion)
            throws Exception {
        //System.out.println("empiezo el genetico");

        //Inicializar solucion;
        Solucion solucion = new Solucion();
        solucion.setFitness(Integer.MAX_VALUE);
        solucion.setGeneracion(-1);

        //Inicializar población de cromosomas al azar.
        List<Gen> poblacion = inicializarPoblacion(cantidadPoblacion, peticionesFinales.size());

        //Aunque la cantidad de generación <criterio de parada
        //si no converge en 10 suelta
        int contadorConvergencia = 0;
        boolean cambioSolucion = false;
        for (int i = 0; i < cantidadGeneraciones; i++) {
            //System.out.println("Generacion: " + i);
            //imprimirPoblacion(poblacion);
            //para cada cromosoma en la población, el cálculo de los cromosomas valor de fitness.
            for (int j = 0; j < cantidadPoblacion; j++) {
                /*aplicar conversion del gen para poder usar el algoritmo*/
                Gen individuoDeTurno = poblacion.get(j);

                int resultadoBFMRA = Integer.MAX_VALUE;
                int resultadoIndice = Integer.MAX_VALUE;
                int rechazados = 0;
                double defracmentacion = 0.0;
                String resultadoString;
                try { 
                            resultadoString = algoritmosAsignacionEspectro.BFMRA(individuoDeTurno.getIndividuo(), peticionesFinales, grafo, cantidadFS, tamanhoFS);
                            String[] split = resultadoString.split("-");
                            resultadoBFMRA = Integer.parseInt(split[0]);
                            resultadoIndice = Integer.parseInt(split[1]);
                            rechazados = Integer.parseInt(split[2]);
                            defracmentacion = Double.parseDouble(split[3]);
                            
                } catch (Exception ex) {
                    ex.printStackTrace();
                    //logger.error("Error:", ex);
                }
//                System.out.print("Individuo: {");
//                for (int k : individuoDeTurno.getIndividuo()) {
//                    System.out.print(k +",");
//                }
//                System.out.print("}");
//                System.out.println("");
//                System.out.println("Resultado: " + resultadoBFMRA);
//                System.out.println("Indice: " + resultadoIndice);
                poblacion.get(j).setFitness(resultadoBFMRA);
                // que hago cuando empatan???? decido por que ????
                if (resultadoBFMRA < solucion.getFitness()) {
                    solucion.setFitness(resultadoBFMRA);
                    solucion.setIndividuo(individuoDeTurno.getIndividuo());
                    solucion.setGeneracion(i);
                    solucion.setIndice(resultadoIndice);
                    solucion.setRechazados(rechazados);
                    solucion.setDefracmencion(defracmentacion);
                    cambioSolucion = true;
                }else{
                    cambioSolucion = false;
                }

            }

            if (!cambioSolucion) {
                contadorConvergencia++;
            } else {
                contadorConvergencia = 0;
            }
            if (contadorConvergencia == CRITERIO_PARADA) {
                return solucion;
            }

            Collections.sort(poblacion, (s1, s2) -> Double.compare(s1.getFitness(), s2.getFitness()));
            //imprimirPoblacion(poblacion);
            //reproducirse población entera.
            int numeroDescendientes = 0;
            //mientras que el número total de descendientes <tamaño de la población
            List<Gen> nuevaPoblacion = new ArrayList<>();
            nuevaPoblacion.add(poblacion.get(0));
            nuevaPoblacion.add(poblacion.get(1));
            numeroDescendientes += 2;

            // restamos 2 porque los dos mejores de la poblacion actual pasan a la siguiente poblacion
            while (numeroDescendientes < cantidadPoblacion) {
                //System.out.println("estoy generando la nueva poblacion");
                //seleccionar dos padres.
                Gen padre1 = seleccionarPoblacion(cantidadPoblacion, poblacion);
                Gen padre2 = seleccionarPoblacion(cantidadPoblacion, poblacion);

                //aplicar operador de cruce de los dos padres con probabilidad, 0.75.
                //Si aplica cruzado
                Gen hijo1 = new Gen();
                Gen hijo2 = new Gen();

                //System.out.println("se va a realizar el cruce");
                List<int[]> descendientes = operadores.orderCrosover(padre1.getIndividuo(), padre2.getIndividuo());

                int[] descendiente1 = descendientes.get(0);
                int[] descendiente2 = descendientes.get(1);

                hijo1.setIndividuo(descendiente1);
                hijo2.setIndividuo(descendiente2);

                numeroDescendientes += 2;

                //para cada posición de genes en cada descendiente
                operadores.mutarIndividuo(hijo1, mutacion);
                operadores.mutarIndividuo(hijo2, mutacion);

                nuevaPoblacion.add(hijo1);
                nuevaPoblacion.add(hijo2);
            }
            //ver si no hay drama con la referenciacion
            poblacion = nuevaPoblacion;
        }
        return solucion;

    }

}
