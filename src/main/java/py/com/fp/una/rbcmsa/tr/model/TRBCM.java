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

    @Override
    public String toString() {
        return "TRBCM{" + "modulacion=" + modulacion + ", baudios=" + baudios + ", FEC=" + FEC + ", landaCalculado=" + landaCalculado + '}';
    }
}
