/* Subor: htab.h
*  Autor: Juraj Ondrej Dubrava
*  Projekt: IJC_DU2 b)
*  Popis: Rozhranie pre program hashovacej tabulky
*  Datum: 21.4.2016
*  Prelozene: gcc 5.3.1 Fedora 23
*/

//rozhranie pre staticku kniznicu
#ifndef HTABLE_H
#define HTABLE_H

//struktura polozky v hashovacej tabulke
struct htab_listitem
{

	char *key;  //zadany kluc(index)        
	unsigned int data;  //pocet vyskytov slova     
	struct htab_listitem *next; //ukazatel na dalsiu polozku  	     
};

//struktura hashovacej tabulky
//chyba este ukazatel na hashovaciu funkciu
struct htab_t
{
   unsigned int htab_size;	//velkost tabulky
   unsigned int (*hash_fun_ptr)(const char* , unsigned);   //ukazatel na hashovaciu funkciu
   unsigned int n;      //aktualny pocet zaznamov
   struct htab_listitem *ptr[];  //pole ukazovatelov na polozky htab_listitem
} ;


unsigned int hash_function(const char *str, unsigned htab_size);
void htab_clear(struct htab_t *table);
void htab_free(struct htab_t *table);
struct htab_t *htab_init(unsigned int size);
void htab_remove(struct htab_t *table, char *key);
struct htab_listitem *htab_lookup_add(struct htab_t *table, const char *key);
void htab_foreach(struct htab_t *table, void (*function)(char *key, unsigned int value));
struct htab_t *htab_init2(unsigned int size,unsigned int (*hash_fun_ptr)(const char* , unsigned) );

#endif