/* Subor: htab_remove.c
*  Autor: Juraj Ondrej Dubrava
*  Projekt: IJC_DU2 b)
*  Popis: Implementacia funkcie ktora podla zadaneho kluca odstrani zaznam tabulky
*  Datum: 21.4.2016
*  Prelozene: gcc 5.3.1 Fedora 23
*/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "htable.h"



void htab_remove(struct htab_t *table, char *key)
{
	unsigned int index = table->hash_fun_ptr(key,table->htab_size);

    htab_listitem* item_ptr = table->ptr[index];
	htab_listitem* item = table->ptr[index];

	while(item != NULL)
	{ 
	//zacina sa hladat podla zadaneho kluca
		if(strcmp(item->key,key) == 0)
		{ 
		   //polozka bola najdena
			if(item_ptr == item)
			{ 
		  // polozka bola najdena ako prva v zozname, reset zoznamu
				table>ptr[index] = item->next;
			}
			item_ptr->next = item->next;

			free(item);
			
			break;
		}
		else
		{ 
			//ideme dalej
			item_ptr = item;
			item = item->next;
		}
	}


}