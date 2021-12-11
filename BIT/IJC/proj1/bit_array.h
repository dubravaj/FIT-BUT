/*
 *  Súbor:	bit_array.h
 *	Názov:	IJC DU1, úloha a)
 *	Autor:  Juraj Ondrej Dúbrava
 *  Dátum:  20.3.3016
 *  Popis:  Definícia makier a funkcií pre prácu s bitovým polom	
 *  Preložené: 	gcc 5.3.1 Fedora 23 
 */

#include <limits.h> 
#include "error.h"

#ifndef BIT_ARRAY_H
#define BIT_ARRAY_H 
typedef unsigned long bit_array_t[];

/*Pocet bitov v unsigned long */
#define RANGE	(sizeof(unsigned long)*CHAR_BIT)

/*Zisti potrebny pocet bajtov pre pole */
#define Array_size(velikost)   ((velikost % RANGE == 0) ? (velikost / RANGE) : ((velikost / RANGE) + 1)) 

/*Zisti index pola na ktorom sa nachadza index 'i' */
#define TYPE_ELEMENTS(p,i) (i/GET_TYPE_BITS(p))

/*Zisti pocet bitov typu daneho bitoveho pola */
#define GET_TYPE_BITS(jmeno_pole) (CHAR_BIT*sizeof(jmeno_pole[0])) 

/*Definuje a nuluje bitove pole zadanej velkosti */
#define ba_create(jmeno_pole,velikost)	 unsigned long jmeno_pole[Array_size(velikost)+1] = {(unsigned long)(velikost),}

#ifndef USE_INLINE

/*Pomocne makro na nastavenie hodnoty bitu na zadanom indexe podla hodnoty vyrazu, nekontroluje medze pola*/
#define DU1_SET_BIT(p,i,b) ((b) ? (p[TYPE_ELEMENTS(p,i)+1] |= (1UL << (i % GET_TYPE_BITS(p)))) : (p[TYPE_ELEMENTS(p,i)+1] &= ~(1UL << (i % GET_TYPE_BITS(p)))))

/*Pomocne makro na zistenie hodnoty bitu na indexe i v zadanom poli p, nekontroluje medze pola*/
#define DU1_GET_BIT(p,i) (p[TYPE_ELEMENTS(p,i)+1] & (1UL << (i % GET_TYPE_BITS(p))))
					
/*Vrati velkost pola v bitoch */
#define ba_size(jmeno_pole)  (jmeno_pole[0])

/*Nastavi bit na zadanom indexe podla hodnoty vyrazu*/
#define ba_set_bit(jmeno_pole, index, vyraz) (((index >= jmeno_pole[0]) || (index < 0)) ? (fatal_error("Index %ld mimo rozsah 0..%ld ",(long)index,(long)jmeno_pole[0]-1),1) : (DU1_SET_BIT(jmeno_pole,index,vyraz)))						                                          	        

/*Vracia hodnotu bitu na danom indexe*/
#define ba_get_bit(jmeno_pole, index) (((index >= jmeno_pole[0]) || (index < 0)) ? (fatal_error("Index %ld mimo rozsah 0..%ld ",(long)index,(long)jmeno_pole[0]-1),1) : (DU1_GET_BIT(jmeno_pole,index)))        

#else

/*Inline funkcia kt. vracia velkost pola v bitoch*/
inline unsigned long  ba_size(bit_array_t jmeno_pole)
{
	return jmeno_pole[0];
}

/*Inline funkcia, nastavuje bit na indexe podla zadaneho vyrazu,kontroluje medze pola*/
inline void ba_set_bit(bit_array_t jmeno_pole,unsigned long int index, int vyraz)
{
	if((index >= ba_size(jmeno_pole)) || (index < 0))
	{
		fatal_error("Index %ld mimo rozsah 0..%ld ",(long)index,(long)jmeno_pole[0]-1);
	}

	if(vyraz==1)
	{
		(jmeno_pole[TYPE_ELEMENTS(jmeno_pole,index)+1] |= (1UL << (index % GET_TYPE_BITS(jmeno_pole))));
	}
	else
	{
		(jmeno_pole[TYPE_ELEMENTS(jmeno_pole,index)+1] &= ~(1UL << (index % GET_TYPE_BITS(jmeno_pole))));
	}
}

/*Inline funkcia, ziska hodnotu bitu na zadanom indexe,kontroluje medze pola*/
inline unsigned long int ba_get_bit(bit_array_t jmeno_pole, unsigned long int index)
{
	if((index >= ba_size(jmeno_pole)) || (index < 0))
	{
		fatal_error("Index %ld mimo rozsah 0..%ld ",(long)index,(long)jmeno_pole[0]-1);
	}

	return jmeno_pole[TYPE_ELEMENTS(jmeno_pole,index)+1] & (1UL << (index % GET_TYPE_BITS(jmeno_pole)));
}

#endif /* USE_INLINE */
#endif /* !BIT_ARRAY_H */