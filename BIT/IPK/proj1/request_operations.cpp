#include "request_operations.h"
#include <string>
#include <regex.h>
#include <ctime>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <sys/stat.h>
#include <fstream>
#include <dirent.h>
#include <string.h>
#include <stdlib.h>

using namespace std;

//simulating trim
std::string trim(std::string str){
	size_t first = str.find_first_not_of(' ');
	if(first == std::string::npos){
		return "";
	}
	size_t last = str.find_last_not_of(' ');
	return str.substr(first,(last-first+1));
}

//replaces string with another string 
void replace_string(std::string &subject, const std::string &search, const std::string &replace){
	size_t pos = 0;
	while((pos = subject.find(search,pos)) != std::string::npos){
		subject.replace(pos,search.length(),replace);
		pos += replace.length();
	}
}

//extracting informations from user request
int extract_request(command_data *url_data,http_request *req,std::string data,int socket){

//*************************************
std::string command;
std::string path;
std::string http;
std::string http_data = data;
std::istringstream http_stream(http_data);
std::string line;
std::getline(http_stream,line);
std::istringstream http_headers(line);
http_headers >> command >> path >> http;
if(path.find("%20")){
	replace_string(path,"%20"," ");
	replace_string(path," ","");
}

//*************************************
char rm_path[100];
char type[10];
//*******extract user-account,remote_path,type*********
sscanf(path.c_str(),"%[^?]?type=%s",rm_path,type);
req->type = type;

std::string user_path(rm_path);

if(user_path.at(0) == '/'){
	user_path = user_path.erase(0,1);
}

int pos;
std::string temp_path;

if((pos = user_path.find('/')) != std::string::npos){
		temp_path = user_path;
		temp_path = temp_path.substr(0,pos+1);
		url_data->user_account = temp_path;
		if(temp_path.length() == 0){
	
		}
}

//*****************************************************
//save command and path
req->command = command;
req->path = rm_path;
//**********************http headers check***********
//map with http headers
map<string , string> headers;
//position of \r\n\r\n, then we can find data
int data_start;
string key,value;
//
	//get http headers from client request
	while(std::getline(http_stream,line)){
		size_t pos;
		if((pos = line.find(':')) != std::string::npos){
			key = line.substr(0,pos);
			key = trim(key);
			value = line.substr(pos+1,string::npos);
			value = trim(value);
			headers[key] = value;
		}
	}

	
	if(headers.count("Date")){
		req->date = headers["Date"];		
	}
	if(headers.count("Accept")){
		req->accept = headers["Accept"];
	}
	if(headers.count("Accept-Encoding")){
		req->accept_encoding = headers["Accept-Encoding"];
	}
	if(headers.count("Content-Type")){
		req->content_type = headers["Content-Type"];
	}
	if(headers.count("Content-Length")){
		req->content_length = stoi(headers["Content-Length"]);
	}

	
}

//extract data from response
int extract_response(http_request *req,std::string data){

//*************************************


std::string head;	
int code;	
std::string message;	
std::string http_data = data;
std::istringstream http_stream(http_data);
std::string line;
std::getline(http_stream,line);
std::istringstream http_headers(line);
http_headers >> head >> code >> message;
//*************************************
req->response_code = code;


//**********************http headers check***********
//map with http headers
map<string , string> headers;
string key,value;

	//get http headers from client request
	while(std::getline(http_stream,line)){
		size_t pos;
		if((pos = line.find(':')) != std::string::npos){
			key = line.substr(0,pos);
			key = trim(key);
			value = line.substr(pos+1,string::npos);
			value = trim(value);
			headers[key] = value;
			
		}
	}


	if(headers.count("Date")){
		req->date = headers["Date"];		
	}
	if(headers.count("Accept")){
		req->accept = headers["Accept"];
	}
	if(headers.count("Accept-Encoding")){
		req->accept_encoding = headers["Accept-Encoding"];
	}
	if(headers.count("Content-Type")){
		req->content_type = headers["Content-Type"];
	}
	if(headers.count("Content-Length")){
		req->content_length = stoi(headers["Content-Length"]);
	}

	
}


