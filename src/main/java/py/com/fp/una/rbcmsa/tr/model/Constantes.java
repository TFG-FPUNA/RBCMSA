/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.tr.model;

/**
 *
 * @author Richard
 */
public class Constantes {
    public static final Integer NO_FEC = 0;
    public static final Integer TIPO_1_FEC = 1;
    public static final Integer TIPO_2_FEC = 2;
    public static final Integer TIPO_3_FEC = 3;
    public static final Integer TIPO_4_FEC = 4;
    public static final double[]OH_FEC = new double[]{0.0,0.0669,0.1334,0.2120,0.6666};
    public static final String RUTA_ARCHIVOS = "rutaArchivos";
    public static final String RUTA_ARCHIVO_ILP = "rutaArchivoILP";
    public static final String RUTA_ARCHIVO_AG = "rutaArchivoAG";
    public static final String NOMBRE_ARCHIVO_TR = "nombreArchivoTR";
    public static final String NOMBRE_ARCHIVO_MATRIZ = "nombreArchivoMatriz";
    
    public static final String NOMBRE_ARCHIVO_PETICIONES = "nombreArchivoPeticiones";
    public static final String NOMBRE_ARCHIVO_ILP_FASEI = "nombreArchivoILPFaseI";
    public static final String NOMBRE_ARCHIVO_ILP_FASEII = "nombreArchivoILPFaseII";
    public static final String NOMBRE_ARCHIVO_JPILP = "nombreArchivoJPILP";
    public static final String NOMBRE_ARCHIVO_AG = "nombreArchivoGA";
    public static final String FILAS = "filas";
    public static final String COLUMNAS = "columnas";
    public static final String ALGORITMO = "algoritmo";
    
    public static final String LIMITE = "limite";
    public static final String TAMANHO_FS= "tamanhoFS";
    public static final String CANTIDAD_SP = "cantidadSP";
    public static final String GUARBAN = "guarban";
    /*AG YSA*/
    public static final String RUTA_AG = "ag\\";
    public static final String MOGA_1 = "moga1";
    public static final String MOGA_2 = "moga2";
    public static final String FIN_DE_RUTA = "\\";
    public static final String ARCHIVO_GA = "ga.txt";
    public static final String K = "k";
    public static final String SOLUCION_NUMERO = "\nSolucion numero: ";
    public static final String CANTIDAD_BLOQUEOS = "\nCantidad de bloqueados: ";
    public static final String COSTO = "\nCosto: ";
    public static final String DISTANCIA = "\nDistancia: ";
    public static final String ESPECTRO_MAYOR = "\nEspectro Mayor: ";
    public static final String FITNESS = "\nFitness: ";
    public static final String ENLACES = "\nEnlaces: ";
    public static final String SALTO_LINEA = "\n";
    public static final String RUTEOS = "\nRuteos: ";
    public static final String FIN_ARCHIVO = "\n\n";
    public static final String EJECUCION = "\nTiempo total de ejecucion: ";
    public static final String UNIDAD_TIEMPO = " minutos.";
    public static final String CORRIDA = "_corridaNro_";
    public static final String EXTENSION =".txt";
    /*AGP*/
    public static final int CRITERIO_PARADA =10;
}
