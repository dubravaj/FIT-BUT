#include <stdio.h>
#include <stdlib.h>
#include <algorithm>
#include <map>
#include <string>




using namespace std;

typedef map<uint32_t,size_t> freq_map;
typedef pair<uint32_t,string> hc_item;
typedef map<uint32_t,string> huff_map;
typedef pair<int,int> node;
typedef pair<char,int> code_item;
typedef pair<char,int> huff_item;


struct TreeNode{

    uint32_t data;
    size_t freq;

    TreeNode *left;
    TreeNode *right;

    TreeNode(uint32_t data, size_t freq){
        left = NULL;
        right = NULL;
        this->data = data;
        this->freq = freq;
    }
    
};

struct compare{

        bool operator()(TreeNode* l_node, TreeNode* r_node){

            return (l_node->freq > r_node->freq);
        }
};

TreeNode *create_huffman_tree(freq_map input_data);
void delete_huff_tree(TreeNode *root);
void create_codes(TreeNode *root,huff_map &huff_codes, string code);
bool is_leaf(TreeNode *node);
void encode(freq_map freq_data, huff_map &codes, unsigned char *buffer,size_t size, ofstream &outfile);
void decode_raw_static(ifstream &input, ofstream &output,bool use_model);
void read_raw(FILE *input, unsigned char *buffer,size_t raw_size);
size_t get_raw_size(FILE *input);
freq_map get_raw_freqs(unsigned char *buffer,size_t size);
void encode_raw_static(FILE *infile,ofstream &outfile, bool use_model);
