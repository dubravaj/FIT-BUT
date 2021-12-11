/* Subor: htab_init2.c
*  Autor: Juraj Ondrej Dubrava
*  Projekt: IJC_DU2 b)
*  Popis: Implementacia funkcie inicializacie hash tabulky s lubovolne zadanou hashovaciou funkciou, ktora ma byt pouzita,vracia ukazatel na tabulku
*  Datum: 21.4.2016
*  Prelozene: gcc 5.3.1 Fedora 23
*/

#include <stdio.h>
#include "htable.h"

struct htab_t *htab_init2(unsigned int size, unsigned int (*hasfn)(const char* , unsigned))
{

    unsigned int i=0;

//alokacia struktury tabulky

struct htab_t *hash_table = malloc(sizeof(struct htab_t) + size * sizeof(struct htab_listitem ));

//alokacia sa nepodarila,vracia NULL
  if(hash_table == NULL)
  {
  	 return NULL;
  }    
 //inicializacia vsetkych poloziek na NULL
  for(unsigned int i = 0; i < size ; i++)
  {
    hash_table->ptr[i] = NULL;
  }
  
  hash_table->htab_size = size;
  //priradenie zvolenej hashovacej funkcie
  hash_table->hash_fun_ptr = hasfn;
  
  return hash_table;
}