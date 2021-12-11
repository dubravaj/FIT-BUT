#include <stdio.h>
#include <stdlib.h>
#include <algorithm>
#include <map>
#include <string>

#define MAX_ORDER 512
#define NYT_VAL 256
#define INTERNAL_NODE_VAL 257
#define SYMBOLS 257
#define FAIL 2

using namespace std;


struct AdaptiveNode{

	uint32_t data;
	size_t weight;
	int32_t parent;
	int32_t left;
	int32_t right;

        AdaptiveNode(uint32_t data,size_t weight, int32_t parent,int32_t left,int32_t right){
	    this->data = data;
	    this->weight = weight;
	    this->parent = parent;
	    this->right = right;
	    this->left = left;
        }	    

};



size_t encode_raw_adaptive(ifstream &infile,ofstream &outfile,int64_t size, bool use_model);
size_t decode_raw_adaptive(ifstream &infile, ofstream &outfile,int64_t size, bool use_model);
int64_t get_raw_size_adaptive(ifstream &input);
