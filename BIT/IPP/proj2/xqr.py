#!/usr/bin/python3.6
import sys
import argparse
import os
import xml.dom.minidom as minidom

#override behaviour of error method
class ArgumentParser(argparse.ArgumentParser):
    def error(self, message):
        sys.stderr.write('Error: %s\n' % message)
        sys.exit(1)

########################### arguments ##########################
if(sys.argv[1] == '--help') and (len(sys.argv) == 2):
	sys.stdout.write("This is help for XML query script\n")
	sys.stdout.write("Script evaluates entered query, which is similar to SQL query, in input XML file.\n")
	sys.stdout.write("--help print help of script usage and its options\n"  \
	"--input=filename input option specifies XML file to be processed with query\n" \
	"--output=filename output option specifies output file in XML format with content defined by entered query\n" \
	"--query='dotaz' query option specifies user query to be used\n" \
	"--qf=filename qf option specifies file with query if query option is not provided\n" \
	"--n if this option is entered, XML header is not generated on script output\n" \
	"--root=element specifies name of root pair element, if not entered,root pair element is not written\n")
	sys.exit(0)

arg_parser = ArgumentParser(add_help=False ,description="XML query script")
arg_parser.add_argument('--help',action='store_false',help="help for this script")
arg_parser.add_argument('--input',action='append',help='Input XML file to be processed.')
arg_parser.add_argument('--output',action='append',help='Output XML file with results from entered query.')
arg_parser.add_argument('--query',action='append',help='Query to be used on XML file.')
arg_parser.add_argument('--qf',action='append',help='File with query if not entered on command line.')
arg_parser.add_argument('-n',action='store_false',help='XML head is not generated.')
arg_parser.add_argument('--root',action='append',help='Name of pair root element.')

args = arg_parser.parse_args()

#cannot combine help with other options
if('--help' in sys.argv[1:] and len(sys.argv) > 2):
	arg_parser.error("Cannot combine help with other options.")

#input option, avoiding duplicates
if(args.input is not None):
	if(len(args.input) > 1):
		arg_parser.error('Multiple occurence of option.')
	else:
		input_file = str(args.input[0])
else:
	input_file =None

#output option, avoiding duplicates
if(args.output is not None):
	if(len(args.output) > 1):
		arg_parser.error('Multiple occurence of option.')
	else:
		output_file = str(args.output[0])
else:
	output_file=None

#query option, check value of option
if(args.query is not None):
	if(len(args.query) > 1):
		arg_parser.error('Multiple occurence of option.')
	if(args.query[0] == ''): 
		sys.stderr.write('Error: Empty query.\n')
		sys.exit(80)

#query file option
if(args.qf is not None):
	if(len(args.qf) > 1):
		arg_parser.error('Multiple occurence of option.')

#do or do not generate XML hader
generate_header=False
if(args.n is not None):
	generate_header=False
else:
	generate_header=True

#root aoption, root element is added according to add_root value
add_root = False
if(args.root is not None):
	if(len(args.root) > 1):
		arg_parser.error('Multiple occurence of option.')
	add_root = True
	root_elem = str(args.root[0])
else:
	add_root = False


infile = None

#try to open input file
if(input_file is not None):
	try:
		infile = open(input_file,'r',encoding='utf-8')
		pass
	except IOError:
		sys.stderr.write('Cannot open input file\n')
		sys.exit(2)
else:
	infile = sys.stdin

#try to open output file
if(output_file is not None):
	try:
		outfile = open(output_file,'w',encoding='utf-8')
		pass
	except IOError:
		sys.stderr.write('Cannot open output file\n')
		sys.exit(3)
else:
	outfile = sys.stdout

#Query and query file wasnt entered
if(not args.query and not args.qf):
	sys.stderr.write("Query options not found.")
	sys.exit(80)

#Cannot combine query from file and from command line
if(args.query and args.qf):
	arg_parser.error("Cannot combine query file and query.")

#query is entered from command line as query option value
if(args.query and not args.qf):
	sel_query = str(args.query[0])

#query is in query file, we need to open it and read query from file
elif(args.qf and not args.query):
	if('' in (args.qf)):
		sys.stderr.write('No query file specified.\n')
		sys.exit(80)
	else:
		qf_file=(args.qf[0])
	#try to open query file
	try:
		with open(qf_file,'r',encoding='utf-8') as qfile:
			sel_query = qfile.read()
			if(not sel_query):
				sys.stderr.write('Error: Empty query in query file.\n')
				sys.exit(80)
	except IOError:
		sys.stderr.write('Error while openig query file.\n')
		sys.exit(80) 

#################### lexical analysis #######################


#token representation
class Token(object):
	def __init__(self,type,value):
		self.type = type
		self.value=value

#keyword array,used in finite state machine
keywords = ['SELECT','WHERE','FROM','NOT','CONTAINS','LIMIT','ROOT']
#append @ to end of query, represents pseudo EOF
sel_query += "@"
#position to query string
pos = 0

