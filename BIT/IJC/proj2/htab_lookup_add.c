/* Subor: htab_foreach.c
*  Autor: Juraj Ondrej Dubrava
*  Projekt: IJC_DU2 b)
*  Popis: Vyhladanie polozky v tabulke, ak sa tam este nenechadza, prida novy zaznam do tabulky
*  Datum:
*  Prelozene: 
*/
#include <stdlib.h>
#include <string.h>
#include "htable.h"

//vyhladanie polozky v tabulke podla zadaneho kluca
//vracia ukazatel na zaznam polozky,ak sa zaznam v tabulke
//nenachadza, prida ho do tabulky a vrati ukazatel na tento zaznam

struct htab_listitem *htab_lookup_add(struct htab_t *table, const char *key)
{
   struct htab_listitem *tmpPtr = NULL;
   //index do hashovacej tabulky kt vracia hashovacia funkcia
   unsigned int word_index = table->hash_fun_ptr(key,table->htab_size);
   //hash_function(key,table->htab_size);


   if(table == NULL)
   {
   	  return NULL;
   }
   
 //vyhladanie slova v zozname na zadanom indexe   

   if(table->ptr[word_index] != NULL)
   {
   		for(tmpPtr=table->ptr[word_index]; tmpPtr != NULL; tmpPtr = tmpPtr->next)
   		{
   			if(strcmp(tmpPtr->key,key) == 0)
   			{
   				
   				return tmpPtr;
   			}
   		}
   }
//ak hladanie dopadlo neuspesne,je potrebne pridat novy zaznam
//na danom indexe este nie je ziadna polozka


  if(table->ptr[word_index] == NULL)
  {
  	    //alokovanie pamate pre polozku
        table->ptr[word_index] = malloc(sizeof(struct htab_listitem));
        if(table->ptr[word_index] == NULL)
        {
        	return NULL;
        }
    
        table->ptr[word_index]->key = malloc(strlen(key) + 1);
   
         if(table->ptr[word_index]->key == NULL)
         {
         	return NULL;
         }

        //inicializacia noveho zaznamu
        strcpy(table->ptr[word_index]->key,key);
        table->ptr[word_index]->data = 0;
        table->ptr[word_index]->next = NULL;
  
        tmpPtr = table->ptr[word_index];
  }   
//nastava situacia ze na danom indexe uz polozka existuje
//potrebne pridat novu polozku s danym indexom na zaciatok zoznamu  
  else
  {

     tmpPtr = malloc(sizeof(struct htab_listitem));
          
          if(tmpPtr == NULL)
          {
          	return NULL;
          }

     tmpPtr->key = malloc(strlen(key) + 1);
       
         if(tmpPtr->key == NULL)
         {
         	free(tmpPtr);
         	return NULL;
         }


      strcpy(tmpPtr->key,key);
      tmpPtr->data = 0; 
      tmpPtr->next = table->ptr[word_index];
      table->ptr[word_index] = tmpPtr;
  }

 return tmpPtr;

}




