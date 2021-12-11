/**
* Adaptive huffman encoding and decoding 
* Juraj Ondrej Dubrava (xdubra03)
*
*/

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <algorithm>
#include <iostream>
#include <fstream>
#include <vector>
#include <map>
#include <string>
#include "huffman_adaptive.h"


/* Use difference between two neighbour pixels to preprocess data, create buffer with differences
 * in_buffer buffer with input data to be preprocessed
 * out_buffer preprocessed data
 * size size of input buffer
 */
void use_model_encoding_adaptive(unsigned char *in_buffer,char *out_buffer, size_t size){

    for(size_t i=0; i < size; i++){
        if(i == 0){
            out_buffer[i] = (char)in_buffer[i];
        }
        else{
            out_buffer[i] = (char)in_buffer[i-1] - (char)in_buffer[i];
        }
    }
}

/* Reconstruct data if model was used for preprocessing
 * in_buffer input data to be reconstructed
 * out_buffer reconstructed data
 */
void use_model_decoding_adaptive(vector<char> &in_buffer,vector<char> &out_buffer){


    for(size_t i=0; i < in_buffer.size(); i++){
        if(i == 0){
           out_buffer.push_back(in_buffer[i]);
        }
        else{
           out_buffer.push_back(out_buffer[i-1] - in_buffer[i]);
        }
    }

}


/* Get size of input data in bytes
 * input input data stream 
 */
int64_t get_raw_size_adaptive(ifstream &input){

    size_t size;
    size = input.tellg();
    input.close();

    return size;
}

/* Read data from input stream to buffer
 * input input data stream
 * buffer output buffer
 * raw_size size of input data stream
 */
void read_raw(ifstream &input, unsigned char *buffer,int64_t raw_size){

    int64_t result;

    if(buffer == NULL){
        cout << "Not enough memory" << endl;
        exit(2);
    }

    input.read(reinterpret_cast<char *>(buffer),raw_size);
    result = input.gcount();
    if(result != raw_size){
        cout << "Error while reading input data" << endl;
        exit(2);
    }
}

/* Deallocate array of nodes pointers
 * huff_nodes nodes array
 * size size of nodes array
 */
void free_nodes_array(AdaptiveNode *huff_nodes[],size_t size){

    for(size_t i=0; i< size; i++){
        if(huff_nodes[i] != NULL){
            delete huff_nodes[i];
	}
    }
}



/* Find whether it is data's first occurence in array of huffman tree nodes
 *  huff_nodes array with tree nodes
 *  size size of nodes array
 *  data data to be find in tree
 *  return true if data are contained in certain node, false otherwise
 */
bool first_data_occurence(AdaptiveNode *huff_nodes[],size_t size, uint32_t data){
    for(size_t i=0; i < size; i++){
        if(huff_nodes[i]!= NULL){
            if(huff_nodes[i]->data == data){
                return false;
            }
        }
    }
    return true;
}

/* Get index in nodes array for searched data
 * huff_nodes nodes array
 * size nodes array size
 * data searched data
 * return index if found, -1 otherwise
 */
int32_t get_data_position(AdaptiveNode *huff_nodes[],size_t size, uint32_t data){
    for(size_t i=0; i < size; i++){
        if(huff_nodes[i]!= NULL){
            if(huff_nodes[i]->data == data){
                return i;
            }
        }
    }
    return -1;
}

/* Get index in nodes array for searched node
 * huff_nodes nodes array
 * size nodes array size
 * node searched node
 * return index if found, -1 otherwise
 */
size_t get_node_position(AdaptiveNode *huff_nodes[],size_t size, AdaptiveNode *node){
    for(size_t i=0; i < size; i++){
        if(huff_nodes[i] == node){
            return i;
	}
    }
    return -1;

}

/* Insert first occurence of input data value to nodes array, creates new NYT node and data node
 * huff_nodes nodes array
 * size nodes array size
 * data inserted data
 * return true if inserted, false otherwise
 */
