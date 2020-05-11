/*********************************************
 * OPL 12.6.0.0 Model
 * Author: ACER
 * Creation Date: 11/08/2016 at 08:18:20
 *********************************************/

 // constantes
 
 int K = ...;
 int SD = ...;
 float Ftotal = ...;
 int G = ...;
 //int s2d2 = ...;
 
 //variables

 range sd = 1.. SD;
 //range S2D2 = 1 .. s2d2;
 int l[sd][sd] = ...;
 
 int alfa[sd]=...;
 
 dvar int+ f[sd];
 
 dvar int delta[sd][sd] in 0..1;

 
 dvar int+ c;
 
 
 
 minimize c;
 
 
 subject to {
 	forall(s_d in sd) {
 	 	c >= f[s_d] + alfa[s_d]; 	 	
 	};
 	 
 	/*
 	forall(s_d in sd) {
 		sum(p in p) x[s_d][p] == 1;
	};	
 	*/
	
	forall(t in sd){
		forall(u in sd){
			if((l[t][u] == 1) && (t != u)) 
 		 	{
 		 		delta[t][u] + delta[u][t] == 1;
 		 		f[u]-f[t] <= Ftotal * delta[t][u];
 		 		f[t]-f[u] <= Ftotal * delta[u][t];
 		 	}			 		
 		} 		 	
	}; 
	
	forall(t in sd){
		forall(u in sd){
			if((l[t][u] == 1) && (t != u)) 
	 		{
	 			f[t] + alfa[t] + G - f[u] <= 
	 						(Ftotal + G) * (1 - delta[t][u]);
				f[u] + alfa[u] + G - f[t] <= 
							(Ftotal + G) * (1 - delta[u][t]);     
			}							 					
		}
	};
	
	forall(s_d in sd){
			f[s_d] <= Ftotal-1;
	}
}