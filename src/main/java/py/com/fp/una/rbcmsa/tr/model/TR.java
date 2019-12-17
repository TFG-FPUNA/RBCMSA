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
public class TR {
    private String modulacion;
    private Integer baudios;
    private Integer noFEC;
    private Integer T1FEC;
    private Integer T2FEC;
    private Integer T3FEC;
    private Integer T4FEC;

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

    public Integer getNoFEC() {
        return noFEC;
    }

    public void setNoFEC(Integer noFEC) {
        this.noFEC = noFEC;
    }

    public Integer getT1FEC() {
        return T1FEC;
    }

    public void setT1FEC(Integer T1FEC) {
        this.T1FEC = T1FEC;
    }

    public Integer getT2FEC() {
        return T2FEC;
    }

    public void setT2FEC(Integer T2FEC) {
        this.T2FEC = T2FEC;
    }

    public Integer getT3FEC() {
        return T3FEC;
    }

    public void setT3FEC(Integer T3FEC) {
        this.T3FEC = T3FEC;
    }

    public Integer getT4FEC() {
        return T4FEC;
    }

    public void setT4FEC(Integer T4FEC) {
        this.T4FEC = T4FEC;
    }

    @Override
    public String toString() {
        return "TR{" + "modulacion=" + modulacion + ", baudios=" + baudios + ", noFEC=" + noFEC + ", T1FEC=" + T1FEC + ", T2FEC=" + T2FEC + ", T3FEC=" + T3FEC + ", T4FEC=" + T4FEC + '}';
    }      
    
}
