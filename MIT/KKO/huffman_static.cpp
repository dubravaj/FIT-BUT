/*****
* Static huffman enconding and decoding
* Juraj Ondrej Dubrava (xdubra03)
*/

#include <stdio.h>
#include <stdlib.h>
#include <algorithm>
#include <queue>
#include <iostream>
#include <vector>
#include <map>
#include <bitset>
#include <string>
#include <fstream>
#include "huffman_static.h"

using namespace std;

/* Use model to preprocess input data
 * in_buffer input buffer 
 * out_buffer output buffer
 * size size of buffers
 */
void use_model_encoding(unsigned char *in_buffer,char *out_buffer, size_t size){

    for(size_t i=0; i < size; i++){
        if(i == 0){
            out_buffer[i] = (char)in_buffer[i];
        }
        else{
            out_buffer[i] = (char)in_buffer[i-1] - (char)in_buffer[i];
        }
    }
}

/* Use model to reconstruct data
 * in_buffer input data vector
 * out_buffer output data vector
 */
void use_model_decoding(vector<char> &in_buffer,vector<char> &out_buffer){


    for(size_t i=0; i < in_buffer.size(); i++){
        if(i == 0){
           out_buffer.push_back(in_buffer[i]);
        }
        else{
           out_buffer.push_back(out_buffer[i-1] - in_buffer[i]);
        }
    }

}


/* Get size of input RAW file
 * input input file
 * return size of file in bytes
 */
size_t get_raw_size(FILE *input){

    size_t size;	
    fseek(input,0,SEEK_END);
    size = ftell(input);
    rewind(input);

    return size;
}

/* Get frequencies of bytes in input file
 * buffer array with stored bytes
 * size size of buffer 
 * return map with frequency for each byte value in the buffer
 */
freq_map get_raw_freqs(unsigned char *buffer,size_t size){
  
    freq_map freqs;
    
    for(size_t i=0; i < size; i++){
       if(freqs.find((int)buffer[i]) != freqs.end()){
           freqs[(int)buffer[i]] += 1;
       }
       else{
           freqs[(int)buffer[i]] = 1;
       }
    }
   
    return freqs;
}

/* Read RAW file into buffer
 * input input RAW file
 * buffer buffer where bytes from file are stored
 * raw_size size of RAW file 
 */
void read_raw(FILE *input, unsigned char *buffer,size_t raw_size){

    size_t result;

    if(buffer == NULL){
        cout << "Not enough memory" << endl;
	exit(2);
    }

    result = fread(buffer,1,raw_size,input);
    if(result != raw_size){
        cout << "File error" << endl;
	exit(2);
    }
	
    fclose(input);
}

/* Check if tree node is leaf
 * node node in tree
 * return true if node is leaf, false otherwise
 */
bool is_leaf(TreeNode *node){
    if(node->right == NULL && node->left == NULL){
        return true;
    }
    return false;
}

/* Create huffman codes for bytes
 * root tree root node
 * huff_codes map where codes for each byte are stored
 * code created huffman code
 */
void create_codes(TreeNode *root,huff_map &huff_codes, string code){

    if(!root)
        return;
    
    if(is_leaf(root)){
        huff_codes[root->data] = code;
    }

    create_codes(root->left,huff_codes,code+"0");
    create_codes(root->right,huff_codes,code+"1");

}

/* Encodes RAW file and writes it to output file
 * freq_data frequency map for bytes
 * codes huffman codes
 * buffer buffer with input bytes
 * size size of RAW file
 * outfile output stream
 */
void encode(freq_map freq_data, huff_map &codes, unsigned char *buffer, size_t size, ofstream &outfile){

     // get code string for message
     string encoded_message = "";
     for(size_t i=0; i < size; i++){
          int key = (int) buffer[i];
          encoded_message += codes[key];

     }
     encoded_message += codes[256];

    uint16_t codebook_size = freq_data.size();
    outfile.write(reinterpret_cast<char *>(&codebook_size),sizeof(uint16_t));
   
    // encode codebook
    freq_map::iterator freq_item;
    for(freq_item = freq_data.begin(); freq_item != freq_data.end(); freq_item++){
        uint32_t key = freq_item->first;
	outfile.write(reinterpret_cast<char *>(&key),1);
	size_t data = freq_item->second;
	outfile.write(reinterpret_cast<char *>(&data),sizeof(data));
    }
   
    unsigned char byte = 0;
    uint16_t j = 0;
    for(size_t i=0; i < encoded_message.size(); i++){ 
        if(j == 8){
	    outfile.write(reinterpret_cast<char *>(&byte),1);
	    byte = 0;
	    j = 0;  
	}
	
	byte &= ~(1 << (7-(i % 8)));
	byte |= ((encoded_message[i] - '0') << (7-(i % 8)));
	j++;

    }
    if(j != 0){
        outfile.write(reinterpret_cast<char *>(&byte),1);
    }
}


/* Decodes data encoded with static huffman encoding
 * input input stream
 * output output stream
 */
