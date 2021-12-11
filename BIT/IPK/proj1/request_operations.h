#ifndef	REQUEST_H
#define REQUEST_H
#include <string>
#include <iostream>
#include <sstream>
#include <map>
#define OK 200
#define NOT_FOUND 404
#define BAD_REQUEST 400


using namespace std;

//informations of request
typedef struct http_request{
	std::string command;
	std::string path;
	std::string type;
	std::string date;
	std::string accept;
	std::string accept_encoding;
	std::string content_type;
	int content_length;
	int content_position;
	int response_code;

}http_request;


typedef struct http_response{
	std::string code;
	int response_data_len;
}http_response;


typedef struct request_data{

	std::string server;
	std::string path;
	int port_number;

}request_data;

typedef struct command_data{

	std::string user_account;
	std::string remote_path;
	std::string type; //file or folder

}command_data;

void parse_request(request_data *request,const char* argv[]);


std::string trim(std::string str);

void replace_string(std::string &subject, const std::string &search, const std::string &replace);

int extract_request(command_data *url_data,http_request *req,std::string data,int socket);
int extract_response(http_request *req,std::string data);
void send_response(int socket,int ret_code,std::string message);


std::string create_request(std::string command,std::string path,std::string type,
	std::string date, std::string accept,std::string encoding,std::string content);

std::string put_post_request(std::string command,std::string path,std::string type,
	std::string date, std::string accept,std::string encoding, std::string contype,std::string conlen,std::string content);

std::string create_response(int code,std::string date,std::string contype,std::string conlength,std::string conencoding,std::string data);
std::string date();
void extract_response(http_request *response_data,char response[]);

int receive_data(int sock, char buffer[]);
std::string read_content(int sock,int conlength);

std::string replace_spaces(std::string text);

int make_dir(std::string dir_path);
int create_file(std::string filename);

bool dirEmpty(std::string path);
bool is_dir(std::string path);
bool is_file(std::string path);
std::string make_ls(std::string path);
bool exists(std::string file);
int change_dir(std::string path);
int rm_dir(std::string dir_path);
int rm_file(std::string path);
std::string read_file(std::string path);
std::string get_mime_type(std::string filename);
int move_file(std::string filename);

#endif