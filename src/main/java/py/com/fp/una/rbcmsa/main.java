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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Richard
 */
public class main {

    private static final Logger logger = LogManager.getLogger(main.class.getName());

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
        //caso 3
//        int[][] matriz = {
//            {0, 1000, 0, 0, 0, 200},
//            {1000, 0, 100, 0, 400, 0},
//            {0, 100, 0, 700, 0, 0},
//            {0, 0, 700, 0, 1000, 0},
//            {0, 400, 0, 1000, 0, 200},
//            {200, 0, 0, 0, 200, 0}
//        };
        // caso ag
//        int[][] matriz = {
//            {0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 1, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0}
//        };
        int[][] matriz = {
            {0, 1310, 760, 390, 0, 0, 740, 0, 0, 0, 0},
            {1310, 0, 550, 0, 390, 0, 0, 450, 0, 0, 0},
            {760, 550, 0, 660, 210, 390, 0, 0, 0, 0, 0},
            {390, 0, 660, 0, 0, 0, 340, 1090, 0, 660, 0},
            {0, 390, 210, 0, 0, 220, 0, 300, 0, 0, 930},
            {0, 0, 390, 0, 220, 0, 730, 400, 350, 0, 0},
            {740, 0, 0, 340, 0, 730, 0, 0, 565, 320, 0},
            {0, 450, 0, 1090, 300, 400, 0, 0, 600, 0, 820},
            {0, 0, 0, 0, 0, 350, 565, 600, 0, 730, 320},
            {0, 0, 0, 660, 0, 0, 320, 0, 730, 0, 820},
            {0, 0, 0, 0, 930, 0, 0, 820, 320, 820, 0}
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
        //String nombreArchivo = "Peticiones_arpanet.txt";
        String nombreArchivo = "Peticiones_prueba_ilp.txt";
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
//            weld.shutdowsdn();
//        }
    }

