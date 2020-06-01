/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.ilp;

/**
 *
 * @author Ivan
 */
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import py.com.fp.una.rbcmsa.grafos.model.Grafo;
import py.com.fp.una.rbcmsa.ilp.propios.Adaptaciones;
import py.com.fp.una.rbcmsa.peticion.model.PeticionBCM;

public class JPILP {
    @Inject
    Adaptaciones adaptacionesBeans;

    public void PILP(String ruta, String nombre, Integer k, List<PeticionBCM> peticionesFinales,
            Grafo grafo, String guardBan, int fTotal) throws IOException {
        
        adaptacionesBeans.preparaArchivoJPILP(ruta, nombre, k+"", peticionesFinales, grafo, guardBan, fTotal);
        /* Probamos la conexión de Java con CPLEX */
        String argumentos[] = {"-v", "./src/JPILP/JP-ILP.mod", "./src/resultados/JPILP.dat", "./src/resultados/salidaCplexILP1.txt"};
        

        OplRunILP.main(argumentos);        
    }
}
