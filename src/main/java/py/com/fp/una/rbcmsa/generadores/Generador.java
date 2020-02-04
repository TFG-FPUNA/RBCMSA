/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.generadores;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import py.com.fp.una.rbcmsa.archivos.Archivo;

/**
 *
 * @author Richard
 */
public class Generador {
    @Inject
    Archivo archivoBean;
    
    public void GenerarArchivo(int cantidadSolicitudes, int numNodos, int landaMin, int landaMax, String rutaArchivo, String nombrearchivo) throws IOException{
        List<String> solicitudes = this.generarSolicitudes(cantidadSolicitudes, numNodos, landaMin, landaMax);
        archivoBean.crearArchivo(rutaArchivo+nombrearchivo, solicitudes);
    }
    
    private List<String> generarSolicitudes(int cantSolicitudes, int numNodos, int landaMin, int landaMax){
        List<String> solicitudes = new ArrayList<>();
        for (int i = 0; i < cantSolicitudes; i++) {
            String solicitud = this.generarSolicitud(0, numNodos, landaMin, landaMax);
            solicitudes.add(solicitud);
        }
        return solicitudes;
    }
    
    private String generarSolicitud(int inicioRandom, int finRandom, int inicioLanda, int finLanda){
        int origen = (int) Math.floor(Math.random()*(finRandom - inicioRandom + 1)+inicioRandom);
        int destino = -1;
        boolean terminar = false;
        while (!terminar) {
           destino = (int) Math.floor(Math.random()*(finRandom - inicioRandom + 1)+inicioRandom);
            if (destino != origen) {
                terminar = true;
            }
        }
        
        int landa = (int) Math.floor(Math.random()*(finLanda - inicioLanda + 1)+inicioLanda);
        String solicitud = origen+"-"+destino+" "+landa;
        
        return solicitud;
    }
}
