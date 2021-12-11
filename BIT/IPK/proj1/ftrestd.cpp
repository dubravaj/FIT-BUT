#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <iostream>
#include <fstream>
#include <string>
#include <sstream>
#include <map>
#include "request_operations.h"
#include <sys/stat.h>
#include <errno.h>
#include <dirent.h>
#define MINPORT 1024
#define MAXPORT 65535
#define OK 200
#define NOT_FOUND 404
#define BAD_REQUEST 400
using namespace std;


   
int main (int argc, const char * argv[]) {
	

   http_request client_request;
   command_data url_data;

	int rc;
	int welcome_socket;
	struct sockaddr_in6 sa;
	struct sockaddr_in6 sa_client;
	char str[INET6_ADDRSTRLEN];
    int port_number;



//------------------------------------------------
//ARGUMENTS
//--------------------------------------------------
	std::string root_folder;
    std::string opt_port;
    std::string opt_root;
    bool root_f = false;
    std::string opt;
    //default value for port number
   	port_number = 6677; 
    
   	if(argc == 3){
   		opt = argv[1];
   		//port option entered
    	if(opt == "-p"){
    		port_number = stoi(argv[2]);
    		if(port_number < MINPORT || port_number > MAXPORT){
    			cerr << "Error: Bad port number entered." << endl;
                exit(EXIT_FAILURE);
    			
    		}
    	}
    	//root folder option entered
    	else if(opt == "-r"){
    		root_f = true;
    		root_folder = argv[2];
    	}
    	else{
    		cerr << "Error: Bad parameters" << endl;
            exit(EXIT_FAILURE);
    		
    	}
    }
    else if(argc == 5){
    	opt = argv[1];
    	if(opt == "-r"){
    		root_f = true;
    		root_folder = argv[2];
    	}
    	else{
    		cerr << "Error: Bad parameters." << endl;
            exit(EXIT_FAILURE);
    	}
    	opt_port = argv[3];
    	if(opt_port == "-p"){
    		port_number = stoi(argv[4]);
    		if(port_number < MINPORT || port_number > MAXPORT){
    			cerr << "Error: Bad port number entered." << endl;
    			exit(EXIT_FAILURE);
    		}
    	}
    	else{
    		cerr << "Error: Bad parameters." << endl;
            exit(EXIT_FAILURE);
    	}

    }
    else if(argc > 5){
    	cerr << "Error: Bad parameters." << endl;
        exit(EXIT_FAILURE);
    }
   
//---------------
//END ARGUMENTS

   

//COMMUNICATION

	socklen_t sa_client_len=sizeof(sa_client);
	if ((welcome_socket = socket(PF_INET6, SOCK_STREAM, 0)) < 0)
	{
		cerr << "Unknown error" << endl;
		exit(EXIT_FAILURE);
	}
	
	memset(&sa,0,sizeof(sa));
	sa.sin6_family = AF_INET6;
	sa.sin6_addr = in6addr_any;
	sa.sin6_port = htons(port_number);	
        
    
    
	if ((rc = bind(welcome_socket, (struct sockaddr*)&sa, sizeof(sa))) < 0)
	{
		cerr << "Unknown error" << endl;
		exit(EXIT_FAILURE);		
	}
	if ((listen(welcome_socket, 1)) < 0)
	{
		cerr << "Unknown error." << endl;
		exit(EXIT_FAILURE);				
	}
	
	
    while(1){


		int comm_socket = accept(welcome_socket, (struct sockaddr*)&sa_client, &sa_client_len);		
		if (comm_socket > 0)
		{
			if(inet_ntop(AF_INET6, &sa_client.sin6_addr, str, sizeof(str))) {
				
			}
			
			std::string data_buff;
			int pos;
			char buff;
			int res = 0;

			//receive socket with data from client 
			//we need to find where http headers end
			while(1)		
			{	
				res = receive_data(comm_socket,&buff);
				
               	data_buff += buff;
                if(data_buff.find("\r\n\r\n") != std::string::npos){
                	break;
                }
             	
			}
			//extract http headers from request,save data to http structure
			extract_request(&url_data,&client_request,data_buff,comm_socket);
			
            std::string check_account;

            std::string cur_path;
                    //control which folder is set to be root folder 
                    if(root_f){
                       
                        if(root_folder.at(root_folder.size() - 1) == '/'){
                                root_folder = root_folder.erase(root_folder.size()-1,1);
                        }
                      cur_path = root_folder + client_request.path;

                        if(cur_path.at(0) == '/'){
                                cur_path = cur_path.erase(0,1);
                        }
                         check_account = root_folder + "/"+ url_data.user_account;

                    }
                    else{
                        cur_path = client_request.path;
                        
                        if(cur_path.at(0) == '/'){
                                cur_path = cur_path.erase(0,1);
                        }
                        check_account = url_data.user_account;

                    }

                    

//--------------COMMAND SECTION---------------
			std::string error_message;
			//GET command
			//GET option for folder = lst operation
			if(client_request.command == "GET"){
				if(client_request.type == "folder"){
                    
                    
                    if(!exists(check_account)){
                        error_message = "User account does not exist.";
                        send_response(comm_socket,404,error_message);
                    }

					//check if path exists
            		else if(!exists(cur_path)){
                		error_message = "Directory not found.";
                		send_response(comm_socket,404,error_message);
                		
            		}
                    //check if path is directory 
            		else if(is_file(cur_path)){
                		error_message = "Not a directory.";
                		send_response(comm_socket,400,error_message);
            		}
                    else{
                        
                	
                    	//prepares content from ls command and sends it back to client
                    	std::string ls_content = make_ls(cur_path);
                		std::string mime = "inode/directory";
                    	std::string ls_size = to_string(ls_content.size());
                 		std::string ls_response = create_response(OK,date(),mime,ls_size,"identity",ls_content.data());
                        //send response with data from LS command
                		//control of send bytes 
                		int send_byte;
                    	int data_size = ls_response.size();
                
                		while(1){
                			send_byte = send(comm_socket,ls_response.data(),ls_response.size(),0);
                		    data_size -= send_byte;
                			if(data_size <= 0 ){
                    			break;
                			}

            			}
					   
                    }

				}
				//GET option - get command
				//copy file from remote path to entered local path or current working directory 
				else{
					
    				std::stringstream user_data;
    				std::string file_data = "";
                    
                    if(!exists(check_account)){
                        error_message = "User account does not exist.";
                        send_response(comm_socket,404,error_message);
                    }
    				//check if file from remote path exists
					else if(!exists(cur_path)){
                		error_message = "File not found.";
                		send_response(comm_socket,404,error_message);
            		}
            		  //check if remote path is file
            		else if(!is_file(cur_path)){
                		error_message = "Not a file";
                		send_response(comm_socket,400,error_message);
                		
            		}
            		//copy content of file to be downloaded to string 
            		else{
                        if((cur_path).at(0) == '/'){
    							cur_path = cur_path.erase(0,1);
    						
    					}
                		std::ifstream file(cur_path,std::ios::binary);
                		if(!file){
                			error_message = "Unknown error";
                			send_response(comm_socket,400,error_message);
                			
                		}
                        else{
        					user_data << file.rdbuf();
        					file_data = user_data.str();
        					file.close();
        					//create response with filecontent
        					std::string mime_type = get_mime_type(cur_path);
        					std::string file_response = create_response(OK,date(),mime_type,to_string(file_data.size()),"identity",file_data.data());	
        					//send response with data from GET command
                        	int send_byte;
                        	int data_size = file_response.size();
                        	while(1){
                				send_byte = send(comm_socket,file_response.data(),file_response.size(),0);
                				data_size -= send_byte;
                				if(data_size <= 0 ){
                         			break;
                				}

            				}
                        }
    				}
                }
			}
			//DELETE option - rmd command
			else if(client_request.command == "DELETE"){
					
    				if(client_request.type == "folder"){
    					if(!exists(check_account)){
                            error_message = "User account does not exist.";
                            send_response(comm_socket,404,error_message);
                        }
    					//check if object from remote path exists
    					else if(!exists(cur_path)){
                    		error_message = "Directory not found.";
                    		send_response(comm_socket,404,error_message);
            			}
            			//check if remote path is folder
            			else if(is_file(cur_path)){
                   			error_message = "Not a directory";
                    		send_response(comm_socket,400,error_message);
                		}
                        else{
                    		int result;
                    		//remove entered dir
                    		result = rm_dir(cur_path);
                			if(result == 1){
                    		  error_message = "Directory not empty.";
                    		  send_response(comm_socket,400,error_message);

                			}
                			else if(result == 2){
                                error_message = "User account could not be removed.";
                    		    send_response(comm_socket,400,error_message);
                    		}
                    		else{
                            //command was successfully done
                    		  send_response(comm_socket,200,"");
                            }
                		}
                			
					}
					//DELETE option - del command
					else{
                        if(!exists(check_account)){
                            error_message = "User account does not exist.";
                            send_response(comm_socket,404,error_message);
                        }
						//check if remote path object exists
						else if(!exists(cur_path)){
                		
                			error_message = "File not found.";
                			send_response(comm_socket,404,error_message);

            			}
            			//check if remote path is file
            			else if(!is_file(cur_path)){
                			
                			error_message = "Not a file.";
                			send_response(comm_socket,400,error_message);
            			}
            			else{
                             //removes entered file
                			int rm;
                			rm = rm_file(cur_path);
                			if(rm == 1){
                				 
                				error_message = "Unknown error.";
                				send_response(comm_socket,400,error_message);   

                			}
                            else{     			
                			     send_response(comm_socket,200,"");
                            }
                        }
					}

			
			} // END DELETE
			//PUT option - mkd command 
			else if(client_request.command == "PUT"){
				if(client_request.type == "folder"){
					
					//creates new folder, controls are done inside function
					int mk;
					mk = make_dir(cur_path);
					if(mk == 1){
						
						error_message = "No permission to create user account.";
						send_response(comm_socket,400,error_message);
					}
					else if(mk == 2){
						
						error_message = "Already exists.";
						send_response(comm_socket,400,error_message);
					}
                    else{
					   send_response(comm_socket,200,"");
                    }

				}
				//PUT option - put command
				else{
					int data_size = client_request.content_length;
                    std::string cnt;
                    if(data_size > 0){
                        cnt = read_content(comm_socket,data_size);
                    }
                    else{
                        cnt = "";
                    }   
					//reads filecontent from received socket
					//std::string cnt = read_content(comm_socket,data_size);
					if((cur_path).at(0) == '/'){
						cur_path = cur_path.erase(0,1);
					}
					//check if file already exists
                    if(!exists(check_account)){
                       
                        error_message = "User account does not exist.";
                        send_response(comm_socket,404,error_message);
                    }
                    else if(is_dir(cur_path)){
                        
                        error_message = "Not a file.";
                        send_response(comm_socket,400,error_message);
                    }
					else if(exists(cur_path)){
                			
                			error_message = "Already exists.";
                			send_response(comm_socket,400,error_message);
            		}
            		else{
    					//creates new file with entered name
    					std::string name = cur_path;
    					std::ofstream outfile(name.c_str(),ios::binary);
    					if(!outfile){
    						
    						error_message = "Unknown error.";
    						send_response(comm_socket,400,error_message);
    					}
                        else{
    					   outfile << cnt;
    					   outfile.close();

    					   send_response(comm_socket,200,"");
                          
                        }
                    }

				}
			}

		}
		else
		{
			cerr << "Unknown error." << endl;
			exit(EXIT_FAILURE);
		}
		close(comm_socket);
	}	
}

