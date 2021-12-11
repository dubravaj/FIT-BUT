#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <netdb.h>
#include <iostream>
#include <string>
#include "request_operations.h"
#include <sys/stat.h>
#include <fstream>
#include <dirent.h>
#include <sstream>
using namespace std;

#define OK 200
#define NOT_FOUND 404
#define BAD_REQUEST 400


enum command{
    PUT,
    DEL,
    GET,
    MKD,
    LST,
    RMD
};

char buf[1024];

int main (int argc, const char * argv[]) {


http_request response;
request_data request;
std::string new_request;
std::stringstream data;
std::string f_path;
std::ifstream file;
std::string f_data;
std::string mime;
std::string f_size;
//extract information from entered arguments
//save server, port number and remote path
parse_request(&request,argv);
//entered operation
std::string operation = argv[1];
char command;
std::string local_path;

//set operation
if(operation == "put"){
    
    command = PUT;
    if(argc == 3){
        cout << "Error: No local path entered." << endl;
        exit(EXIT_FAILURE);
    }
    f_path = argv[3];
    if(f_path.at(0) == '/'){
        f_path = f_path.erase(0,1);            
    }
    if(!exists(f_path)){
        cerr << "Error: File does not exists." << endl;
        exit(EXIT_FAILURE);
    }
    else if(!is_file(f_path)){
        cerr << "Error : Local path is not a file." << endl;
        exit(EXIT_FAILURE);
    }

    //read data to be put on the server
    ifstream file(f_path,std::ios::binary);


    data << file.rdbuf();
    f_data = data.str();

    ostringstream convert;
    convert << f_data.size();
    f_size = convert.str(); 
    if(!file){
        cerr << "Unknown error" << endl;
        exit(EXIT_FAILURE);
    }
    file.close();
}
else if(operation == "get"){
    command = GET;
}
else if(operation == "del"){
    command = DEL;
}
else if(operation == "lst"){
    command = LST;
}
else if(operation == "mkd"){
    command = MKD;
}
else if(operation == "rmd"){
    command = RMD;
}

//port number
int port = request.port_number;
int ftrest_socket;
int send_byte;
int receive_byte;

//server name
const char *server_name = (request.server).c_str();

struct hostent *server;
struct sockaddr_in server_address;

	//find server name
	if((server = gethostbyname(server_name)) == NULL){

		cerr << "Unknown error." <<endl; 
		exit(EXIT_FAILURE);
	}
	//find IP adddress of server
	bzero((char *) &server_address, sizeof(server_address));
    server_address.sin_family = AF_INET;
    bcopy((char *)server->h_addr, (char *)&server_address.sin_addr.s_addr, server->h_length);
    server_address.sin_port = htons(port);

    //create socket
    if((ftrest_socket = socket(AF_INET,SOCK_STREAM,0)) <= 0){

    	cerr << "Unknown error." << endl;
    	exit(EXIT_FAILURE);
    }

    if (connect(ftrest_socket, (const struct sockaddr *) &server_address, sizeof(server_address)) != 0)
    {
		cerr << "Unknown error." << endl;
		exit(EXIT_FAILURE);       
    }

 
    //COMMAND part
    //for each operation is created its own type of request for server
    switch(command){
        case PUT:
            
            //create request for put command
            mime = get_mime_type(f_path);
            new_request = put_post_request("PUT",request.path,"file",date(),"text/plain","identity",mime,f_size,f_data.data());
            break;
        case GET:
            //create request for get command
            new_request = create_request("GET",request.path,"file",date(),"text/plain","identity","");
            break;
        case DEL:
            //create request for del command
           new_request  = create_request("DELETE",request.path,"file",date(),"text/plain","identity","");
            break;
        case MKD:
          //create request for mkd command
          new_request  = put_post_request("PUT",request.path,"folder",date(),"text/plain","identity","inode/directory","0","");
            break;
        case LST:
          //create request for lst command
           new_request  = create_request("GET",request.path,"folder",date(),"text/plain","identity","");

            break;
        case RMD:
           //create request for rmd command
           new_request  = create_request("DELETE",request.path,"folder",date(),"text/plain","identity","");
            break;
    }

    //send request to the server
    int data_size = new_request.size();

    while(1){
        send_byte = send(ftrest_socket,new_request.data(),new_request.size(),0);
        data_size -= send_byte;
        if(data_size <= 0 ){
            break;
        }

    }

 //**************************************************
//Receive response from server 

    std::string data_buff;
    int pos;
    char buff;
    int rec = 0;
    while(1){   
        rec = receive_data(ftrest_socket,&buff);
        data_buff += buff;
        if(data_buff.find("\r\n\r\n") != std::string::npos){
            break;
        }
                
    }
    
    //received response is parsed
    extract_response(&response,data_buff);
   //if operation on server failed, accordind to received error code, error message is printed to user 
   if(response.response_code == BAD_REQUEST){
        int error_size = response.content_length;
        std::string content = read_content(ftrest_socket,error_size);
        cerr << "ERROR:" << content << endl;
        exit(1);
    }
    else if(response.response_code == NOT_FOUND){
        int error_size = response.content_length;
        std::string content = read_content(ftrest_socket,error_size);
        cerr << "ERROR:" << content << endl;
        exit(1);
    }



    //if get command was entered, we need to extract data from response and create new file and save content
    if(command == GET){
        int d_size = response.content_length;
        std::string content;
        if(d_size > 0){
            content = read_content(ftrest_socket,d_size);
        }
        else{
            content = "";
        }

        if(argc == 4){
            std::string localpath;
            localpath = argv[3];
            if(!exists(localpath)){
                cerr << "Directory not found." << endl;
                exit(EXIT_FAILURE);
            }
            //local path is file, so is overwritten
            if(!is_dir(local_path)){
                if(localpath.at(0) == '/'){
                    localpath = localpath.erase(0,1);            
                }
                std::ofstream outfile(localpath.c_str(),std::ios::binary);
                if(!outfile){
                    cerr << "Unknown error" << endl;
                    exit(EXIT_FAILURE);
                }
                outfile << content;
                outfile.close();
            }
            //local path is directory,file is saved there
            else{

                if(localpath.at(0) == '/'){
                    localpath = localpath.erase(0,1);            
                }
                cout << localpath << endl;
                chdir(localpath.c_str());
                std::string base = (request.path).substr((request.path).find_last_of("/") + 1);
                std::ofstream outfile(base.c_str(),std::ios::binary);
                if(!outfile){
                    cerr << "Unknown error" << endl;
                    exit(EXIT_FAILURE);
                }
                outfile << content;
                outfile.close();
            }
        }
        else{
            //create file in current directory 
            std::string base = (request.path).substr((request.path).find_last_of("/") + 1);
            std::ofstream outfile(base.c_str(),std::ios::binary);
            if(!outfile){
                cerr << "Unknown error" << endl;
                exit(EXIT_FAILURE);
            }
            outfile << content;
            outfile.close();
        }

    }
    //if lst command was entered, we received directory content in response,content is printed to stdout for user
    else if(command == LST){
        
            int d_size = response.content_length;
            std::string content = read_content(ftrest_socket,d_size);
            cout << content << endl;
        
    }

    close(ftrest_socket);
    return 0;






}	