def getchar(self):
	return self[pos]

#FSM to get token from query
def get_next_token(self):
	c = ""
	token_value=""
	state = 0
	global pos 
	while(True):
		c = getchar(self)
		pos +=1
		if(c == '@' and token_value == ''):
			token = Token('EOF','@')
			return token
		#state == 0 is SPACE
		if(state == 0):
			if(not c.isspace()):
				if(c.isalpha() or c == '_' or c == ':'):
					token_value += c
					state = 1 #state ==1 identifier
				elif(c.isdigit()):
					token_value += c
					state = 2 #state == 2 number(int)
				elif(c == '"'):
					token_value +=c
					state = 3 #state == 3 string literal
				elif(c == '+' or c == '-'):
					token_value +=c
					state = 4 #state ==4 start of number(int or double)
				elif(c == '.'):
					token_value += c
					state = 5 #state == 5 .attribute
				elif(c == '<'):
					token = Token('LESS',c)
					return token
				elif(c == '>'):
					token = Token('GRT',c)
					return token
				elif(c == '='):
					token = Token('EQUAL',c)
					return token
				else:
					pos -= 1
					token = Token('ERROR',None)
					sys.stderr.write('LEXICAL ERROR\n')
					sys.exit(80)
		elif(state == 1):
			if(c.isalnum() or c == '_' or c == '-' or c == ':'):
				token_value+=c #element
			elif(c == '.'):
				token_value+=c
				state = 6 # element.attribute
			else:
				if(token_value in keywords):
					pos -= 1
					token = Token('KEYWORD',token_value)
				else:
					pos -= 1
					token = Token('ELEMENT',token_value)
				return token
		elif(state == 2): #number
			if(c.isdigit()):
				token_value+=c
			else:
				pos -= 1
				token = Token('INT_NUM',token_value)
				return token
		elif(state == 3):
			if(c == '"'): #string literal
				token_value+=c
				token = Token('STRING',token_value)
				return token
			elif(c.isspace()):
				token_value+=c
			elif(ord(c) <= 31):
				pos -= 1
				token = Token('LEX_ERROR',None)	
				sys.stderr.write('LEXICAL ERROR\n')
				sys.exit(80)
			else:
				token_value += c
		elif(state == 4):
			if(c.isdigit()):
				state = 8
				token_value += c
			else:
				pos -= 1
				token = Token('LEX_ERROR',None)
				sys.stderr.write('LEXICAL ERROR\n')
				sys.exit(80)
		elif(state == 5):
			if(c.isalpha() or c == '_' or c == ':'):
				state = 11
				token_value += c #.attribute start
			else:
				pos -= 1
				token = Token('LEX_ERROR',None)
				sys.stderr.write('LEXICAL ERROR\n')
				sys.exit(80)
		elif(state == 6):
			if(c.isalpha() or c == '_' or c == ':'):
				token_value +=c
				state = 9	#opet attribute start, but element.attribute 
			else:
				token = Token('LEX_ERROR',None)
				sys.stderr.write('LEXICAL ERROR\n')
				sys.exit(80)
		elif(state == 8):		#+/-33 
			if(c.isdigit()):
				token_value += c
			else:
				pos -= 1
				token = Token('INT_NUM',token_value)
				return token
		elif(state == 9):
			if(c.isalnum() or c == '_' or c == '-' or c == ':'):
				token_value += c
			else:
				pos -= 1
				token = Token('ELEMATTR',token_value)
				return token
		
		elif(state == 11):
			if(c.isalnum() or c == '_' or c == '-' or c == ':'):
				token_value += c
			else:
				pos -= 1
				token = Token('ATTR',token_value)
				return token
		
		
############### parser ###################

token_t=""
condition_element=""

#dictionary representing items in SELECT query 
query_items = {
	"select_elem":"",
	"from_elem":"",
	"from_elem_type":"",
	"where":"", #check if where is enetered
	"not_cond":[], #add each NOT keyword to list to know number of NOT keywords
	"cond_elem":"",
	"cond_elem_type":"",
	"cond_op":"",
	"cond_literal":"",
	"cond_literal_type":"",
	"limit":"",	#check if limit is entered
	"limit_number":""
}

############# syntactic analysis ############

#limit rule
def limitn(self):
	global token_t
	if(token_t.value == 'LIMIT'):
		query_items["limit"] = token_t.value
		token_t = get_next_token(self)
		if(token_t.type == 'INT_NUM'):
			#check for negative limit number
			if(int(token_t.value) < 0):
				sys.stderr.write('ERROR: Negative number in LIMIT entered.\n')
				sys.exit(80)
			#add limit number to dictionary
			query_items["limit_number"] = token_t.value
			token_t = get_next_token(self)
			if(token_t.type == 'EOF'):
				return True
			else:
				return False
		else:
			return False
	elif(token_t.type == 'EOF'):
		return True
	else:
		return False

