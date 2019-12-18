/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import py.com.fp.una.rbcmsa.archivos.LecturaArchivo;
import py.com.fp.una.rbcmsa.grafos.business.BuscarCaminos;
import py.com.fp.una.rbcmsa.grafos.model.Camino;
import py.com.fp.una.rbcmsa.grafos.model.Rutas;
import py.com.fp.una.rbcmsa.peticion.model.Peticion;
import py.com.fp.una.rbcmsa.peticion.model.PeticionBCM;
import py.com.fp.una.rbcmsa.tr.business.BuscarTR;
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
    LecturaArchivo lecturaArchivoBean;

    @Inject
    BuscarTR buscarTRBean;

    public static void main(String[] args) throws CloneNotSupportedException {
        //int [][] matriz = {{0,1,0,0,0,1},{1,0,1,0,1,0},{0,1,0,1,0,0},{0,0,1,0,1,0},{0,1,0,1,0,1},{1,0,0,0,1,0}};
        //int[][] matriz = {{0, 1, 0, 1}, {1, 0, 1, 1}, {0, 1, 0, 1}, {1, 1, 1, 0}};
        int[][] matriz = {{0, 2233, 0, 0}, {0, 0, 1, 1}, {0, 0, 0, 1}, {1, 0, 0, 0}};//dirigido
        String nombreArchivoTR = "C:\\Users\\Richard\\Documents\\NetBeansProjects\\RBCMSA\\src\\main\\resources\\TR.txt";
        String sepadadorTR = " ";
        String nombreArchivoPeticiones = "C:\\Users\\Richard\\Documents\\NetBeansProjects\\RBCMSA\\src\\main\\resources\\Peticiones.txt";
        String sepadadorPeticiones = " ";
        int filas = 4;
        int columnas = 4 ;
        String nombreArchivoMatriz = "C:\\Users\\Richard\\Documents\\NetBeansProjects\\RBCMSA\\src\\main\\resources\\Matriz.txt";
        int limite = 5;
        double tamanhoFS = 12.5;

        Weld weld = new Weld();
        try {
            WeldContainer container = weld.initialize();
            container.select(main.class).get().procesar(matriz, nombreArchivoTR, sepadadorTR, nombreArchivoPeticiones, sepadadorPeticiones, limite, tamanhoFS);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        finally {
//            weld.shutdown();
//        }
    }

    private void procesar(int matriz[][], String nombreArchivoTR, String separadorTR,
            String nombreArchivoPeticiones, String separadorPeticiones, int limite, double tamanhoFS) 
            throws CloneNotSupportedException {
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
        HashMap<String, TR> TRS = (HashMap<String, TR>) lecturaArchivoBean.cargarArchivo(nombreArchivoTR, separadorTR, flagTR,0, 0);
        
        //Se cargan las peticiones
        int flagPeticion = 1;
        HashMap<String, Peticion> Peticiones = (HashMap<String, Peticion>) lecturaArchivoBean.cargarArchivo(nombreArchivoPeticiones, separadorPeticiones, flagPeticion, 0, 0);
        
        //Se calculan los TR Finales (B,C,M y Landafinal)
        List<TRBCM> trFinales = new ArrayList<>();
        HashMap<String, PeticionBCM> PeticionesFinales = new HashMap();
        
        for (Map.Entry<String, Peticion> entry : Peticiones.entrySet()) {
            String key = entry.getKey();
            Peticion peticion = entry.getValue();
            Rutas ruta = rutasCompletas.get(peticion.getPedido());
            for (Camino camino : ruta.getCaminos()) {
                TRBCM trFinal = buscarTRBean.buscarTR(camino.getDistancia(), TRS, peticion, tamanhoFS);
                trFinales.add(trFinal);
            }
            PeticionBCM peticionFinal =  new PeticionBCM();
            peticionFinal.setCaminos(ruta.getCaminos());
            peticionFinal.setTrfinal(trFinales);
            peticionFinal.setPeticionOriginal(peticion);
            PeticionesFinales.put(key, peticionFinal);
            
        }

    }
}
