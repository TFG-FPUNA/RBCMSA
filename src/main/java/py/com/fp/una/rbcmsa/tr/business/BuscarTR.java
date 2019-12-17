/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.tr.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import py.com.fp.una.rbcmsa.tr.model.TR;
import py.com.fp.una.rbcmsa.tr.model.TRBCM;

/**
 *
 * @author Richard
 */
public class BuscarTR {
    public TRBCM buscarTR(Integer distancia, HashMap<String, TR> TRtotales){
        List<TR> TRSeleccionados = new ArrayList<>();
        seleccionarTRCandidatos(distancia, TRtotales, TRSeleccionados);
        TRBCM trFinal = new TRBCM();
        if (!TRSeleccionados.isEmpty()) {
            for (TR TRSeleccionado : TRSeleccionados) {
                
            }
            //llamar al que calcula
            
        }
        return trFinal;
        
    }
    private List<TR> seleccionarTRCandidatos(Integer distancia, HashMap<String, TR> TRtotales, List<TR> seleccionTR){
        //List<TR> seleccionTR = new ArrayList<>();
        
        for (Map.Entry<String, TR> entry : TRtotales.entrySet()) {
            String key = entry.getKey();
            TR value = entry.getValue();
            
            Integer seleccionado = verificarMenor(distancia, value);
            if (seleccionado != 0) {
                seleccionTR.add(value);
            }
        }
        return seleccionTR;
        
    }
    
    private Integer verificarMenor(Integer distancia, TR tr){
        Integer  mayor = 0;
        if (distancia < tr.getNoFEC()) {
            mayor = tr.getNoFEC();
        }
        if (distancia < tr.getT1FEC() && tr.getT1FEC() < mayor) {
            mayor = tr.getT1FEC();
        }
        if (distancia < tr.getT2FEC() && tr.getT2FEC() < mayor) {
            mayor = tr.getT2FEC();
        }
        if (distancia < tr.getT3FEC() && tr.getT3FEC() < mayor) {
            mayor = tr.getT3FEC();
        }
        if (distancia < tr.getT4FEC() && tr.getT4FEC() < mayor) {
            mayor = tr.getT4FEC();
        }
        return mayor;
    }
    
}