#order clause rule
def order_clause(self):
	global token_t
	if(token_t.value == 'LIMIT'):
		return limitn(self)
	elif(token_t.type == 'EOF'):
		return True
	else:
		return False

#from element rule
def from_elm(self):
	global token_t
	if(token_t.type == 'ELEMENT' or token_t.type == 'ATTR' or token_t.type == 'ELEMATTR'):
		if(elm_or_attr(self) == True):
			#add from element to dictionary
			query_items["from_elem"] = token_t.value
			query_items["from_elem_type"] = token_t.type
			token_t = get_next_token(self)
			return where_clause(self)
	elif(token_t.value == 'ROOT'):
		query_items["from_elem"] = "ROOT"
		token_t = get_next_token(self)
		return where_clause(self)
	elif(token_t.value == 'LIMIT' or token_t.value == 'WHERE' or token_t.type == 'EOF'):
		#output will be empty file or with header/root element according to entered options
		return True
	else:
		return False

#where clause rule
def where_clause(self):
	global token_t
	if(token_t.value == 'LIMIT' or token_t.type == 'EOF'):
		return order_clause(self)
	elif(token_t.value == 'WHERE'):
		#add where to dictionary
		query_items["where"] = "WHERE"
		token_t = get_next_token(self)
		return condition(self)
	else:
		return False

#condition rule
def condition(self):
	global token_t
	global condition_element
	if(token_t.value == 'NOT'):
		#add not to dictionary
		query_items["not_cond"].append("NOT")
		token_t = get_next_token(self)
		return condition(self)
	elif(token_t.type == 'ELEMENT' or token_t.type == 'ATTR' or token_t.type == 'ELEMATTR'):
		condition_element = token_t
		#add where element to dictionary
		query_items["cond_elem"] = token_t.value
		query_items["cond_elem_type"] = token_t.type
		token_t = get_next_token(self)
		if(rel_op(self) == True):
			if(literal(self) == True):
				token_t = get_next_token(self)
				return order_clause(self)
			else:
				return False
		else:
			return False
	else:
		return False

#literal rule
def literal(self):
	global token_t
	global condition_element
	if(token_t.type == 'STRING'):
		escape = (token_t.value).find('\\')
		if(escape != -1):
			if((token_t.value)[escape+1] == 'n' or (token_t.value)[escape+1] == 't' or (token_t.value)[escape+1] == 'r'):
				#control for escape sequence in string 
				sys.stderr.write('Error escape sequence\n')
				sys.exit(80) 
		else:
			#add literal from condition to dictionary
			query_items["cond_literal"] = token_t.value
			query_items["cond_literal_type"] = token_t.type
			return True
	elif(token_t.type == 'INT_NUM'):
		query_items["cond_literal"] = token_t.value
		query_items["cond_literal_type"] = token_t.type
		condition_element.type = token_t.type 
		return True
	else:
		return False

#operator rule
def rel_op(self):
	global token_t
	if(token_t.type == 'GRT' or token_t.type == 'LESS' or token_t.type == 'EQUAL'):
		query_items["cond_op"] = token_t.value
		token_t = get_next_token(self)
		return True
	elif(token_t.value == 'CONTAINS'):
		query_items["cond_op"] = token_t.value
		token_t = get_next_token(self)
		if(token_t.type == 'INT_NUM'):
			sys.stderr.write('ERROR: Cannot apply CONTAINS on integer.\n')
			sys.exit(80) 
		return True
	else:
		return False

#elem-or-attr rule
def elm_or_attr(self):
	global token_t
	if(token_t.type == 'ELEMENT' or token_t.type == 'ATTR' or token_t.type == 'ELEMATTR'):
		return True
	else:
		return False

#start rule for parser
def parse_query(self):
	global token_t
	token_t = get_next_token(self)
	if(token_t.value == 'SELECT'):
		token_t = get_next_token(self)
		if(token_t.type == 'ELEMENT'):
			query_items["select_elem"] = token_t.value
			token_t = get_next_token(self)
			if(token_t.value == 'FROM'):
				token_t = get_next_token(self)
				return from_elm(self)
			else:
				sys.stderr.write('Syntax Error\n')
				sys.exit(80)
		else:
			sys.stderr.write('Syntax Error\n')
			sys.exit(80)
	elif(token_t.type == 'EOF'):
		sys.stderr.write('Syntax Error\n')
		sys.exit(80)
	else:
		sys.stderr.write('Syntax Error\n')
		sys.exit(80)

#check for parser result
result = parse_query(sel_query)

#syntactit analysis passed, continue
if(result == True):
	pass
else:
	sys.stderr.write('Syntax or semantic error.\n')
	sys.exit(80)

############## parse XML document ###################

#load input XML file and parse it 
query_xml = minidom.parse(infile)
from_element=""
from_attribute=""
root = ""
select_element = query_items["select_elem"]
correct_elems=[]
source_item=""

