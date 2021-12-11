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
#include <ctime>
#include <err.h>
#include <errno.h>
#include <ifaddrs.h>
#include <stdexcept>
#include "bot_functions.h"
#include "bot_connection.h"
#define NICK "xdubra03"
#define DEFAULT_SYSLOG_IP "127.0.0.1"

Online_users online_users;

using namespace std;

/*
* Print help for user
*/
void print_help(){
    cerr << "----HELP IRC bot----" << endl;
    cerr << "IRC bot for tracking communication on IRC server. Bot has two functions:"<< endl;
    cerr << "?today - if message containing ?today is delivered, bot is sending actual date to server"<< endl;
    cerr << "?msg <nick>:<message> - if this message is delivered, bot sends message content to user with <nick> if is online,else stores message and sends it after user join channel again"<< endl;
    cerr << endl;
    cerr << "Usage of program: ./isabot HOST[:PORT] CHANNELS [-s SYSLOG_SERVER] [-l HIGHLIGHT] [-h|--help]" << endl;
    cerr << "HOST - specifies address of IRC server" << endl;
    cerr << "PORT - specifies IRC port number,optional value,default PORT number is 6667" << endl;
    cerr << "CHANNELS - string of channels starting with # or & delimited with comma" << endl;
    cerr << "-s SYSLOG_SERVER - optional parameter, value of syslog server's IP address,default value is localhost 127.0.0.1" << endl;
    cerr << "-l HIGHLIGHT - optional parameter, string of keywords to be searched in incoming messages" << endl;
    cerr << "messages which contain some of the keywords are send to syslog server" << endl;
    cerr << "-h|--help - print help" << endl;
    cerr << endl;
    cerr << "Example: ./isabot irc.freenode.net \"#nwa,#nwa2,#nwa3\" -l \"isa,bot\" -s \"127.0.0.1\""<< endl;

    exit(0);
}

/*
* Parse input argument and store them
* @param arg number of arguments
* @param argv arguments
* @param arguments arguments structure
* @return exception if arguments are invalid,else 0
*/
int parse_arguments(int argc,const char *argv[], Arguments * arguments){

    //check if user entered help option
    for(int i=1; i < argc; i++){
        if((strcmp(argv[i],"-h") == 0) || (strcmp(argv[i],"--help") == 0)){
            print_help();
        }
    }
    //check and store input arguments
    switch(argc){

    	case 3:
    	   arguments->host = argv[1];
    	   arguments->channels = argv[2];
           break;

    	case 5:
    	   arguments->host = argv[1];
    	   arguments->channels = argv[2];

    	   if(strcmp(argv[3],"-s") == 0){
    	       arguments->syslog_server = argv[4];
    	   }
    	   else if(strcmp(argv[3],"-l") == 0){
    		   arguments->keywords = argv[4];
               arguments->Flags.is_keyword = true;
    		}
    		else{
    			throw std::invalid_argument("Invalid arguments.");
    		}

    		break;


		case 7:
			arguments->host = argv[1];
    		arguments->channels = argv[2];

            if(strcmp(argv[3],"-s") == 0){
    			arguments->syslog_server = argv[4];
    		}
    		else if(strcmp(argv[3],"-l") == 0){
    			arguments->keywords = argv[4];
                arguments->Flags.is_keyword = true;
    		}
            else{
                throw std::invalid_argument("Invalid arguments.");
            }

            if(strcmp(argv[5],"-s") == 0){
    			arguments->syslog_server = argv[6];
    		}
    		else if(strcmp(argv[5],"-l") == 0){
    			arguments->keywords = argv[6];
                arguments->Flags.is_keyword = true;
            }
            else{
                throw std::invalid_argument("Invalid arguments.");
            }

    		break;

    	default:
            throw std::invalid_argument("Invalid number of arguments.");
    }

    //check for port option
    is_port(arguments);


	return 0;
}

/*
* Set status of irc user in the map
* @param channel actual channel
* @param user actual user
* @param status status value(true or false)
*/
void set_status(string channel,string user,bool status){
    online_users[channel][user].first = status;
}

