/* Subor: htab_foreach.c
*  Autor: Juraj Ondrej Dubrava
*  Projekt: IJC_DU2 b)
*  Popis: Implementacia funkcie ktora pre kazdy prvok tabulky zavola funkciu z parametra
*  Datum: 21.4.2016
*  Prelozene: gcc 5.3.1 Fedora 23
*/
//hashovacia funkcia, vracia index do hashovacej tabulky
unsigned int hash_function(const char *str, unsigned htab_size) 
{ 
   unsigned int h=0; 
   const unsigned char *p; 
   for(p=(const unsigned char*)str; *p!='\0'; p++) 
        h = 65599*h + *p; 
        return h % htab_size; 
}
