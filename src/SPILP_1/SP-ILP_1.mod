/*********************************************
 * OPL 12.6.0.0 Model
 * Author: ACER
 * Creation Date: 11/08/2016 at 08:18:20
 *********************************************/

 // constantes
 
 int K = ...;
 int SD = ...;
 int G = ...;
 int E = ...;

 //indices
 
 range p = 1..K;
 range sd = 1.. SD;
 range sdp = 1..K*SD;

 range l = 1..E;
 //dato entrada
 int alfa[sd][p]=...;

 int R[sdp][l]=...;
 //variables
 dvar int x[sd][p] in 0..1;

 dvar int+ c;
 
 dvar int F[l][sd];

 minimize c;

 subject to {
 	//se verifica cuantos FS utiliza cada enlace por cada peticion
 	forall(e in l, s_d in sd) { 	
        //c >= sum(s_d in sd, p in p) R[s_d*K-K+p][e]*x[s_d][p]*(alfa[s_d][p] + G);
        //Se le resta la cantidad de guarbands al ultimo ya que no hace falta al final
 		//c >= sum(s_d in sd, p in p) R[s_d*K-K+p][e]*x[s_d][p]*(alfa[s_d][p] + G)-G; 	
                F[e][s_d] == sum(p1 in p) R[s_d*K-K+p1][e] * x[s_d][p1] * alfa[s_d][p1] + 
				             sum(p1 in p) R[s_d*K-K+p1][e] * x[s_d][p1] * G;
 	}; 
    //determina el numero maximo de FS sobre toda la red    
    forall(e in l) { 	
        c >= sum(s_d in sd) F[e][s_d]- G;
 	}; 
 	//determinacion de que ruta usa cada solicitud
 	forall(s_d in sd) {
 		sum(p1 in p) x[s_d][p1] == 1;
	};	
 	
}