/*
* Get status of irc user
* @param channel actual channel
* @param user actual user
*/
bool get_status(string channel,string user){
    return online_users[channel][user].first;
}
/*
* Get undelivered user messages
* @param channel actual channel
* @param user actual user
*/
vector<string> get_messages(string channel,string user){
    return online_users[channel][user].second;
}
/*
* Change nickname of user on the channel
* @param channel actual channel
* @param old_nick old nickname
* @param new_nick new nickname
*/
void change_nick(string channel,string old_nick,string new_nick){
    online_users[channel][new_nick] = online_users[channel][old_nick];
    online_users[channel].erase(old_nick);
}
/*
* Clear vector of undelivered messages after they were sent
* @param channel actual channel
* @param user actual user
*/
void clear_messages(string channel,string user){
    online_users[channel][user].second.clear();
}
/*
* Add message to the vector of undelivered messages
* @param channel actual channel
* @param user actual user
* @param message message to be added
*/
void add_message(string channel,string user,string message){
    online_users[channel][user].second.push_back(message);
}
/*
* Receive data from server
* @param sock socket for communication
* @return one IRC message or exception
*/
string receive_data(int sock){

    string data="";
    char buff[1024];
    int rec = 0;
    while(1){
        while(data.find("\r\n") == string::npos){
            if((rec = recv(sock,buff,1,0)) == -1){
               throw std::runtime_error("Receive failed.");
            }
            data+=string(buff,rec);
        }
        return data;
    }
    return data;
}

//print map with online users,only for testing purpose
void print_map(){
    for(Online_users::const_iterator ptr=online_users.begin();ptr!=online_users.end(); ptr++) {
        cout << "KEy1: " <<ptr->first << "\n";
        for( map<string,pair<bool,vector<string>>>::const_iterator eptr=ptr->second.begin();eptr!=ptr->second.end(); eptr++){
            cout << "Key2: "<<eptr->first << endl;
            pair<bool,vector<string>> it = eptr->second;
            cout << "IS online: " <<it.first << endl;
            for(size_t i=0; i < it.second.size(); i++){
                cout << "Value: " << it.second[i]<< endl;
            }
        }
    }
}

/*
* Send actual date to IRC server
* @param channel IRC channel where date will be send
* @param sock socket
* @return exception or 0
*/
int send_actual_date(string channel,int sock){
    time_t rawtime;
    struct tm *timeinfo;
    char buffer[40];

    //find time informations from computer,create string with date
    time(&rawtime);
    timeinfo = localtime(&rawtime);
    strftime(buffer,sizeof(buffer),"%d.%m.%Y",timeinfo);
    string date_string(buffer);

    //create and send date message
    string date_message = "PRIVMSG "+channel+" :"+date_string+"\r\n";
    try{
        send_data(sock,date_message);
    }
    catch(const std::runtime_error& e){
        throw std::runtime_error("Send failed.");
    }
    return 0;
}

/*
* Send data to IRC server
* @param sock socket
* @param message message to be send
* @return exception or 0
*/
int send_data(int sock,string message){
    size_t length = 0;
    size_t send_length = 0;
    size_t message_length = message.size();
    const char *data = message.c_str();

    while(send_length < message_length){

        length = send(sock,data,strlen(data),0);
        send_length += length;

        if(length < 0){
            throw std::runtime_error("Send failed.");
        }

        data += length;
    }
    return 0;
}

/*
* Create syslog message
* @param user string with username of message sender
* @param user_message content of message
* @return syslog message string
*/
string create_syslog_message(string user,string user_message){
   time_t rawtime;
   struct tm *timeinfo;
   char buffer[40];

   //find out current time
   time(&rawtime);
   timeinfo = localtime(&rawtime);
   strftime(buffer,sizeof(buffer),"%b %e %H:%M:%S",timeinfo);
   string syslog_date(buffer);
   string my_ip = get_my_ip();
   //create syslog message
   string syslog_message = "<134>"+syslog_date+" "+my_ip+" isabot"+" "+user+": "+user_message;

   return syslog_message;
}

/*
* Send syslog message to syslog server
* @param syslog_socket socket for communication with syslog server
* @param message syslog message
* @param sys syslog informations structure
*/
void send_syslog(int syslog_socket,string message,sys_info *sys){

    size_t length = 0;
    size_t send_length = 0;
    size_t message_length = message.size();
    const char *syslog_data = message.c_str();

    while(send_length < message_length){
        //send syslog message,if sendto failed,throw error
        length = sendto(syslog_socket,syslog_data,strlen(syslog_data),0,(struct sockaddr *) &sys->syslog_info,sys->sys_len);
        send_length += length;

        //according to RFC, we should not care whether there is running SYSLOG server on the other side or not
        if(length < 0){
            continue;
        }
        syslog_data += length;
    }

}

