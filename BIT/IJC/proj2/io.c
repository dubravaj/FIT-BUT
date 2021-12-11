/* Subor: io.c
*  Autor: Juraj Ondrej Dubrava
*  Projekt: IJC_DU2 b)
*  Popis: Implementacia funkcie ktora zo vstupneho suboru cita slova, uklada ich do pomocneho buffera,pri prekroceni limitu slovo skracuje
*  Datum: 21.4.2016
*  Prelozene: gcc 5.3.1 Fedora 23
*/
#include <stdio.h>
#include <ctype.h>
#include "io.h"
 
int get_word(char *s, int max, FILE *f)
 {

    int c;
    int i = 0;
    unsigned length = 0;  // Pocitadlo poctu uz precitanych znakov
    static int warning = 0;
    
    // Vymaze pripadne biele znamky na zaciatku streamu
    while ((c = fgetc(f)) != EOF) {
        if (!isspace(c))
            break;
    }
    if (c == EOF)
        return EOF;

    // Vrati posledny precitany znak
    ungetc(c, f);

    while ((c = fgetc(f)) != EOF) 
    {
    	
        if (isspace(c) || (i == max - 1)) 
        {
            if (i == max -1) 
            {
                if (warning == 0) 
                {    
                 //kontola dlzky slova                             
                    fprintf(stderr, "Prekroceny limit 127 znakov na slovo.\n");
                    warning= 1;
                }
                //orezanie slova na prislusny limit
                while(!isspace(c) && c != EOF) 
                { 
                    c = fgetc(f);
                }
                if (c == EOF)
                    return EOF;
            }
            break;
        }
        s[i] = c;
        i++;
        length++;
    }
    if (c == EOF)
        return EOF;
    
    s[i] = '\0';
    return length;


}