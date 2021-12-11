CFLAGS= -std=c++11 -g
CC=g++

all:chat_client  

chat_client.o:chat_client.cpp 
	$(CC) $(CFLAGS) -c chat_client.cpp -pthread
chat_client:chat_client.o
	$(CC) $(CFLAGS) -o chat_client chat_client.o -pthread  

.PHONY:clean

clean:
	rm chat_client.o



