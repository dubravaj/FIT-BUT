/*
 *  Súbor: primes.c
 *  Názov: IJC DU1, úloha a)
 *  Autor: Juraj Ondrej Dúbrava
 *  Dátum: 12.3.2016
 *  Popis: Vypocet poslednych 10 prvocisel pomocou bitoveho pola zo zadaneho limitu vypisanych vzostupne 
 *  Preložené: gcc 5.3.1 Fedora 23  
 */

#include "bit_array.h"
#include "eratosthenes.h"
#include "error.h"
#define LEAST_TEN 10
#define N 202000000
int main(void)
{  
   		ba_create(pole,N);
  		eratosthenes(pole);

      int count = 0;
      int index = 9;
      unsigned long int i,j;
      i=0;
      j=0;
      unsigned long int t[10];
     
     /*vypocet indexov poslednych 10 prvocisel z daneho rozsahu*/
      for(i=N-1; count < LEAST_TEN; i--)
      {
        if((ba_get_bit(pole,i)) == 0)
        {
          t[index]=i;
           count++;
           if(index >=0) index--;

        }
      }
      
      /*vypis poslednych 10 prvocisel vzostupne*/
      for(j=0; j < LEAST_TEN; j++ )
      {
        printf("%lu\n",t[j]);
      }
  		 
      
	return 0;


}

