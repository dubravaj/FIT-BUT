#Subor: Makefile 
# Autor: Juraj Ondrej Dubrava
#Projekt: IJC_DU2 b)
#Popis: Makefile pre IJC DU2
#Datum: 21.4.2016
#Prelozene: gcc 5.3.1 Fedora 23

CFLAGS=-std=c99 -Wall -Wextra -pedantic -g
CXXFLAGS=-std=c++11 -pedantic -Wall
CPP=g++
CC=gcc
LIBRARY=htab_lookup_add.o hash_function.o htab_clear.o htab_foreach.o htab_free.o htab_init.o 

all: tail wordcount wordcount-dynamic


tail:tail.c tail2.cc
	$(CC) $(CFLAGS) tail.c -o tail
	$(CPP) $(CXXFLAGS) tail2.cc -o tail2



wordcount:wordcount.o io.o libhtable.a
	$(CC) $(CFLAGS) -o wordcount -static io.o wordcount.o libhtable.a
wordcount-dynamic: wordcount.o io.o libhtable.so
	$(CC) $(CFLAGS) -o wordcount-dynamic wordcount.o io.o libhtable.so 



libhtable.a: $(LIBRARY)
	ar crs $@ $<

libhtable.so: $(LIBRARY)
	$(CC) $(CFLAGS) -shared -o libhtable.so $(LIBRARY)

hash_function.o:hash_function.c
	$(CC) $(CFLAGS) -c -fPIC hash_function.c

htab_init.o:htab_init.c
	$(CC) $(CFLAGS) -c -fPIC htab_init.c

htab_foreach.o:htab_foreach.c
	$(CC) $(CFLAGS) -c -fPIC htab_foreach.c

htab_lookup_add.o:htab_lookup_add.c
	$(CC) $(CFLAGS) -c -fPIC htab_lookup_add.c

htab_clear.o:htab_clear.c
	$(CC) $(CFLAGS) -c -fPIC htab_clear.c

htab_free.o:htab_free.c
	$(CC) $(CFLAGS) -c -fPIC htab_free.c