/*
* Obtain IP address for syslog message
* @return string with first IP address which isn't loopback
*/
string get_my_ip(){

    struct ifaddrs *ifaddr, *ifa;
    int result;
    char host[NI_MAXHOST];
    string my_ip;

    //find IP addresses on all interfaces
    if(getifaddrs(&ifaddr) == -1){
        throw std::runtime_error("Getifaddrs failed.");
    }

    //list of IP addresses and interfaces is obtained
    //iterate over all aof them and find first different than loopback
    for(ifa = ifaddr; ifa != NULL; ifa = ifa->ifa_next){
        if(ifa->ifa_addr == NULL)
            continue;

        //informations about interface
        result = getnameinfo(ifa->ifa_addr,sizeof(struct sockaddr_in),host, NI_MAXHOST, NULL, 0, NI_NUMERICHOST);

        //find out first non-loopback address
        if((strcmp(ifa->ifa_name,"lo")!=0)&&( ifa->ifa_addr->sa_family==AF_INET)){
            if(result != 0){
                throw std::runtime_error("Getnameinfo failed.");
            }
            //address found,store it
            stringstream host_string;
            host_string << host;
            my_ip = host_string.str();
            break;
        }
    }
    freeifaddrs(ifaddr);

    return my_ip;
}
/*
* Create vector of channel names and check their format
* @param channels string with channel names
* @retun exception if wrong format occured
*/
void check_channels(string channels){
    stringstream channels_vec(channels);
    string ch_name;
    vector<string> irc_channels;
    //split channel names into a vector, delimiter is comma
    while(getline(channels_vec,ch_name, ',')){
        irc_channels.push_back(ch_name);
    }
    //check prefix in channel name
    for(size_t i=0; i < irc_channels.size(); i++){
        string name = irc_channels[i];
        if((name[0] != '#') && (name[0] != '&')){
            throw std::invalid_argument("Wrong channel name format.");
        }
    }

}


/*
* Create vector of keywords to be searched in IRC messages
* @param keywords string of keywords
* @retun vector of keywords
*/
vector<string> create_keywords_vector(string keywords){

    stringstream kwords(keywords);
    string k_token;
    vector<string> key_words;
    //split words into a vector, delimiter is comma
    while(getline(kwords,k_token, ',')){
        key_words.push_back(k_token);
    }
    return key_words;
}

/*
* Split message into words
* @param message message to be split
* @return vector of words
*/
vector<string> split_message(string message){

    stringstream sentence(message);
    string word;
    vector<string> msg_words;
    //split message into single words stored in msg_words vector
    while(sentence >> word){
        msg_words.push_back(word);
    }
    return msg_words;
}

/*
* Transform nickname to lowercase
* @param nickname
* @retun lowercase nickname
*/
string to_lowercase(string nickname){

    //accoding to RFC transform characters {}|~ to its lowercase equivalents
    string lower_nick = nickname;
    for(size_t i=0; i < lower_nick.size(); i++){
        if(nickname[i] == '{'){
            lower_nick[i] = '[';
        }
        else if(nickname[i] == '}'){
            lower_nick[i] = ']';
        }
        else if(nickname[i] == '~'){
            lower_nick[i] = '^';
        }
        else if(nickname[i] == '\\'){
            lower_nick[i] = '|';
        }
        else{
            lower_nick[i] = tolower(nickname[i]);
        }
    }

    return lower_nick;

}



/*
* Seach for keywords in IRC message
* @param message IRC message
* @param args arguments structure
* @return 1 if keyword is found,else 0
*/
int search_keywords(string message,Arguments *args){


    vector<string> msg_words = split_message(message);
    vector<string> keywords_arr = create_keywords_vector(args->keywords);

    //compare input keywords and words in IRC message
    for(size_t i=0; i < keywords_arr.size(); i++){
        for(size_t j=0; j < msg_words.size(); j++){
            if(msg_words[j].compare(keywords_arr[i]) == 0){
                return 1;
            }
        }
    }
    return 0;
}

/*
* Function receiving and parsing IRC messages,
* @param sock irc socket
* @param syslog_sock syslog socket
* @param args arguments structure
* @param sys_inf syslog informations structure
*/

