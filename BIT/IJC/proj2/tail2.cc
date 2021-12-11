/*
 * Subor: tail2.cc
 * Datum: 21.4.2016
 * Autor: Juraj Ondrej Dubrava
 * Popis: Funkcia tail, ma napodobnit fungovanie unixovej utility tail, bez zadaneho parametru
 * vypisuje poslednych 10 riadkov suboru, inak zvoleny pocet
 * Prelozene: g++ 5.3.1 Fedora 23
 */
#include <iostream>
#include <fstream>
#include <string>
#include <deque>
#include <stdlib.h>
#include <string.h>

//defaultny pocet riadkov na vypis
#define NLINES 10

using namespace std;

int checkLineArguments(int argc, char *argv[]); 
istream *checkFileArguments(fstream *fd,int argc, char *argv[]);
void printLines(istream *fd,unsigned int lines);

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


cerr << "Chyba, nespravne argumenty vstupu." << endl; 
return -1;
}

/* Kontrola parametrov pre subor
* vracia subor s ktorym sa bude pracovat (stdin|subor)
*/
istream *checkFileArguments(fstream *fd,int argc, char *argv[])
{

    if (argc == 1 || (argc == 2 && strcmp(argv[1], "-") == 0) ) 
    {
        return &cin;
    }
	
	if ((argc == 2 && strcmp(argv[1], "-n") != 0) || (argc == 3 && strcmp(argv[1], "-n") != 0))
	{
    (*fd).open(argv[1], ios::in);
		if ((*fd).fail())
			cerr << "Chyba, sobor sa nepodarilo otvorit. " << argv[1] << endl;
		else
			return fd;
	}
	
    
  if (argc == 4 && strcmp(argv[1], "-n") == 0)
	{
		(*fd).open(argv[3], ios::in);
		if ((*fd).fail())
			cerr << "Chyba, subor sa nepodarilo otvorit. " << argv[3] << endl;
		else
			return fd;
	}

  if (argc == 4 && strcmp(argv[2], "-n") == 0)
 {
    (*fd).open(argv[1], ios::in);
		if ((*fd).fail())
			cerr << "Chyba, sobor sa nepodarilo otvorit. " << argv[1] << endl;
		else
		 	return fd;
  }



return NULL;
}


void printLines(istream *fd, unsigned int lines)
{

   deque<string>lineBuffer;
   string data;

//nacitanie retazca zo vstupu
   while(getline(*fd,data))
   {
//pridanie retazca na koniec buffera
       lineBuffer.push_back(data);

         if(lineBuffer.size() > lines)
         {
            lineBuffer.pop_front();
         }

   }


    deque<string>::iterator line_iter;
    for (line_iter = lineBuffer.begin(); line_iter != lineBuffer.end(); line_iter++)
    {
		cout << *line_iter << endl;
    }

}


int main(int argc, char *argv[])
{
  ios::sync_with_stdio(false);
  fstream file;
  istream *fd =  checkFileArguments(&file,argc,argv);

  if(fd == NULL)
  {
   return -1;
  }

  int lines = checkLineArguments(argc,argv);
   printLines(fd,lines);
  file.close();


return EXIT_SUCCESS;
}
