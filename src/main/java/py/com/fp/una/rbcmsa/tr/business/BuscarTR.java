/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.tr.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import py.com.fp.una.rbcmsa.peticion.model.Peticion;
import static py.com.fp.una.rbcmsa.tr.model.Constantes.*;
import py.com.fp.una.rbcmsa.tr.model.TR;
import py.com.fp.una.rbcmsa.tr.model.TRBCM;

/**
 *
 * @author Richard
 */
public class BuscarTR {

    public TRBCM buscarTR(Integer distancia, HashMap<String, TR> TRtotales, Peticion peticion, Double tamanhoFS) {
        
        TR TRSeleccionado = seleccionarTRCandidatos(distancia, TRtotales, peticion.getLanda());
        System.out.println("seleccionado: " + TRSeleccionado);
        TRBCM trFinal = new TRBCM();
        trFinal.setBaudios(TRSeleccionado.getBaudios());
        trFinal.setFEC(TRSeleccionado.getSeleccionado());
        trFinal.setModulacion(TRSeleccionado.getModulacion());
        
        Double tamanhoFSRequerido = calcularNumeroFS(peticion.getLanda(), OH_FEC[TRSeleccionado.getSeleccionado()], tamanhoFS, TRSeleccionado.getFormatoModulacion());
        System.out.println("tamanho: " + tamanhoFSRequerido);

        trFinal.setTamanhoFS(tamanhoFSRequerido.intValue());
        return trFinal;

    }

    private Double calcularNumeroFS(Integer landaInicial, Double porcentajeFEC, Double tamanhoFS, Integer nivelModulacion) {
//        System.out.println("landaInicial: " + landaInicial);
//
//        System.out.println("porcentaje: " + porcentajeFEC);
//
//        System.out.println("tamanhoFs: " + tamanhoFS);
//
//        System.out.println("nivel modulacion: " + nivelModulacion);
        Double resultado = (landaInicial * (1 + porcentajeFEC)) / (tamanhoFS * nivelModulacion);
        resultado = Math.ceil(resultado);
        resultado = resultado + 1;
        return resultado;
    }

    private TR seleccionarTRCandidatos(Integer distancia, HashMap<String, TR> TRtotales, Integer landaInicial) {
        List<TR> seleccionTR = new ArrayList<>();
        for (Map.Entry<String, TR> entry : TRtotales.entrySet()) {
            String key = entry.getKey();
            TR tr = entry.getValue();

            double seleccionado = verificarMenor(distancia, tr);
            if (seleccionado != 0) {
                if (landaInicial <= tr.getDistanciaSoportada()) {
                    seleccionTR.add(tr);
                }
                
            }
        }

        //encontrar el menor
        //System.out.println("seleccionado: " + seleccionTR);
        Collections.sort(seleccionTR, (s2, s1) -> Integer.compare(s2.getFEC().get(s2.getSeleccionado()), s1.getFEC().get(s1.getSeleccionado())));
        return seleccionTR.get(0);

    }

    private double verificarMenor(Integer distancia, TR tr) {
        double mayor = 0;
        if (distancia < tr.getFEC().get(NO_FEC)) {
            mayor = tr.getFEC().get(NO_FEC);
            tr.setSeleccionado(NO_FEC);
            return mayor;
        }
        if (distancia < tr.getFEC().get(TIPO_1_FEC)) {
            mayor = tr.getFEC().get(TIPO_1_FEC);
            tr.setSeleccionado(TIPO_1_FEC);
            return mayor;
        }
        if (distancia < tr.getFEC().get(TIPO_2_FEC)) {
            mayor = tr.getFEC().get(TIPO_2_FEC);
            tr.setSeleccionado(TIPO_2_FEC);
            return mayor;
        }
        if (distancia < tr.getFEC().get(TIPO_3_FEC)) {
            mayor = tr.getFEC().get(TIPO_3_FEC);
            tr.setSeleccionado(TIPO_3_FEC);
            return mayor;
        }
        if (distancia < tr.getFEC().get(TIPO_4_FEC)) {
            mayor = tr.getFEC().get(TIPO_4_FEC);
            tr.setSeleccionado(TIPO_4_FEC);
            return mayor;
        }
        return mayor;
    }

}
