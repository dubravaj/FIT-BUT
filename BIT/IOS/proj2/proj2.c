/* Nazov: Roller Coaster synchronization problem
 * Autor: Juraj Ondrej Dubrava
 * Datum: 29.4.2016
 * Popis: Implementacia synchronizacneho problemu roller coaster za pomoci semaforov
 *
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <unistd.h>
#include <sys/wait.h>
#include <semaphore.h>
#include <fcntl.h>
#include <sys/shm.h>
#include <signal.h>
#include <time.h>

//struktura pre parametre
typedef struct params
{
   int P;  //pocet pasazierov
   int C;  //kapacita vozika
   int PT;  //pasazierov cas
   int RT;  //run time vozika
}RCParams;

//deklaracia potrebnych funkcii
int create_resources();
void free_resources();
int loadParams(int argc, char *argv[], RCParams *p);
void printAction(FILE *fp,char *processName, char *action, int processID);
void printAction1(FILE *fp,char *processName, char *action, int processID,int order);


//deklaracia semaforov
sem_t *mutex , *mutex2 , *mutex3, *mutex4, *boardQueue, *unboardQueue, *allAboard, *allAshore;
//pomocna struktura na nacitanie parametrov
RCParams RCparam; 
//inicializacia zdielanych premennych
int *Boarders = NULL;
int *Unboarders = NULL;
int *sharedAction = NULL;

int sharedBoardersID = 0;
int sharedUnboardersID = 0; 
int sharedActionID = 0; 

int main(int argc, char *argv[])
{

//kontrola spravnosti vstupnych argumentov 
  int checkParams = loadParams(argc,argv,&RCparam);
  
  switch(checkParams)
  {
    case 1:
      fprintf(stderr,"Chyba: Nespravny pocet argumentov\n");
      return 1;
      break;
    case 2:
      fprintf(stderr,"Chyba: Nespravny format vstupnych argumentov\n");
      return 1;
       break;
  }

//otvorenie suboru na zapis, vsetok vystup programu sa zapisuje do suboru 
 FILE *proj2;
 proj2  = fopen("proj2.out","w");
 if(proj2 == NULL)
 {
   fprintf(stderr,"Chyba, subor sa nepodarilo otvorit pre zapis.\n");
   return 1;
 }



 int check_creation = create_resources();
 if(check_creation == 2)
 {
   fprintf(stderr,"Chyba pri vytvarani zdrojov.\n");
   return 2;
 }
//nastavenie setbuf pre korektny zapis do suboru
 setbuf(stderr,NULL);
 setbuf(proj2, NULL);     


//pomocny proces
 pid_t passengerCreator = fork();  
//proces pasaziera 
 pid_t passenger[RCparam.P];
//identifikator pasaziera                  
 int id = 0;


if(passengerCreator == 0)
{
    srandom(time(0));
    
      
   for(int i =0; i < RCparam.P; i++)
   {
    
     int sleep_time = 0;      
     id = id +1;  
     passenger[i] = fork();  
     sleep_time = (rand() % (RCparam.PT+1));
      usleep(sleep_time * 1000); 
       
       if(passenger[i] == 0)
       {
        
        
         printAction(proj2,"P","started",id);
              
         sem_wait(boardQueue);
                                          
         printAction(proj2,"P","board",id);
              
         //kriticka sekcia 1, nastupovanie pasazierov do vozika
         sem_wait(mutex);
      
          (*Boarders)++;
         //otvaranie semaforu az kym nenastupia vsetci pasazieri
         if((*Boarders) < RCparam.C)
            sem_post(boardQueue);
         
         if((*Boarders) == RCparam.C)
          {

            printAction(proj2,"P","board last",id);
            sem_post(allAboard);
            (*Boarders) = 0;

          }
          else if((*Boarders) < RCparam.C )
          { 

            printAction1(proj2,"P","board order",id,(*Boarders));
                    
          }
                               
          sem_post(mutex);   
          sem_wait(unboardQueue);
          printAction(proj2,"P","unboard",id);
           
          //kriticka sekcia 2, vystupovanie pasazierov
          sem_wait(mutex2);
          (*Unboarders)++;
         
          //otvaranie semaforu az kym nevystupia vsetci pasazieri z vozika          
          if((*Unboarders) < RCparam.C)
             sem_post(unboardQueue);
         
          if((*Unboarders) == RCparam.C)
          {
            
            printAction(proj2,"P","unboard last",id);
            sem_post(allAshore);
             (*Unboarders) = 0;
           }
           else if((*Unboarders) < RCparam.C) 
           { 
            printAction1(proj2,"P","unboard order ",id,(*Unboarders));
           } 
          sem_post(mutex2);
            
          //kriticka sekcia, ukoncenie procesov pasazierov
          sem_wait(mutex4);
         
          printAction(proj2,"P","finished",id);
          sem_post(mutex4);

                
          exit(0);       
       }
       else if(passenger[i] < 0)
       {
          fprintf(stderr,"Chyba, nie je mozne vytvorit proces pasaziera.\n");
          exit(2);
       }
       else
       {

       }
      
      
      
       
    } //end for cyklus    
  
     for(int j = 0; j < RCparam.P; j++)
     {
        waitpid(passenger[j],NULL,0); 
     }
     exit(0); //koniec potomka

  }
  else if(passengerCreator < 0)
  {
     fprintf(stderr, "Chyba, nie je mozne vytvorit pomocny proces.\n");
     exit(2);
  }
  else
  {


  }

 


//proces vytvarania vozika
pid_t createCar = fork();
int car_id = 1;
if(createCar == 0)
{

int sleep_time = 0;  
printAction(proj2,"C","started",car_id);

srandom(time(0));

 for(int i = 0; i < (RCparam.P/RCparam.C); i++)
 {   
    
   printAction(proj2,"C","load",car_id);    
   sem_post(boardQueue);
   sem_wait(allAboard);
   printAction(proj2,"C","run",car_id);
   sleep_time = ( rand() % (RCparam.RT+1));
   usleep(sleep_time * 1000 );
   printAction(proj2,"C","unload",car_id); 
   sem_post(unboardQueue);
   sem_wait(allAshore);
       
 }    
   
    
    printAction(proj2,"C","finished",car_id);
    sem_post(mutex4);
  
    exit(0);

}
else if(createCar < 0)
{
   fprintf(stderr,"Chyba, nemozno vytvorit proces vozika.\n");
   exit(2);
}
else
{
   
}

waitpid(createCar,NULL,0);
waitpid(passengerCreator,NULL,0);

free_resources();
fclose(proj2);    


return EXIT_SUCCESS;
}



int loadParams(int argc, char *argv[], RCParams *p)
{
  int  PARAMS_OK = 0;
  char *error;

    if(argc == 5)
    {
      p->P= strtol(argv[1],&error,10);
      if(*error != 0 || !(p->P > 0))
      {
        PARAMS_OK = 1;
      }
      p->C= strtol(argv[2],&error,10);
      if(*error != 0 || !(p->P % p->C == 0))
      {
       PARAMS_OK = 1;
      }
      p->PT= strtol(argv[3],&error,10);
      if(*error != 0 || !(p->PT >= 0 && p->PT < 5001))
      {
        PARAMS_OK = 1;
      }
      
      p->RT= strtol(argv[4],&error,10);
      if(*error != 0 || !(p->RT >=0 && p->RT < 5001))
      {
         PARAMS_OK = 1;
      }
  
     }
     else if(argc != 5)
     {
        return 1;
     }
  
       if(PARAMS_OK == 1)
       {
          return 2;
       }

return PARAMS_OK;
}

//vytvorenie potrebnych zdrojov pre program - zdielana pamat + semafory
int create_resources()
{
  
  if((sharedBoardersID = shmget(IPC_PRIVATE, sizeof(int),IPC_CREAT | 0666)) == -1)
  {
    perror("shmget");
    return 2;
  }
  if((Boarders = (int *) shmat(sharedBoardersID,NULL,0)) == NULL )
  {
    perror("shmat");
   return 2;
  }
  
  if((sharedUnboardersID = shmget(IPC_PRIVATE,sizeof(int),IPC_CREAT | 0666)) == -1)
  {
    perror("shmget");
    return 2;
  }

  if((Unboarders = (int *) shmat(sharedUnboardersID,NULL,0)) == NULL)
  {
    perror("shmat");
    return 2;
  }  
  if((sharedActionID = shmget(IPC_PRIVATE, sizeof(int),IPC_CREAT | 0666)) == -1)
  {
    perror("shmget");
    return 2;
  }

  if((sharedAction = (int *) shmat(sharedActionID,NULL,0)) == NULL)
  { 
    perror("shmat");
    return 2;
  }  


//mutex chrani akciu vstupu pasazierov do vozika 
if((mutex = sem_open("/xdubra03_mutex",O_CREAT | O_EXCL, 0666,1)) == SEM_FAILED)
{
    perror("sem_open");
}
//mutex2 chrani akciu vystupovania pasazierov  
 if(( mutex2 = sem_open("/xdubra03_mutex2",O_CREAT | O_EXCL, 0666,1)) == SEM_FAILED)
{ 
   perror("sem_open");
}

//semafor pre vstup pasazierov 
if((boardQueue = sem_open("/xdubra03_boardQueue",O_CREAT | O_EXCL, 0666,0)) == SEM_FAILED)
{
   perror("sem_open");
}
//semafor pre vystup pasazierov
if((unboardQueue = sem_open("/xdubra03_unboardQueue",O_CREAT | O_EXCL, 0666,0)) == SEM_FAILED)
{
  perror("sem_open");
}
//semafor pre identifikaciu ze vsetci pasazieri su na palube
if((allAboard = sem_open("/xdubra03_allAboard",O_CREAT | O_EXCL, 0666,0)) == SEM_FAILED)
{
  perror("sem_open");
}
//semafor na identifikaciu ze vsetci pasazieri vystupili
if((allAshore = sem_open("/xdubra03_allAshore",O_CREAT | O_EXCL, 0666,0)) == SEM_FAILED)
{
  perror("sem_post");
}
//semafor na ochranu zapisu
if((mutex3 = sem_open("/xdubra03_mutex3", O_CREAT | O_EXCL, 0666, 1)) == SEM_FAILED)
{
  perror("sem_post");
}
//semafor pre ukoncovanie procesov 
if((mutex4 = sem_open("/xdubra03_mutex4", O_CREAT | O_EXCL, 0666, 0)) == SEM_FAILED)
{
  perror("sem_post");  
}


  *Boarders = 0;
  *Unboarders = 0;
  *sharedAction = 0;

return 1; 
}

//uvolnenie naalokovanych zdrojov - zdielanej pamate, semaforov
void free_resources()
{  
   
    shmdt(Boarders);
    shmdt(Unboarders);
    shmdt(sharedAction);
    shmctl(sharedBoardersID, IPC_RMID, NULL);
    shmctl(sharedUnboardersID, IPC_RMID, NULL);
    shmctl(sharedActionID, IPC_RMID, NULL);
    sem_close(mutex);
    sem_close(mutex2);
    sem_close(mutex3);
    sem_close(mutex4);
    sem_close(boardQueue);
    sem_close(unboardQueue);
    sem_close(allAboard);
    sem_close(allAshore);
    sem_unlink("/xdubra03_mutex");
    sem_unlink("/xdubra03_mutex2");
    sem_unlink("/xdubra03_mutex3");
    sem_unlink("/xdubra03_mutex4"); 
    sem_unlink("/xdubra03_boardQueue");
    sem_unlink("/xdubra03_unboardQueue");
    sem_unlink("/xdubra03_allAboard");
    sem_unlink("/xdubra03_allAshore");

}


//vypis akcie , zapis je chraneny semaforom na spravny zapis
void printAction(FILE *fp,char *processName, char *action, int processID)
{
  sem_wait(mutex3);
  ++*sharedAction;
  fprintf(fp,"%d \t: %s %d \t: %s\n",*sharedAction,processName,processID,action);
  sem_post(mutex3);

}

//vypis akcie , zapis chraneny semaforom, nutny este 1 parameter naviac oproti printAction (vypis poradia board a unboard)
void printAction1(FILE *fp,char *processName, char *action, int processID,int order)
{
  sem_wait(mutex3);
  ++*sharedAction;
  fprintf(fp,"%d \t: %s %d \t: %s %d\n",*sharedAction,processName,processID,action,order);
  sem_post(mutex3);

}




