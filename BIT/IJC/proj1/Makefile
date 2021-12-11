 #  Súbor:	Makefile
 #	Názov:	IJC DU1, úloha a)
 #	Autor:  Juraj Ondrej Dúbrava
 #  Dátum:  20.3.3016
 #  Popis:  Makefile pre subory z ulohy a) a b)	
 #  Preložené: 	gcc 5.3.1 Fedora 23 
 


all:primes primes-inline steg-decode

error.o: error.c error.h
	gcc -O2 -Wall -std=c99 -pedantic -c error.c -o error.o

eratosthenes.o:	eratosthenes.c eratosthenes.h bit_array.h
	gcc -O2 -Wall -std=c99 -pedantic -c eratosthenes.c -o eratosthenes.o

ppm.o: ppm.c ppm.h error.h
	gcc -O2 -Wall -std=c99 -pedantic -c ppm.c -o ppm.o

primes.o:	primes.c eratosthenes.h error.h
	gcc -O2 -Wall -std=c99 -pedantic -c primes.c -o primes.o
primes: primes.o eratosthenes.o error.o 
	gcc -O2 -Wall -std=c99 -pedantic  primes.o eratosthenes.o error.o -o primes 	

primes-inline.o: primes.c eratosthenes.h error.h
	gcc -O2 -Wall -std=c99 -pedantic -DUSE_INLINE -c primes.c -o primes-inline.o
primes-inline: primes-inline.o eratosthenes.o error.o
	gcc -O2 -Wall -std=c99 -pedantic primes-inline.o eratosthenes.o error.o -o primes-inline 

steg-decode.o: 	steg-decode.c eratosthenes.h error.h ppm.h
	gcc -O2 -Wall -std=c99 -pedantic -c steg-decode.c -o steg-decode.o
steg-decode: steg-decode.o ppm.o error.o eratosthenes.o
	gcc -O2 -Wall -std=c99 -pedantic steg-decode.o ppm.o error.o eratosthenes.o -o steg-decode

