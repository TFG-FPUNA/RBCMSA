/*********************************************
 * OPL 12.6.0.0 Model
 * Author: USER- ACER
 * Creation Date: 16/06/2018 at 13:39:16
 *********************************************/

  // constantes
 
 int K = ...;
 int SD = ...;
 //int Ftotal = ...;
 float Ftotal = ...;
 int G = ...;
 //int s2d2 = ...;
 
 //variables
 
 range p = 1..K;
 range sd = 1.. SD;
 range sdp = 1..K*SD;
 //range S2D2 = 1 .. s2d2;
 
 int alfa[sd][p]=...;
 int l[sdp][sdp] = ...;
 
 dvar int x[sd][p] in 0..1;
 
 dvar int+ f[sd];
 
 dvar int delta[sd][sd] in 0..1;

 
 dvar int+ c;
 
 
 
 minimize c;
 
 
 subject to {
 	forall(s_d in sd) { 
 	 	c >= f[s_d] + sum(p in p) alfa[s_d][p] * x[s_d][p]; 
 	 	sum(p in p) x[s_d][p] == 1;	 	
 	}
 	
	forall(t in sd){ 
		forall(u in sd){
			forall(a in p){
				forall(b in p){
 					if((l[t*K-K+a][u*K-K+b] == 1) && (t != u)) 
 		 			{
 		 				delta[t][u] + delta[u][t] == 1;
	 					f[t] + alfa[t][a] * x[t][a] + G <= f[u] + (Ftotal + G) * (1 - delta[t][u] + 2 - x[t][a] - x[u][b]);
						f[u] + alfa[u][b] * x[u][b] + G <= f[t] + (Ftotal + G) * (1 - delta[u][t] + 2 - x[t][a] - x[u][b]);   	 		
 		 			}
    			} 		 		
 			}
 		} 		 	
	}
	 
	forall(s_d in sd){ 
			f[s_d] <= Ftotal-1;
	}
}

