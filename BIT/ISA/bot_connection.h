#ifndef CONNECTBOT_H
#define CONNECTBOT_H

#include <string>
#include <map>
#include "bot_functions.h"

using namespace std;

void is_port(Arguments * arguments);
int create_connection(int port_number,string host,sys_info *sys);
void irc_login(int sock);
void irc_join_channel(int sock, Arguments *args);
void send_pong(string message,int sock);
#endif