######################### get root element for searching

#check if NOT is in query,evaluate NOT condition
def not_condition(value):
	not_len = len(query_items['not_cond'])
	if(not_len % 2 == 0):
		return value
	elif(not_len % 2 == 1):
		return (not value)

#evaluate condition from WHERE condition
def check_condition(cond_elem,cond_literal):
	#operator CONTAINS
	if(query_items['cond_op'] == "CONTAINS"):
		if(not isinstance(cond_literal,str)):
			sys.stderr.write('ERROR, must be string.\n')
			sys.exit(80)
		else:
			#cond_elem is string, we can search for substring
			if(isinstance(cond_elem,str)):
				cond_literal = cond_literal.replace("\"","")
				if(cond_elem.find(cond_literal)!= -1):
					if(query_items['not_cond']):
						ret = not_condition(True)
						return ret
					else:
						return True
				else:
					#substring not found
					if(query_items['not_cond']):
						ret = not_condition(False)
						return ret
					else:
						return False
			else:
				#cond_elem is not string, so bad type is false
				if(query_items['not_cond']):
					ret = not_condition(False)
					return ret 
				else:
					return False
	#LESS operation
	elif(query_items['cond_op'] == '<'):
		
		if(query_items['cond_literal_type'] == "INT_NUM"):
			cond_literal = int(cond_literal)
		
		#literal is string
		if(isinstance(cond_literal,str)):	
			cond_literal = cond_literal.replace("\"","")
			result = cond_elem < cond_literal
			if(query_items['not_cond']):
				res = not_condition(result)
				return res
			else:
				return result
		#literal is 
		elif(isinstance(cond_literal,int)):
			try:
				cond_literal = float(cond_literal)
				cond_elem = float(cond_elem)
			except:
				if(query_items['not_cond']):
					res = not_condition(False)
					return res
				else:
					return False
			result = cond_elem < cond_literal
			if(query_items['not_cond']):
				res = not_condition(result)
				return res
			else:
				return result
	#GRT operation
	elif(query_items['cond_op'] == '>'):
		
		if(query_items['cond_literal_type'] == "INT_NUM"):
			cond_literal = int(cond_literal)
		
		#literal is string
		if(isinstance(cond_literal,str)):
			cond_literal = cond_literal.replace("\"","")
			result =  cond_elem > cond_literal
			if(query_items['not_cond']):
				res = not_condition(result)
				return res
			else:
				return result
		#literal is int
		elif(isinstance(cond_literal,int)):
			try:
				cond_literal = float(cond_literal)
				cond_elem = float(cond_elem)
			except:
				if(query_items['not_cond']):
					res = not_condition(False)
					return res
				else:
					return False
			result = cond_elem > cond_literal
			if(query_items['not_cond']):
				res = not_condition(result)
				return res
			else:
				return result
	#EQUAL operator
	elif(query_items['cond_op'] == '='):
		
		if(query_items['cond_literal_type'] == "INT_NUM"):
			cond_literal = int(cond_literal)
		#literal is string
		if(isinstance(cond_literal,str)):
			cond_literal = cond_literal.replace("\"","")
			result = cond_elem == cond_literal
			if(query_items['not_cond']):
				res = not_condition(result)
				return res
			else:
				return result
		#literal is int
		elif(isinstance(cond_literal,int)):
			try:
				cond_literal = float(cond_literal)
				cond_elem = float(cond_elem)
			except:
				if(query_items['not_cond']):
					res = not_condition(False)
					return res
				else:
					return False
			result = cond_elem == cond_literal
			if(query_items['not_cond']):
				res = not_condition(result)
				return res
			else:
				return result

####################################### from ELEMENT is ROOOT ######################


#FROM element wasnt entered, output list of elements is empty
if(query_items["from_elem"] == ""):
	correct_elems = []

