/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.ag.ysa.adaptaciones;

import py.com.fp.una.rbcmsa.ilp.propios.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import py.com.fp.una.rbcmsa.archivos.Archivo;
import py.com.fp.una.rbcmsa.grafos.model.Grafo;
import py.com.fp.una.rbcmsa.ilp.propios.model.AlphaR;
import py.com.fp.una.rbcmsa.peticion.model.CaminoTR;
import py.com.fp.una.rbcmsa.peticion.model.PeticionBCM;

/**
 *
 * @author Richard
 */
public class AdaptacionesAG {

    @Inject
    Archivo archivoBean;
    private static final String FIN_LINEA = "\n";
    private static final String SEPARADOR = "\t";
    private static final String SEPARADOR_CAMINO = "-";

    public void generarEntradaAG(List<PeticionBCM> peticionesFinales, int k, String ruta, String nombre) throws IOException {
        List<String> archivoAG = new ArrayList<>();
        for (PeticionBCM peticionFinal : peticionesFinales) {
            for (int i = 0; i < k; i++) {
                if (peticionFinal.getCaminosTR().size() >= k) {
                    CaminoTR caminoTR = peticionFinal.getCaminosTR().get(i);
                    String caminoAG = "";
                    for (Integer nodo : caminoTR.getCamino().getNodos()) {
                        caminoAG = caminoAG + nodo + SEPARADOR_CAMINO;
                    }
                    caminoAG = caminoAG.substring(0, caminoAG.length() - 1);
                    String origen = caminoTR.getCamino().getNodos().get(0) + "";
                    String destino = caminoTR.getCamino().getNodos().get(caminoTR.getCamino().getNodos().size() - 1) + "";
                    String FS = caminoTR.getTrfinal().getTamanhoFS() + "";
                    String distancia = caminoTR.getCamino().getDistancia() + "";
                    Integer calculo = caminoTR.getCamino().getDistancia() * peticionFinal.getPeticionOriginal().getLanda();

                    String lineAG = origen + destino
                            + SEPARADOR + origen
                            + SEPARADOR + destino
                            + SEPARADOR + caminoAG
                            + SEPARADOR + FS
                            + SEPARADOR + distancia
                            + SEPARADOR + calculo
                            ;
                    archivoAG.add(lineAG);
                }

            }

        }
        archivoBean.crearArchivo(ruta + nombre, archivoAG, true);
    }
}
