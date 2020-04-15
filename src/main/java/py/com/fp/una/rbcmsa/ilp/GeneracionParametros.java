/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.ilp;

import java.io.IOException;
import java.util.Random;

/**
 *
 * @author miguel
 */
public class GeneracionParametros {
    
    public static void main(String[] args) throws IOException {
        Double probabilidadMutacion = 0.5;
        Double cantidadPoblacion = 8.0;
        Random r = new Random();
        Double rm = r.nextGaussian();
        Double rp = r.nextGaussian()*10;
        probabilidadMutacion += rm;
        cantidadPoblacion += rp;
        //llamar a main;
        double c = 0.9; //constante.
        //DOuble probabilidadMutacionesExitosas; 
        System.out.println(Math.random());
        
    }
    
}
