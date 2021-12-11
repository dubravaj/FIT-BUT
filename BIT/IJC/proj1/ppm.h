/*
 *  Súbor: ppm.h
 *  Názov: IJC DU1, úloha b)
 *	Autor: Juraj Ondrej Dúbrava
 *  Dátum: 20.3.2016
 *  Popis: Definícia funkcií na čítanie formátu ppm a na zápis ppm obrázka do súboru	
 *  Preložené:	gcc 5.3.1 Fedora 23
 */


#ifndef PPM_H
#define PPM_H

#include "error.h"
#include <ctype.h>


/*Struktura pre uchovanie dat z PPM obrazka*/

 struct ppm { 
 unsigned xsize; 
 unsigned ysize; 
 char data[];   // RGB bajty, celkom 3*xsize*ysize 
 };

/*Funkcia na nacitanie obrazovych dat PPM formatu*/
 struct ppm *ppm_read(const char *filename);

/*Funkcia na zapis dat formatu PPM do suboru*/
int ppm_write(struct ppm *p, const char *filename);

#endif
