 //CONSTANTES
 //numeros de nodos
 int n = ...;
 float t = ...;
 //numero de demandas
 int d = ...;
  //indices
 //indice de nodos
 range I  = 0 ..n-1;
 range J = 0 .. n-1;
 range K = 0 .. n-1;
 //indice de demanda
 range D = 1 .. d;
 //flujo de carga de demanda d
 float C[D] = ... ;
 //nodo de fuente de la demanda d
 int S[D] = ... ;
 //nodo destino de la demanda d
 int E[D]= ... ;

//variables de decision
//1si enlace(i,j) es usado por d y 0 otro casa
dvar int enlaceUsado [D][I][J] in 0..1;
//flujo de carga en (i,j) 
dvar int+ flujo[I][J];
//Funcion objetivo
minimize 
sum(i in I ,j in J)flujo[i][j]; 

constraint a;
constraint b;
//Restriccions
subject to {
a= forall(i in I, j in J){
   if(i != j){
    flujo[i][j] >=  sum(l in D)((enlaceUsado[l][i][j] * C[l])/t);
     }
   }  
      
 b =  forall( k in K, l in D){
   	if(S[l] == k){
      sum(j in J:j!=k)enlaceUsado[l][k][j] - sum(i in I: i!=k)enlaceUsado[l][i][k] == 1 ;
   }else if(E[l] == k){
      sum(j in J:j!=k)enlaceUsado[l][k][j] - sum(i in I: i!=k)enlaceUsado[l][i][k] == -1 ;
   }else if(E[l] != k &&  S[d]!= k)  {
      sum(j in J:j!=k)enlaceUsado[l][k][j] - sum(i in I: i!=k)enlaceUsado[l][i][k] == 0 ;
   }
     }         
 
}