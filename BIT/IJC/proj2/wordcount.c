/* Subor: wordcount.c
*  Autor: Juraj Ondrej Dubrava
*  Projekt: IJC_DU2 b)
*  Popis: Hlavny program, vytvorenie hashovacej tabulky, nacitanie slov zo stdin a vytvorenie zaznamu pre slova 
*  Datum: 21.4.2016
*  Prelozene: gcc 5.3.1 Fedora 23
*/
#include <stdio.h>
#include "htable.h"
#include "io.h"

#define WORDLENGTHLIMIT 128  //maximalna dlzka slova

//ako limit zvolene prvocislo, minimalizuje to zhlukovanie v tabulke
//kedze neni specifikovany pocet slov na spracovanie, je toto cisla lubovolne
#define HTABSIZE 24593

//funckia ktora je volana na vypis polozky
void printWord(char *key, unsigned int value)
{
	printf("%s\t%d\n", key, value);
}


int main(void)
{

//vytvorenie pomocneho buffera pre slova   
   char wordsBuffer[WORDLENGTHLIMIT];

//inicializacia tabulky
struct htab_t *hash_table = htab_init(HTABSIZE);
   
    if(hash_table == NULL)
    {
    	if(hash_table != NULL)
    		fprintf(stderr, "Nepodarilo sa alokovat pamet pre tabulku.\n");
    	    htab_free(hash_table);
    }  

//pomocny item pre lookup
struct htab_listitem *item;

//nacitanie slov zo vstupu
   while(get_word(wordsBuffer,WORDLENGTHLIMIT,stdin) != EOF)
   {   

   	   //htab_lookup_add vyhlada zaznam,bud vytvori novy alebo inkrementuje pocet vyskytov slova
       
        item = htab_lookup_add(hash_table,wordsBuffer);

           if(item == NULL)
           {
           	  fprintf(stderr, "Nepodarila sa alokacia pamate pre novu polozku.\n");
           	  htab_free(hash_table);
           }
       //zvysenie poctu vyskytov slova
       item->data++;
       //zvysenie poctu zaznamov
       hash_table->n++;
  
   }

   htab_foreach(hash_table,printWord);

//uvolnenie tabulky z pamate
htab_free(hash_table);

return 0;
}