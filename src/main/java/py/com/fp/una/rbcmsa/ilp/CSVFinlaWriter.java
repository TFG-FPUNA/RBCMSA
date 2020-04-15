/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.ilp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import py.com.fp.una.rbcmsa.ilp.ResultadoEstatico;

/**
 *
 * @author SantiagoMiguel
 */
public class CSVFinlaWriter {
    
     public static void generarCabeceraCSV() {

//        String csvFile
//                = "src"
//                + File.separator
//                + "main"
//                + File.separator
//                + "resources"
//                + File.separator
//                + "out.csv";
        String csvFile = "datosFinal.csv";

        FileWriter writer = null;

        File file = null;

        try {

            file = new File(csvFile);
            file.createNewFile();

            writer = new FileWriter(file);

            /*cabecera del archivo*/
            CSVUtils.writeLine(writer, Arrays.asList(
                    "Cantidad Total De solicitudes",
                    "Probabilidad Bloqueo",
                    "Cantidad Transpondedores",
                    "Costo",
                    "Cantidad Espectro", 
                    "Earlang", 
                    "Tiempo"
            ));

        } catch (IOException ex) {
            ex.printStackTrace();

        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

    }

    public static void addToCSV(int cantSol,double bloque, double transponder,double costo ,double Espectro, int earlang, long milisengundos) {

//        String csvFile
//                = "src"
//                + File.separator
//                + "main"
//                + File.separator
//                + "resources"
//                + File.separator
//                + "out.csv";
        String csvFile = "datosFinal.csv";
        FileWriter writer = null;

        File file = null;

        try {

            file = new File(csvFile);

            writer = new FileWriter(file, true);

            /*cabecera del archivo*/
            CSVUtils.writeLine(writer, Arrays.asList(
                    String.valueOf(cantSol),
                    String.valueOf(bloque),
                    String.valueOf(transponder),
                    String.valueOf(costo),
                    String.valueOf(Espectro),
                    String.valueOf(earlang),
                    String.valueOf(milisengundos)
                   
            ));

        } catch (IOException ex) {
            ex.printStackTrace();

        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

    }
    
    public static void generarCabeceraCSVEstatico(int i) {

//        String csvFile
//                = "src"
//                + File.separator
//                + "main"
//                + File.separator
//                + "resources"
//                + File.separator
//                + "out.csv";
        String csvFile = "datosFinalEstatico_prueba" + i +   ".csv";

        FileWriter writer = null;

        File file = null;

        try {

            file = new File(csvFile);
            file.createNewFile();

            writer = new FileWriter(file);

            /*cabecera del archivo*/
            CSVUtils.writeLine(writer, Arrays.asList(
                    "Cantidad de solicitudes",
                    "Espectro total",
                    "Cantidad Transpondedores",
                    "Tiempo computacional"
            ));

        } catch (IOException ex) {
            ex.printStackTrace();

        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

    }
    
    public static void addToCSVEstatico(ResultadoEstatico resultado, int i) {

//        String csvFile
//                = "src"
//                + File.separator
//                + "main"
//                + File.separator
//                + "resources"
//                + File.separator
//                + "out.csv";
        String csvFile = "datosFinalEstatico_prueba" + i +   ".csv";
        FileWriter writer = null;

        File file = null;

        try {

            file = new File(csvFile);

            writer = new FileWriter(file, true);

            /*cabecera del archivo*/
            CSVUtils.writeLine(writer, Arrays.asList(
                    String.valueOf(resultado.cantSolicitudes),
                    String.valueOf(resultado.espectro),
                    String.valueOf(resultado.numTotalTransponder),
                    String.valueOf(resultado.tiempo)
                   
            ));

        } catch (IOException ex) {
            ex.printStackTrace();

        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

    }
}