    public void procesar(int matriz[][], String separadorTR, String sepadadorPeticiones,
            String rutaArchivos, String nombrePerticiones, String rutaArchivoILP,
            String nombreArchivoILPFaseI, String nombreArchivoILPFaseII, String nombreArchivoJPIL,
            String rutaArchivoAG, String nombreArchivoAG)
            throws CloneNotSupportedException, IOException, Exception {
        logger.info("Logueando");
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
        HashMap<Integer, Peticion> Peticiones = (HashMap<Integer, Peticion>) archivoBean.cargarArchivo(rutaArchivos + nombrePerticiones, sepadadorPeticiones, flagPeticion, 0, 0);
        System.out.println("\n" + "Peticiones: ");
        for (Map.Entry<Integer, Peticion> entry : Peticiones.entrySet()) {
            Integer key = entry.getKey();
            Peticion value = entry.getValue();
            System.out.println("key:" + key + " value: " + value);

        }
        //Se calculan los TR Finales (B,C,M y Landafinal)

        //HashMap<String, PeticionBCM> PeticionesFinales = new HashMap();
        List<PeticionBCM> peticionesFinales = new ArrayList<>();
        List<CaminoTR> caminos = new ArrayList<>();
        for (Map.Entry<Integer, Peticion> entry : Peticiones.entrySet()) {
            Integer key = entry.getKey();
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
                //en este lugar anadir
                caminos.add(caminoTrFinal);
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
//        List<int[]> permutaciones = new ArrayList<>();
//        //System.out.println("Permutaciones");
//        AGP.permute(new int[]{0, 1, 2, 3, 4,5,6,7,8,9,10,11,12,13,14}, permutaciones, 10, (1307674368000 -1));
//        //System.out.println("longitud: " + permutaciones.size());
//        int mayor = -1;
//        for (int[] permutacion : permutaciones) {
//            String total = algoritmosAsignacionEspectro.BFMRA(permutacion, peticionesFinales, grafo, cantidadSP, tamanhoFS);
//            String[] split = total.split("-");
//            int resultadoBFMRA = Integer.parseInt(split[0]);
//            if (resultadoBFMRA > mayor) {
//                mayor = resultadoBFMRA;
//            }
////            System.out.print("{");
////            for (int i = 0; i < permutacion.length; i++) {
////                System.out.print(permutacion[i]);
////            }
////            System.out.println("}\n");
//        }
//        System.out.println("Resultado Final: " + mayor);
        //System.out.println("");
//        long inicioEU = System.currentTimeMillis();
//        String total = algoritmosAsignacionEspectro.BFMRA(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29}, peticionesFinales, grafo, cantidadSP, tamanhoFS);
//        long finEU = System.currentTimeMillis();
//        System.out.println("Total: " + total);
//        System.out.println("Tiempo: " + (finEU - inicioEU));
        //algoritmosAsignacionEspectro.BFMRA2(peticionesFinales, grafo, cantidadSP ,tamanhoFS, limite);
        //generadorBean.GenerarArchivo(10, 5, 100, 400, rutaArchivo, nombreArchivo);
        /**
         * ******************************ILP******************************************
         */
//        long acumuladorJILP = 0;
//        for (int i = 0; i < 1; i++) {
//            long inicioILP = System.currentTimeMillis();
//            SPILP.ILP(rutaArchivoILP, nombreArchivoILPFaseI, nombreArchivoILPFaseII, limite, peticionesFinales, grafo, guarBan + "", cantidadSP);
//            long finILP = System.currentTimeMillis();
//            archivoBean.eliminarDirectorio(rutaArchivoILP+nombreArchivoILPFaseI);
//            archivoBean.eliminarDirectorio(rutaArchivoILP+nombreArchivoILPFaseII);
//            //String nombreSalida1 = "salidaCplexSP-ILP_11.txt";
//            //archivoBean.eliminarDirectorio(rutaArchivoILP+nombreSalida1);
//            //archivoBean.eliminarDirectorio(rutaArchivoILP+"salidaCplexSP-ILP_21.txt");
//            acumuladorJILP += finILP - inicioILP;
//        }
        /**
         * ***************************************************************************
         */
        /**
         * ****************************JPILP******************************************
         */
//        long acumuladorILP = 0;
//        for (int i = 0; i < 1; i++) {
//            long inicioJILP = System.currentTimeMillis();
//            SPILP.JPILP(rutaArchivoILP, nombreArchivoJPIL, limite, peticionesFinales, caminos, grafo, guarBan + "", cantidadSP);
//            long finJILP = System.currentTimeMillis();
//            archivoBean.eliminarDirectorio(rutaArchivoILP+nombreArchivoJPIL);
//            //archivoBean.eliminarDirectorio(rutaArchivoILP+"salidaCplexILP1.txt");
//            acumuladorILP += finJILP - inicioJILP;
//        }
        /**
         * ***************************************************************************
         */
        /**
         * ****************************AGYSA******************************************
         */
//        long inicioAGYSA = System.currentTimeMillis();
//        AG.AG(peticionesFinales, rutaArchivoAG, nombreArchivoAG, limite);
//        long finAGYSA = System.currentTimeMillis();
//        long tiempoAGYsa = finAGYSA - inicioAGYSA;
        /**
         * ***************************************************************************
         */
        /**
         * ****************************AGPER******************************************
         */
        List<Double> promedios = new ArrayList<>();
        List<Integer> generaciones = new ArrayList<>();
//
        long acumuladorAG = 0;
        int promedio = 0;
        int generacion = 0;
        //System.out.println("mutacion: " + muta);
        for (int i = 0; i < 30; i++) {
            long inicioAGPropio = System.currentTimeMillis();
            Solucion solucion = AGP.algoritmoGenetico(100, 100, peticionesFinales, grafo, cantidadSP, tamanhoFS, 0.2d);
            long finAGPropio = System.currentTimeMillis();
            acumuladorAG += finAGPropio - inicioAGPropio;
            logger.info("****************Corrida "+i+" ************************");
            logger.info("Fitness solucion: " + solucion.getFitness());
            logger.info("Indice: " + solucion.getIndice());
            logger.info("Genracion: " + solucion.getGeneracion());
            promedio += solucion.getFitness();
            promedios.add(solucion.getFitness());
            generacion += generacion;
            generaciones.add(generacion);

            String individuoSting = "";
            for (int j = 0; j < solucion.getIndividuo().length; j++) {
                individuoSting += solucion.getIndividuo()[j] + ",";
            }
            logger.info("Individuo Solucion: {" + individuoSting.substring(0, individuoSting.length() - 1) + "}\n");
            logger.info("*****************************************************");
//promedios.add(promedio / 30f);
            }
            logger.info("Tiempo AGP: " + acumuladorAG / 30);
            logger.info("Promedio Final: " + promedio / 30);
            logger.info("Promedio Generaciones: " + generacion / 30);

            logger.info("*****************Resultados********************");
            for (Double prom : promedios) {
                logger.info("result: " + prom);
            }
            logger.info("*****************Generaciones********************");
            for (Integer generacione : generaciones) {
                logger.info("Generacion: " + generacione);
            }
            /**
             * ***************************************************************************
             */
            /**
             * *****************************AUX*******************************************
             */
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
            /**
             * ***************************************************************************
             */
            //System.out.println("Tiempo ILP: " + acumuladorILP);
            //System.out.println("Tiempo JPILP: " + acumuladorJILP);
//        System.out.println("Tiempo ILP: " + acumuladorAGYSA/30);
            //System.out.println("Tiempo ag ysa: " + tiempoAGYsa);
        

    }
}
