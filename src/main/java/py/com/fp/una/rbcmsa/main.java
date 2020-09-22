/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.inject.Inject;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import py.com.fp.una.rbcmsa.ag.propio.AGP;
import py.com.fp.una.rbcmsa.ag.propio.Gen;
import py.com.fp.una.rbcmsa.ag.propio.Operadores;
import py.com.fp.una.rbcmsa.ag.propio.Solucion;
import py.com.fp.una.rbcmsa.ag.ysa.AG;
import py.com.fp.una.rbcmsa.ag.ysa.adaptaciones.AdaptacionesAG;
import py.com.fp.una.rbcmsa.algoritmos.AlgoritmosAsignacionEspectro;
import py.com.fp.una.rbcmsa.archivos.Archivo;
import py.com.fp.una.rbcmsa.generadores.Generador;
import py.com.fp.una.rbcmsa.grafos.OperacionesGrafos;
import py.com.fp.una.rbcmsa.grafos.business.BuscarCaminos;
import py.com.fp.una.rbcmsa.grafos.model.Camino;
import py.com.fp.una.rbcmsa.grafos.model.Grafo;
import py.com.fp.una.rbcmsa.grafos.model.Rutas;
import py.com.fp.una.rbcmsa.ilp.JPILP;
import py.com.fp.una.rbcmsa.ilp.SPILP;
import py.com.fp.una.rbcmsa.ilp.propios.Adaptaciones;
import py.com.fp.una.rbcmsa.peticion.model.CaminoTR;
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

    @Inject
    SPILP SPILP;

    @Inject
    JPILP JPILP;

    @Inject
    Adaptaciones adaptacionesBean;

    @Inject
    AdaptacionesAG adaptacionesAG;

    @Inject
    AG AG;

    @Inject
    AGP AGP;

    @Inject
    Operadores operadores;

    public static void main(String[] args) throws CloneNotSupportedException {
        //caso 2 para SFMRA
//        int[][] matriz = {
//            {0, 1000, 0, 0, 0, 200}, 
//            {1000, 0, 1000, 0, 400, 0}, 
//            {0, 1000, 0, 500, 0, 0}, 
//            {0, 0, 500, 0, 100, 0}, 
//            {0, 400, 0, 100, 0, 200}, 
//            {200, 0, 0, 0, 200, 0}
//        };
        // caso 3
//        int[][] matriz = {
//            {0, 1000, 0, 0, 0, 200},
//            {1000, 0, 100, 0, 400, 0},
//            {0, 100, 0, 700, 0, 0},
//            {0, 0, 700, 0, 1000, 0},
//            {0, 400, 0, 1000, 0, 200},
//            {200, 0, 0, 0, 200, 0}
//        };
        // caso ag
        int[][] matriz = {
            {0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0}
        };
        //int[][] matriz = {{0, 1, 0, 1}, {1, 0, 1, 1}, {0, 1, 0, 1}, {1, 1, 1, 0}};
        //int[][] matriz = {{0, 2233, 0, 0}, {0, 0, 1, 1}, {0, 0, 0, 1}, {1, 0, 0, 0}};

        //String nombreArchivoTR = "C:\\Users\\Richard\\Documents\\NetBeansProjects\\RBCMSA\\src\\main\\resources\\TR.txt";
        String sepadadorTR = " ";
        String rutaArchivo = "C:\\Users\\Richard\\Documents\\NetBeansProjects\\RBCMSA\\src\\main\\resources\\";
        String rutaArchivoILP = "C:\\Users\\Richard\\Documents\\NetBeansProjects\\RBCMSA\\src\\resultados\\";
        String rutaArchivoAG = "C:\\Users\\Richard\\Documents\\NetBeansProjects\\RBCMSA\\src\\";
//        String nombreArchivo = "Peticiones.txt";
//        String nombreArchivo = "Peticiones_caso2.txt";
        //String nombreArchivo = "Peticiones_caso3.txt";
        //String nombreArchivo = "Peticiones_caso_comparacion_ysa.txt";
        String nombreArchivo = "Peticiones_arpanet.txt";
        String nombreArchivoILPFaseI = "SP-ILP_11.dat";
        String nombreArchivoILPFaseII = "SP-ILP_21.dat";
        String nombreArchivoJPIL = "JPILP.dat";
        String nombreArchivoAG = "ga.txt";
        String sepadadorPeticiones = " ";
        //String nombreArchivoMatriz = "C:\\Users\\Richard\\Documents\\NetBeansProjects\\RBCMSA\\src\\main\\resources\\Matriz.txt";
        //int limite = 5;
        //double tamanhoFS = 12.5;
        //int cantidadSP = 320;

        Weld weld = new Weld();
        try {
            WeldContainer container = weld.initialize();
            container.select(main.class).get().procesar(matriz, sepadadorTR, sepadadorPeticiones,
                    rutaArchivo, nombreArchivo, rutaArchivoILP, nombreArchivoILPFaseI,
                    nombreArchivoILPFaseII, nombreArchivoJPIL, rutaArchivoAG, nombreArchivoAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        finally {
//            weld.shutdown();
//        }
    }

    public void procesar(int matriz[][], String separadorTR, String sepadadorPeticiones,
            String rutaArchivos, String nombrePerticiones, String rutaArchivoILP,
            String nombreArchivoILPFaseI, String nombreArchivoILPFaseII, String nombreArchivoJPIL,
            String rutaArchivoAG, String nombreArchivoAG)
            throws CloneNotSupportedException, IOException, Exception {
        Properties p = archivoBean.cargarPropiedades();
        String nombreArchivoTR = (String) p.get(NOMBRE_ARCHIVO_TR);
        int limite = Integer.parseInt((String) p.get(LIMITE));
        double tamanhoFS = Double.parseDouble((String) p.get(TAMANHO_FS));
        int cantidadSP = Integer.parseInt((String) p.get(CANTIDAD_SP));
        int guarBan = Integer.parseInt((String) p.get(GUARBAN));
        //SPILP.obtenerFFaseII(1);
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
            //List<TRBCM> trFinales = new ArrayList<>();
            List<CaminoTR> caminosTrFinales = new ArrayList<>();
            Integer FSMenor = Integer.MAX_VALUE;
            Integer FSMayor = Integer.MIN_VALUE;
            for (Camino camino : ruta.getCaminos()) {
                CaminoTR caminoTrFinal = new CaminoTR();
                TRBCM trFinal = buscarTRBean.buscarTR(camino.getDistancia(), TRS, peticion, tamanhoFS);
                caminoTrFinal.setCamino(camino);
                caminoTrFinal.setTrfinal(trFinal);
                if (FSMenor > trFinal.getTamanhoFS()) {
                    FSMenor = trFinal.getTamanhoFS();
                }
                if (FSMayor < trFinal.getTamanhoFS()) {
                    FSMayor = trFinal.getTamanhoFS();
                }
                //trFinales.add(trFinal);
                caminosTrFinales.add(caminoTrFinal);
            }
            PeticionBCM peticionFinal = new PeticionBCM();
            //peticionFinal.setCaminos(ruta.getCaminos());
            //peticionFinal.setTrfinal(trFinales);
            peticionFinal.setPeticionOriginal(peticion);
            peticionFinal.setFSMenor(FSMenor);
            peticionFinal.setFSMayor(FSMayor);
            peticionFinal.setCaminosTR(caminosTrFinales);
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
        //algoritmosAsignacionEspectro.SFMRA(peticionesFinales, grafo);

        //Llamada algoritmo 2 del paper base
        //algoritmosAsignacionEspectro.MFMRA(peticionesFinales, grafo, cantidadSP ,tamanhoFS);
        //Llamada algoritmo 3 del paper base
        //int total = algoritmosAsignacionEspectro.BFMRA(new int[]{5,3,4,1,0,2,6}, peticionesFinales, grafo, cantidadSP, tamanhoFS);
        //System.out.println("Total: " + total);
        //algoritmosAsignacionEspectro.BFMRA2(peticionesFinales, grafo, cantidadSP ,tamanhoFS, limite);
        //generadorBean.GenerarArchivo(10, 5, 100, 400, rutaArchivo, nombreArchivo);
        long inicioILP = System.currentTimeMillis();
        //SPILP.ILP(rutaArchivoILP, nombreArchivoILPFaseI, nombreArchivoILPFaseII, limite, peticionesFinales, grafo, guarBan+"",cantidadSP);
        long finILP = System.currentTimeMillis();
        long tiempoILP = finILP - inicioILP;

        //SPILP.JPILP(rutaArchivoILP, nombreArchivoJPIL, limite, peticionesFinales, grafo, guarBan+"",cantidadSP);
        //adaptacionesBean.preparaArchivoFaseIIILP(rutaArchivoILP, nombreArchivoILP, limite+"", peticionesFinales, grafo, guarBan+"", 0 , alphaR, null);
        //adaptacionesAG.generarEntradaAG(peticionesFinales, 0, rutaArchivoAG, nombreArchivoAG);
        long inicioAGYSA = System.currentTimeMillis();
        //AG.AG(peticionesFinales, rutaArchivoAG, nombreArchivoAG, limite);
        long finAGYSA = System.currentTimeMillis();
        long tiempoAGYsa = finAGYSA - inicioAGYSA;
        List<Double> mutacion = new ArrayList<>();
//        mutacion.add(0.1d);
//        mutacion.add(0.15d);
//        mutacion.add(0.2d);
//        mutacion.add(0.25d);
//        mutacion.add(0.3d);
//        mutacion.add(0.35d);
//        mutacion.add(0.4d);
//        mutacion.add(0.45d);
//        mutacion.add(0.5d);
//        mutacion.add(0.55d);
//        mutacion.add(0.6d);
//        mutacion.add(0.65d);
        mutacion.add(0.7d);
        List<Float> promedios = new ArrayList<>();
        List<Float> generaciones = new ArrayList<>();

        for (Double muta : mutacion) {
            int promedio = 0;
            int generacion = 0;
            System.out.println("mutacion: " + muta);
            for (int i = 0; i < 1; i++) {
                long inicioAGPropio = System.currentTimeMillis();
                Solucion solucion = AGP.algoritmoGenetico(10, 10, peticionesFinales, grafo, cantidadSP, tamanhoFS, muta);
                long finAGPropio = System.currentTimeMillis();
                long tiempoAGPropio = finAGPropio - inicioAGPropio;
                System.out.println("Fitness solucion: " + solucion.getFitness());
                System.out.println("Genracion: " + solucion.getGeneracion());
                promedio += solucion.getFitness();
                generacion += generacion;
                System.out.print("Individuo: {");
                for (int j = 0; j < solucion.getIndividuo().length; j++) {
                    System.out.print(solucion.getIndividuo()[j] + ",");
                }
                System.out.println("}\n");
            }
            promedios.add(promedio / 1f);
        }
        for (Float promedio : promedios) {
            System.out.println("Promedio: " + promedio);
        }

//        List<Gen> genes = AGP.inicializarPoblacion(10, 6);
//        System.out.println("Lista: " + genes.size());
//        for (Gen gen : genes) {
//            System.out.print("Gen: {");
//            for (int i = 0; i < gen.getIndividuo().length; i++) {
//                System.out.print(gen.getIndividuo()[i]);
//            }
//            System.out.println("}\n");
//        }
//        List<int[]> permutaciones = new ArrayList<>();
//        System.out.println("Permutaciones");
//        AGP.permute(new int[]{1, 2, 3, 4}, permutaciones, 4, 23);
//        System.out.println("longitud: " + permutaciones.size());
//        for (int[] permutacion : permutaciones) {
//            System.out.print("{");
//            for (int i = 0; i < permutacion.length; i++) {
//                System.out.print(permutacion[i]);
//            }
//            System.out.println("}\n");
//        }
//        System.out.println("");
//        int[] primerIndividuo = {9, 8, 4, 5, 6, 7, 1, 2, 3, 10};
//        int[] segundoIndividuo = {8, 7, 1, 2, 3, 10, 9, 5, 4, 6};
//        List<int[]> resultado = operadores.orderCrosover(primerIndividuo, segundoIndividuo);
//        for (int[] result : resultado) {
//            for (int i = 0; i < result.length; i++) {
//                System.out.print(result[i] + ",");
//            }
//            System.out.println("");
//        }
//        int[] individuoMutacion = {9, 4, 2, 1, 5, 7, 6, 10, 3, 8};
//        int[] mutado = operadores.mutacion(individuoMutacion);
//        System.out.println("Mutacion");
//        for (int i = 0; i < mutado.length; i++) {
//            System.out.print(mutado[i] + ";");
//        }
//        String str = "1234";
//        StringBuffer strBuf = new StringBuffer(str);
//        List<StringBuffer> permutaciones = new ArrayList<>();
//
//        System.out.println("Permutacion");
//        AGP.doPerm(strBuf, str.length(), permutaciones);
//        for (StringBuffer permutacione : permutaciones) {
//            System.err.println(permutacione);
//            
//        }
//        List<Gen> genes = AGP.inicializarPoblacion(20, 4);
//        System.out.println("Lista: " + genes.size());
//        for (Gen gen : genes) {
//            System.out.print("Gen: {");
//            for (int i = 0; i < gen.getIndividuo().length; i++) {
//                System.out.print(gen.getIndividuo()[i]);
//            }
//            System.out.println("}\n");
//        }
    }

}
