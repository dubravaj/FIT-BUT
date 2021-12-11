#!/usr/bin/env python

import ca_lib as calib
import sys
import subprocess

def init_function1_demo():
    used_functions = []
    used_functions.append(calib.default_add_fun1)
    used_functions.append(calib.add_420_fun1)
    used_functions.append(calib.add_459_fun1)
    used_functions.append(calib.add_465_fun1)
    used_functions.append(calib.add_95_fun1)
    used_functions.append(calib.add_398_fun1)
    return used_functions

def init_function2_demo():
    used_functions = []
    used_functions.append(calib.default_add_fun2)
    used_functions.append(calib.add_420_fun2)
    used_functions.append(calib.add_459_fun2)
    used_functions.append(calib.add_465_fun2)
    used_functions.append(calib.mul_24_fun2)
    used_functions.append(calib.mul_317_fun2)
    return used_functions

def init_function3_demo():
    used_functions = []
    used_functions.append(calib.default_add_fun3)
    used_functions.append(calib.add_420_fun3)
    used_functions.append(calib.add_459_fun3)
    used_functions.append(calib.add_465_fun3)
    used_functions.append(calib.mul_24_fun3)
    used_functions.append(calib.mul_317_fun3)
    return used_functions

def init_function4_demo():
    used_functions = []
    used_functions.append(calib.default_add_fun4)
    used_functions.append(calib.add_420_fun4)
    used_functions.append(calib.add_459_fun4)
    used_functions.append(calib.add_465_fun4)
    used_functions.append(calib.add_95_fun4)
    used_functions.append(calib.add_398_fun4)
    used_functions.append(calib.mul_24_fun4)
    used_functions.append(calib.mul_317_fun4)
    used_functions.append(calib.mul_365_fun4)
    used_functions.append(calib.mul_101_fun4)
    return used_functions

def run_demo():
    outpath = sys.argv[1]
    f_param = sys.argv[2]
    if(f_param != "-f"):
        print("Expected -f parameter")
        sys.exit(1)
    used_function = sys.argv[3]
    states = [1]
    init_state = 10

    for i in states:
        command = "./BiCAS.py -f {} -s {} -i {} -d {}".format(used_function,i,init_state,outpath)
        p = subprocess.Popen(command, shell=True,stdout=subprocess.PIPE)
        out = p.communicate()


def main():
    run_demo()


if __name__ == "__main__":
    main()