bool insert_first(AdaptiveNode *huff_nodes[],size_t size, uint32_t data){

    int32_t nyt_pos = get_data_position(huff_nodes,size,NYT_VAL);
    if(nyt_pos != -1){
        AdaptiveNode *old_nyt = huff_nodes[nyt_pos];
        AdaptiveNode *left = new AdaptiveNode(NYT_VAL,0,nyt_pos,-1,-1);
        AdaptiveNode *right = new AdaptiveNode(data,0,nyt_pos,-1,-1);
        huff_nodes[nyt_pos-2] = left;
        huff_nodes[nyt_pos-1] = right;
        old_nyt->data = INTERNAL_NODE_VAL;
        old_nyt->left = nyt_pos-2;
        old_nyt->right = nyt_pos-1;

	return true;
    }
    return false;

}

/* Get code for certain data 
 * huff_nodes nodes array
 * size nodes array size
 * data searched data
 * return vector of bool values representing inverted code
 */
vector<bool> get_code(AdaptiveNode *huff_nodes[],size_t size,uint32_t data){
  
    vector<bool> symbol_code;
    int32_t data_pos = get_data_position(huff_nodes,size,data);
    
    AdaptiveNode *node = huff_nodes[data_pos];

    // traverse tree from leaf to root and creates inverted code
    while(node->parent != -1){
       int32_t right = huff_nodes[node->parent]->right;
       AdaptiveNode *right_son = huff_nodes[right];
       
       if(node == right_son){
           symbol_code.push_back(1);
       }
       else{
           symbol_code.push_back(0);
       }
       
       if(node->parent != -1){
           node  = huff_nodes[node->parent]; 
       }
       else{
	 break;
       }	 
    }

    return symbol_code;
}

/* Update huffman tree to keep sibling property
 * huff_nodes nodes array
 * size nodes array size
 * data updated data
 */
void update_tree(AdaptiveNode *huff_nodes[], uint32_t size, uint32_t data){

    int32_t data_pos = get_data_position(huff_nodes,size,data);
    
   AdaptiveNode *updated_node = huff_nodes[data_pos];
   
   // traverse tree
   while(updated_node != NULL){

	AdaptiveNode *same_weight_node = updated_node;
	uint32_t node_pos = get_node_position(huff_nodes,size,updated_node);
	int32_t same_weight_node_position = -1;
        // find node with same weight but greater order
	for(uint32_t i=node_pos; i< size; i++){	
            if(huff_nodes[i] != NULL){
       	         if(huff_nodes[i]->weight == updated_node->weight && i > node_pos){
                      same_weight_node = huff_nodes[i];
	              same_weight_node_position = i;
		 }
           } 
        }

	// updating, switching nodes in huff_nodes array	
	if(same_weight_node != updated_node && same_weight_node_position != updated_node->parent){
	    
	    uint32_t tmp = updated_node->parent;

	    updated_node->parent = same_weight_node->parent;
	    same_weight_node->parent = tmp;
	    if(updated_node-> left != -1 && updated_node->right != -1){

                AdaptiveNode *left = huff_nodes[updated_node->left];
	        AdaptiveNode *right = huff_nodes[updated_node->right];
                left->parent = same_weight_node_position;
		right->parent = same_weight_node_position;

	    }	    

	    if(same_weight_node->left != -1 && same_weight_node->right != -1){

                AdaptiveNode *left = huff_nodes[same_weight_node->left];
                AdaptiveNode *right = huff_nodes[same_weight_node->right];
                left->parent = node_pos;
                right->parent = node_pos;

            }

	    huff_nodes[same_weight_node_position] = updated_node;
	    huff_nodes[node_pos] = same_weight_node;
	}

	updated_node->weight++;
	if(updated_node->parent != -1){
            updated_node = huff_nodes[updated_node->parent];
	}
	else{
            break;
	}
   }
}

/* Initializes huffman tree, inserts first node (NYT node)
 * huff_nodes nodes array
 * return true if initial node was inserted, false otherwise 
 */
