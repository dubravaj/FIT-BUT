/*
 *  Súbor: ppm.c
 *  Názov:	IJC DU1, úloha a),b)
 *	Autor: Juraj Ondrej Dúbrava
 *  Dátum: 12.3.2016
 *  Popis: Implementacia chybovych funkcii	
 *  Preložené:	gcc 5.3.1 Fedora 23
 */

#include "error.h"

void warning_msg(const char *fmt, ...)
{
    fprintf(stderr,"CHYBA: ");
    va_list arguments;
    va_start(arguments, fmt);
    vfprintf(stderr,fmt,arguments);
    va_end(arguments);
}

void fatal_error(const char *fmt, ...)
{

	fprintf(stderr,"CHYBA: ");
	va_list arguments;
    va_start(arguments, fmt);
    vfprintf(stderr,fmt,arguments);
    va_end(arguments);
    exit(1);
}