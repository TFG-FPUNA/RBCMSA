/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import py.com.fp.una.rbcmsa.grafos.business.BuscarCaminos;
import py.com.fp.una.rbcmsa.grafos.model.Rutas;

/**
 *
 * @author Richard
 */
public class main {
    
    public static void main(String[] args) throws CloneNotSupportedException {
        //int [][] matriz = {{0,1,0,0,0,1},{1,0,1,0,1,0},{0,1,0,1,0,0},{0,0,1,0,1,0},{0,1,0,1,0,1},{1,0,0,0,1,0}};
        int [][] matriz = {{0,1,0,1},{1,0,1,1},{0,1,0,1},{1,1,1,0}};
        //int [][] matriz = {{0,1,0,0},{0,0,1,1},{0,0,0,1},{1,0,0,0}};//dirigido
        
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[0].length; j++) {
                System.out.print(matriz[i][j] + " ");
            }
            System.out.println("");
        }
        BuscarCaminos bean = new BuscarCaminos(); 
        HashMap <String, Rutas> rutasCompletas = bean.busqueda(matriz);
        for (Map.Entry<String, Rutas> entry : rutasCompletas.entrySet()) {
            String key = entry.getKey();
            Rutas value = entry.getValue();
            System.out.println("Ruta:" + value);
            
        }
//        Weld weld = new Weld();
//        try {
//            WeldContainer container = weld.initialize();
//            container.select(BuscarCaminos.class).get().busqueda(matriz);
//        }catch(Exception e){
//            e.printStackTrace();
//        } 
////        finally {
////            weld.shutdown();
////        }
    }

//        @Inject
//        BuscarCaminos bean;
//        
//        
//        public void procesarTest() throws Exception {
//            
//            List<Rutas> rutas = bean.busqueda(matriz);
//            for (Rutas ruta : rutas) {
//                System.out.println("Ruta:" + ruta);
//            }
//
//        }
    
}
