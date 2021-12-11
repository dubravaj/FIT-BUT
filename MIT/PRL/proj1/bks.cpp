/**
 * Bucket sort algorithm
 * Juraj Ondrej Dubrava (xdubra03)
 *
 */

#include <mpi.h>
#include <iostream>
#include <fstream>
#include <algorithm>
#include <stdlib.h>
#include <math.h>

#define GET_LEVEL(i)(floor(log2(i+1)))

using namespace std;

/* Find number of numbers in input file */ 
int count_numbers(char infile[]){

    std::ifstream input_file(infile, std::ios::binary | std::ios::ate);
    int file_size = input_file.tellg();
    return file_size;
}
/* Print loaded numbers */
void print_numbers(unsigned char *root_array,int count){

    for(int i=0; i<count; i++){
        if(i == count-1){
	    cout << (int)root_array[i];
	}
	else{
	    cout << (int)root_array[i] << " ";
    	}

    }
}
/* Store numbers from file to array */ 
void load_numbers(unsigned char *root_array, char infile[]){

    unsigned char num;
    int counter = 0;
    fstream f;

    f.open(infile,ios::in);

    while(f.good()){
	num = f.get();
        if(!f.good()){
           break;
        }
	root_array[counter] = num;
        counter++;
    }
    f.close();
}

/* Print array items */
void print_arr(unsigned char *arr, int size){
	
    cout << '\n';
    for(int i=0; i < size; i++){
        cout << (int)(arr[i]) << endl;
    } 
}


int main(int argc, char *argv[]){
	
	int proc_count;
	int my_rank;
	MPI_Status stat;
	int new_input_count;
	int no_numbers;	

	MPI_Init(&argc,&argv);
        MPI_Comm_size(MPI_COMM_WORLD, &proc_count); 
        MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);

	if(my_rank == 0){

		char infile[] = "numbers";
        	no_numbers = count_numbers(infile);
		int no_leafs = (proc_count +1) / 2 ; 
        	int bucket_size = (int) ceil((double) no_numbers / no_leafs);
        	int new_input_size = no_leafs * bucket_size;
		new_input_count = new_input_size;
        	unsigned char *root_array = new unsigned char[new_input_size]();
		
		//load numbers from numbers file
		load_numbers(root_array,infile);
		print_numbers(root_array,no_numbers);



		if(no_numbers == 1){
                   print_arr(root_array,no_numbers);
		   delete root_array;
		   MPI_Finalize();
		   return 0;
            
		}
		else if(no_numbers == 2){
		         unsigned char tmp;
		         if(root_array[0] > root_array[1]){
		             tmp = root_array[0];
		             root_array[0] = root_array[1];
		             root_array[1] = tmp;
		         }
		         print_arr(root_array,no_numbers);
		         delete root_array;
		         MPI_Finalize();
		         return 0;
		
		}
		else{
		    //send size of input to all processors
		    for(int i=1; i< proc_count; i++){
		        MPI_Send(&new_input_size,1,MPI_INT,i,0,MPI_COMM_WORLD);
		    }
		    //send data to each leaf processor

		    //start_time = MPI_Wtime();
		    for(int i=0; i<no_leafs; i++){
		        MPI_Send(root_array+i*bucket_size,bucket_size,MPI_BYTE,proc_count - i - 1,0,MPI_COMM_WORLD);
		    }
		
		    delete root_array;
		}

	}
        else{		
	      // all processors receive size of input
	      MPI_Recv(&new_input_count,1,MPI_INT,0,0,MPI_COMM_WORLD, &stat);
		
	       int m_leafs = (proc_count + 1) / 2;
		
	       // leaf processors receive data from master
	       int right_son = 2*my_rank + 2;
	       int left_son = 2*my_rank + 1;
	       if(right_son >= proc_count && left_son >= proc_count){
		
		   int leaf_size = new_input_count / m_leafs;
                       
		    // store data from rooot processor	
	            unsigned char *leaf_array = new unsigned char[leaf_size];
	            MPI_Recv(leaf_array,leaf_size, MPI_BYTE, 0, 0, MPI_COMM_WORLD, &stat);
			
	            // sort processor's array and send it to parent
	            std::sort(leaf_array,leaf_array+leaf_size);
	            int parent = (my_rank-1) / 2;
			
	            MPI_Send(leaf_array,leaf_size,MPI_BYTE,parent,0,MPI_COMM_WORLD);

		    delete leaf_array;		
		}
	}
	
	/*-------------------*/

	
	int leaf_count = (proc_count + 1) / 2;
	double tmp_log_m = log2((double) leaf_count);
	int log_m = (int) tmp_log_m;
	
	// traverse tree levels and send data to the parent's node
	for(int j=1; j <= log_m; j++){
	
	        // find current level, nodes on that level are receiving data	
		if(GET_LEVEL(my_rank) == log_m - j){
		   		   
		   int current_level = 1 << (log_m - j);
		   int bucket_size = new_input_count / current_level ;
		   int small_bucket = bucket_size / 2;
		   unsigned char *rec_array1 = new unsigned char[small_bucket];
		   unsigned char *rec_array2 = new unsigned char[small_bucket];
		   unsigned char *merged_array = new unsigned char[bucket_size];
		 
		   int parent = (my_rank-1) / 2;

		   /* receive data from both children nodes */
	           MPI_Recv(rec_array1,small_bucket, MPI_BYTE, 2*my_rank+1, 0, MPI_COMM_WORLD, &stat);
	           MPI_Recv(rec_array2,small_bucket, MPI_BYTE, 2*my_rank+2, 0, MPI_COMM_WORLD, &stat);
		
		   /* sort data and merge sorted arrays */
		   std::sort(rec_array1,rec_array1+small_bucket);
		   std::sort(rec_array2,rec_array2+small_bucket);
		   std::merge(rec_array1,rec_array1+small_bucket,rec_array2,rec_array2+small_bucket,merged_array);
		   
		   //root will print result array		   
		   if(my_rank == 0){

		       int root_array_start = new_input_count - no_numbers; 
		       print_arr(merged_array+root_array_start,no_numbers);
		   }
		   else{			  
			
		       MPI_Send(merged_array,bucket_size,MPI_BYTE,parent,0,MPI_COMM_WORLD);
		     
		   }

		   delete rec_array1;
		   delete rec_array2;
		   delete merged_array;

		}		
	}

	MPI_Finalize();
	return 0;

}