//save inforamtion from user input for creating new request
void parse_request(request_data *request,const char* argv[]){


std::string server;
std::string remote_path;
int port_number;
int end_pos = strlen("http://");
std::string s = argv[2];
std::string buffer;
int pos;
bool no_port = false;
	if((pos = s.find("http://")) != std::string::npos){
			s.erase(pos,end_pos);
		
	}
	else{
		exit(1);
	}

	if((pos = s.find(':')) != std::string::npos){
		server = s.substr(0,pos);
		request->server = server;
		s.erase(0,pos+1);
		no_port = false;
	}
	else{
		if((pos = s.find('/')) != std::string::npos){
		server = s.substr(0,pos);
		request->server = server;
		s.erase(0,pos+1);
		no_port = true;
		}
	}
	int num_pos = 0;
	if(!no_port){
		while(isdigit(s[num_pos])){
			buffer += s[num_pos];
			num_pos++;
		}
		s.erase(0,num_pos);
		port_number = stoi(buffer);
		request->port_number = port_number;
	}
	else{
		port_number = 6677;
		request->port_number = port_number;
	}
	if(s.length() > 0){
		remote_path =s;
		replace_string(remote_path," ","%20");
		request->path = remote_path;
	}
	

}
//client request fot the server
std::string create_request(std::string command,std::string path,std::string type,
	std::string date, std::string accept,std::string encoding,std::string content){

	
	std::string header = command+" "+path+"?type="+type+" "+"HTTP/1.1"+"\r\n";
	std::string http_headers = "Date:"+date+"\r\n"+
			"Accept:"+accept+"\r\n"+
			"Accept-Encoding:"+encoding+"\r\n"+
			"\r\n";
	std::string data = content;
	std::string client_request = header + http_headers + data;

	return client_request; 

}
//specific request for PUT command
std::string put_post_request(std::string command,std::string path,std::string type,
	std::string date, std::string accept,std::string encoding, std::string contype,std::string conlen,std::string content){

	std::string header = command+" "+path+"?type="+type+" "+"HTTP/1.1"+"\r\n";
	std::string http_headers = "Date:"+date+"\r\n"+
			"Accept:"+accept+"\r\n"+
			"Accept-Encoding:"+encoding+"\r\n"+
			"Content-Type:"+contype+"\r\n"+
			"Content-Length:"+conlen+"\r\n"+
			"\r\n";
	std::string data = content;
	std::string client_request = header + http_headers + data;

	return client_request; 

}

//creates new response from server with exit code of operation and data for lst and get command
std::string create_response(int code,std::string date,std::string contype,std::string conlength,std::string conencoding,std::string data){
	std::string code_message;
	switch(code){
		case 200:
			code_message = "OK";
			break;
		case 404:
			code_message = "Not Found";
			break;
		case 400:
			code_message = "Bad Request";
			break;
	}
	std::string res_code = std::to_string(code);
	std::string header = string("HTTP/1.1")+" "+res_code+" "+code_message+"\r\n";
	std::string http_headers = "Date:"+date+"\r\n"+
								"Content-Type:"+contype+"\r\n"+
								"Content-Length:"+conlength+"\r\n"+
								"Content-Encoding:"+conencoding+"\r\n"+"\r\n";
	std::string response_data = data;								
	std::string response = header + http_headers + response_data;

	return response;
}

//use for timestamp of client when request is made
std::string date(){

	time_t rawtime;
	struct tm *timeinfo;
	char buffer[40];

	time(&rawtime);
	timeinfo = localtime(&rawtime);
	strftime(buffer,sizeof(buffer),"%a, %d %b %Y %I:%M:%S %Z",timeinfo);
	std::string date_string(buffer);

	return date_string;
}

//receive data from socket
int receive_data(int sock, char buffer[]){
	int res = 0;
	res = recv(sock,buffer,1,0);
	return res;
}

//reading data from socket
std::string read_content(int sock,int conlength){
	int res = 0;
	std::string content = "";
	char buffer[1024];
	int length = conlength;

	while(1){
		res = recv(sock,buffer,1024,0);
		content += std::string(buffer,res);
		memset(buffer,0,1024);
		length = length - res;
		if(length <= 0){
			break;
		}
	}

	return content;
}

//makes new directory
int make_dir(std::string dir_path){
	int status;
	struct stat st = {0};
	if(dir_path.at(0) == '/'){
		dir_path = dir_path.erase(0,1);
	}
	std::string temp_path;
	int pos;
	if((pos = dir_path.find('/')) != std::string::npos){
		temp_path = dir_path;
		temp_path = temp_path.erase(0,pos+1);
		if(temp_path.length() == 0){
			return 1;
		}

	}
	else{
		return 1;
	}

	if(stat(dir_path.c_str(),&st) == -1){
		status = mkdir(dir_path.c_str(),S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH);
	}
	else{
		return 2;
	}	
}

