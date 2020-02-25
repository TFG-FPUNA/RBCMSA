/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.inject.Inject;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import py.com.fp.una.rbcmsa.algoritmos.AlgoritmosAsignacionEspectro;
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
    
    @Inject
    AlgoritmosAsignacionEspectro algoritmosAsignacionEspectro;

    public static void main(String[] args) throws CloneNotSupportedException {
        int[][] matriz = {
            {0, 1000, 0, 0, 0, 200}, 
            {1000, 0, 1000, 0, 400, 0}, 
            {0, 1000, 0, 500, 0, 0}, 
            {0, 0, 500, 0, 100, 0}, 
            {0, 400, 0, 100, 0, 200}, 
            {200, 0, 0, 0, 200, 0}
        };
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

        //HashMap<String, PeticionBCM> PeticionesFinales = new HashMap();
        List<PeticionBCM> peticionesFinales = new ArrayList<>();

        for (Map.Entry<String, Peticion> entry : Peticiones.entrySet()) {
            String key = entry.getKey();
            Peticion peticion = entry.getValue();
            Rutas ruta = rutasCompletas.get(peticion.getPedido());
            List<TRBCM> trFinales = new ArrayList<>();
            Integer FSMayor = -1;
            for (Camino camino : ruta.getCaminos()) {
                TRBCM trFinal = buscarTRBean.buscarTR(camino.getDistancia(), TRS, peticion, tamanhoFS);
                if (FSMayor < trFinal.getTamanhoFS()) {
                    FSMayor = trFinal.getTamanhoFS();
                }
                trFinales.add(trFinal);
            }
            PeticionBCM peticionFinal = new PeticionBCM();
            peticionFinal.setCaminos(ruta.getCaminos());
            peticionFinal.setTrfinal(trFinales);
            peticionFinal.setPeticionOriginal(peticion);
            peticionFinal.setFSMayor(FSMayor);
            peticionesFinales.add(peticionFinal);

            //PeticionesFinales.put(key, peticionFinal);
            //hablar con divina sobre el tema de ordenar por TFS porque cada camino tiene un TFS diferente y estos caminos estan asociados a una peticion
        }       
        
//        for (Map.Entry<String, PeticionBCM> entry : PeticionesFinales.entrySet()) {
//            String key = entry.getKey();
//            PeticionBCM value = entry.getValue();
//            System.out.println("key:" + key + " value: " + value);
//
//        }

        Grafo grafo = operacionesGrafos.cargaGrafo(matriz, cantidadSP);
        //Llamada algoritmo 1 del paper base
        algoritmosAsignacionEspectro.SFMRA(peticionesFinales, grafo);
        
        //Llamada algoritmo 2 del paper base
        algoritmosAsignacionEspectro.MFMRA(peticionesFinales, grafo);
        
        //Llamada algoritmo 3 del paper base
        
        //generadorBean.GenerarArchivo(10, 5, 100, 400, rutaArchivo, nombreArchivo);
    }
}
