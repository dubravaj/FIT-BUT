/*
 *  Súbor: error.h
 *  Názov:	IJC DU1, úloha a),b)
 *	Autor: Juraj Ondrej Dúbrava
 *  Dátum: 20.3.2016
 *  Popis: Rozhranie pre error.c	
 *  Preložené: gcc 5.3.1 Fedora 23
 */

#ifndef ERROR_H
#define ERROR_H

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>

/*funkcia vypisuje chybu, program po hlaseni pokracuje*/
void warning_msg(const char *fmt, ...);

/*funkcia vypisuje chybu, ukonci cely program*/
void fatal_error(const char *fmt, ...);

#endif