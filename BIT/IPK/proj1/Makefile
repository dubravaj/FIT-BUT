CFLAGS= -std=c++11 -g
CC=g++
LIBS = request_operations.o

all:ftrest ftrestd  

request_operations.o:
	$(CC) $(CFLAGS) -c request_operations.cpp
ftrest.o:ftrest.cpp
	$(CC) $(CFLAGS) -c ftrest.cpp 
ftrestd.o:ftrestd.cpp 
	$(CC) $(CFLAGS) -c ftrestd.cpp 
ftrestd:ftrestd.o request_operations.o
	$(CC) $(CFLAGS) -o ftrestd ftrestd.o request_operations.o   
ftrest:ftrest.o request_operations.o
	$(CC) $(CFLAGS) -o ftrest ftrest.o request_operations.o 

clean:
	rm -f *.o 