void decode_raw_static(ifstream &input, ofstream &output,bool use_model){

    freq_map freqs;
    size_t byte_freq;
    uint32_t letter;
    size_t freq;
    unsigned char byte_key;
    TreeNode *root;
    vector<char> model_data;

    if(input.is_open())
    {
        uint16_t size;
	// get size of codebook
    	input.read(reinterpret_cast<char *>(&size),sizeof(uint16_t));
	size_t codebook_size = (int)size;
	
	// read codebook data from file 
        for(size_t i=0; i < 2*codebook_size; i++){
	    if(i % 2 == 0){
		input.read(reinterpret_cast<char *>(&byte_key),1);
		letter = (uint32_t) byte_key;
	    }
	    else{
		input.read(reinterpret_cast<char *>(&byte_freq),sizeof(freq));
		freq = (size_t) byte_freq;
	    }
	    if(i > 0 && i % 2 == 1){
		freqs[letter] = freq;
	    }
        }


	freq_map::iterator freq_item;
    
	huff_map huff_codes;
        root = create_huffman_tree(freqs);


	unsigned char code;
	int j;
	bool flag = false;
	int bit;
	TreeNode *current = root;

	// traverse the tree and find corresponding values for code
	while(true){
            input.read(reinterpret_cast<char *>(&code),1);

	   for(size_t i=0; i < 8; i++){
               bit = (code >> (7 - i)) & 1;
	       if(bit == 0){
	           current = current->left;
	       }
	       else{
		   current = current->right;
	       }
	       if(current->left == NULL && current->right == NULL){
		    if(current->data == 256){
			   flag = true;
		  	   break;
		    }
		    else{
			char data = (char) current->data;
			if(use_model){
			    model_data.push_back(data);	
			}
			else{
			    output.write(reinterpret_cast<char *>(&data),1);
			}
		        current = root;		
		    }

	       }

	   }
           if(flag){
	       break;
	   }
	   j++;
	}


        delete_huff_tree(root);
        root = NULL;
    }
    else{
        cout << "Unable to open file" << endl;
    
    }


    if(use_model){
        vector<char> out_data;
        use_model_decoding(model_data,out_data);
        for(size_t i=0; i< out_data.size(); i++){
            output.write(reinterpret_cast<char *>(&out_data[i]),1);
	}
    }

}

/* Create huffman tree for input frequencies
 * input_data frequency map for bytes
 * return root node of created tree
 */
TreeNode *create_huffman_tree(freq_map input_data){
    
	TreeNode *left, *right, *top;

	priority_queue<TreeNode*, vector<TreeNode*>, compare> huffmanHeap;

	freq_map::iterator freq_item;
	for(freq_item = input_data.begin(); freq_item != input_data.end(); freq_item++){
            huffmanHeap.push(new TreeNode(freq_item->first,freq_item->second));
	}
	huffmanHeap.push(new TreeNode(256,1));
	
	// iterate over heap and merge nodes with lowest frequency
	while(huffmanHeap.size() != 1){

	    left = huffmanHeap.top();
	    huffmanHeap.pop();
	    right = huffmanHeap.top();
	    huffmanHeap.pop();

	    top = new TreeNode(257,left->freq+right->freq);
	    top->left = left;
	    top->right = right;

	    huffmanHeap.push(top);

	}
	

	return huffmanHeap.top();
}

/* Delete huffman tree
 * root tree root node
 */
void delete_huff_tree(TreeNode *root){
    if(root->left != NULL){
        delete_huff_tree(root->left);
    }
    if(root->right != NULL){
	delete_huff_tree(root->right);
    }
    delete root;

}



/* Encode input file
 * infile input file
 * outfile output stream
 */
void encode_raw_static(FILE *infile, ofstream &outfile, bool use_model){
   
    unsigned char *raw_buffer;
    unsigned char *raw_buff;
    size_t raw_size;
    raw_size = get_raw_size(infile);
    raw_buff = (unsigned char *) malloc(sizeof(unsigned char) * raw_size);
    char *model_buff = NULL;
    read_raw(infile,raw_buff,raw_size);

    if(use_model){
         model_buff = (char *) malloc(sizeof(char) * raw_size);
	 if(model_buff == NULL){
             cout << "Cannot allocate memory" << endl;
	     exit(1);
	 }
	 use_model_encoding(raw_buff,model_buff,raw_size);
	 raw_buffer = (unsigned char*) model_buff;
    
    }
    else{
        raw_buffer = raw_buff;
    
    }
    


    if(raw_buff == NULL){
        cout << "Allocation failed." << endl;
        exit(2);
    }

    // create frequency map
    freq_map raw_freqs;
    raw_freqs = get_raw_freqs(raw_buffer,raw_size);
    freq_map::iterator it;

    huff_map huff_codes;
    TreeNode *root;
    // create huffman tree
    root = create_huffman_tree(raw_freqs);

    // create codes
    create_codes(root,huff_codes,"");
    delete_huff_tree(root);
    root = NULL;
    encode(raw_freqs,huff_codes,raw_buffer,raw_size,outfile);
    free(raw_buffer);
    if(use_model){
        free(raw_buff);
    }
    outfile.close();
}















