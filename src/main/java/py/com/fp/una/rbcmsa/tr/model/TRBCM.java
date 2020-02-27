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
public class TRBCM {

    private String modulacion;
    private Integer baudios;
    private Integer FEC;
    private Integer landaCalculado;
    private Integer tamanhoFS;
    private Integer formatoModulacion;

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

    public Integer getFEC() {
        return FEC;
    }

    public void setFEC(Integer FEC) {
        this.FEC = FEC;
    }

    public Integer getLandaCalculado() {
        return landaCalculado;
    }

    public void setLandaCalculado(Integer landaCalculado) {
        this.landaCalculado = landaCalculado;
    }

    public Integer getTamanhoFS() {
        return tamanhoFS;
    }

    public void setTamanhoFS(Integer tamanhoFS) {
        this.tamanhoFS = tamanhoFS;
    }

    public Integer getFormatoModulacion() {
        return formatoModulacion;
    }

    public void setFormatoModulacion(Integer formatoModulacion) {
        this.formatoModulacion = formatoModulacion;
    }

    @Override
    public String toString() {
        return "TRBCM{" + "modulacion=" + modulacion + ", baudios=" + baudios + ", FEC=" + FEC + ", landaCalculado=" + landaCalculado + ", tamanhoFS=" + tamanhoFS + ", formatoModulacion=" + formatoModulacion + '}';
    }

    

    
}
