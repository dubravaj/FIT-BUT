<?php

//Author: Juraj Ondrej Dubrava
//PHP scrip for C header analysis

//options from command line
$shortopt = "";
$longopt = array(
	"help",
	"input:",
	"output:",
	"pretty-xml::",
	"no-inline",
	"max-par:",
	"no-duplicates",
	"remove-whitespace",
);

//control for input parameters
$options = getopt($shortopt,$longopt);
if(count($options) !== ($argc-1)) {
	fprintf(STDERR,"Invalid parameters entered.\n");
	exit(1);
}


//default values
$spaces = 0;
//default path for input
$path = "./";
//default output value 
$output = "php://stdout";

//check entered parameters
foreach (array_keys($options) as $key){

	switch($key){
	
		case "help":
			if($argc > 2){
				fwrite(STDERR,"HELP can not be used with other parameters.\n");
				exit(1);
			}
			else{
				fwrite(STDOUT,"HELP:\n");
				fwrite(STDOUT,"Script analizes C header files and extracts functions.\n");
				fwrite(STDOUT,"You can run this script with following options:\n");
				fwrite(STDOUT,"--input=fileordir fileordir is input file with C source code\n");
				fwrite(STDOUT,"If --input is not entered, default input is current directory.\n");
				fwrite(STDOUT,"--output=filename filename is output file in XML format or STDOUT if not entered\n");
				fwrite(STDOUT,"--pretty-xml=k k is number of spaces for indent, default value is 4, 0 if parameter not entered\n"); 
				fwrite(STDOUT,"--no-inline script does not include inline functions\n");
				fwrite(STDOUT,"--max-par=n functions with less or equal parameters are included\n");
				fwrite(STDOUT,"--no-duplicates only first occurance of functions with the same name are included\n");
				fwrite(STDOUT,"--remove-whitespace unneccessary whitespaces are removed from rettype and type attributes\n");
				exit(0);
			}
			break;
		case "input":
			$path = $options["input"];
			break;
		case "output":
			$output = $options["output"];
			break;
		case "pretty-xml":
			if($options["pretty-xml"] === false){
				$spaces = 4;
			}
			else{
				if(!is_numeric($options["pretty-xml"])){
					fwrite(STDERR,"Non numeric value for this parameter.\n");
					exit(1);
				}
				$spaces = $options["pretty-xml"];	
			}
			break;
		case "no-inline":
			break;
		case "max-par":
			if(!is_numeric($options["max-par"])){
					fwrite(STDERR,"Non numeric value for this parameter.\n");
					exit(1);
			}
			$params = $options["max-par"];
			break;
		case "no-duplicates":
			
			break;
		case "remove-whitespace":
			
			break;
	}
}

$file_content = array();

//open entered dir and find all header files, store them to array of files to be lately proccessed
function search_dir($dir){
	$files = array();
	
	$directory = new RecursiveDirectoryIterator($dir);
	$iterator = new RecursiveIteratorIterator($directory,RecursiveIteratorIterator::SELF_FIRST);
	foreach($iterator as $file){
		//check if we could read from subfolder
		if(is_dir($file)){
			if(!is_readable($file)){
				fprintf(STDERR, "Permission denied for this directory.\n");
				//var_dump($file);
				exit(2);
			}
		}
		//add new header file to array
		if(pathinfo($file,PATHINFO_EXTENSION) === "h"){

			$files[] = $file;

		}
	}
	return($files);
}

function open_file($file){
	$content = file_get_contents($file);
	if($content === false){
		fwrite(STDERR, "Cannot open the file.\n");
		exit(2);
	}
	return $content;
}

