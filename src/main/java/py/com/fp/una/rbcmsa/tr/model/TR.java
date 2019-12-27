/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.tr.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Richard
 */
public class TR {
    private Integer distanciaSoportada;
    private Integer formatoModulacion;
    private String modulacion;
    private Integer baudios;
    private List<Integer> FEC;
    private Integer seleccionado;

    public TR() {
        this.FEC = new ArrayList<>();
    }

    public Integer getDistanciaSoportada() {
        return distanciaSoportada;
    }

    public void setDistanciaSoportada(Integer distanciaSoportada) {
        this.distanciaSoportada = distanciaSoportada;
    }

    public Integer getFormatoModulacion() {
        return formatoModulacion;
    }

    public void setFormatoModulacion(Integer formatoModulacion) {
        this.formatoModulacion = formatoModulacion;
    }

    
    public String getModulacion() {
        return modulacion;
    }

    public void setModulacion(String modulacion) {
        this.modulacion = modulacion;
    }

    public Integer getBaudios() {
        return baudios;
    }

    public void setBaudios(Integer baudios) {
        this.baudios = baudios;
    }

    public List<Integer> getFEC() {
        return FEC;
    }

    public void setFEC(List<Integer> FEC) {
        this.FEC = FEC;
    }

    public Integer getSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(Integer seleccionado) {
        this.seleccionado = seleccionado;
    }

    @Override
    public String toString() {
        return "TR{" + "distanciaSoportada=" + distanciaSoportada + ", formatoModulacion=" + formatoModulacion + ", modulacion=" + modulacion + ", baudios=" + baudios + ", FEC=" + FEC + ", seleccionado=" + seleccionado + '}';
    }

}
