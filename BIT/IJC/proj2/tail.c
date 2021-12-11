/*
 * Subor: tail.c
 * Datum: 21.4.2016
 * Autor: Juraj Ondrej Dubrava
 * Popis: Funkcia tail, ma napodobnit fungovanie unixovej utility tail, bez zadaneho parametru
 * vypisuje poslednych 10 riadkov suboru, inak zvoleny pocet
 * Prelozene: gcc 5.3.1 Fedora 23
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Maximalna dlzka 1 riadku
#define MAXLINE 511

#define NLINES 10



//Deklaracia funkcii
FILE *checkFileArguments(int argc, char *argv[]);
int checkLineArguments(int argc, char *argv[]);
char **lineStorage(int lines);
void printLines(FILE *fd, char *buffer[],int lines);
void freeLineStorage(char **buffer, int lines);


/* Kontrola parametrov pre riadky
* vracia pocet riadkov
*/

int checkLineArguments(int argc, char *argv[])
{
    if (argc == 1 || (argc == 2 && strcmp(argv[1], "-n")==0))
		return NLINES;
	
    if (argc == 2 && strcmp(argv[1], "-n")) 
		return NLINES;
	
     if((argc == 3 || argc == 4) && strcmp(argv[1],"-n") == 0)
     {
      return  atoi(argv[2]); 
     }
   
    if (argc == 4 && strcmp(argv[2], "-n") == 0) 
    {
		return atoi(argv[3]);
    }

return -1;
}

/* Kontrola parametrov pre subor
* vracia subor s ktorym sa bude pracovat (stdin|subor)
*/


FILE *checkFileArguments(int argc, char *argv[])
{
  

    if (argc == 1 || (argc == 3 && strcmp(argv[1], "-n") == 0) ) 
    {
        return stdin;
    }
	
	if (argc == 2 || (argc == 3 && strcmp(argv[1], "-n") != 0))
	{
        FILE *file = fopen(argv[1], "r");
		if (file == NULL)
         fprintf(stderr, "Chyba, subor sa nepodarilo otvorit\n");
		else
	       return file;
	}
	
    
    if (argc == 4 && strcmp(argv[1], "-n") == 0)
	{
		FILE *file = fopen(argv[3], "r");
		if (file == NULL)
        fprintf(stderr, "Chyba, subor sa nepodarilo otvorit\n");
		else
			return file;
	}

    if (argc == 4 && strcmp(argv[2], "-n") == 0)
    {
     FILE *file = fopen(argv[1], "r");
     if (file == NULL)
        fprintf(stderr, "Chyba, subor sa nepodarilo otvorit\n");
     else
        return file;
    }



return NULL;
}   



//funkcia na alokaciu prepisovatelneho buffera pre zadany pocet riadkov, dynamicky sa alokuje potrebne miesto
char **lineStorage(int lines)
{
   char **buffer;

//alokacia pola riadkov  
   buffer = malloc(lines * sizeof(char *));
      
        if(buffer == NULL)
           return NULL;
       
//alokacia riadkov
for(int i = 0; i<lines; i++)
  {
    buffer[i] = malloc(MAXLINE * sizeof(char *));

//uvolnenie ak sa nepodari alokovat  
        if(buffer[i] == NULL)
        {
           freeLineStorage(buffer,i);

         return NULL;
        }
  } 
return buffer;

}

//funkcia na uvolnenie pamati buffera
void freeLineStorage(char **buffer, int lines)
{
//uvolnenie riadka
   for(int i=0; i<lines; i++)
    {  
       free(buffer[i]);
    }

//uvolnenie celeho buffera
free(buffer);

}

//funkcia na vypis pozadovaneho poctu riadkov od konca suboru
void printLines(FILE *fd, char *buffer[],int lines)
{
//lines -1 
    int num_lines=0;

//nacitanie riadka do buffera 
     while(fgets(buffer[num_lines%lines],MAXLINE, fd) != NULL)
     { 
//overenie dlzky riadka       
        if(strchr(buffer[num_lines%lines],'\n') == NULL && (strlen(buffer[num_lines%lines]) == MAXLINE -1))
        {
           fprintf(stderr,"Chyba, prekroceny limit poctu znakov riadku\n");
           
         
            int c;
//nacitanie zvysku riadku kvoli ignorovaniu
            while((c = fgetc(fd)) != '\n' && c != EOF)
            {
               
            }
         }
        num_lines++;

   }
   //ak je pocet riadov na vypsi vacsi ako skutocny pocet v subore, vypise sa len pocet riadkov suboru
 if(lines > num_lines)
       lines = num_lines;  
//zistenie pozicie v bufferi od ktorej treba vypisovat
int end_position = num_lines%lines;
int j = end_position;

//vypisanie riadkov v bufferi
    while( j < lines)
   {
      printf("%s",buffer[j]);
       j++;

   }

   j=0;
   
   while( j < end_position)
   { 
      printf("%s",buffer[j]);
      j++;

   }
}

int main(int argc, char *argv[])
{

FILE *fd = checkFileArguments(argc,argv);
  if(fd == NULL)
  {
        return -1;
  }    

//pocet riadkov k vypisu
int lines = checkLineArguments(argc,argv);    
char **buffer = lineStorage(lines);

     if(buffer != NULL)
     {
          printLines(fd,buffer,lines);
          freeLineStorage(buffer,lines);
     }
    
     else
     {
         fprintf(stderr,"Chyba,nepodarilo sa alokovat\n");
         return -1;
     }
fclose(fd);

return 0;
}

