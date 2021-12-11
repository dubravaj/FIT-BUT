/* Subor: htab_foreach.c
*  Autor: Juraj Ondrej Dubrava
*  Projekt: IJC_DU2 b)
*  Popis: Implementacia funkcie ktora pre kazdy prvok tabulky zavola funkciu z parametra
*  Datum: 21.4.2016
*  Prelozene: gcc 5.3.1 Fedora 23
*/
#include <stdlib.h>
#include "htable.h"

//funkcia vola zadanu funkciu pre kazdy prvok tabulky
void htab_foreach(struct htab_t *table, void (*function)(char *key, unsigned int value))
{
   if(table == NULL)
   {
   	return;
   }
   unsigned int i;

//pomocna premenna na cyklenie zaznamami 
   struct htab_listitem *tmpItem;

     for(i=0; i< table->htab_size; i++)
     {
        if(table->ptr[i] != NULL)
        {
        	for(tmpItem=table->ptr[i]; tmpItem != NULL; tmpItem = tmpItem->next)
        	{
            //volanie zadanej funkcie 
                function(tmpItem->key,tmpItem->data);

        	}
        }

     }
}