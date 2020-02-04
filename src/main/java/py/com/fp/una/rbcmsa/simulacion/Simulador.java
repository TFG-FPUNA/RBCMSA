/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.simulacion;

import java.io.IOException;
import javax.inject.Inject;
import py.com.fp.una.rbcmsa.generadores.Generador;
import py.com.fp.una.rbcmsa.main;

/**
 *
 * @author Richard
 */
public class Simulador {
    @Inject
    Generador generadorBean;
    
    @Inject
    main mainBean;
    public void simular(int cantidadSimulaciones, int cantidadSolicitudes, int numNodos, int distanciaMin, int distanciaMax, String rutaArchivo, String nombreArchivo) throws IOException{
        for (int i = 0; i < cantidadSimulaciones; i++) {
            for (int j = 0; j < cantidadSolicitudes; j++) {
                this.generarArchivos(cantidadSolicitudes, numNodos, distanciaMin, distanciaMax, rutaArchivo, nombreArchivo);
            }
            mainBean.procesar(matriz, rutaArchivo, rutaArchivo);
            
            
        }
    }
    
    private void generarArchivos(int cantidadSolicitudes, int numNodos, int distanciaMin, int distanciaMax, String rutaArchivo, String nombreArchivo) throws IOException{
        generadorBean.GenerarArchivo(cantidadSolicitudes, numNodos, distanciaMin, distanciaMax, rutaArchivo, nombreArchivo);
    }
    
}