bool create_tree(AdaptiveNode *huff_nodes[]){
    
    AdaptiveNode *first_nyt = new AdaptiveNode(NYT_VAL,0,-1,-1,-1);
    if(first_nyt == NULL){
        return false;
    }
    huff_nodes[MAX_ORDER] = first_nyt;
    return true;
}

/* Save code for symbol to output file
 * buffer output buffer to be saved to file
 * buffer_pos current position in buffer
 * data saved data
 * huff_nodes nodes array
 * size nodes array size
 * output output stream 
 */
void save_encoded_symbol(unsigned char *buffer, uint16_t *buffer_pos, uint32_t data, AdaptiveNode *huff_nodes[],size_t size, ofstream &output){

     uint16_t position = *buffer_pos;
     bool bit;
     vector<bool> code = get_code(huff_nodes,size,data);
   
     for(size_t i=code.size(); i > 0; i--){
         bit = code.back();
	 code.pop_back();
        
	 *buffer  &= ~(1 << (7-(position % 8)));
         *buffer |= (bit << (7-(position % 8)));
         position++;        
 
	 //buffer is full, byte can be saved to file
         if(position == 8){
            output.write(reinterpret_cast<char *>(buffer),1);
            position = 0;
            *buffer = 0;
        }

     }
     //save actual position in buffer
     *buffer_pos = position;

}

/* Save symbol to file without encoding if it is symbol's first occurence
 * buffer output buffer
 * buffer_pos position in buffer
 * data saved data
 * output output stream
 */
void save_first_symbol(unsigned char *buffer,uint16_t *buffer_pos, unsigned char data,ofstream &output){

     uint16_t position = *buffer_pos;
     
     for(int i=0; i< 8; i++){
          
	bool bit = (data >> (7 - i)) & 1;
        *buffer  &= ~(1 << (7-(position % 8)));
        *buffer |= (bit << (7-(position % 8)));
         position++;
	
	//buffer is full, byte can be saved to file
        if(position == 8){
            output.write(reinterpret_cast<char *>(buffer),1);
	    position = 0;
	    *buffer = 0;
	}
     }
     //save actual position in buffer
     *buffer_pos = position;
}

/* Decode data from input file
 * infile input stream with encoded data
 * outfile output stream with decoded data
 * return 0 if decoding was successful, FAIL otherwise
 */
size_t decode_raw_adaptive(ifstream &infile, ofstream &outfile,int64_t size, bool use_model){

    AdaptiveNode *huff_nodes[2*SYMBOLS] = {NULL};
    unsigned char output_buffer = 0;
    uint16_t buffer_position = 0;
    vector<char> out_model_buffer;

    bool is_created = create_tree(huff_nodes);
    if(!is_created){
        return FAIL;
    } 

    char data_byte;
    unsigned char data;
    AdaptiveNode *root = huff_nodes[MAX_ORDER];
    AdaptiveNode *node = root;
    bool reading_NYT = true;

    while(infile.get(data_byte)){

        data = (unsigned char) data_byte;
	bool bit;
	
	for(int i = 0; i < 8; i++){

            bit = (data >> (7 - i)) & 1;
		
	    // code for NYT was read, read next bytes representing not encoded data value
            if(reading_NYT == true){
               output_buffer  &= ~(1 << (7-(buffer_position % 8)));
               output_buffer |= (bit << (7-(buffer_position % 8))); 
	       buffer_position++;
	       if(buffer_position == 8){
		   
		   bool is_inserted = insert_first(huff_nodes,MAX_ORDER+1,(uint32_t)output_buffer);   
	           if(!is_inserted){
		      return FAIL;
		   }
		   update_tree(huff_nodes,MAX_ORDER+1,(uint32_t)output_buffer);
		   if(use_model){
                       out_model_buffer.push_back((char)output_buffer);
		   }
		   else{
		       outfile.write(reinterpret_cast<char *>(&output_buffer),1);
		   }
		   output_buffer = 0;
	           buffer_position = 0;
	           reading_NYT = false;

	       }
              
	    }	    
	    else{ //read bits representing code of certain data value, traverse tree and find corresponding data value
	         
	        if(bit == 0){
                    node = huff_nodes[node->left];
	        }
	        else{
	            node = huff_nodes[node->right];
	        }
	        if(node->left == -1 && node->right == -1){
                    if(node->data == NYT_VAL){
		        reading_NYT = true;
		        node = root;
		    }
		    else if(node->data < NYT_VAL){
			if(use_model){
			    out_model_buffer.push_back((char)node->data);
			}
			else{
                            outfile.write(reinterpret_cast<char *>(&node->data),1);
			}
			update_tree(huff_nodes,MAX_ORDER+1,node->data);
		        node = root;
		    }
	        } 
	   }
	}
    }

    if(use_model){
        vector<char> out_data;
        use_model_decoding_adaptive(out_model_buffer,out_data);
        for(size_t i=0; i< out_data.size(); i++){
            outfile.write(reinterpret_cast<char *>(&out_data[i]),1);
        }
    }

   
    
    free_nodes_array(huff_nodes,MAX_ORDER+1);
    if(!reading_NYT){
       cout << "EOF NOT READ" << endl;
       return FAIL;
    }
    
    return 0;
}


