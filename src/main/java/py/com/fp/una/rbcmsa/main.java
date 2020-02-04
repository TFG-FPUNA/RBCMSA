/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.inject.Inject;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import py.com.fp.una.rbcmsa.archivos.Archivo;
import py.com.fp.una.rbcmsa.generadores.Generador;
import py.com.fp.una.rbcmsa.grafos.OperacionesGrafos;
import py.com.fp.una.rbcmsa.grafos.business.BuscarCaminos;
import py.com.fp.una.rbcmsa.grafos.model.Arista;
import py.com.fp.una.rbcmsa.grafos.model.AuxArista;
import py.com.fp.una.rbcmsa.grafos.model.Camino;
import py.com.fp.una.rbcmsa.grafos.model.Grafo;
import py.com.fp.una.rbcmsa.grafos.model.Rutas;
import py.com.fp.una.rbcmsa.peticion.model.Peticion;
import py.com.fp.una.rbcmsa.peticion.model.PeticionBCM;
import py.com.fp.una.rbcmsa.tr.business.BuscarTR;
import static py.com.fp.una.rbcmsa.tr.model.Constantes.*;
import py.com.fp.una.rbcmsa.tr.model.TR;
import py.com.fp.una.rbcmsa.tr.model.TRBCM;

/**
 *
 * @author Richard
 */
public class main {

    @Inject
    BuscarCaminos buscarCaminosBean;

    @Inject
    Archivo archivoBean;

    @Inject
    BuscarTR buscarTRBean;

    @Inject
    OperacionesGrafos operacionesGrafos;

    @Inject
    Generador generadorBean;

    public static void main(String[] args) throws CloneNotSupportedException {
        int[][] matriz = {{0, 500, 0, 0, 0, 500}, {500, 0, 500, 0, 500, 0}, {0, 500, 0, 500, 0, 0}, {0, 0, 500, 0, 500, 0}, {0, 500, 0, 500, 0, 500}, {500, 0, 0, 0, 500, 0}};
        //int[][] matriz = {{0, 1, 0, 1}, {1, 0, 1, 1}, {0, 1, 0, 1}, {1, 1, 1, 0}};
        //int[][] matriz = {{0, 2233, 0, 0}, {0, 0, 1, 1}, {0, 0, 0, 1}, {1, 0, 0, 0}};

        //String nombreArchivoTR = "C:\\Users\\Richard\\Documents\\NetBeansProjects\\RBCMSA\\src\\main\\resources\\TR.txt";
        String sepadadorTR = " ";
        String rutaArchvo = "C:\\Users\\Richard\\Documents\\NetBeansProjects\\RBCMSA\\src\\main\\resources\\";
        String nombreArchivo = "Peticiones.txt";
        String sepadadorPeticiones = " ";
        //String nombreArchivoMatriz = "C:\\Users\\Richard\\Documents\\NetBeansProjects\\RBCMSA\\src\\main\\resources\\Matriz.txt";
        //int limite = 5;
        //double tamanhoFS = 12.5;
        //int cantidadSP = 320;

        Weld weld = new Weld();
        try {
            WeldContainer container = weld.initialize();
            container.select(main.class).get().procesar(matriz, sepadadorTR, sepadadorPeticiones, rutaArchvo, nombreArchivo);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        finally {
//            weld.shutdown();
//        }
    }

    public void procesar(int matriz[][], String separadorTR, String sepadadorPeticiones, String rutaArchivos, String nombrePerticiones)
            throws CloneNotSupportedException, IOException {
        Properties p = archivoBean.cargarPropiedades();
        String nombreArchivoTR = (String) p.get(NOMBRE_ARCHIVO_TR);
        int limite = Integer.parseInt((String) p.get(LIMITE));
        double tamanhoFS = Double.parseDouble((String) p.get(TAMANHO_FS));
        int cantidadSP = Integer.parseInt((String) p.get(CANTIDAD_SP));

        //String rutaArchivo = "C:\\Users\\Richard\\Documents\\NetBeansProjects\\RBCMSA\\src\\main\\resources\\";
        //String nombreArchivo = "Peticiones1.txt";
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[0].length; j++) {
                System.out.print(matriz[i][j] + " ");
            }
            System.out.println("");
        }
        //Se calculan los caminos posibles y sus distancias
        HashMap<String, Rutas> rutasCompletas = buscarCaminosBean.busqueda(matriz, limite);
        for (Map.Entry<String, Rutas> entry : rutasCompletas.entrySet()) {
            String key = entry.getKey();
            Rutas value = entry.getValue();
            System.out.println("Ruta:" + value);

        }
        //Se cargan los datos de la Tabla TR
        int flagTR = 0;
        HashMap<String, TR> TRS = (HashMap<String, TR>) archivoBean.cargarArchivo(nombreArchivoTR, separadorTR, flagTR, 0, 0);

        //Se cargan las peticiones
        int flagPeticion = 1;
        HashMap<String, Peticion> Peticiones = (HashMap<String, Peticion>) archivoBean.cargarArchivo(rutaArchivos + nombrePerticiones, sepadadorPeticiones, flagPeticion, 0, 0);
        System.out.println("\n" + "Peticiones: ");
        for (Map.Entry<String, Peticion> entry : Peticiones.entrySet()) {
            String key = entry.getKey();
            Peticion value = entry.getValue();
            System.out.println("key:" + key + " value: " + value);

        }
        //Se calculan los TR Finales (B,C,M y Landafinal)

        HashMap<String, PeticionBCM> PeticionesFinales = new HashMap();

        for (Map.Entry<String, Peticion> entry : Peticiones.entrySet()) {
            String key = entry.getKey();
            Peticion peticion = entry.getValue();
            Rutas ruta = rutasCompletas.get(peticion.getPedido());
            List<TRBCM> trFinales = new ArrayList<>();
            for (Camino camino : ruta.getCaminos()) {
                TRBCM trFinal = buscarTRBean.buscarTR(camino.getDistancia(), TRS, peticion, tamanhoFS);
                trFinales.add(trFinal);
            }
            PeticionBCM peticionFinal = new PeticionBCM();
            peticionFinal.setCaminos(ruta.getCaminos());
            peticionFinal.setTrfinal(trFinales);
            peticionFinal.setPeticionOriginal(peticion);
            PeticionesFinales.put(key, peticionFinal);

        }

        System.out.println("Peticiones Finales: ");
        for (Map.Entry<String, PeticionBCM> entry : PeticionesFinales.entrySet()) {
            String key = entry.getKey();
            PeticionBCM value = entry.getValue();
            System.out.println("key:" + key + " value: " + value);

        }

        Grafo grafo = operacionesGrafos.cargaGrafo(matriz, cantidadSP);

        this.asignarFS(PeticionesFinales, grafo);
        //generadorBean.GenerarArchivo(10, 5, 100, 400, rutaArchivo, nombreArchivo);
    }

