#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <unistd.h>
#include <pthread.h>
#include <string>
#include <iostream>
#include <sstream>
#include <signal.h>
#include <csignal>
using namespace std;

int socket_t;
std::string user;
/*structure for storing information about username and chat server*/
typedef struct chat_info{
	std::string server_address;
	std::string username;
}chat_info;

/**
* Check argumnets for client program
* order have to be -i address -u username
*/
int check_arguments(int argc, const char * argv[],chat_info *user_data){
	

	if(argc == 5){
		std::string opt;
		opt = argv[1];
		if(opt == "-i"){
			user_data->server_address = argv[2];
		}
		else{
			return 1;
		}
		opt = argv[3];
		if(opt == "-u"){
			user_data->username = argv[4];
		}
		else{
			return 1;
		}		
	}
	else{
		return 1;
	}
	return 0;
}

//crate new socket  
int create_socket(){
	 int client_socket;
	 if((client_socket = socket(AF_INET,SOCK_STREAM,0)) <= 0){
	 	return -1;
    }
    return client_socket;
}

//send user login message in new socket
void send_log_message(){
	std::string log_message = user+" logged in\r\n";
	int send_byte;
	send_byte = send(socket_t,log_message.data(),log_message.size(),0);
	if(send_byte < 0){
		cerr << "Error: Cannot send data." << endl;
		exit(1);
	}
}

//send logout message after SIGINT signal
void send_logout_message(){
	std::string log_message = user+" logged out\r\n";
	int send_byte;
	send_byte = send(socket_t,log_message.data(),log_message.size(),0);
	if(send_byte < 0){
		cerr << "Error: Cannot send data." << endl;
		exit(1);
	}
}

//create new connection for communication
void create_connection(const char *server_name,int client_socket){

	struct hostent *server;
	struct sockaddr_in server_address;
	//set destination port number
	int port = 21011;
	//find server name
	if((server = gethostbyname(server_name)) == NULL){

		cerr << "Error: Could not resolve server name." <<endl; 
		exit(EXIT_FAILURE);
	}
	//find IP adddress of server
	bzero((char *) &server_address, sizeof(server_address));
    	server_address.sin_family = AF_INET;
    	bcopy((char *)server->h_addr, (char *)&server_address.sin_addr.s_addr, server->h_length);
    	server_address.sin_port = htons(port);

    	if (connect(client_socket, (const struct sockaddr *) &server_address, sizeof(server_address)) != 0)
    	{
		cerr << "Error: Cannot connect to server." << endl;
		exit(EXIT_FAILURE);       
    	}
}

/**
* function for handling message sending
* users message is read from standard input and send it after enter is pressed
*/
void *send_message(void *ptr){
	//read message from input
	int send_byte;
	std::string message;
	while(getline(cin,message)){
		if(message.size() > 0){
			std::string msg = user+":"+" "+message+"\r\n";		
			send_byte = send(socket_t,msg.data(),msg.size(),0);
			if(send_byte < 0){
				cerr << "Error: Could not send data." << endl;
				exit(EXIT_FAILURE);
			}
		}
		else{
		
		}
	}
}

/**
* function for handling message receive
* message is read from socket through buffer of size 1024 characters
*/
void *receive_message(void *ptr){

	std::string content="";
	int res;
	char buffer[1024];
	while(1){
		res = recv(socket_t,buffer,1024,0);
                if(res < 0){
	           cerr << "Error in receiving message" << endl;
	           exit(1);		
		}
		content += std::string(buffer,res);
		if(content.find("\r\n") != std::string::npos){
        	}
        //print receive message from socket
		cout << content;
		content ="";
		memset(buffer,0,1024);
	}
}

// handler function for catching SIGINT signal
// after signal is caught, client sends logout message and exit
void my_handler(int sig){

	send_logout_message();
	close(socket_t);
	exit(0);
}


int main (int argc, const char * argv[]) {

	chat_info comm_data;

	int check_args = check_arguments(argc,argv,&comm_data);
	if(check_args == 1){
		cerr << "Error: Bad arguments entered." << endl;
		exit(1);
	}
	const char *server_name = (comm_data.server_address).c_str();
	socket_t = create_socket();
	
	if(socket_t == -1){
    		cerr << "Error: Cannot create socket." << endl;
   		exit(1);
   	}
   	user = comm_data.username;
	//set connecton with server
	create_connection(server_name,socket_t);
   	//send log message
   	send_log_message();
   	//create threads to handle sending and receiving messages
   	pthread_t send_thread;
   	pthread_t receive_thread;

 
   	pthread_create(&send_thread,NULL,send_message,NULL);
   	pthread_create(&receive_thread,NULL,receive_message,NULL);
	//waiting for SIGINT from user
    	signal(SIGINT,my_handler);
        
   	pthread_join(send_thread,NULL);
   	pthread_join(receive_thread,NULL);
	return 0;
}