int parse_message(int sock,int syslog_sock,Arguments *args,sys_info *sys_inf){

    //receive IRC message
    string message = receive_data(sock);
    //parts of the message
    string user_part;
    string command;
    string message_part;

//-------message with prefix------------------
    //irc message starts with prefix
    if(message[0]==':'){

        //split irc message into user,command,message parts
        user_part = message.substr(0,message.find(" "));
        message = message.substr(message.find(" ")+1,string::npos);
        command = message.substr(0,message.find(" "));
        message = message.substr(message.find(" ")+1,string::npos);
        message_part = message;
        int err;
        stringstream err_string;
        err_string << command;
        err_string >> err;

        //check if server sent error message,throw exception if error occured
        if(err > 400){

            string error_msg = message_part.substr(message_part.find(":")+1,string::npos);
            throw std::invalid_argument("IRC server error " + error_msg);
        }
//--------message with active users----------------------
        //irc message with all active users on certain channel
        else if(command == "353"){

            string bot_name;
            string delim;
            string channel;
            stringstream ss(message_part);
            //split delivered message
            ss >> bot_name >> delim >> channel;

            string active_users;
            string name;
            size_t pos = 0;
            //get names of active users
            active_users = message_part.substr(message_part.find(":")+1,string::npos);
            active_users.erase(active_users.find("\r\n"),2);

            //add all active users to the online_users map
            do{
                pos = active_users.find(" ",0);
                name = active_users.substr(0,pos);

                //remove @ from channel operator name
                if(name.find("@") == 0){
                    name = name.substr(1,pos);
                }
                //convert username to lowercase because irc is case-insensitive
                string low_name = to_lowercase(name);
                set_status(channel,low_name,true);

                active_users = active_users.erase(0,pos+1);

            }while(pos != string::npos);


        //end of NAMES command
        }
//---------------END active_users---------------------------
//---------------JOIN message-------------------------------
        //new user joined a channel
        else if(command == "JOIN"){

            string joined_channel = message_part.erase(message_part.find("\r\n"),2);
            string joined_user = user_part.substr(1,user_part.find("!")-1);

            joined_user = to_lowercase(joined_user);
            //create item in the map for new user
            set_status(joined_channel,joined_user,true);
            //get vector of user messages
            vector<string> undelivered_messages = get_messages(joined_channel,joined_user);

            //if joined user has some undelivered messages,send them one by one to the channel
            if(!undelivered_messages.empty()){
                for(std::vector<string>::iterator it = undelivered_messages.begin(); it != undelivered_messages.end(); ++it){
                    string msg = "PRIVMSG "+joined_channel+" :"+(*it)+"\r\n";
                    send_data(sock,msg);
                }
                //clear messages from user's record
                clear_messages(joined_channel,joined_user);
            }
        }
//-------------END JOIN--------------------------------------
//-------------NICK message----------------------------------
        //user is changing nick on server
        else if(command == "NICK"){

            string joined_user = user_part.substr(1,user_part.find("!")-1);
            string new_nickname = message_part.substr(message_part.find(":")+1,string::npos);
            new_nickname.erase(new_nickname.find("\r\n"),2);
            joined_user = to_lowercase(joined_user);
            new_nickname = to_lowercase(new_nickname);

            //change previous nickname to new for all channels
            for(Online_users::const_iterator ptr=online_users.begin();ptr!=online_users.end(); ptr++) {
                change_nick(ptr->first,joined_user,new_nickname);
            }


        }
//--------------END NICK---------------------------------------
//--------------PART message------------------------------------
        //user is leaving channel using PART command,bot set this user as offline
        else if(command == "PART"){

            stringstream leave(message_part);
            string leave_message;
            string leaving_channel;

            string leaving_user = user_part.substr(1,user_part.find("!")-1);
            leave >> leaving_channel >> leave_message;
            leaving_user = to_lowercase(leaving_user);
            //set user status to offline
            set_status(leaving_channel,leaving_user,false);
        }
//--------------END PART-----------------------------------------
//--------------KICK message-------------------------------------
        //user was kicked from channel by channel operator
        else if(command == "KICK"){

            stringstream leave(message_part);
            string leaving_user;
            string leaving_channel;
            leave >> leaving_channel >> leaving_user;
            leaving_user = to_lowercase(leaving_user);
            //bot was kicked from channel,so he cannot do his functionality,error
            if(leaving_user == NICK){
                throw std::runtime_error("Bot kicked from the channel.");
            }
            //set user as offline
            set_status(leaving_channel,leaving_user,false);
        }
//------------END KICK----------------------------------------------
//-------------QUIT message-----------------------------------------
        //user leaving channel using QUIT command
        else if(command == "QUIT"){

            stringstream leave(message_part);
            string leave_message;
            string leaving_channel;

            string leaving_user = user_part.substr(1,user_part.find("!")-1);
            leaving_user = to_lowercase(leaving_user);
            //set user as offline in all channels
            for(Online_users::const_iterator ptr=online_users.begin();ptr!=online_users.end(); ptr++) {

                string leaving_channel = ptr->first;
                set_status(leaving_channel,leaving_user,false);
            }
       }
//----------------END QUIT------------------------------------------
//---------------PRIVMSG message------------------------------------
       //IRC message with content from user on a channel
       else if(command == "PRIVMSG"){

               stringstream user_message(message_part);
               string active_channel;
               string received_message;
               string joined_user = user_part.substr(1,user_part.find("!")-1);

               user_message >> active_channel;
               received_message = message_part.substr(message_part.find(":")+1,string::npos);

               //check if user wants to search for keywords in messages
               if(args->Flags.is_keyword){
                   //keyword found, send message to syslog server
                   if(search_keywords(received_message,args)){
                       string rc_msg = received_message;
                       rc_msg.erase(rc_msg.find("\r\n"),2);
                       string sys_msg = create_syslog_message(joined_user,rc_msg);
                       send_syslog(syslog_sock,sys_msg,sys_inf);
                   }
               }


               //if message contains ?today, bot sends actual date to the channel
               //?today is special function of bot
               if(received_message == "?today\r\n"){

                   send_actual_date(active_channel,sock);
               }
               //message contains ?msg command of bot,if is correct
               //bot sends message with username and content to channel,else
               //stores message in user's record
               else if(received_message.substr(0,5) == "?msg "){
                        if(received_message.at(6) != ' ' && received_message.substr(6,string::npos) != "\r\n"){

                             string name;
                             size_t pos;
                             //divide received message
                             string msg_message = received_message.erase(0,5);
                             pos = msg_message.find(":");
                             name = msg_message.substr(0,pos);
                             string l_name = to_lowercase(name);
                             string usr_message = msg_message.substr(0,msg_message.find("\r\n"));
                             bool is_online = get_status(active_channel,l_name);

                             //if user from message is online,immediately send message to the channel
                             if(is_online){
                                 if(received_message.substr(pos+1,string::npos) == "\r\n"){
                                     //empty message is not send
                                 }
                                 else{
                                     //send message to the channel
                                     string message = "PRIVMSG "+active_channel+" :"+usr_message+"\r\n";
                                     send_data(sock,message);

                                 }
                             }
                             //save mesage, user is offline
                             else{
                                 add_message(active_channel,l_name,usr_message);

                             }
                         }//end of msg
              }//end of msg option
         }
//---------------END PRIVMSG-------------------------
//---------------NOTICE message----------------------
         //NOTICE message from server
         else if(command == "NOTICE"){
             stringstream user_message(message_part);
             string active_channel;
             string received_message;
             user_message >> active_channel;

             string joined_user = user_part.substr(1,user_part.find("!")-1);
             received_message = message_part.substr(message_part.find(":")+1,string::npos);
             user_message >> active_channel;

             //do not control messages from server,bot also cannot respond to NOTICE messages
             if(active_channel != "*"){
                 //check if user wants to search for keywords
                 if(args->Flags.is_keyword){
                     if(search_keywords(received_message,args)){
                         received_message.erase(received_message.find("\r\n"),2);
                         string sys_msg = create_syslog_message(joined_user,received_message);
                         send_syslog(syslog_sock,sys_msg,sys_inf);
                     }
                 }
             }
         }
  }
  //PING message from server to find out we are still here,send PONG response
  else if(message.substr(0,4) == "PING"){
        send_pong(message,sock);
  }
  //error message from the server,throw exception
  else if(message.substr(0,5) == "ERROR"){
       throw std::invalid_argument("Server connection error");
  }

  return 0;
}
