#!/usr/bin/env python

import sys
from ctypes import *

fun = cdll.LoadLibrary('../_approxlib.so')

# adder add8_001
def add_1(num1, num2):
    fun.add8_001.argtypes = [c_int8, c_int8]
    fun.add8_001.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.add8_001(num1, num2)
    return res

# adder add8_007
def add_7(num1, num2):
    fun.add8_007.argtypes = [c_int8, c_int8]
    fun.add8_007.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.add8_007(num1, num2)
    return res

# adder add8_029
def add_29(num1, num2):
    fun.add8_029.argtypes = [c_int8, c_int8]
    fun.add8_029.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.add8_029(num1, num2)
    return res

# adder add8_056
def add_56(num1, num2):
    fun.add8_056.argtypes = [c_int8, c_int8]
    fun.add8_056.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.add8_056(num1, num2)
    return res

# adder add8_057
def add_57( num1, num2):
    fun.add8_057.argtypes = [c_uint8, c_uint8]
    fun.add8_057.restype = c_uint16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.add8_057(num1, num2)
    return res

# adder add8_462
def add_462( num1, num2):
    fun.add8_462.argtypes = [c_uint8, c_uint8]
    fun.add8_462.restype = c_uint16
    num1 = c_uint8(num1)
    num2 = c_uint8(num2)
    res = fun.add8_462(num1, num2)
    return res

# adder add8_468
def add_468( num1, num2):
    fun.add8_468.argtypes = [c_uint8, c_uint8]
    fun.add8_468.restype = c_uint16
    num1 = c_uint8(num1)
    num2 = c_uint8(num2)
    res = fun.add8_468(num1, num2)
    return res

# adder add8_420
def add_420(num1, num2):
    fun.add8_420.argtypes = [c_uint8, c_uint8]
    fun.add8_420.restype = c_uint16
    num1 = c_uint8(num1)
    num2 = c_uint8(num2)
    res = fun.add8_420(num1, num2)
    return res

# adder add8_334
def add_334(num1, num2):
    fun.add8_334.argtypes = [c_uint8, c_uint8]
    fun.add8_334.restype = c_uint16
    num1 = c_uint8(num1)
    num2 = c_uint8(num2)
    res = fun.add8_334(num1, num2)
    return res

# adder add8_212
def add_212(num1, num2):
    fun.add8_212.argtypes = [c_int8, c_int8]
    fun.add8_212.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.add8_212(num1, num2)
    return res

# adder add8_470
def add_470(num1, num2):
    fun.add8_470.argtypes = [c_int8, c_int8]
    fun.add8_470.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.add8_470(num1, num2)
    return res

# adder add8_466
def add_466(num1, num2):
    fun.add8_466.argtypes = [c_int8, c_int8]
    fun.add8_466.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.add8_466(num1, num2)
    return res

# adder add8_459
def add_459(num1, num2):
    fun.add8_459.argtypes = [c_int8, c_int8]
    fun.add8_459.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.add8_459(num1, num2)
    return res

# adder add8_459
def add_165(num1, num2):
    fun.add8_165.argtypes = [c_int8, c_int8]
    fun.add8_165.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.add8_165(num1, num2)
    return res

def add_398(num1, num2):
    fun.add8_398.argtypes = [c_int8, c_int8]
    fun.add8_398.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.add8_398(num1, num2)
    return res

def add_465(num1, num2):
    fun.add8_465.argtypes = [c_int8, c_int8]
    fun.add8_465.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.add8_465(num1, num2)
    return res

def add_95(num1, num2):
    fun.add8_095.argtypes = [c_int8, c_int8]
    fun.add8_095.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.add8_095(num1, num2)
    return res

# mul8_012
def mul_12(num1, num2):
    fun.mul8_012.argtypes = [c_uint8, c_uint8]
    fun.mul8_012.restype = c_uint16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.mul8_012(num1, num2)
    return res

def mul_21(num1, num2):
    fun.mul8_021.argtypes = [c_int8, c_int8]
    fun.mul8_021.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.mul8_021(num1, num2)
    return res

def mul_27(num1, num2):
    fun.mul8_027.argtypes = [c_int8, c_int8]
    fun.mul8_027.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.mul8_027(num1, num2)
    return res

def mul_41(num1, num2):
    fun.mul8_041.argtypes = [c_int8, c_int8]
    fun.mul8_041.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.mul8_041(num1, num2)
    return res

def mul_70(num1, num2):
    fun.mul8_070.argtypes = [c_int8, c_int8]
    fun.mul8_070.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.mul8_070(num1, num2)
    return res

def mul_494(num1, num2):
    fun.mul8_494.argtypes = [c_int8, c_int8]
    fun.mul8_494.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.mul8_494(num1, num2)
    return res

def mul_361(num1, num2):
    fun.mul8_361.argtypes = [c_int8, c_int8]
    fun.mul8_361.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.mul8_361(num1, num2)
    return res

def mul_185(num1, num2):
    fun.mul8_185.argtypes = [c_int8, c_int8]
    fun.mul8_185.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.mul8_185(num1, num2)
    return res

def mul_11(num1, num2):
    fun.mul8_011.argtypes = [c_int8, c_int8]
    fun.mul8_011.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.mul8_011(num1, num2)
    return res

def mul_122(num1, num2):
    fun.mul8_122.argtypes = [c_int8, c_int8]
    fun.mul8_122.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.mul8_122(num1, num2)
    return res

def mul_105(num1, num2):
    fun.mul8_105.argtypes = [c_int8, c_int8]
    fun.mul8_105.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.mul8_105(num1, num2)
    return res

def mul_89(num1, num2):
    fun.mul8_089.argtypes = [c_int8, c_int8]
    fun.mul8_089.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.mul8_089(num1, num2)
    return res

def mul_24(num1, num2):
    fun.mul8_024.argtypes = [c_int8, c_int8]
    fun.mul8_024.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.mul8_024(num1, num2)
    return res

def mul_498(num1, num2):
    fun.mul8_498.argtypes = [c_int8, c_int8]
    fun.mul8_498.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.mul8_498(num1, num2)
    return res

def mul_101(num1, num2):
    fun.mul8_101.argtypes = [c_int8, c_int8]
    fun.mul8_101.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.mul8_101(num1, num2)
    return res

def mul_365(num1, num2):
    fun.mul8_365.argtypes = [c_int8, c_int8]
    fun.mul8_365.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.mul8_365(num1, num2)
    return res

def mul_317(num1, num2):
    fun.mul8_317.argtypes = [c_int8, c_int8]
    fun.mul8_317.restype = c_int16
    num1 = c_int8(num1)
    num2 = c_int8(num2)
    res = fun.mul8_317(num1, num2)
    return res