/* Encode data from input file
 * infile input stream with not encoded data
 * outfile output stream with encoded data
 * return 0 if encoding was successful, FAIL otherwise
 */
size_t encode_raw_adaptive(ifstream &infile,ofstream &outfile,int64_t size, bool use_model){

 
    AdaptiveNode *huff_nodes[2*SYMBOLS] = {NULL};
    unsigned char output_buffer = 0;
    uint16_t buffer_position = 0;
    unsigned char data;
    uint32_t in_data;
    unsigned char *raw_buffer;
    unsigned char *raw_buff = (unsigned char*)malloc(sizeof(unsigned char) * size);
    char *model_buff = NULL;
    
    if(raw_buff == NULL){
        cout << "Not enough memory" << endl;
	exit(1);
    }
    read_raw(infile,raw_buff,size);
    
    if(use_model){
        model_buff = (char *) malloc(sizeof(char) * size);
        use_model_encoding_adaptive(raw_buff,model_buff, size);
        raw_buffer = (unsigned char*) model_buff;
    }
    else{
        raw_buffer = raw_buff;
    }
    int64_t curr_pos = 0;

    bool tree_created = create_tree(huff_nodes);
    if(!tree_created){
        return FAIL;
    }

    while(curr_pos < size){
	 data = raw_buffer[curr_pos];
	 in_data = (uint32_t) data;
	 
	 if(first_data_occurence(huff_nodes,MAX_ORDER+1,data)){
             save_encoded_symbol(&output_buffer,&buffer_position,NYT_VAL,huff_nodes,MAX_ORDER+1,outfile);
             bool is_inserted = insert_first(huff_nodes,MAX_ORDER+1,data);
	     if(!is_inserted){
                 return FAIL;
	     }
	     save_first_symbol(&output_buffer,&buffer_position,data,outfile);

	 }
	 else{
	    save_encoded_symbol(&output_buffer,&buffer_position,in_data,huff_nodes,MAX_ORDER+1,outfile);
	 }

         update_tree(huff_nodes,MAX_ORDER+1,data);
	 curr_pos++;
    }
    // NYT symbol used as EOF 
    save_encoded_symbol(&output_buffer,&buffer_position,NYT_VAL,huff_nodes,MAX_ORDER+1,outfile);
   
    //write output buffer if NYT was not written full
    if(buffer_position != 0){
       outfile.write(reinterpret_cast<char *>(&output_buffer),1);
    }

    free(raw_buffer);
    if(use_model){
        free(raw_buff);   
    }

    free_nodes_array(huff_nodes,MAX_ORDER+1);
    return 0;
}





