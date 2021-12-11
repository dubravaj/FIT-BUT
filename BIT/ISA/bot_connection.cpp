#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <netdb.h>
#include <sys/stat.h>
#include <string.h>
#include <iostream>
#include <sstream>
#include <vector>
#include "bot_functions.h"
#include "bot_connection.h"
#define NICK "xdubra03"		//nickname for irc server
#define USERNAME "xdubra03"	//username for irc server
#define REALNAME "xdubra03" //realname for irc server
#define DEFAULT_SYSLOG_IP "127.0.0.1" //default syslog IP
#define SYS_PORT 514

/*
* Check for port in input arguments
* @param arguments pointer to structure with parameter values
*/
void is_port(Arguments *arguments){
	size_t pos;
	string s;
	string port_num;
	if((pos = (arguments->host).find(':')) != string::npos){
		s = arguments->host;
		arguments->host = (arguments->host).substr(0,pos);
		//check if port number contains only digits
		port_num = s.substr(pos+1,string::npos);
		for(size_t i=0; i < port_num.size(); i++){
			if(isalpha(port_num[i])){
				throw std::invalid_argument("Bad port argument.");
			}
		}
		try{
			arguments->port = stoi(s.substr(pos+1,string::npos));

		}
		catch(const std::invalid_argument& e){
			throw std::invalid_argument("Bad port argument.");
		}
	}
}

/*
* Function creating connection with remote server
* @param port_number port number on remote server
* @param host host domain name/IP addres of syslog server
* @param sys pointer to structure with syslog informations
* @return created socket
*/
int create_connection(int port_number,string host,sys_info *sys){

    //create connection with host using addrinfo
    struct addrinfo hints;
    struct addrinfo *result, *p;
	//store informations for syslog logging
	struct sockaddr_in syslog_server;
    string port = std::to_string(port_number);
    const char *host_port = port.c_str();
    const char *host_name = (host).c_str();
    int irc_socket,irc_s;

    memset(&hints,0,sizeof(struct addrinfo));
    hints.ai_family = AF_INET;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = 0;
    hints.ai_protocol = 0;

	//syslog port entered,store information about server,create socket for communication
	if(port_number == SYS_PORT){

		//create socket for UDP connection with syslog server
		irc_socket = socket(AF_INET,SOCK_DGRAM,0);
		if(irc_socket == -1){
			throw std::runtime_error("Cannot create socket.");
		}
		//syslog host is always IP address, not domain name,so we dont have to resolve ip address with gethostbyname
		syslog_server.sin_addr.s_addr = inet_addr(host.c_str());   // set the syslog server address
 		syslog_server.sin_family = AF_INET;
 		syslog_server.sin_port = htons(SYS_PORT);                // set the server port
		//save informations about syslog connection
		sys->sys_len = sizeof(syslog_server);
		sys->syslog_info = syslog_server;

		return irc_socket;
	}

	//creating TCP connection with IRC server
    //addrinfo returns list of address structures
    irc_s = getaddrinfo(host_name,host_port,&hints,&result);

    if(irc_s != 0){
    	throw std::runtime_error("Addrinfo error.");
    }

    //try to create connection for each address from the list,if connection succeeded then break
    for(p = result; p != NULL; p = p->ai_next){

		//try to create socket
        irc_socket = socket(p->ai_family,p->ai_socktype,p->ai_protocol);

        if(irc_socket == -1){
            continue;
        }
		//try to connect
	    if(connect(irc_socket,p->ai_addr,p->ai_addrlen) != -1){
			break;
	    }
		close(irc_socket);
	 }

		freeaddrinfo(result);

		//cannot connect to any of addresses from the list
	    if(p == NULL){
			throw std::runtime_error("Cannot connect.");
	    }

    	return irc_socket;
}

/*
* Log in on IRC server
* @param sock socket for communication
*/
void irc_login(int sock){

	//create login message
    string nick_string = "NICK "+ string(NICK) + "\r\n";
    string user_string = "USER "+ string(NICK) +" "+string(USERNAME)+" "+string(REALNAME)+": hello"+"\r\n";
    string log_parameters = nick_string + user_string;

	//send login message
    send_data(sock,log_parameters);

}

/*
* Join specified channels on IRC server
* @param sock socket for communication
* @param args pointer to parameters structure
*/
void irc_join_channel(int sock, Arguments *args){

	check_channels(args->channels);
	//create join message
    string channels_string = "JOIN " + (args->channels)+ "\r\n";
	//send join message
    send_data(sock,channels_string);
}

/*
* Send pong response to server
* @param message received message from server
* @param sock socket
*/
void send_pong(string message,int sock){

	int pos;
    pos = message.find(':');
	//create pong response
    string pong_response = "PONG :"+message.substr(pos+1,string::npos)+"\r\n";
	//send pong message
   send_data(sock,pong_response);

}
