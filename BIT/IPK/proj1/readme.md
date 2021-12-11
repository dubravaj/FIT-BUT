# NAME

**ftrestd** - server application using RESTful API 

# SYNOPSIS

**ftrestd [-r ROOT-FOLDER] [-p PORT]**

# DESCRIPTION 

**ftrestd** - server application based on RESTful API. Application provides operations with files and 
directories. Server allows several operations to users. Users can create new directory in their home directory, removing emty directories and removing files, upload files from their local filesystem, download files to their local filesystem, get content of entered directory (simulating ls command).

# IMPLEMENTATION

Server application is implemented as iterative server so it can provide operation only for one client at time. 
All functions providing certain operation are implemented in request_operations library.
At first, server receive request from user application with HTTP headers and parse them using parse_request function. According to command and type parameter from user request send from client application, which could be file or folder request is provided certain operation on server. Informations from request are saved in http_request structure. Important information in structure is content-length. If content-length is bigger than 0, server continues with receiving data from user through string buffer with size of 1024 bytes,data from buffer are added to new string. If provided operation works with files, file content is stored in new string. Each operation provides controls if remote path object exist, if it is a file/folder, if alredy exists. Control if user account exists is also provided. User cannot make and delete user folder. After controls are done, content is written to file described in remote path. If operation does not use content-length information, only previously mentioned controls are done before operation and if operation was successfully done, server sends http response with code of operation or data could be also sent for lst and get operations. If some of the controls fails or operation wasn't done successfully, server also sends http response with exit code of operation and also description of problem. Response is created and send using send_response function.

# OPTIONS

**-r**
:   Option specifies root directory where users data will be saved. Default value of root directory
is actual directory.

**-p**
:   Option specifies port number used for connection. Default value for port number is 6677.

# ERRORS

If incorrect arguments for server were entered, application returns EXIT_FAILURE. The same is for errors caused by connection problems. If operation failed, http response is send to client application with appropriate exit code and error message describing cause of failure.

# EXAMPLES

**ftrestd** 
:   set up server, root directory is actual and port is with default value 6677

**ftrestd -p 7788** 
:   port number is now set to 7788, root directory is actual

**ftrestd -r /data/users** 
:   root directory where server searches for users accounts is /data/users directory, port has default value

**ftrestd - r /data/users -p 7788** 
:   root directory is /data/users and actual port number is 7788

# AUTHOR

Juraj Ondrej Dúbrava (xdubra03) 


# NAME

**ftrest** - client application providing communication with server.

# SYNOPSIS

**ftrest COMMAND REMOTE-PATH [LOCAL-PATH]**

# DESCRIPTION 

**ftrest** - client application providing communication with server. User can use client application for
uploading/downloading files from remote server,delete remote files/directories,get content of remote directory and make new directories in users remote directory. Client application uses RESTful API for creating requests for server where mentioned operations are performed by server application.

# IMPLEMENTATION

ftrest is client application which provides several operations for users. Users can use this application for uploading files, creating new directories, delete directories and files, download files to entered local path in their filesystem and print content of remote directory on the server. 
Client application firstly parse user input using parse_response function. Input contains name of the remote server, port number and path in remote filesystem. These informations are stored in request_data structure. Application expects http protocol to be used in input. If no port was entered, default value is 6677. After the input was parsed, according to entered operation from user is created new request for server application using RESTful API. If operation works with files, values for content-type (mime-type) of file is found and content-length are set in request. If user upload file, request also contains binary data in string which is the body of request. After request is send, server application provides operation from user and send response with exit code and data for get and lst operations. Client application is waiting for response from server, when it arrives, it is parsed. Application checks the exit code in http response, if code means error, application reads data from http response body containing error message which is printed on stderr and application end with return code 1. If connection or error with arguments occurs, application prints error message Unknown error and returns EXIT_FAILURE. If no error was received from the server, application ends with return code 0. For get command was entered, we need to read binary data from response and save them to new file. If no local path was entered, application save file in current directory. If local path is directory, file is saved there, if local path is file, file is saved as file with this name. If file already exists, then is overwritten. For lst command application reads data from response and print content for user.

# OPTIONS

:   client application has 3 options - command, remote path, local path. Command and remote path are always required.


## COMMAND

**del**
:   Delete file specified in REMOTE PATH on server.

**get**
:   Download file from REMOTE PATH to actual directory or to place entered in LOCAL PATH.

**put**
:   Upload file specified in LOCAL PATH to directory specified in REMOTE PATH.

**lst**
:   Print content of remote directory.

**mkd**
:   Create new directory specified in REMOTE PATH on the server.

**rmd**
:   Remove directory specified in REMOTE PATH from the server.
 
## REMOTE PATH

:   Remote path contains informations about server name, port number and path on remote server.
Application supports only http protocol, default value for port is 6677 if port wasn't entered.
Remote path has following format:

:   **[http://] server [port number : ] /path**

## LOCAL PATH

:   Local path specifies place in local filesystem, required for put command.

# ERRORS

:   If error occured on the server side, server responds with error message and exit code.
Error messages from the server are following:

**Not a directory.** 
:   REMOTE-PATH points to a file but operations lst or rmd are used.

**Directory not found.**
:   REMOTE-PATH points to existing object when lst or rmd is used.

**Directory not empty.**
:   REMOTE-PATH points to empty directory when rmd operation is used.

**Not a file.**
:   REMOTE-PATH points to a directory when operations del or get are used.

**File not found.**
:   REMOTE-PATH points to nonexisting object when operations del or get are used.

**User Acount Not Found.**
:   Operation is performed on nonexisting user.

**Unknown error.**
:   File opening failed, removing file failed. Also used when wrong arguments were entered.


# EXAMPLES 

**ftrest mkd http://localhost:12345/tonda/foo/bar**
:   new directory *bar* is created in *foo* directory   
  
**ftrest put http://localhost:12345/tonda/foo/bar/doc.pdf ~/doc.pdf**
:   file *doc.pdf* from local filesystem is uploaded on server to *bar* directory 

**ftrest get http://localhost:12345/tonda/foo/bar/doc.pdf**
:   file *doc.pdf* is downloaded from server to actual directory

**ftrest del http://localhost:12345/tonda/foo/bar/doc.pdf**
:   delete file *doc.pdf* from remote directory *bar*  

**ftrest rmd http://localhost:12345/tonda/foo/bar**
:   delete remote directory *bar*


# AUTHOR

Juraj Ondrej Dúbrava (xdubra03) 





