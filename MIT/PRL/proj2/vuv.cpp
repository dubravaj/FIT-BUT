/**
 * Vertex level algorithm
 * Juraj Ondrej Dubrava (xdubra03)
 *
 */

#include <mpi.h>
#include <iostream>
#include <fstream>
#include <algorithm>
#include <stdlib.h>
#include <math.h>
#include <vector>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>



using namespace std;

// Get position of nodes neighbours
int get_position(int **arr,int position, int number){

    for(int i=0; i < 3; i++){
      if(arr[position][i] == number)
         return i;

    }
    return -1;

}

// Create array with nodes neighbours
void get_neighbours(int nodes_count, int **nodes_neighbours){

    int left, right, parent;

    for(int i=0; i < nodes_count; i++){
        left = 2*i+1;
        right = 2*i+2;
        parent = (i-1) / 2;
	nodes_neighbours[i] = new int[3]{-1,-1,-1};

        if(left < nodes_count){
	    
	    if(parent == i){
	        nodes_neighbours[i][0] = left;
	    }
	    else{
	       nodes_neighbours[i][1] = left;
	   }

        }

	if(right < nodes_count){
           if(parent == i){
              nodes_neighbours[i][1] = right;
	   }
	   else{
	      nodes_neighbours[i][2] = right;
	   }
	}
        
        if(parent != i){
           nodes_neighbours[i][0] = parent;
        }
    }
}



int main(int argc, char *argv[]){

    
        int proc_count;
        int my_rank;
        MPI_Status stat;
	double start_time;
	double end_time;	

        MPI_Init(&argc,&argv);
        MPI_Comm_size(MPI_COMM_WORLD, &proc_count);
        MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);


        // create array with nodes names
	int size = strlen(argv[1]) + 1;
	char *nodes_array =  new char[size];
	strcpy(nodes_array,argv[1]);
	
	/** Create adjacency list for each vertex **/	
	int nodes_count = strlen(argv[1]);
	int **nodes_neighbours = new int*[nodes_count];
	get_neighbours(nodes_count,nodes_neighbours);
	int *final_nodes_levels = new int[proc_count];

	/**** Create Etour ***/
	int etour_edges = proc_count;
	int *etour_array = new int[etour_edges]();
	int edge_count = nodes_count -1;
	int *weights_array = new int[etour_edges]; 

		/** First half of the processors are for forward edges **/    
	        if(my_rank < etour_edges/ 2){
		    int dest = my_rank+1;
		    int src = ((my_rank+1) - 1) / 2;
		
		    int val_position = get_position(nodes_neighbours,dest,src);
		    if(val_position+1 > 2){
			etour_array[my_rank] = dest + edge_count;
		    }
		    else{
			 int next = val_position +1;
			    int next_val = nodes_neighbours[dest][next];
			    if(next_val == -1){
			        etour_array[my_rank] = dest + edge_count;
			    }
			    else{
			    	etour_array[my_rank] = nodes_neighbours[dest][next];
			    }
		    }
	        } 
	        else{
		    /** Backward edges **/	
		    int src = my_rank+1 - edge_count ;
		    int dest = ((my_rank +1  - edge_count) - 1) / 2;
		    
		    int val_position = get_position(nodes_neighbours,dest,src);
                    if(val_position == 2){
                        etour_array[my_rank] = dest + edge_count;
                    }
                    else{
                         int next = val_position +1;
                            int next_val = nodes_neighbours[dest][next];
                            if(next_val == -1){
				etour_array[my_rank] = dest + edge_count;
                            }
                            else{
                                etour_array[my_rank] = nodes_neighbours[dest][next];
                            }
                        }
		
	        }
		etour_array[my_rank] -= 1;
	  
	  
	    // send computed follower to each processor
	    MPI_Allgather(&etour_array[my_rank],1,MPI_INT,etour_array,1,MPI_INT,MPI_COMM_WORLD);


	
	/** last edge in Etour is pointing to itself to end the tour **/
	int last_edge;
	if(nodes_count > 2){
	    last_edge = edge_count +1;
	}
	else{
	    last_edge = 1;
	}

	etour_array[last_edge] = last_edge;

	/*** Setup weight for each edge*/
	if(my_rank < etour_edges/ 2){
	    weights_array[my_rank] = -1;
	}
	else{
	    weights_array[my_rank] = 1;
	}
	// send to each processor, simulating shared memory 
	MPI_Allgather(&weights_array[my_rank],1,MPI_INT,weights_array,1,MPI_INT,MPI_COMM_WORLD);
	
	/** Init val array ***/
	int *val_array = new int[etour_edges];
	memcpy(val_array,weights_array,sizeof(int)*etour_edges);
	
	int vi;
        if(etour_array[my_rank] == my_rank){
	    vi = 0;
	}
	else{
	    vi = weights_array[my_rank];
	}

	// send to each processor, simulating shared memory
	MPI_Allgather(&vi,1,MPI_INT,val_array,1,MPI_INT,MPI_COMM_WORLD);



	/**** Suffix sum ****/
	int log_n = ceil(log2(etour_edges));
	for(int i=1; i <= log_n; i++){
	    int next = etour_array[my_rank];
	    val_array[my_rank] += val_array[next];

	    int etour_next = etour_array[my_rank];
	    etour_array[my_rank] = etour_array[etour_next];
	    
            // send to each processor, simulating shared memory	    
	    MPI_Allgather(&val_array[my_rank],1,MPI_INT,val_array,1,MPI_INT,MPI_COMM_WORLD);
	    MPI_Allgather(&etour_array[my_rank],1,MPI_INT,etour_array,1,MPI_INT,MPI_COMM_WORLD); 
	}
	if(weights_array[last_edge] != 0){
	    val_array[my_rank] += weights_array[last_edge];
	}
        
	// send to each processor, simulating shared memory	
	MPI_Allgather(&val_array[my_rank],1,MPI_INT,val_array,1,MPI_INT,MPI_COMM_WORLD);	
	
	
	int level = -1;
        if(my_rank < etour_edges / 2){
	    level = val_array[my_rank] + 1;
	}
	
	// send to each processor, simulating shared memory
	MPI_Allgather(&level,1,MPI_INT,final_nodes_levels,1,MPI_INT,MPI_COMM_WORLD);
	
	// print levels of tree vertexes
	if(my_rank == 0){
	
            int root_level = 0;
	    if(proc_count > 1){
                cout << nodes_array[0] <<":" << root_level << ",";
	    }
            else{
               cout << nodes_array[0] <<":" << root_level << endl;
	    }
            for(int i=0; i < proc_count / 2; i++){
		if(i == ((proc_count / 2) -1)){
		    cout << nodes_array[i+1] <<":" << final_nodes_levels[i] << endl;
		
		}
		else{
		    cout << nodes_array[i+1] <<":" << final_nodes_levels[i] << ",";
		}
		
	    }
	}


	delete nodes_array;
	delete val_array;
	delete weights_array;
	delete etour_array;
	delete final_nodes_levels;
        for(int i=0; i < nodes_count; i++){
            delete [] nodes_neighbours[i];
	}
        delete [] nodes_neighbours;


 	MPI_Finalize();
 	return 0;

}






