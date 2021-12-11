/*
 *  Súbor: eratosthenes.c
 *  Názov: IJC DU1, úloha a)
 *  Autor: Juraj Ondrej Dúbrava
 *  Dátum: 12.3.2016
 *  Popis: Implementácia výpočtu prvočísiel pomocou Eratosthenovho sita    
 *  Preložené: gcc 5.3.1 Fedora 23  
 */

#include "eratosthenes.h"
#include "bit_array.h"

void eratosthenes(bit_array_t pole)
{
    unsigned long int i,j;   
	ba_set_bit(pole,0,1);
	ba_set_bit(pole,1,1);
 
 /*algoritmus Eratosthenovho sita na pocitanie prvocisel pomocou bitoveho pola*/    
	for(i=2; i < ba_size(pole); i++)
	{
   		 ba_set_bit(pole,i,0);
	}

	for(i=2; i < ba_size(pole); i++)
	{
    	if((ba_get_bit(pole,i)) == 0)
    	{
        	for(j=i*i; j < ba_size(pole); j+=i)
     		{
     			ba_set_bit(pole,j,1);
     		}
    	} 
 	} 	
}
