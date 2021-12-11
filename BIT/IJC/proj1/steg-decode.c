/*
 *  Súbor: steg-decode.c
 *  Názov: IJC DU1, úloha b)
 *	Autor: Juraj Ondrej Dúbrava
 *  Dátum: 20.3.2016
 *  Popis: Dekódovanie správy zakódovanej v obrázku formátu PPM , bity znakov sa nachadzaju na prvociselnych bitoch	
 *  Preložené:	gcc 5.3.1 Fedora 23
 */
 
#include "bit_array.h"
#include "eratosthenes.h"
#include "ppm.h"
#include <limits.h>
#include <ctype.h>
#define MAX_LIMIT 2000*2000*3

int main(int argc, const char *argv[])
{
    if(argc !=2 )
    {
    	fatal_error("Nespravny pocet argumentov.");
    }

   const char *filename = argv[1];
   
   struct ppm *ppm_vut = ppm_read(filename);

   if(ppm_vut == NULL)
   {
   	fatal_error("Nespravny format vstupeho suboru.");
   }
   
   ba_create(primes,MAX_LIMIT);
   eratosthenes(primes);
  
   char letter[1];
   unsigned int count = 0;
   letter[0] = 0;
   int end = 0;

   /*zistenie hodnot z pola data leziacich na prvociselnych indexoch*/
     for(unsigned long int i=2; i < MAX_LIMIT; i++)
     {
     	if(ba_get_bit(primes,i) == 0)
     	{   
     		 
     		 unsigned int bit = (ppm_vut->data[i] & 1);
     		 if(bit == 1)
     		 {
     		 /*nacitanie potrebnych bitov na 1 znak*/ 	
     		 	unsigned char temp = 1;
     		 	temp <<= count;
     		 	letter[0] |= temp;
     		 }
         
            if(count < CHAR_BIT-1) count++;
            else if(letter[0] == 0)
            {   
            	 end = 1;
            	 break;
            }
            else if(count == CHAR_BIT-1)
            {
                if(!isprint(letter[0]))
                { 
                   free(ppm_vut);
                   fatal_error("Netlacitelny znak."); 
                }
                /*ak sa podarilo nacitat vsetky bity, vypise znak*/
                printf("%c",letter[0]);
                letter[0]=0;
                count = 0;
            }
     	}  
     }  
     /*vypisuje sa chyba v pripade neukoncenia znaku nulovym znakom*/ 
     if(end == 0)
     {
     	fatal_error("Sprava neukoncena nulovym znakom.");
     }        
   putchar('\n');  
   free(ppm_vut);

   return 0;
}  

