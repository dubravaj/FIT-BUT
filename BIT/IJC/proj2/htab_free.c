/* Subor: htab_free.c
*  Autor: Juraj Ondrej Dubrava
*  Projekt: IJC_DU2 b)
*  Popis: Funkcia na uvolnenie tabulky z pamate
*  Datum: 21.4.2016
*  Prelozene: gcc 5.3.1 Fedora 23
*/
#include <stdlib.h>
#include "htable.h"

//funkcia na uvolnenie pamate po tabulke, vyuziva sa volanie funkcie htab_clear
void htab_free(struct htab_t *table)
{
   if(table != NULL)
   {
        htab_clear(table);
        free(table);
   }
   
}
