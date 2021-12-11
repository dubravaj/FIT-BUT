CC = g++
CFLAGS = -std=c++11 -Wextra -Wall -pedantic -O2 



all: huff_codec

huff_codec: huff_codec.cpp huffman_static.cpp huffman_adaptive.cpp
	$(CC) $(CFLAGS) -o $@ $^








