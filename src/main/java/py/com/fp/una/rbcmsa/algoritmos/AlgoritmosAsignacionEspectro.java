/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.algoritmos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import py.com.fp.una.rbcmsa.grafos.model.Arista;
import py.com.fp.una.rbcmsa.grafos.model.AuxArista;
import py.com.fp.una.rbcmsa.grafos.model.Camino;
import py.com.fp.una.rbcmsa.grafos.model.Grafo;
import py.com.fp.una.rbcmsa.peticion.model.PeticionBCM;
import py.com.fp.una.rbcmsa.tr.model.TRBCM;

/**
 *
 * @author Richard
 */
public class AlgoritmosAsignacionEspectro {

    public void SFMRA(List<PeticionBCM> peticionesFinales, Grafo grafo) {
        System.out.println("------------- SFMRA -------------");
        this.imprimirPeticiones(peticionesFinales);
        
        this.asignarFS(peticionesFinales, grafo);
    }

    public void MFMRA(List<PeticionBCM> peticionesFinales, Grafo grafo) {
        System.out.println("------------- MFMRA -------------");
        this.imprimirPeticiones(peticionesFinales);
        
        this.ordenarPorFS(peticionesFinales);
        System.out.println("Ordenamiento");
        this.imprimirPeticiones(peticionesFinales);
        
        this.asignarFS(peticionesFinales, grafo);
    }

    private void imprimirPeticiones(List<PeticionBCM> peticionesFinales) {
        for (PeticionBCM peticionesFinal : peticionesFinales) {
            System.out.println("Peticion Final:" + peticionesFinal);

        }
    }

    private void ordenarPorFS(List<PeticionBCM> peticionesFinales) {
        Collections.sort(peticionesFinales, (s1, s2) -> Integer.compare(s2.getFSMayor(), s1.getFSMayor()));

    }

    private void asignarFS(List<PeticionBCM> peticionesFinales, Grafo grafo) {
        int rechazados = 0;
        int aceptados = 0;

        for (PeticionBCM peticionFinal : peticionesFinales) {
            for (int i = 0; i < peticionFinal.getCaminos().size(); i++) {
                Camino camino = peticionFinal.getCaminos().get(i);
                TRBCM tr = peticionFinal.getTrfinal().get(i);
                int tamanhoRequeridoFS = tr.getTamanhoFS();
                int cantidadNodos = camino.getNodos().size();

                Integer nodoInicial = camino.getNodos().get(0);
                Integer nodoSiguiente = camino.getNodos().get(1);

                String identificador = nodoInicial + "-" + nodoSiguiente;
                AuxArista auxArista = new AuxArista();
                int posicionInicial = -1;
                int contador = 0;
                List<Arista> aristasSeleccionadas = new ArrayList<>();

                Arista arista = grafo.getAristas().get(identificador);

                aristasSeleccionadas.add(arista);

                for (int k = 0; k < arista.getSP().length; k++) {
                    if (!arista.getSP()[k]) {
                        posicionInicial = k;
                        contador++;
                        for (int l = posicionInicial + 1; l < posicionInicial + tamanhoRequeridoFS; l++) {
                            if (!arista.getSP()[l]) {
                                contador++;
                            }

                        }
                        if (contador == tamanhoRequeridoFS) {
                            //buscar en las demas aristas
                            boolean confirmado = true;
                            for (int j = 1; j < cantidadNodos - 1; j++) {
                                confirmado = confirmado && this.verificarAristas(camino.getNodos().get(j), camino.getNodos().get(j + 1),
                                        tamanhoRequeridoFS, grafo, posicionInicial, aristasSeleccionadas);
                            }
                            if (confirmado) {
                                //marcar fs
                                for (Arista aristasSeleccionada : aristasSeleccionadas) {
                                    for (int j = posicionInicial; j < posicionInicial + tamanhoRequeridoFS; j++) {
                                        grafo.getAristas().get(aristasSeleccionada.getIdentificador()).getSP()[j] = true;
                                        System.out.println("Arista:" + aristasSeleccionada.getIdentificador() + "posicion fs usado: " + j);
                                    }
                                }
                                aceptados++;
                            } else {
                                posicionInicial = -1;
                                contador = 0;
                                aristasSeleccionadas.clear();
                                aristasSeleccionadas.add(arista);
                            }
                        } else {
                            posicionInicial = -1;
                            contador = 0;
                            aristasSeleccionadas.clear();
                            aristasSeleccionadas.add(arista);
                        }
                        if (posicionInicial != -1) {
                            break;
                        }
                    }
                }

                if (posicionInicial != -1) {
                    //se rechaza la peticion
                    //rechazados++;
                    break;
                }

            }
        }
    }

    private boolean verificarAristas(Integer nodoInicial, Integer nodoSiguiente, int tamanhoRequeridoFS, Grafo grafo,
            int posicionInicial, List<Arista> aristasSeleccionadas) {

        String identificador = nodoInicial + "-" + nodoSiguiente;
        int contador = 0;

        Arista arista = grafo.getAristas().get(identificador);
        for (int i = posicionInicial; i < posicionInicial + tamanhoRequeridoFS; i++) {
            if (!arista.getSP()[i]) {
                contador++;
            }
        }
        if (contador == tamanhoRequeridoFS) {
            aristasSeleccionadas.add(arista);
            return true;
        }

        return false;

    }

}
