/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.ag.propio;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Alexander
 */
public class Operadores {

    private int[] inicializarHijos(int tamanho) {
        int[] hijo = new int[tamanho];
        for (int i = 0; i < tamanho; i++) {
            hijo[i] = -1;
        }
        return hijo;
    }

    public int[] sustraerIndividuo(int[] individuo, int[] hijo, int tamanhoNuevo,
            int punto1, int punto2) {
        int[] nuevoIndividuo = new int[tamanhoNuevo];
        int k = 0;
        for (int i = 0; i < individuo.length; i++) {
            boolean habilitado = true;
            for (int j = punto1; j <= punto2; j++) {
                if (individuo[i] == hijo[j]) {
                    habilitado = false;
                    break;
                }
            }
            if (habilitado) {
                nuevoIndividuo[k] = individuo[i];
                k++;
            }
            habilitado = true;
        }
        return nuevoIndividuo;
    }

    public int generarCromosoma(int limite) {
//        Random rand = new Random(System.currentTimeMillis());
//        int result = rand.nextInt(limite);
//        result = result +1;
//        return result;
        return (int) (Math.random() * (limite)) + 1;
    }

    public int verificarNoCero(int numero, int limite) {
        int evaluar = numero;
        while (evaluar < 1) {
            evaluar = (int) (Math.random() * (limite + 1));
        }
        return evaluar;
    }

    public void mutarIndividuo(Gen hijo) {
        if (Math.random() < 0.025f) {
            //System.out.println("se va a realizar la mutacion");
            int[] mutado = mutacion(hijo.getIndividuo());
            //descendencia se copian de nuevo en la población, en sustitución de los padres.
            hijo.setIndividuo(mutado);
        }
    }

    public int generarPrimerPunto(int limite) {
        return (int) (Math.random() * (limite + 1));
    }

    public int generarSegundoPunto(int limite, int primerPunto) {
        int aSumar = (int) (Math.random() * (limite - primerPunto));
        while (aSumar == 0) {
            aSumar = (int) (Math.random() * (limite - primerPunto));
        }
        return primerPunto + aSumar;
    }

    public List<int[]> orderCrosover(int[] primerIndividuo, int[] segundoIndividuo) {
        //System.out.println("estoy haciendo en crosover");
        List<int[]> desendencia = new ArrayList<>();
        int tamanho = primerIndividuo.length;
        int[] hijo1 = inicializarHijos(tamanho);
        int[] hijo2 = inicializarHijos(tamanho);

        int punto1 = generarPrimerPunto(tamanho / 2);
        //System.out.println("Punto 1: " + punto1);

        int punto2 = generarSegundoPunto(punto1, tamanho);
        //System.out.println("Punto 2:" + punto2);

        for (int i = punto1; i <= punto2; i++) {
            hijo1[i] = primerIndividuo[i];
            hijo2[i] = segundoIndividuo[i];
        }

        int nuevoTamanho = hijo1.length - (punto2 - punto1) - 1;
        int[] segundoIndividuoAux = sustraerIndividuo(segundoIndividuo, hijo1, nuevoTamanho, punto1, punto2);
        int[] primerIndividuoAux = sustraerIndividuo(primerIndividuo, hijo2, nuevoTamanho, punto1, punto2);
        

        int contHijo1 = 0;
        int contHijo2 = 0;
        for (int i = 0; i < hijo1.length; i++) {
            if (hijo1[i] == -1) {
                hijo1[i] = segundoIndividuoAux[contHijo1];
                contHijo1++;
            }
            if (hijo2[i] == -1) {
                hijo2[i] = primerIndividuoAux[contHijo2];
                contHijo2++;
            }
        }
//        System.out.println("");
//        System.out.println("Hijo 1");
//        for (int j = 0; j < hijo1.length; j++) {
//            System.out.print(hijo1[j] + ",");
//        }
//        System.out.println("");
//        System.out.println("Hijo 2");
//        for (int j = 0; j < hijo2.length; j++) {
//            System.out.print(hijo2[j] + ",");
//        }
        desendencia.add(hijo1);
        desendencia.add(hijo2);

        return desendencia;
    }

    public int[] asignar(int[] individuoAsignar) {
        int[] nuevo = new int[individuoAsignar.length];
        for (int i = 0; i < individuoAsignar.length; i++) {
            nuevo[i] = individuoAsignar[i];
        }
        return nuevo;
    }

    public int[] generarNuevoCromosomaMutacion(int[] actual, int limite, int punto) {
        int[] mutado = asignar(actual);
        int nuevoCromosoma = generarCromosoma(limite);
        int puntoActual = actual[punto];

        while (comprobarNuevoCromosoma(puntoActual, nuevoCromosoma)) {
            nuevoCromosoma = generarCromosoma(limite);
        }
        mutado[punto] = nuevoCromosoma;
        return mutado;

    }

    public boolean comprobarNuevoCromosoma(int actual, int nuevo) {
        if (actual == nuevo) {
            return true;
        } else {
            return false;
        }
    }

    public int[] mutacion(int[] individuoMutar) {
        //System.out.println("estoy haciendo en mutacion");
        int puntoMutar = (int) (Math.random() * (individuoMutar.length));
        int puntoInsercion = (int) (Math.random() * (individuoMutar.length));
        int[] mutado = inicializarHijos(individuoMutar.length);
        mutado[puntoInsercion] = puntoMutar;
        
        int nuevoTamanho = mutado.length - 1;
        int[] individuoMutadoAux= sustraerIndividuo(individuoMutar, mutado, nuevoTamanho, puntoInsercion, puntoInsercion);
//        System.out.println("Individuo aux mutado:");
//        for (int i = 0; i < individuoMutadoAux.length; i++) {
//            System.out.print(individuoMutadoAux[i] + ",");
//        }
        int contMutado = 0;
        for (int i = 0; i < mutado.length; i++) {
            if (mutado[i] == -1) {
                mutado[i] = individuoMutadoAux[contMutado];
                contMutado++;
            }
        }
        return mutado;
    }

}
