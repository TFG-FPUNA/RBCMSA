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

/**
 *
 * @author ortizsan
 */
public class CSVWriter {

    public static void generarCabeceraCSV() {

//        String csvFile
//                = "src"
//                + File.separator
//                + "main"
//                + File.separator
//                + "resources"
//                + File.separator
//                + "out.csv";
        String csvFile = "datospreparadosFinal.csv";

        FileWriter writer = null;

        File file = null;

        try {

            file = new File(csvFile);
            file.createNewFile();

            writer = new FileWriter(file);

            /*cabecera del archivo*/
            CSVUtils.writeLine(writer, Arrays.asList(
                    "Tiempo",
                    "Cantidad Solicitud",
                    "Carga Erlang",
                    "Cantidad de bloqueos",
                    "Costo",
                    "Espectro",
                    "#TRANSPONER",
                    "Tiempo milisegundos"
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

    public static void addToCSV(int tiempo, int cantSolicitud,int cargaErlang ,double cantBloqueados, double costo, double espectro, double numTransponder, long milisegundos) {

//        String csvFile
//                = "src"
//                + File.separator
//                + "main"
//                + File.separator
//                + "resources"
//                + File.separator
//                + "out.csv";
        String csvFile = "datospreparadosFinal.csv";
        FileWriter writer = null;

        File file = null;

        try {

            file = new File(csvFile);

            writer = new FileWriter(file, true);

            /*cabecera del archivo*/
            CSVUtils.writeLine(writer, Arrays.asList(
                    String.valueOf(tiempo),
                    String.valueOf(cantSolicitud),
                    String.valueOf(cargaErlang),
                    String.valueOf(cantBloqueados),
                    String.valueOf(costo),
                    String.valueOf(espectro),
                    String.valueOf(numTransponder),
                    String.valueOf(milisegundos)
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

