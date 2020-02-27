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
import static py.com.fp.una.rbcmsa.tr.model.Constantes.OH_FEC;
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

    public void MFMRA(List<PeticionBCM> peticionesFinales, Grafo grafo, int cantidadFS, double tamanhoFS) {
        System.out.println("------------- MFMRA -------------");
        this.imprimirPeticiones(peticionesFinales);

        this.ordenarPorFS(peticionesFinales);
        System.out.println("Ordenamiento");
        this.imprimirPeticiones(peticionesFinales);
        for (PeticionBCM peticionFinal : peticionesFinales) {
            int i=0;
            for (Camino camino : peticionFinal.getCaminos()) {
                int cantidadDisponibleFs = this.calcularFsDisponibles(camino, cantidadFS, grafo);
                int BWCamino = this.calcularBW(cantidadDisponibleFs,OH_FEC[peticionFinal.getTrfinal().get(i).getFEC()], tamanhoFS, peticionFinal.getTrfinal().get(i).getFormatoModulacion());
                camino.setCantidadFsDisponibles(cantidadDisponibleFs);
                camino.setAnchoBandaAsignable(BWCamino);
            }
            Collections.sort(peticionFinal.getCaminos(), (s1, s2) -> Integer.compare(s2.getAnchoBandaAsignable(), s1.getAnchoBandaAsignable()));
        }
        
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
    
    private Integer calcularBW(Integer FsAsignable, Double porcentajeFEC, Double tamanhoFS, Integer nivelModulacion){
        Double bw = FsAsignable*tamanhoFS * nivelModulacion/(1 + porcentajeFEC);
        
        return bw.intValue();
    }

    private Integer calcularFsDisponibles(Camino camino, int cantidadFS, Grafo grafo) {
        Integer nodoInicial = camino.getNodos().get(0);
        Integer nodoSiguiente = camino.getNodos().get(1);
        int cantidadNodos = camino.getNodos().size();

        String identificador = nodoInicial + "-" + nodoSiguiente;
        List<Arista> aristasSeleccionadas = new ArrayList<>();

        Arista arista = grafo.getAristas().get(identificador);
        aristasSeleccionadas.add(arista);

        for (int j = 1; j < cantidadNodos - 1; j++) {
            nodoInicial = camino.getNodos().get(j);
            nodoSiguiente = camino.getNodos().get(j + 1);
            identificador = nodoInicial + "-" + nodoSiguiente;
            Arista siguienteArista = grafo.getAristas().get(identificador);
            aristasSeleccionadas.add(siguienteArista);
        }

        int mayor = -1;
        int cant = 0;
        for (int i = 0; i < cantidadFS; i++) {
            boolean aux = true;
            
            for (Arista aristasSeleccionada : aristasSeleccionadas) {
                aux = aux && aristasSeleccionada.getSP()[i];
            }
            if (aux) {
                cant ++;
            }else{
                
                if (cant > mayor) {
                    mayor = cant;
                }
                cant = 0;
            }
        }
        if (cant > mayor) {
            mayor = cant;
        }
        return mayor;
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
                                        grafo.getAristas().get(aristasSeleccionada.getIdentificador()).setCantidadSP(grafo.getAristas().get(aristasSeleccionada.getIdentificador()).getCantidadSP() - 1);
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
