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
#include <signal.h>
#include <iostream>
#include <stdexcept>
#include "bot_functions.h"
#include "bot_connection.h"
#define DEFAULT_SYSLOG_IP "127.0.0.1"
#define SYS_PORT 514
using namespace std;

int bot_socket;
int syslog_sock;
bool connected;

/*
* Handler function when CTRL-C is caught
* @param sig signal
*/
void connection_handler(int sig){
	if(connected){
		close(syslog_sock);
	}
	close(bot_socket);
	exit(1);

}

int main(int argc, const char * argv[]){

	Arguments args;
	//default values for port and syslog IP address
	args.port = 6667;
	args.syslog_server = DEFAULT_SYSLOG_IP;
	sys_info sys;
	connected = false;

	//create connection with IRC server
	try{
		//save arguments
		parse_arguments(argc,argv,&args);
		//create connection
		bot_socket = create_connection(args.port,args.host,NULL);
		irc_login(bot_socket);
		irc_join_channel(bot_socket,&args);
	}
	catch(const std::runtime_error& e){
		cerr << "Error: " << e.what() << endl;
		exit(EXIT_FAILURE);
	}
	catch(const std::invalid_argument& e){
		cerr << "Error: " << e.what() << endl;
		exit(EXIT_FAILURE);
	}

	//create connection for syslog server
	if(args.Flags.is_keyword){
		try{

			syslog_sock = create_connection(SYS_PORT,args.syslog_server,&sys);
			connected = true;
		}
		catch(const std::runtime_error& e){
			cerr << "Error: " << e.what() << endl;
			exit(EXIT_FAILURE);
		}

	}
	//receive messages and parse them
	while(1){

			try{
				parse_message(bot_socket,syslog_sock,&args,&sys);
			}
			catch(const std::invalid_argument& e){
					cerr << "Error: " << e.what() << endl;
					exit(EXIT_FAILURE);
			}
			catch(const std::runtime_error& e){
					cerr << "Error: " << e.what() << endl;
					exit(EXIT_FAILURE);
			}
			signal(SIGINT,connection_handler);
	}

	signal(SIGINT,connection_handler);

	return 0;
}