#FROM element is ROOT element of XML file
elif(query_items["from_elem"] == "ROOT"):
	from_element = 'ROOT'
	no_attribute = True
	
	#get root element of document
	root = query_xml.documentElement      
	if(query_items["where"] == "WHERE"):
		
		roots = root.getElementsByTagName(select_element)
		
		#process element
		if(query_items['cond_elem_type'] == "ELEMENT"):
			for elem in roots:

				#element is same as in select
				if(elem.tagName == query_items['cond_elem']):

					if(elem.nodeType == minidom.Node.ELEMENT_NODE):
						for txt_node in elem.childNodes:
							if txt_node.nodeType==minidom.Node.TEXT_NODE:
								res = check_condition(txt_node.wholeText,query_items['cond_literal'])
								if(res == True):
									#if condition is True, we can add element to output list
									correct_elems.append(elem)
									#element doesnt have text value inside but another element, error
							elif(txt_node.nodeType==minidom.Node.ELEMENT_NODE):
								sys.stderr.write('ERROR, no text value in element.\n')
								sys.exit(4)

				else:
					#if element from condition doesnt exist, nothing is added to output list
					check_elem = elem.getElementsByTagName(query_items['cond_elem'])
					if(check_elem.length == 0):
						if(query_items['not_cond']):
							res = not_condition(False)
						else:
							res = False
						if(res == True):
							correct_elems.append(elem)
					else:
						#check for SELECT elements in subelements
						for ch_elem in check_elem:
							if(ch_elem.nodeType == minidom.Node.ELEMENT_NODE):
								for txt_node in ch_elem.childNodes:
									if txt_node.nodeType==minidom.Node.TEXT_NODE:
										res = check_condition(txt_node.wholeText,query_items['cond_literal'])
										if(res == True):
										#if condition is True, we can add element to output list
											correct_elems.append(elem)
										#element doesnt have text value inside but another element, error
										
										elif(txt_node.nodeType==minidom.Node.ELEMENT_NODE):
											sys.stderr.write('ERROR, no text value in element.\n')
											sys.exit(4)
							break

		#element.attribute in condition
		elif(query_items['cond_elem_type'] == "ELEMATTR"):
			no_attribute = True
			cond_element,cond_attribute = query_items['cond_elem'].split(".")

			for elem in roots:

				#element is the same as in select, check if it has attribute
				if(elem.tagName == cond_element):
					if(elem.hasAttribute(cond_attribute)):
						attr_value = elem.getAttribute(cond_attribute)
						res = check_condition(attr_value,query_items['cond_literal'])
						if(res == True):
							correct_elems.append(elem)

				else:
					#if element is not the same, check for element and also for attribute
					check_elems = elem.getElementsByTagName(cond_element)
					if(check_elems.length == 0):
						if(query_items['not_cond']):
							res = not_condition(False)
						else:
							res = False
						if(res == True):
							correct_elems.append(elem)
					else:
						#try to find attribute in subelements
						for ch_elem in check_elems:
							
							if(ch_elem.hasAttribute(cond_attribute)):
								attr_value = ch_elem.getAttribute(cond_attribute)
								no_attribute = False
								res = check_condition(attr_value,query_items['cond_literal'])
								if(res == True):
									correct_elems.append(elem)
								break
						#if attribute doesnt exist,check if NOT is in query and evaluate condition
						if(no_attribute == True):
							if(query_items['not_cond']):
								res = not_condition(False)
								if(res == True):
									correct_elems.append(elem)
							else:
								correct_elems = []


		#attribute in condition
		elif(query_items['cond_elem_type'] == "ATTR"):
			attribute = query_items['cond_elem'][1:]

			for elem in roots:
				#check if select element has attribute
				if(elem.hasAttribute(attribute)):
					attr_value = elem.getAttribute(attribute)
					res = check_condition(attr_value,query_items['cond_literal'])
					if(res == True):
						correct_elems.append(elem)
				else:
					
					#get all subelements and check if element contains attribute
					attr_elems = elem.getElementsByTagName("*")
					for attr_elem in attr_elems:
						if(attr_elem.hasAttribute(attribute)):
							attr_value = attr_elem.getAttribute(attribute)
							no_attribute = False
							res = check_condition(attr_value,query_items['cond_literal'])
							if(res == True):
								correct_elems.append(elem)
							break

					if(no_attribute == True):
						if(query_items['not_cond']):
							res = not_condition(False)
							if(res == True):
								correct_elems.append(elem)
							else:
								correct_elems = []
	else:
		#no WHERE condition
		roots = root.getElementsByTagName(select_element)
		#add correct elements to array of output elements
		for item in roots:
			correct_elems.append(item)

