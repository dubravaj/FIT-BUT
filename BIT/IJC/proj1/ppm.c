/*
 *  Súbor:  ppm.c
 *  Názov: IJC DU1, úloha b)
 *  Autor:  Juraj Ondrej Dúbrava
 *  Dátum:  20.3.2016
 *  Popis:  Implementácia funckií na čítanie formátu PPM a na jeho zápis
 *  Preložené:  gcc 5.3.1 Fedora 23  
 */


#include <stdlib.h>
#include <stdio.h>
#include "ppm.h"

struct ppm *ppm_read(const char *filename)
{
  FILE *ppmImage = fopen(filename,"rb");
     
    /*otvaranie suboru na citanie bin.dat*/ 
    if(ppmImage == NULL)
    {
        warning_msg("Nepodarilo sa otvorit zadany subor.");
        return NULL;
    }
    
    /*nacitanie dat z hlavicky*/
    unsigned xsize,ysize,color;
   
    int check_Header = fscanf(ppmImage,"P6\n%u\n%u %u\n", &xsize, &ysize,&color);

    /*overenie formatu hlavicky*/
    if(check_Header != 3 || color != 255)
    {
      fclose(ppmImage);
    	warning_msg("Nespravny format hlavicky suboru.");
    	return NULL;
    }
  
  
  struct ppm *ppm_struct = malloc(sizeof(struct ppm) + 3*xsize*ysize);
  
  /*overenie alokacie pamate pre ppm strukturu*/
  if(ppm_struct == NULL)
  {
    fclose(ppmImage);
    free(ppm_struct);
  	warning_msg("Nepodarilo sa alokovanie pamate.");
  	return NULL;
  }
  
  /*nacitanie binarnych dat a overenie ich nacitania*/
  if(fread(ppm_struct->data,1,3*xsize*ysize, ppmImage) != 3*xsize*ysize)
  {   
      fclose(ppmImage);
      free(ppm_struct);
      warning_msg("Nepodarilo sa nacitat binarne data.");
      return NULL;
  }
 
    ppm_struct->xsize = xsize;
    ppm_struct->ysize = ysize;
    
    fclose(ppmImage);

 return ppm_struct;

}

int ppm_write(struct ppm *p, const char *filename)
{

  FILE *ppmImage = fopen(filename, "wb");

  if(ppmImage == NULL)
  {
     warning_msg("Chyba pri otvarani suboru, nepodarilo sa otvorit.");
     fclose(ppmImage);
     return -1;	
  }
 
 /*zapis hlavicky formatu ppm do suboru*/
  fprintf(ppmImage,"P6\n%u %u\n255\n",p->xsize,p->ysize);

  /*zapis binarnych dat do suboru*/
  if(fwrite(p->data,1,3*(p->xsize)*(p->ysize),ppmImage) != 3*(p->xsize)*(p->ysize))
  {
  	warning_msg("Nepodarilo sa zapisat binarne data.");
  	fclose(ppmImage);
  	return -1;
  }

return 0;
}



