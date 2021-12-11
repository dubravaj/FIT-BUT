/* Subor: htab_clear.c
*  Autor: Juraj Ondrej Dubrava
*  Projekt: IJC_DU2 b)
*  Popis: Implementacia funkcie ktora uvolni z pameti vsetky polozky
*  Datum: 21.4.2016
*  Prelozene: gcc 5.3.1 Fedora 23
*/
#include <stdlib.h>
#include "htable.h"

void htab_clear(struct htab_t *table)
{
   
   unsigned int i;   
      
//uvolenenie vsetkych zaznamov tabulky
   for( i=0; i < table->htab_size ; i++)
   {
         //ukazatel na prvy zaznam
        struct htab_listitem *ptr = table->ptr[i];
        
        //ziadny zaznam nie je 
          if(ptr == NULL)
          	continue;
        
        while(ptr->next != NULL)
        {
          //ulozenie co mame uvolnit
        	struct htab_listitem *tmpPtr = ptr;
        	
        	ptr = ptr->next;
          //uvolnenie retazca kluca
        	free(tmpPtr->key);
          //uvolnenie celej polozky
        	free(tmpPtr);

        }
 //uvolnenie poslednej polozky tabulky
     free(ptr->key);
     free(ptr);
      
     table->ptr[i] = NULL;
   }
   table->n=0;
}