############################### from ELEMENT is ELEMENT###################
elif(query_items["from_elem_type"] == "ELEMENT"):
	from_element = query_items['from_elem']
	no_attribute = True
	
	#get first occurence of element to be searched
	root = query_xml.getElementsByTagName(from_element)
	if(root.length > 0):
		root = root[0]
	else:
		root = None
	#if FROM element was found,continue
	if(root is not None):
		if(query_items["where"] == "WHERE"):
			
			#get all elements from SELECT, we need to check them according to condition
			roots = root.getElementsByTagName(select_element)
			#element in condition
			if(query_items['cond_elem_type'] == "ELEMENT"):
				for elem in roots:
					
					#element is same as in select
					if(elem.tagName == query_items['cond_elem']):
						
						if(elem.nodeType == minidom.Node.ELEMENT_NODE):
							for txt_node in elem.childNodes:
								if txt_node.nodeType==minidom.Node.TEXT_NODE:
										res = check_condition(txt_node.wholeText,query_items['cond_literal'])
										if(res == True):
											#if condition is True, we can add element to output array
											correct_elems.append(elem)
								#element doesnt have text value inside but another element, error
								elif(txt_node.nodeType==minidom.Node.ELEMENT_NODE):
									sys.stderr.write('ERROR, no text value in element.\n')
									sys.exit(4)

					else:
						#check in subelements
						check_elem = elem.getElementsByTagName(query_items['cond_elem'])
						if(check_elem.length == 0):
							if(query_items['not_cond']):
								res = not_condition(False)
							else:
								res = False
							if(res == True):
								correct_elems.append(elem)
						else:
							for ch_elem in check_elem:
								if(ch_elem.nodeType == minidom.Node.ELEMENT_NODE):
									for txt_node in ch_elem.childNodes:
										if txt_node.nodeType==minidom.Node.TEXT_NODE:
											res = check_condition(txt_node.wholeText,query_items['cond_literal'])
											if(res == True):
												#if condition is True, we can add element to output array
												correct_elems.append(elem)
										#element doesnt have text value inside but another element, error
										elif(txt_node.nodeType==minidom.Node.ELEMENT_NODE):
											sys.stderr.write('ERROR, no text value in element.\n')
											sys.exit(4)
								break

			#element.attribute in condition
			elif(query_items['cond_elem_type'] == "ELEMATTR"):
				cond_element,cond_attribute = query_items['cond_elem'].split(".")
				
				for elem in roots:
					#element same as in select
					if(elem.tagName == cond_element):
						#check if element has required attribute
						if(elem.hasAttribute(cond_attribute)):
							attr_value = elem.getAttribute(cond_attribute)
							res = check_condition(attr_value,query_items['cond_literal'])
							if(res == True):
								correct_elems.append(elem)

					else:
						#check for element in subelements
						check_elems = elem.getElementsByTagName(cond_element)
						if(check_elems.length == 0):
							if(query_items['not_cond']):
								res = not_condition(False)
							else:
								res = False
							if(res == True):
								correct_elems.append(elem)

						for ch_elem in check_elems:
							#check for entered attribute in subelements 
							if(ch_elem.hasAttribute(cond_attribute)):
								attr_value = ch_elem.getAttribute(cond_attribute)
								no_attribute = False
								res = check_condition(attr_value,query_items['cond_literal'])
								if(res == True):
									correct_elems.append(elem)
								break
						#attribute was not found 
						if(no_attribute == True):
							if(query_items['not_cond']):
								res = not_condition(False)
								if(res == True):
									correct_elems.append(elem)
							else:
								correct_elems = []

			# only attribute is in where condition			
			elif(query_items['cond_elem_type'] == "ATTR"):
				attribute = query_items['cond_elem'][1:]
				for elem in roots:
					#if element from select has attribute, add it to output array
					if(elem.hasAttribute(attribute)):
						attr_value = elem.getAttribute(attribute)
						res = check_condition(attr_value,query_items['cond_literal'])
						if(res == True):
							correct_elems.append(elem)
					else:
						#get all subelements and check if it contains attribute
						attr_elems = elem.getElementsByTagName("*")
						for attr_elem in attr_elems:
							#element has this attribute, we need to evaluate condition
							if(attr_elem.hasAttribute(attribute)):
								attr_value = attr_elem.getAttribute(attribute)
								res = check_condition(attr_value,query_items['cond_literal'])
								if(res == True):
									correct_elems.append(elem)
								break
		#NO WHERE CONDITION		
		else:
			#WHERE condition was not entered, este zistit ci je chyba ak select elem neexistuje
			roots = root.getElementsByTagName(select_element)
			for item in roots:
				correct_elems.append(item)
	else:
		correct_elems = []