//removes directory
int rm_dir(std::string dir_path){
	int status;
	struct stat st = {0};
	if(dir_path.at(0) == '/'){
		dir_path = dir_path.erase(0,1);
	}
	std::string temp_path;
	int pos;
	if((pos = dir_path.find('/')) != std::string::npos){
		temp_path = dir_path;
		temp_path = temp_path.erase(0,pos+1);
		if(temp_path.length() == 0){
			return 2;
		}

	}
	else{
		return 2;
	}

	if(rmdir(dir_path.c_str()) == -1){
		return 1;
	}
	return 0;	
}

//removes file
int rm_file(std::string path){
	int status;
	struct stat st = {0};
	if(path.at(0) == '/'){
		path = path.erase(0,1);
	}
	if(remove(path.c_str()) != 0){
		return 1;
		
	}
	return 0;
}


int create_file(std::string filename){



	if(filename.at(0) == '/'){
		filename = filename.erase(0,1);
	}
	std::fstream file(filename.c_str(),fstream::out);
}

//check if entered object exists
bool exists (std::string file){
	struct stat buf;
	if(file.at(0) == '/'){
		file = file.erase(0,1);
	}
	return (stat(file.c_str(),&buf) == 0);
}

//check if entered path is directory
bool is_dir(std::string path){
	struct stat sb;
	if(path.at(0) == '/'){
		path = path.erase(0,1);
	}
	if(stat(path.c_str(),&sb) == 0 and S_ISDIR(sb.st_mode)){
		return true;
	}
	else{
		return false;
	}
}

//check if entered path is file
bool is_file(std::string path){
	struct stat sb;
	if(path.at(0) == '/'){
		path = path.erase(0,1);
	}
	if(stat(path.c_str(),&sb) == 0 and  S_ISREG(sb.st_mode)){
		return true;
	}
	else{
		return false;
	}
}

size_t getFilesize(std::string path){
	struct stat sb;
	if(path.at(0) == '/'){
		path = path.erase(0,1);
	}
	if(stat(path.c_str(),&sb) != 0){
		return -1;
	}
	 return sb.st_size;
}

//if dir contains only . and .. then is empty and n is 2
bool dirEmpty(std::string path){
	int n = 0;
	dirent *d;
	DIR *dir = opendir(path.c_str());
		if(dir == NULL){
			return 0;
		}
		while((d=readdir(dir)) != NULL){
			n++;
		}
		closedir(dir);

		if(n > 2){
			return false;
		}
		else{
			return true;
		}
}	

//simulating ls command
std::string make_ls(std::string path){

		FILE *in;
	    char buff[512];
	 	std::string ls_data;
	 	char command[30];
	 	sprintf(command,"ls  %s",path.c_str());
	    if(!(in = popen(command, "r"))){
	        cerr << "ERROR" << endl;
	    }
	 	//reads ls data to string 
	    while(fgets(buff, sizeof(buff), in)!=NULL){
	        std::string buff_data(buff);
	        ls_data += buff_data;
	    }
	    cout << ls_data << endl;
	    pclose(in);
	    //return data from ls command
	    return ls_data;
}

//gets mime type of entered object
std::string get_mime_type(std::string filename){
	
		FILE *in;
	
	   
	 	if(filename.at(0) == '/'){
			filename = filename.erase(0,1);
		}
		char buff[100];
		char command[30];
		sprintf(command,"file -b --mime-type %s",filename.c_str());
	    if(!(in = popen(command,"r"))){
	        cerr << "Unknown error." << endl;
	    }
	    while(fgets(buff, sizeof(buff), in)!=NULL){
	        size_t len = strlen(buff);
	        if(len > 0 && buff[len-1] == '\n'){
	        	buff[len-1] = '\0';
	        }
	    }
	    pclose(in);
	    std::string mime_type(buff);

	    return mime_type;

}


int change_dir(std::string path){
	
	if(path.at(0) == '/'){
		path = path.erase(0,1);
	}
	if(chdir(path.c_str()) == -1){
    	return -1;
    }

}

std::string read_file(std::string path){
	std::stringstream data;
    std::ifstream file(path.c_str(),std::ios::binary);
    data << file.rdbuf();     
    std::string ss = data.str();
    //cout << ss << endl;
    file.close();
}


//function for sending response after fail or success, contains return code of operation
void send_response(int socket,int ret_code,std::string message){

	int send_byte;
	std::string ret_message = create_response(ret_code,date(),"",to_string(message.size()),"identity",message.data());
	int data_size = ret_message.size();
    while(1){
        send_byte = send(socket,ret_message.data(),ret_message.size(),0);
        data_size -= send_byte;
        
        if(data_size <= 0 ){
            break;
        }

    }

}







