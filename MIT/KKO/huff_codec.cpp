#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <algorithm>
#include <queue>
#include <iostream>
#include <fstream>
#include <vector>
#include <map>
#include <bitset>
#include <string>
#include "huffman_static.h"
#include "huffman_adaptive.h"




using namespace std;

void print_help(){
    
    cout << "-----------------------HELP-------------------------" << endl;
    cout << "Application for huffman encoding/deconding" << endl;
    cout << " " << endl;
    cout << "Program works in two modes: encoding and decoding. You can choose option for static/adaptive huffman encoding/decoding." << endl;
    cout << "Program parameters: " << endl;
    cout << "-c enables encoding" << endl;
    cout << "-d enables decoding" << endl;
    cout << "-h static enables mode for static huffman" << endl;
    cout << "-h adaptive enables mode for adaptive huffman" << endl;
    cout << "-m enables model, which is used to preprocess input data" << endl;
    cout << "-i infile specifies file with input data (encoded or decoded according to -c/-d option)" << endl;
    cout << "-o outfile specifies file with output data (encoded or decoded according to -c/-d option)" << endl;
    cout << " " << endl;
    cout << "Example of usage: " << endl;
    cout << "./huff_codec -c -h static -i hd02.raw -o huffman_static" << endl;
    cout << "./huff_codec -d -h static -i huffman_static -o hd02_test.raw" << endl; 

}




int main(int argc, char *argv[]){

   int opt;
   bool encoding = false;
   bool use_static = false;
   bool use_model = false;
   char *infile = NULL;
   char *outfile = NULL;

   while((opt = getopt(argc,argv,":cdh:mi:o:iw:")) != -1){
   
       switch(opt){
	   case 'd':
	       encoding = false;
	       break;
	   case 'c':
	       encoding = true;
	       break;
	   case 'h':
	       if(strcmp(optarg,"static") == 0){
		   use_static = true;
	       }
	       else if(strcmp(optarg,"adaptive") == 0){
		   use_static = false;
	       }
	       else{
                   cout << "Argument static or adaptive expected" << endl;
		   cout << " "<< endl;
		   print_help();
		   return 0;
	       }
	       break;
	   case 'm':
	       use_model = true;
	       break;
	   case 'i':
	       infile = optarg;
	       break;
	   case 'o':
	       outfile = optarg;
               break;
	   case 'w':
	       //char *cols = optarg;
	       break;
	   }

   }

   // encoding
   if(encoding){
       
       if(use_static){  // static encoding
       
           FILE *input_file;
           ofstream output_file;
           
	   input_file = fopen(infile,"rb");
           if(input_file == NULL){
               cout << "Unable to open input file." << endl;
               exit(2);
           }
           output_file.open(outfile, ios::out | ios::binary);
           if(!output_file.is_open()){
              cout << "Unable to open output file." << endl; 
	   }
           encode_raw_static(input_file,output_file,use_model);	
       }
       else{  // adaptive encoding
	   ifstream input_file;
	   ofstream output_file;
	   input_file.open(infile, ios::binary | ios::ate);
           int64_t size = get_raw_size_adaptive(input_file);

	   input_file.open(infile,ios::in | ios::binary);
	   if(!input_file.is_open()){
 		cout << "Unable to open input file" << endl;
		exit(2);
	   }
	   output_file.open(outfile,ios::out | ios::binary);
	   if(!output_file.is_open()){
               cout << "Unable to open output file" << endl;
	       exit(2);
	   } 

	   encode_raw_adaptive(input_file, output_file,size,use_model);
       }



   }
   // decoding
   else{
       
       if(use_static){  // static decoding
         ifstream input;
	 ofstream output;

	 input.open(infile,ios::in | ios::binary);
         output.open(outfile,ios::out | ios::binary);
         
	 if(!input.is_open()){
             cout << "Unable to open input file." << endl;
	     exit(2);
	 }
	 if(!output.is_open()){
             cout << "Unable to open output file." << endl;
	     exit(2);
	 }

	 decode_raw_static(input,output,use_model);

       }
       else{ // adaptive decoding

	  ifstream input;
          ofstream output;
          input.open(infile, ios::binary | ios::ate);
          int64_t size = get_raw_size_adaptive(input);



         input.open(infile,ios::in | ios::binary);
         output.open(outfile,ios::out | ios::binary);

         if(!input.is_open()){
             cout << "Unable to open input file." << endl;
             exit(2);
         }
         if(!output.is_open()){
             cout << "Unable to open output file." << endl;
             exit(2);
         }
      
	 decode_raw_adaptive(input,output,size,use_model);
       }

   }

   return 0;
}
