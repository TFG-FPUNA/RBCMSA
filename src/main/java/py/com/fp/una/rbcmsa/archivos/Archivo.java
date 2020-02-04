/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.archivos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import py.com.fp.una.rbcmsa.peticion.model.Peticion;
import py.com.fp.una.rbcmsa.tr.model.TR;

/**
 *
 * @author Richard
 */
public class Archivo {

    public void crearArchivo(String ruta, List<String> solicitudes) throws IOException {

        File file = new File(ruta);
        // Si el archivo no existe es creado
        if (!file.exists()) {
            file.createNewFile();
        }else{
            return;
        }
        FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
        for (String solicitud : solicitudes) {
            bw.write(solicitud+"\n");
        }
         bw.close();

    }
    
    public Properties cargarPropiedades() throws FileNotFoundException, IOException{
        Properties p = new Properties();
        p.load(new FileReader("C:\\Users\\Richard\\Documents\\NetBeansProjects\\RBCMSA\\src\\main\\resources\\properties.properties"));
        return p;
    }

    public Object cargarArchivo(String nombreArchivo, String separador, int indicador, int filas, int columnas) {

        HashMap<String, TR> TRS = new HashMap();
        HashMap<String, Peticion> peticiones = new HashMap();
        int[][] matriz = new int[filas][columnas];
        try {
            FileReader fr = new FileReader(nombreArchivo);
            BufferedReader br = new BufferedReader(fr);

            String linea;
            int i = 0;
            while ((linea = br.readLine()) != null) {
                //System.out.println(linea);

                switch (indicador) {
                    case 0:
                        TR tr = mapearLinea(linea, separador);
                        TRS.put(i + "", tr);
                        i++;
                        break;
                    case 1:
                        Peticion peticion = mapearPeticion(linea, separador);
                        peticiones.put(i + "", peticion);
                        i++;
                        break;
                    case 2:
                        String[] partes = linea.split(separador);
                        for (int j = 0; j < partes.length; j++) {
                            matriz[i][j] = Integer.parseInt(partes[j]);
                        }
                        i++;
                        break;
                    default:
                        break;
                }

            }

            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Excepcion leyendo fichero " + nombreArchivo + ": " + e);
        }
        switch (indicador) {
            case 0:
                return TRS;
            case 1:
                return peticiones;
            case 2:
                return matriz;
            default:
                return null;
        }
    }

    private TR mapearLinea(String linea, String separador) {
        TR tr = new TR();
        String[] partes = linea.split(separador);
        tr.setDistanciaSoportada(Integer.valueOf(partes[0]));
        tr.setFormatoModulacion(Integer.valueOf(partes[1]));
        tr.setModulacion(partes[2]);
        tr.setBaudios(Integer.valueOf(partes[3]));
        tr.getFEC().add(Integer.valueOf(partes[4]));
        tr.getFEC().add(Integer.valueOf(partes[5]));
        tr.getFEC().add(Integer.valueOf(partes[6]));
        tr.getFEC().add(Integer.valueOf(partes[7]));
        tr.getFEC().add(Integer.valueOf(partes[8]));

        return tr;
    }

    private Peticion mapearPeticion(String linea, String separador) {
        Peticion peticion = new Peticion();
        String[] partes = linea.split(separador);
        peticion.setPedido(partes[0]);
        peticion.setLanda(Integer.valueOf(partes[1]));

        return peticion;
    }
}