    public void quicksort(List<TRBCM> A, List<Camino>B, int izq, int der) {

        int pivote = A.get(izq).getTamanhoFS(); // tomamos primer elemento como pivote
        int i = izq; // i realiza la búsqueda de izquierda a derecha
        int j = der; // j realiza la búsqueda de derecha a izquierda
        TRBCM aux = new TRBCM();

        while (i < j) {            // mientras no se crucen las búsquedas
            while (A.get(i).getTamanhoFS() <= pivote && i < j) {
                i++; // busca elemento mayor que pivote
            }
            while (A.get(j).getTamanhoFS() > pivote) {
                j--;         // busca elemento menor que pivote
            }
            if (i < j) {                      // si no se han cruzado                      
                aux = A.get(i);                  // los intercambia
                A.get(i). = A.get(j);
                A.get(j) = aux;
            }
        }
        A[izq] = A[j]; // se coloca el pivote en su lugar de forma que tendremos
        A[j] = pivote; // los menores a su izquierda y los mayores a su derecha
        if (izq < j - 1) {
            quicksort(A, izq, j - 1); // ordenamos subarray izquierdo
        }
        if (j + 1 < der) {
            quicksort(A, j + 1, der); // ordenamos subarray derecho
        }
    }

    private void ordenarPorFS(HashMap<String, PeticionBCM> PeticionesFinales) {
        for (Map.Entry<String, PeticionBCM> entry : PeticionesFinales.entrySet()) {
            String key = entry.getKey();
            PeticionBCM value = entry.getValue();
            int mayor = -1;
            int posicion = -1;
            for (TRBCM trbcm : value.getTrfinal()) {
                trbcm.getTamanhoFS();
            }

        }

    }

    private void asignarFS(HashMap<String, PeticionBCM> PeticionesFinales, Grafo grafo) {
        int rechazados = 0;
        int aceptados = 0;
        for (Map.Entry<String, PeticionBCM> entry : PeticionesFinales.entrySet()) {
            for (int i = 0; i < entry.getValue().getCaminos().size(); i++) {
                Camino camino = entry.getValue().getCaminos().get(i);
                TRBCM tr = entry.getValue().getTrfinal().get(i);
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