############################from ELEMENT is ELEMATTR##################
elif(query_items["from_elem_type"] == "ELEMATTR"):
	from_element,from_attribute = query_items['from_elem'].split(".")
	no_attribute = True
	
	#find all elements with enetered name
	root = query_xml.getElementsByTagName(from_element)
	for item in root:
		if(item.hasAttribute(from_attribute)):
			source_item = item
			break
	
	if(source_item != ""):
		if(query_items["where"] == "WHERE"):
			
			#get all elements from SELECT, we need to check them according to condition
			roots = source_item.getElementsByTagName(select_element)
			
			#element in condition
			if(query_items['cond_elem_type'] == "ELEMENT"):
				for elem in roots:
					#element is same as in select
					if(elem.tagName == query_items['cond_elem']):
						
						#check if node type doesnt contain elements instead of text value
						if(elem.nodeType == minidom.Node.ELEMENT_NODE):
							for txt_node in elem.childNodes:
								if txt_node.nodeType==minidom.Node.TEXT_NODE:
										#evaluate condition
										res = check_condition(txt_node.wholeText,query_items['cond_literal'])
										if(res == True):
											#if condition is True, we can add element to output array
											correct_elems.append(elem)
								#element doesnt have text value inside but another element, error
								elif(txt_node.nodeType==minidom.Node.ELEMENT_NODE):
									sys.stderr.write('ERROR, no text value in element.\n')
									sys.exit(4)


					else:
						#check for element in subelements
						check_elem = elem.getElementsByTagName(query_items['cond_elem'])
						if(check_elem.length == 0):
							if(query_items['not_cond']):
								res = not_condition(False)
							else:
								res = False
							if(res == True):
								correct_elems.append(elem)
						else:
							for ch_elem in check_elem:
								if(ch_elem.nodeType == minidom.Node.ELEMENT_NODE):
									for txt_node in ch_elem.childNodes:
										if txt_node.nodeType==minidom.Node.TEXT_NODE:
											res = check_condition(txt_node.wholeText,query_items['cond_literal'])
											if(res == True):
												#if condition is True, we can add element to output array
												correct_elems.append(elem)
										#element doesnt have text value inside but another element, error
										elif(txt_node.nodeType==minidom.Node.ELEMENT_NODE):
											sys.stderr.write('ERROR, no text value in element.\n')
											sys.exit(4)
								break

			#element.attribute in condition
			elif(query_items['cond_elem_type'] == "ELEMATTR"):
				cond_element,cond_attribute = query_items['cond_elem'].split(".")
				
				for elem in roots:
					if(elem.tagName == cond_element):
						if(elem.hasAttribute(cond_attribute)):
							attr_value = elem.getAttribute(cond_attribute)
							res = check_condition(attr_value,query_items['cond_literal'])
							if(res == True):
								correct_elems.append(elem)

					else:
						#nothing found
						check_elems = elem.getElementsByTagName(cond_element)
						if(check_elems.length == 0):
							if(query_items['not_cond']):
								res = not_condition(False)
							else:
								res = False
							if(res == True):
								correct_elems.append(elem)
						else:
							#search for attribute
							for ch_elem in check_elems:
								if(ch_elem.hasAttribute(cond_attribute)):
									attr_value = ch_elem.getAttribute(cond_attribute)
									no_attribute = False
									res = check_condition(attr_value,query_items['cond_literal'])
									if(res == True):
										correct_elems.append(elem)
									break
							#attribute wasnt found
							if(no_attribute == True):
								if(query_items['not_cond']):
									res = not_condition(False)
									if(res == True):
										correct_elems.append(elem)
								else:
									correct_elems = []
			#attribute in condition			
			elif(query_items['cond_elem_type'] == "ATTR"):
				attribute = query_items['cond_elem'][1:]
				for elem in roots:
					if(elem.hasAttribute(attribute)):
						attr_value = elem.getAttribute(attribute)
						res = check_condition(attr_value,query_items['cond_literal'])
						if(res == True):
							correct_elems.append(elem)
					else:
						#get all subelements and check if it contains attribute
						attr_elems = elem.getElementsByTagName("*")
						for attr_elem in attr_elems:
							if(attr_elem.hasAttribute(attribute)):
								attr_value = attr_elem.getAttribute(attribute)
								res = check_condition(attr_value,query_items['cond_literal'])
								if(res == True):
									correct_elems.append(elem)
								break
		#WHERE CONDITION WASNT ENTERED
		else:
			roots = source_item.getElementsByTagName(select_element)
			for item in roots:
				correct_elems.append(item)
	else:
		correct_elems = []




