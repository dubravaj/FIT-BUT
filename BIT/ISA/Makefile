CC=g++
CFLAGS= -std=c++11 -Wall -g

all: isabot

isabot.o: isabot.cpp
	$(CC) $(CFLAGS) -c isabot.cpp
bot_functions.o: bot_functions.cpp
	$(CC) $(CFLAGS) -c bot_functions.cpp bot_functions.h
bot_connection.o: bot_connection.cpp
	$(CC) $(CFLAGS) -c bot_connection.cpp bot_connection.h
isabot: isabot.o bot_functions.o bot_connection.o
	$(CC) $(CFLAGS) -o isabot isabot.o bot_functions.o bot_connection.o


clean:
	rm -f *.o isabot *~
