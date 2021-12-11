#ifndef IRCBOT_H
#define IRCBOT_H

#include <string>
#include <map>
#include <vector>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <netdb.h>
#include <sys/stat.h>
using namespace std;

//arguments structure
typedef struct Arguments{
    string host;
    int port;
    string channels;
    string syslog_server;
    string keywords;
    int syslog_port;
    struct{
        bool is_help;
        bool is_keyword;
    }Flags;


}Arguments;

//structure for syslog informations
typedef struct sys_info{
    int sys_len;
    struct sockaddr_in syslog_info;

}sys_info;

//map for storing informations about channels and online users
typedef map <string,pair<bool,vector<string>>> user_info;
typedef map <string,user_info> Online_users;

void print_help();
void set_status(string channel,string user,bool status);
bool get_status(string channel,string user);
vector<string> get_messages(string channel,string user);
void change_nick(string channel,string old_nick,string new_nick);
void clear_messages(string channel,string user);
void add_message(string channel,string user,string message);
int parse_arguments(int argc, const char *argv[], Arguments * arguments);
string receive_data(int sock);
void check_channels(string channels);
int check_new_login(string nick,Online_users online_users);
int parse_message(int sock,int syslog_sock,Arguments *args,sys_info *sys_inf);
void print_map();
int send_actual_date(string channel,int sock);
int send_data(int sock,string message);
void send_syslog(int syslog_socket,string message,sys_info *sys);
vector<string> create_keywords_vector(string keywords);
vector<string> split_message(string message);
int search_keywords(string message,Arguments *args);
string create_syslog_message(string user,string user_message);
string get_my_ip();



#endif