#######################from ELEMENT is ATTR######################			
elif(query_items["from_elem_type"] == "ATTR"):
	from_attribute = query_items["from_elem"][1:]
	no_attribute = True
	
	#get all elements of XML document
	root = query_xml.getElementsByTagName("*")
	
	for item in root:
		if(item.hasAttribute(from_attribute)):
			#FROM element which contains attribute was found
			source_item = item
			break
	
	if(source_item != ""):
		if(query_items["where"] == "WHERE"):
			
			#get all elements from SELECT, we need to check them according to condition
			roots = source_item.getElementsByTagName(select_element)
		
			if(query_items['cond_elem_type'] == "ELEMENT"):
				
				for elem in roots:
					#element is same as in select
					if(elem.tagName == query_items['cond_elem']):
						
						#check if node type doesnt contain elements instead of text value
						if(elem.nodeType == minidom.Node.ELEMENT_NODE):
							for txt_node in elem.childNodes:
								if txt_node.nodeType==minidom.Node.TEXT_NODE:
										res = check_condition(txt_node.wholeText,query_items['cond_literal'])
										if(res == True):
											#if condition is True, we can add element to output array
											correct_elems.append(elem)
								#element doesnt have text value inside but another element, error
								elif(txt_node.nodeType==minidom.Node.ELEMENT_NODE):
									sys.stderr.write('ERROR, no text value in element.\n')
									sys.exit(4)


					else:
						#check for element in subelements
						check_elem = elem.getElementsByTagName(query_items['cond_elem'])
						if(check_elem.length == 0):
							if(query_items['not_cond']):
								res = not_condition(False)
							else:
								res = False
							if(res == True):
								correct_elems.append(elem)
						else:
							for ch_elem in check_elem:
								if(ch_elem.nodeType == minidom.Node.ELEMENT_NODE):
									for txt_node in ch_elem.childNodes:
										if txt_node.nodeType==minidom.Node.TEXT_NODE:
											res = check_condition(txt_node.wholeText,query_items['cond_literal'])
											if(res == True):
												#if condition is True, we can add element to output array
												correct_elems.append(elem)
										#element doesnt have text value inside but another element, error
										elif(txt_node.nodeType==minidom.Node.ELEMENT_NODE):
											sys.stderr.write('ERROR, no text value in element.\n')
											sys.exit(4)
								break

			#element.attribute in condition
			elif(query_items['cond_elem_type'] == "ELEMATTR"):
				cond_element,cond_attribute = query_items['cond_elem'].split(".")

				for elem in roots:
					if(elem.tagName == cond_element):
						if(elem.hasAttribute(cond_attribute)):
							attr_value = elem.getAttribute(cond_attribute)
							res = check_condition(attr_value,query_items['cond_literal'])
							if(res == True):
								correct_elems.append(elem)

					else:
						check_elems = elem.getElementsByTagName(cond_element)
						#nothing found
						if(check_elems.length == 0):
							if(query_items['not_cond']):
								res = not_condition(False)
							else:
								res = False
							if(res == True):
								correct_elems.append(elem)
						else:
							for ch_elem in check_elems:
								if(ch_elem.hasAttribute(cond_attribute)):
									attr_value = ch_elem.getAttribute(cond_attribute)
									no_attribute = False
									res = check_condition(attr_value,query_items['cond_literal'])
									if(res == True):
										correct_elems.append(elem)
									break
							if(no_attribute == True):
								if(query_items['not_cond']):
									res = not_condition(False)
									if(res == True):
										correct_elems.append(elem)
								else:
									correct_elems = []
						

			elif(query_items['cond_elem_type'] == "ATTR"):
				attribute = query_items['cond_elem'][1:]

				for elem in roots:
					if(elem.hasAttribute(attribute)):
						attr_value = elem.getAttribute(attribute)
						res = check_condition(attr_value,query_items['cond_literal'])
						if(res == True):
							correct_elems.append(elem)
					else:
						#get all subelements and check if it contains attribute
						attr_elems = elem.getElementsByTagName("*")
						for attr_elem in attr_elems:
							if(attr_elem.hasAttribute(attribute)):
								no_attribute = False
								attr_value = attr_elem.getAttribute(attribute)
								res = check_condition(attr_value,query_items['cond_literal'])
								if(res == True):
									correct_elems.append(elem)
								break
						if(no_attribute == True):
								if(query_items['not_cond']):
									res = not_condition(False)
									if(res == True):
										correct_elems.append(elem)
								else:
									correct_elems = []
		#no WHERE condition
		else:
			roots = source_item.getElementsByTagName(select_element)
			for item in roots:
				correct_elems.append(item)
	else:
		correct_elems = []



#write elements from correct_elements array to output file or stdout
#LIMIT option entered
if(query_items["limit"] == "LIMIT"):
	limit_num = int(query_items["limit_number"])
	#XML header is not generated
	if(args.n == False):
		#add root element
		if(add_root == True):
			outfile.write('<'+root_elem+'>'+'\n')
			for i in range(limit_num):
				outfile.write(correct_elems[i].toxml())
				outfile.write('\n')
			outfile.write('</'+root_elem+'>')
		else:
			for i in range(limit_num):
				outfile.write(correct_elems[i].toxml())
				outfile.write('\n') 
	#generate XML header
	elif(args.n == True):
		#add root element 
		if(add_root == True):
			outfile.write('<?xml version="1.0" encoding="UTF-8"?>\n')
			outfile.write('<'+root_elem+'>'+'\n')
			for i in range(limit_num):
				outfile.write(correct_elems[i].toxml())
				outfile.write('\n')
			outfile.write('</'+root_elem+'>')
		else:
			outfile.write('<?xml version="1.0" encoding="UTF-8"?>\n')
			for i in range(limit_num):
				outfile.write(correct_elems[i].toxml())
				outfile.write('\n')

else:
	#dont generate XML header
	if(args.n == False):
		#add root element 
		if(add_root == True):
			outfile.write('<'+root_elem+'>'+'\n')
			for item in correct_elems:
				outfile.write(item.toxml())
				outfile.write('\n')
			outfile.write('</'+root_elem+'>')
		else:
			for item in correct_elems:
				outfile.write(item.toxml())
				outfile.write('\n') 
	#generate XML header
	elif(args.n == True):
		#add XML header
		if(add_root == True):
			outfile.write('<?xml version="1.0" encoding="UTF-8"?>\n')
			outfile.write('<'+root_elem+'>'+'\n')
			for item in correct_elems:
				outfile.write(item.toxml())
				outfile.write('\n')
			outfile.write('</'+root_elem+'>')
		else:
			outfile.write('<?xml version="1.0" encoding="UTF-8"?>\n')
			for item in correct_elems:
				outfile.write(item.toxml())
				outfile.write('\n') 
