/* Subor: htab_init.c
*  Autor: Juraj Ondrej Dubrava
*  Projekt: IJC_DU2 b)
*  Popis: Funkcia, ktora vytvori alokuje pamat pre hashovaciu tabulku o zadanej velkosti, vracia ukazatel na tabulku
*  Datum: 21.4.2016
*  Prelozene: gcc 5.3.1 Fedora 23
*/
#include <stdio.h>
#include <stdlib.h>
#include "htable.h"

struct htab_t *htab_init(unsigned int size)
{

//alokacia struktury tabulky(spolocne s flexible array member)
struct htab_t *hash_table = malloc(sizeof(struct htab_t) + size * sizeof(struct htab_listitem ));

//ak sa alokacia nepodari, vracia NULL
  if(hash_table == NULL)
  {
  	 return NULL;
  }    

  hash_table->htab_size = size;

//inicializacia vsetkych poloziek na hodnotu NULL
  for(unsigned int i = 0; i < size ; i++)
  {
  	hash_table->ptr[i] = NULL;
  }
//priradenie implicitnej hashovacej funkcie 
 hash_table->hash_fun_ptr = hash_function;
 hash_table->n = 0;
  return hash_table;
}

