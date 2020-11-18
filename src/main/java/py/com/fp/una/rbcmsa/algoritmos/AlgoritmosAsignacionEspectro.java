/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.algoritmos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import py.com.fp.una.rbcmsa.grafos.model.Arista;
import py.com.fp.una.rbcmsa.grafos.model.AuxArista;
import py.com.fp.una.rbcmsa.grafos.model.Camino;
import py.com.fp.una.rbcmsa.grafos.model.Grafo;
import py.com.fp.una.rbcmsa.peticion.model.CaminoTR;
import py.com.fp.una.rbcmsa.peticion.model.Peticion;
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

        this.asignarFsPeticionesListas(peticionesFinales, grafo);
    }

    public void MFMRA(List<PeticionBCM> peticionesFinales, Grafo grafo, int cantidadFS, double tamanhoFS) {
        System.out.println("------------- MFMRA -------------");

        for (PeticionBCM peticionFinal : peticionesFinales) {
            System.out.println("");
            System.out.println("------------- INICIAL -------------");
            System.out.println("");
            this.imprimirPeticion(peticionFinal);
            for (int i = 0; i < peticionFinal.getCaminosTR().size(); i++) {
                System.out.println("cantidad fs total: " + cantidadFS);
                int cantidadDisponibleFs = this.calcularFsDisponibles(peticionFinal.getCaminosTR().get(i).getCamino(), cantidadFS, grafo);
                System.out.println("cantidad dispobible fs: " + cantidadDisponibleFs);
                int BWCamino = this.calcularBW(cantidadDisponibleFs, OH_FEC[peticionFinal.getCaminosTR().get(i).getTrfinal().getFEC()], tamanhoFS, peticionFinal.getCaminosTR().get(i).getTrfinal().getFormatoModulacion());
                System.out.println("bw camino: " + BWCamino);
                peticionFinal.getCaminosTR().get(i).getCamino().setCantidadFsDisponibles(cantidadDisponibleFs);
                peticionFinal.getCaminosTR().get(i).getCamino().setAnchoBandaAsignable(BWCamino);
            }
            Collections.sort(peticionFinal.getCaminosTR(), (s1, s2) -> Integer.compare(s2.getCamino().getAnchoBandaAsignable(), s1.getCamino().getAnchoBandaAsignable()));

            System.out.println("");
            System.out.println("------------- ORDEN -------------");
            System.out.println("");
            this.imprimirPeticion(peticionFinal);
            this.asignarFS(peticionFinal, grafo, true);
        }

    }

    public String BFMRA(int[] orden, List<PeticionBCM> peticionesFinales, Grafo grafo, int cantidadFS, double tamanhoFS) {
        //System.out.println("------------- BFMRA -------------");

        for (int i = 0; i < orden.length; i++) {
            //for (PeticionBCM peticionFinal : peticionesFinales) {
            PeticionBCM peticionFinal = peticionesFinales.get(orden[i]);
            //System.out.println("");
            //System.out.println("------------- INICIAL -------------");
            //System.out.println("");
            //this.imprimirPeticion(peticionFinal);
            //ver si queremos pisar o que onda 
            peticionFinal.setCaminosTR(this.seleccionFS(peticionFinal.getCaminosTR(), peticionFinal.getFSMenor()));
            for (CaminoTR caminoTr : peticionFinal.getCaminosTR()) {
                int cantidadDisponibleFs = this.calcularFsDisponibles(caminoTr.getCamino(), cantidadFS, grafo);
                int BWCamino = this.calcularBW(cantidadDisponibleFs, OH_FEC[caminoTr.getTrfinal().getFEC()], tamanhoFS, caminoTr.getTrfinal().getFormatoModulacion());
                caminoTr.getCamino().setCantidadFsDisponibles(cantidadDisponibleFs);
                caminoTr.getCamino().setAnchoBandaAsignable(BWCamino);
            }
            Collections.sort(peticionFinal.getCaminosTR(), (s1, s2) -> Integer.compare(s2.getCamino().getAnchoBandaAsignable(), s1.getCamino().getAnchoBandaAsignable()));
            //System.out.println("");
            //System.out.println("------------- ORDEN -------------");
            //System.out.println("");
            //this.imprimirPeticion(peticionFinal);
            this.asignarFS(peticionFinal, grafo, true);
        }
        return obtenerTotalFS(grafo);//falta contar los fs

    }

    private String obtenerTotalFS(Grafo grafo){
        int total = 0;
        int indiceMax = -1;
        grafo.getAristas();
        for (Map.Entry<String, Arista> en : grafo.getAristas().entrySet()) {
            String key = en.getKey();
            Arista value = en.getValue();
            int contador = 0;
            for (int i = 0; i < value.getSP().length; i++) {
                if (value.getSP()[i] == true) {
                    contador ++;
                    value.getSP()[i] = false;
                    if (i > indiceMax) {
                        indiceMax = i;
                    }
                    
                }
            }
            if (contador>total) {
                total = contador;
            }
//            System.out.println("Contador: " + contador);
//            System.out.println("Total: " + total);
//            System.out.println("Indice Max: " + indiceMax);
        }
        total += -1;
        return total+"-"+indiceMax;
    }
    public void BFMRA2(List<PeticionBCM> peticionesFinales, Grafo grafo, int cantidadFS, double tamanhoFS, int k) throws CloneNotSupportedException {
        System.out.println("------------- BFMRA -------------");

        for (PeticionBCM peticionFinal : peticionesFinales) {
            System.out.println("");
            System.out.println("------------- INICIAL -------------");
            System.out.println("");
            this.imprimirPeticion(peticionFinal);
            //ver si queremos pisar o que onda 
            peticionFinal.setCaminosTR(this.seleccionFSAmpliado(peticionFinal.getCaminosTR(), peticionFinal.getFSMayor(), k));
            for (CaminoTR caminoTr : peticionFinal.getCaminosTR()) {
                int cantidadDisponibleFs = this.calcularFsDisponibles(caminoTr.getCamino(), cantidadFS, grafo);
                //int BWCamino = this.calcularBW(cantidadDisponibleFs, OH_FEC[caminoTr.getTrfinal().getFEC()], tamanhoFS, caminoTr.getTrfinal().getFormatoModulacion());
                caminoTr.getCamino().setCantidadFsDisponibles(cantidadDisponibleFs);
                //caminoTr.getCamino().setAnchoBandaAsignable(BWCamino);
            }
            Collections.sort(peticionFinal.getCaminosTR(), (s1, s2) -> Integer.compare(s2.getCamino().getCantidadFsDisponibles(), s1.getCamino().getCantidadFsDisponibles()));
            System.out.println("");
            System.out.println("------------- ORDEN -------------");
            System.out.println("");
            this.imprimirPeticion(peticionFinal);

            List<Integer> posicionesFinales = new ArrayList<>();
            int finalLista = peticionFinal.getCaminosTR().size();
            for (int i = 0; i < finalLista; i++) {
                PeticionBCM peticionAux = peticionFinal.clone();
                CaminoTR caminoSeleccionado = peticionFinal.getCaminosTR().get(i);
                peticionAux.getCaminosTR().clear();
                peticionAux.getCaminosTR().add(caminoSeleccionado);
                int posicionInicial = this.asignarFS(peticionAux, grafo, false);
                if (posicionInicial != -1) {
                    posicionesFinales.add(posicionInicial + peticionAux.getCaminosTR().get(0).getTrfinal().getTamanhoFS());
                }
            }
            int posicionAsignar = this.buscarPosicionMenor(posicionesFinales);
            System.out.println("Posiciones: ");
            for (Integer posicionesFinale : posicionesFinales) {
                System.out.println(posicionesFinale);
            }
            CaminoTR caminoSeleccionado = peticionFinal.getCaminosTR().get(posicionAsignar);
            PeticionBCM peticionAux = peticionFinal.clone();
            peticionAux.getCaminosTR().clear();
            peticionAux.getCaminosTR().add(caminoSeleccionado);
            System.out.println("Peticion a asignar: \n" + peticionAux);
            this.asignarFS(peticionAux, grafo, true);
//            Collections.sort(posicionesIniciales, (s1, s2) -> Integer.compare(s2, s1));
//            if (!posicionesIniciales.isEmpty()) {
//                for (Integer posicionInicial : posicionesIniciales) {
//                    System.out.println("Posicion inicial: " + posicionInicial);
//                }
//                
//            }

        }

    }

    private int buscarPosicionMenor(List<Integer> posicionesFinales) {
        int menor = Integer.MAX_VALUE;
        int posicion = -1;

        for (int i = 0; i < posicionesFinales.size(); i++) {
            if (posicionesFinales.get(i) < menor) {
                menor = posicionesFinales.get(i);
                posicion = i;
            }
        }
        return posicion;
    }

    private List<CaminoTR> seleccionFS(List<CaminoTR> caminosTrs, Integer FSMenor) {
        List<CaminoTR> aux = new ArrayList<>();
        for (CaminoTR caminoTr : caminosTrs) {
            if (caminoTr.getTrfinal().getTamanhoFS().equals(FSMenor)) {
                aux.add(caminoTr);
            }
        }
        return aux;
    }

    private List<CaminoTR> seleccionFSAmpliado(List<CaminoTR> caminosTrs, Integer FSMayor, int k) {
        List<CaminoTR> aux = new ArrayList<>();
        int cont = 0;
        Collections.sort(caminosTrs, (s1, s2) -> Integer.compare(s1.getTrfinal().getTamanhoFS(), s2.getTrfinal().getTamanhoFS()));

        for (CaminoTR caminoTr : caminosTrs) {
            if (caminoTr.getTrfinal().getTamanhoFS() <= FSMayor) {
                aux.add(caminoTr);
                cont++;
            }
            if (cont == k) {
                break;
            }
        }
        return aux;
    }

    private void imprimirPeticion(PeticionBCM peticionesFinal) {
        System.out.println("Peticion Final: ");
        System.out.println("\t Peticion Orininal: " + peticionesFinal.getPeticionOriginal());
        System.out.println("\t FS Menor: " + peticionesFinal.getFSMenor());
        System.out.println("\t Caminos & TR: ");
        for (CaminoTR caminoTR : peticionesFinal.getCaminosTR()) {
            System.out.println("\t \t Camino: " + caminoTR.getCamino());
            System.out.println("\t \t TR: " + caminoTR.getTrfinal());
        }

    }

    private void imprimirPeticiones(List<PeticionBCM> peticionesFinales) {
        for (PeticionBCM peticionesFinal : peticionesFinales) {
            this.imprimirPeticion(peticionesFinal);

        }
    }

    private Integer calcularBW(Integer FsAsignable, Double porcentajeFEC, Double tamanhoFS, Integer nivelModulacion) {
        Double bw = FsAsignable * tamanhoFS * nivelModulacion / (1 + porcentajeFEC);

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
                aux = aux && !aristasSeleccionada.getSP()[i];
            }
            if (aux) {
                cant++;
            } else {

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

    private void asignarFsPeticionesListas(List<PeticionBCM> peticionesFinales, Grafo grafo) {
        for (PeticionBCM peticionFinal : peticionesFinales) {
            this.asignarFS(peticionFinal, grafo, true);
        }
    }

    private int asignarFS(PeticionBCM peticionFinal, Grafo grafo, boolean asignar) {
        int rechazados = 0;
        int aceptados = 0;
        for (CaminoTR caminoTR : peticionFinal.getCaminosTR()) {
            Camino camino = caminoTR.getCamino();
            TRBCM tr = caminoTR.getTrfinal();
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
                            if (asignar) {

                                for (Arista aristasSeleccionada : aristasSeleccionadas) {
                                    for (int j = posicionInicial; j < posicionInicial + tamanhoRequeridoFS; j++) {
                                        grafo.getAristas().get(aristasSeleccionada.getIdentificador()).getSP()[j] = true;
                                        grafo.getAristas().get(aristasSeleccionada.getIdentificador()).setCantidadSP(grafo.getAristas().get(aristasSeleccionada.getIdentificador()).getCantidadSP() - 1);
                                        //System.out.println("Arista:" + aristasSeleccionada.getIdentificador() + "posicion fs usado: " + j);
                                    }
                                }
                                aceptados++;
                            } else {
                                return posicionInicial;
                            }

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
        return -1;
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