//clear all unnnecessary content from files
//comments, macros, strings and structs are removed to avoid matching declarations and definitions in them
function clear_text($file){
$types = "(include|define|ifdef|ifndef|endif)";
//delete \" bacause of string
$text = preg_replace("/[\\\]\"/uU","",$file);
//delete carriage return
$text = preg_replace("/\r/uU","",$text);
//delete multiline comments
$text = preg_replace("/\/\*([^*]|(\*+[^*\/]))*\*+\//u","",$text);
//delete single line comments with multiline support
$text = mul_comment($text);
//delete single line commnets - without multiline support
$text =  preg_replace("/(\/\/.*)/u","", $text);
//delete strings
$text = preg_replace("/\".*\"/uUs","",$text);
//delete single line macros, wit multiline support
$text = macro($text);
//delete single line macro 
$text = preg_replace("/#.*/u","", $text);
//remove typedefs/structs from text
$text = preg_replace("/(typedef\s+)?struct(?P<name>\s+[a-zA-Z_]*)?\s*{[\s\w\W]*}\s*(?P<name1>[a-zA-Z_]*)?\s*;/u","",$text);

	return $text;

}
//function for removing macros with multiline support
function macro($file){

$cleaned_file = $file;
$pos = 0;
$ml_macro= false;
$ml_finish = false;
$macro_buffer ="";
$char = substr($file,$pos,1);
while($char || $pos < strlen($file)){
	$macro_buffer .= $char; 
	if($char == "\n" || $char == end(str_split($file))){
		$is_macro = preg_match("/#.*[\\\]/u",$macro_buffer,$arr);
		if($is_macro){
			$cleaned_file = str_replace($arr[0],"",$cleaned_file);

			$ml_macro = true; 
			$ml_finish = true;
		}
		else{

			$ml_continue = preg_match("/.*[\\\]/u",$macro_buffer,$del_arr);
		
			if($ml_continue && $ml_macro){
				
				$cleaned_file = str_replace($del_arr[0],"",$cleaned_file);
				$ml_finish = true;
			}
			else{
				if($ml_finish){
					
					$cleaned_file = str_replace($macro_buffer,"",$cleaned_file);
					$ml_macro = false;
					$ml_finish = false;
				}
			}
		}
	
	$macro_buffer = "";	
	}


	$pos++;
	$char = substr($file,$pos,1);
  }
return $cleaned_file;
}

function mul_comment($file){
$new = $file;
$pos = 0;
$ml_comment= false;
$ml_finish = false;
$comment_buffer ="";
$char = substr($file,$pos,1);
while($char || $pos < strlen($file)){
	$comment_buffer .= $char; 
	if($char == "\n" || $char == end(str_split($file))){
		$is_comment = preg_match("/(\/\/.*)[\\\]/u",$comment_buffer,$arr);
		
		if($is_comment){
			
			$new = str_replace($comment_buffer,"",$new);
			$ml_finish = true;
			$ml_comment = true; 

		}
		else{
			$ml_continue = preg_match("/.*[\\\]/u",$comment_buffer,$del_arr);
		
			if($ml_continue && $ml_comment){
				
				$new = str_replace($comment_buffer,"",$new);
				
				$ml_finish = true;
			}
			else{
				if($ml_finish){
					$new = str_replace($comment_buffer,"",$new);
					$ml_comment= false;
					$ml_finish = false;
				}
			}
		}
	
	$comment_buffer = "";	
	}


	$pos++;
	$char = substr($file,$pos,1);
  }
return $new;
}

//regular expression for matching function like constructions
//function return type, id and params are stored in unique array
$param = array();
$function_match = "/(?:(?<type>(?:[a-zA-Z_][\w]*(?:\s|\*)+)+))(?:(?<id>[a-zA-Z_][\w]*\s*))\((?:(?<params>[^(\\)]*))\)\s*(?:;|{)/u";

//regular expression for matching right construction of function parameters, name + id are required
$param_match = "/(?:(?<type>(?:\s*[a-zA-Z_][\w]*(?:\s|\*)+)+))(?:(?<id>[a-zA-Z_][\w]*))\s*/u";


//check --input parameter
if(!array_key_exists("input",$options)){
	$filenames = search_dir($path);
	$dir = "./";
}
else{
	//entered file or dir does not exist
	if(file_exists($path) === false){
		fwrite(STDERR,"Non existing file or directory.");
		exit(2);
	}
	//we dont have permission to read from this folder
	elseif(is_dir($path)){
		if(!is_readable($path)){
			fwrite(STDERR,"Do not have access for this directory.\n");
			exit(2);
		}
		//store content of directory to filenames array
		$filenames = search_dir($path);
		$dir = $path;
		if(mb_substr($dir,-1) !== '/'){
			$dir = $path . "/";
		}
		
	}
	//we dont have permission to read this file
	elseif(is_file($path)){
		if(!is_readable($path)){
			fwrite(STDERR,"Do not have access for this file.\n");
			exit(2);
		}
		$filenames[] = $path;
		$dir="";
	}
}


//create XML writer for new file where function information will be stored
$array = [];
$unique_names = [];
if(file_exists($output) and !is_writeable($output)){
	fprintf(STDERR,"Error: No permission to write.\n");
	exit(3);
}

$xml_write = new XMLWriter();

$URI_res = @$xml_write->openURI($output);
if($URI_res === false){
	fprintf(STDERR,"Error: Output is a directory.\n");
	exit(3);
}
//if no pretty xml option was entered, we dont indent xml header and other element
if(!array_key_exists("pretty-xml",$options)){
	
	$xml_write->writeRaw("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	$xml_write->setIndent(false);
}
else{
	$xml_write->startDocument('1.0','UTF-8');
	$xml_write->setIndent(true);
}
//set indent according to indent parameter
$xml_write->setIndentString(str_repeat(" ",$spaces));
$xml_write->startElement("functions");
$xml_write->writeAttribute('dir',$dir);

//for every header file with permission,we can find function definitions and declarations
foreach($filenames as $file){

	if(!is_readable($file)){
		fprintf(STDOUT,"Do not have permission to read file.\n");
		exit(2);
	}

	$content = open_file($file);
	//clear unneccessary items from file
	$content_item = clear_text($content);

	//something was found,not empty file
	if(preg_match_all($function_match,$content_item,$array)){

		
		//array of matched functions is processed
		for($i=0; $i < count($array[0]); $i++){

			//if construction like if else(c == 9){ } is matched, we cannot take it as function, so we continue
			if(preg_match("/if|else|return/u",$array["type"][$i]) == 1 || preg_match("/if|else|return/u",$array["id"][$i]) == 1){
				continue;
			}

			//default value for varargs
			$var_args = "no";
			$par_type = [];
			
			//if no-inline option is set, inline functions are reduced
			$is_inline = preg_match("/\s*inline\s*/u",$array["type"][$i]);
			if(array_key_exists("no-inline",$options) and $is_inline){
				continue;
			}

			//function type and id
			$fun_type = trim($array["type"][$i]);
			$fun_type = preg_replace("/[\n\t\r]/u"," ",$fun_type);
			$fun_name = trim($array["id"][$i]);

			//if more functions have the same name, only first occurence is wtitten
			if(array_key_exists("no-duplicates",$options)){
				
				//check if name already exists in array of names
				if(!in_array($fun_name,$unique_names)){
					$unique_names[] = $fun_name;
				}
				//continue if function with same name is detected
				else{
					continue;
				}

			}
			//remove whitespaces option is set
			if(array_key_exists("remove-whitespace",$options)){
				
				$fun_type = preg_replace("/\s+/u"," ",$fun_type);
				$fun_type = preg_replace("/\s*\*+\s*/u","*",$fun_type);
			}

			//number of function parameters, we started from 0
			$par_count = 0;

			//parameters are stored in array, according to , we can split them and processed them
			$trim_par = trim($array["params"][$i]);
			$param = explode(",",$trim_par);
			
				//each param is processed 
				foreach($param as $par_item){
	
					//if varargs are detected,function has var args, we do not count var args as one of parameters
					if(preg_match("/\s*\.\.\.\s*/u",$par_item)){
						$var_args = "yes";
			
					}
					//check if function parameter is right
					preg_match($param_match,$par_item,$par_array);
					
					//check function parameter, must be rettype + name
					if(array_key_exists("type",$par_array) and array_key_exists("id",$par_array)){
						//reduce start and end spaces
						$type = trim($par_array["type"]);
						//reduce whitespaces in return type
						$type = preg_replace("/[\n\t\r]/u"," ",$type);

						//remove unnecessary whitespaces if remove-whitespaces option
						if(array_key_exists("remove-whitespace",$options)){
							$type = preg_replace("/\s+/u"," ",$type);
							$type = preg_replace("/\s*\*+\s*/u","*",$type);
						}
						//store param types to array,
						$par_type[] = $type;
						$par_count++;
					}
					//function has no params or param is void, so we dont increase par counter
					elseif(trim($par_item) === "" or trim($par_item) === "void"){
						
					}	
				}

			//max-par option, function with more than n parameters is reduced
			if(array_key_exists("max-par",$options) and $par_count > $params){
					continue;
			}
			//set file to be relative
			if($dir !== ""){
				$file = str_replace($dir,"",$file);
			}
			
			//write informations about function to xml format
			$xml_write->startElement("function");
			$xml_write->writeAttribute('file',$file);
			$xml_write->writeAttribute('name',$fun_name);
			$xml_write->writeAttribute('varargs',$var_args);
			$xml_write->writeAttribute('rettype',$fun_type);
			
			//if function has parameters, write param element
			if(count($par_type)> 0){
				$no_par = 1;
				for($m =0; $m < $par_count; $m++){
					$xml_write->startElement('param');
					$xml_write->writeAttribute('number',$no_par);
					$xml_write->writeAttribute('type',$par_type[$m]);
					$xml_write->endElement();
					$no_par++;
				}
				$xml_write->endElement();
			}
			else{
				$xml_write->endElement();
			}

		}//endfor
	}//endif
}	
	//end xml document
	$xml_write->endElement();
	$xml_write->endDocument();
	$xml_write->flush();
	unset($xml_write);




?>
