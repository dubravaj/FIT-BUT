import approxlib

def default_add_fun4(north, east, west, south, cent, row, col):
    tmp = north + cent
    tmp1 = tmp + east
    tmp2 = (north * east * west) % 128
    tmp3 = tmp1 + tmp2
    return (tmp3) % 16


def add_334_fun4(north, east, west, south, cent, row, col):
    tmp = approxlib.add_334(north, cent)
    tmp1 = approxlib.add_334(tmp, east)
    tmp2 = (north * east * west) % 128
    tmp3 = approxlib.add_334(tmp1, tmp2)
    return (tmp3) % 16


def add_420_fun4(north, east, west, south, cent, row, col):
    tmp = approxlib.add_420(north, cent)
    tmp1 = approxlib.add_420(tmp, east)
    tmp2 = (north * east * west) % 128
    tmp3 = approxlib.add_420(tmp1, tmp2)
    return (tmp3) % 16


def add_459_fun4(north, east, west, south, cent, row, col):
    tmp = approxlib.add_459(north, cent)
    tmp1 = approxlib.add_459(tmp, east)
    tmp2 = (north * east * west) % 128
    tmp3 = approxlib.add_459(tmp1, tmp2)
    return (tmp3) % 16


def add_7_fun4(north, east, west, south, cent, row, col):
    tmp = approxlib.add_7(north, cent)
    tmp1 = approxlib.add_7(tmp, east)
    tmp2 = (north * east * west) % 128
    tmp3 = approxlib.add_7(tmp1, tmp2)
    return (tmp3) % 16


def add_56_fun4(north, east, west, south, cent, row, col):
    tmp = approxlib.add_56(north, cent)
    tmp1 = approxlib.add_56(tmp, east)
    tmp2 = (north * east * west) % 128
    tmp3 = approxlib.add_56(tmp1, tmp2)
    return (tmp3) % 16


def add_398_fun4(north, east, west, south, cent, row, col):
    tmp = approxlib.add_398(north, cent)
    tmp1 = approxlib.add_398(tmp, east)
    tmp2 = (north * east * west) % 128
    tmp3 = approxlib.add_398(tmp1, tmp2)
    return (tmp3) % 16


def add_468_fun4(north, east, west, south, cent, row, col):
    tmp = approxlib.add_468(north, cent)
    tmp1 = approxlib.add_468(tmp, east)
    tmp2 = (north * east * west) % 128
    tmp3 = approxlib.add_468(tmp1, tmp2)
    return (tmp3) % 16


def add_465_fun4(north, east, west, south, cent, row, col):
    tmp = approxlib.add_465(north, cent)
    tmp1 = approxlib.add_465(tmp, east)
    tmp2 = (north * east * west) % 128
    tmp3 = approxlib.add_465(tmp1, tmp2)
    return (tmp3) % 16


def add_95_fun4(north, east, west, south, cent, row, col):
    tmp = approxlib.add_95(north, cent)
    tmp1 = approxlib.add_95(tmp, east)
    tmp2 = (north * east * west) % 128
    tmp3 = approxlib.add_95(tmp1, tmp2)
    return (tmp3) % 16


def default_add_fun3(north, east, west, south, cent, row, col):
    tmp = 2 * north % 128
    tmp1 = 3 * south % 128
    tmp2 = 4 * west % 128
    tmp3 = 5 * east % 128
    tmp4 = (tmp + tmp1) % 128
    tmp5 = (tmp2 + tmp3) % 128
    tmp6 = (tmp4 + tmp5) % 128
    tmp7 = (tmp6 + cent) % 16
    return tmp7


def add_7_fun3(north, east, west, south, cent, row, col):
    tmp = 2 * north % 128
    tmp1 = 3 * south % 128
    tmp2 = 4 * west % 128
    tmp3 = 5 * east % 128
    tmp4 = approxlib.add_7(tmp, tmp1) % 128
    tmp5 = approxlib.add_7(tmp2, tmp3) % 128
    tmp6 = approxlib.add_7(tmp4, tmp5) % 128
    tmp7 = approxlib.add_7(tmp6, cent) % 16
    return tmp7


def add_56_fun3(north, east, west, south, cent, row, col):
    tmp = 2 * north % 128
    tmp1 = 3 * south % 128
    tmp2 = 4 * west % 128
    tmp3 = 5 * east % 128
    tmp4 = approxlib.add_56(tmp, tmp1) % 128
    tmp5 = approxlib.add_56(tmp2, tmp3) % 128
    tmp6 = approxlib.add_56(tmp4, tmp5) % 128
    tmp7 = approxlib.add_56(tmp6, cent) % 16
    return tmp7


def add_398_fun3(north, east, west, south, cent, row, col):
    tmp = 2 * north % 128
    tmp1 = 3 * south % 128
    tmp2 = 4 * west % 128
    tmp3 = 5 * east % 128
    tmp4 = approxlib.add_398(tmp, tmp1) % 128
    tmp5 = approxlib.add_398(tmp2, tmp3) % 128
    tmp6 = approxlib.add_398(tmp4, tmp5) % 128
    tmp7 = approxlib.add_398(tmp6, cent) % 16
    return tmp7


def add_212_fun3(north, east, west, south, cent, row, col):
    tmp = 2 * north % 128
    tmp1 = 3 * south % 128
    tmp2 = 4 * west % 128
    tmp3 = 5 * east % 128
    tmp4 = approxlib.add_212(tmp, tmp1) % 128
    tmp5 = approxlib.add_212(tmp2, tmp3) % 128
    tmp6 = approxlib.add_212(tmp4, tmp5) % 128
    tmp7 = approxlib.add_212(tmp6, cent) % 16
    return tmp7


def add_334_fun3(north, east, west, south, cent, row, col):
    tmp = 2 * north % 128
    tmp1 = 3 * south % 128
    tmp2 = 4 * west % 128
    tmp3 = 5 * east % 128
    tmp4 = approxlib.add_334(tmp, tmp1) % 128
    tmp5 = approxlib.add_334(tmp2, tmp3) % 128
    tmp6 = approxlib.add_334(tmp4, tmp5) % 128
    tmp7 = approxlib.add_334(tmp6, cent) % 16
    return tmp7


def add_420_fun3(north, east, west, south, cent, row, col):
    tmp = 2 * north % 128
    tmp1 = 3 * south % 128
    tmp2 = 4 * west % 128
    tmp3 = 5 * east % 128
    tmp4 = approxlib.add_420(tmp, tmp1) % 128
    tmp5 = approxlib.add_420(tmp2, tmp3) % 128
    tmp6 = approxlib.add_420(tmp4, tmp5) % 128
    tmp7 = approxlib.add_420(tmp6, cent) % 16
    return tmp7


def add_459_fun3(north, east, west, south, cent, row, col):
    tmp = 2 * north % 128
    tmp1 = 3 * south % 128
    tmp2 = 4 * west % 128
    tmp3 = 5 * east % 128
    tmp4 = approxlib.add_459(tmp, tmp1) % 128
    tmp5 = approxlib.add_459(tmp2, tmp3) % 128
    tmp6 = approxlib.add_459(tmp4, tmp5) % 128
    tmp7 = approxlib.add_459(tmp6, cent) % 16
    return tmp7


def add_468_fun3(north, east, west, south, cent, row, col):
    tmp = 2 * north % 128
    tmp1 = 3 * south % 128
    tmp2 = 4 * west % 128
    tmp3 = 5 * east % 128
    tmp4 = approxlib.add_468(tmp, tmp1) % 128
    tmp5 = approxlib.add_468(tmp2, tmp3) % 128
    tmp6 = approxlib.add_468(tmp4, tmp5) % 128
    tmp7 = approxlib.add_468(tmp6, cent) % 16
    return tmp7


def add_465_fun3(north, east, west, south, cent, row, col):
    tmp = 2 * north % 128
    tmp1 = 3 * south % 128
    tmp2 = 4 * west % 128
    tmp3 = 5 * east % 128
    tmp4 = approxlib.add_465(tmp, tmp1) % 128
    tmp5 = approxlib.add_465(tmp2, tmp3) % 128
    tmp6 = approxlib.add_465(tmp4, tmp5) % 128
    tmp7 = approxlib.add_465(tmp6, cent) % 16
    return tmp7


def add_95_fun3(north, east, west, south, cent, row, col):
    tmp = 2 * north % 128
    tmp1 = 3 * south % 128
    tmp2 = 4 * west % 128
    tmp3 = 5 * east % 128
    tmp4 = approxlib.add_95(tmp, tmp1) % 128
    tmp5 = approxlib.add_95(tmp2, tmp3) % 128
    tmp6 = approxlib.add_95(tmp4, tmp5) % 128
    tmp7 = approxlib.add_95(tmp6, cent) % 16
    return tmp7


def add_7_fun2(north, east, west, south, cent, row, col):
    tmp1 = (north * cent) % 128
    tmp2 = (north * east) % 128
    tmp3 = approxlib.add_7(tmp1, tmp2) % 128
    tmp4 = approxlib.add_7(tmp3, north) % 128
    tmp5 = approxlib.add_7(tmp4, 1)
    return tmp5 % 16


def add_56_fun2(north, east, west, south, cent, row, col):
    tmp1 = (north * cent) % 128
    tmp2 = (north * east) % 128
    tmp3 = approxlib.add_56(tmp1, tmp2) % 128
    tmp4 = approxlib.add_56(tmp3, north) % 128
    tmp5 = approxlib.add_56(tmp4, 1)
    return tmp5 % 16


def add_212_fun2(north, east, west, south, cent, row, col):
    tmp1 = (north * cent) % 128
    tmp2 = (north * east) % 128
    tmp3 = approxlib.add_212(tmp1, tmp2) % 128
    tmp4 = approxlib.add_212(tmp3, north) % 128
    tmp5 = approxlib.add_212(tmp4, 1)
    return tmp5 % 16


def add_334_fun2(north, east, west, south, cent, row, col):
    tmp1 = (north * cent) % 128
    tmp2 = (north * east) % 128
    tmp3 = approxlib.add_334(tmp1, tmp2) % 128
    tmp4 = approxlib.add_334(tmp3, north) % 128
    tmp5 = approxlib.add_334(tmp4, 1)
    return tmp5 % 16


def add_420_fun2(north, east, west, south, cent, row, col):
    tmp1 = (north * cent) % 128
    tmp2 = (north * east) % 128
    tmp3 = approxlib.add_420(tmp1, tmp2) % 128
    tmp4 = approxlib.add_420(tmp3, north) % 128
    tmp5 = approxlib.add_420(tmp4, 1)
    return tmp5 % 16


def add_459_fun2(north, east, west, south, cent, row, col):
    tmp1 = (north * cent) % 128
    tmp2 = (north * east) % 128
    tmp3 = approxlib.add_459(tmp1, tmp2) % 128
    tmp4 = approxlib.add_459(tmp3, north) % 128
    tmp5 = approxlib.add_459(tmp4, 1)
    return tmp5 % 16


def add_468_fun2(north, east, west, south, cent, row, col):
    tmp1 = (north * cent) % 128
    tmp2 = (north * east) % 128
    tmp3 = approxlib.add_468(tmp1, tmp2) % 128
    tmp4 = approxlib.add_468(tmp3, north) % 128
    tmp5 = approxlib.add_468(tmp4, 1)
    return tmp5 % 16


def add_398_fun2(north, east, west, south, cent, row, col):
    tmp1 = (north * cent) % 128
    tmp2 = (north * east) % 128
    tmp3 = approxlib.add_398(tmp1, tmp2) % 128
    tmp4 = approxlib.add_398(tmp3, north) % 128
    tmp5 = approxlib.add_398(tmp4, 1)
    return tmp5 % 16


def add_465_fun2(north, east, west, south, cent, row, col):
    tmp1 = (north * cent) % 128
    tmp2 = (north * east) % 128
    tmp3 = approxlib.add_465(tmp1, tmp2) % 128
    tmp4 = approxlib.add_465(tmp3, north) % 128
    tmp5 = approxlib.add_465(tmp4, 1)
    return tmp5 % 16


def add_95_fun2(north, east, west, south, cent, row, col):
    tmp1 = (north * cent) % 128
    tmp2 = (north * east) % 128
    tmp3 = approxlib.add_95(tmp1, tmp2) % 128
    tmp4 = approxlib.add_95(tmp3, north) % 128
    tmp5 = approxlib.add_95(tmp4, 1)
    return tmp5 % 16


def default_add_fun2(north, east, west, south, cent, row, col):
    return (1 + north + (north * cent) % 128 + (north * east) % 128) % 16


def add_7_fun1(north, east, west, south, cent, row, col):
    tmp1 = (north * cent) % 128
    tmp2 = approxlib.add_7(tmp1, east) % 16
    return tmp2


def add_56_fun1(north, east, west, south, cent, row, col):
    tmp1 = (north * cent) % 128
    tmp2 = approxlib.add_56(tmp1, east) % 16
    return tmp2


def add_398_fun1(north, east, west, south, cent, row, col):
    tmp1 = (north * cent) % 128
    tmp2 = approxlib.add_398(tmp1, east) % 16
    return tmp2


def add_212_fun1(north, east, west, south, cent, row, col):
    tmp1 = (north * cent) % 128
    tmp2 = approxlib.add_212(tmp1, east) % 16
    return tmp2


def add_334_fun1(north, east, west, south, cent, row, col):
    tmp1 = (north * cent) % 128
    tmp2 = approxlib.add_334(tmp1, east) % 16
    return tmp2


def add_420_fun1(north, east, west, south, cent, row, col):
    tmp1 = (north * cent) % 128
    tmp2 = approxlib.add_420(tmp1, east) % 16
    return tmp2


def add_459_fun1(north, east, west, south, cent, row, col):
    tmp1 = (north * cent) % 128
    tmp2 = approxlib.add_459(tmp1, east) % 16
    return tmp2


def add_468_fun1(north, east, west, south, cent, row, col):
    tmp1 = (north * cent) % 128
    tmp2 = approxlib.add_468(tmp1, east) % 16
    return tmp2


def add_465_fun1(north, east, west, south, cent, row, col):
    tmp1 = (north * cent) % 128
    tmp2 = approxlib.add_465(tmp1, east) % 16
    return tmp2


def add_95_fun1(north, east, west, south, cent, row, col):
    tmp1 = (north * cent) % 128
    tmp2 = approxlib.add_95(tmp1, east) % 16
    return tmp2


def default_add_fun1(north, east, west, south, cent, row, col):
    return (((north * cent) % 128) + east) % 16


def mul_494_fun1(north, east, west, south, cent, row, col):
    tmp1 = approxlib.mul_494(north, cent) % 128
    tmp2 = tmp1 + east
    return tmp2 % 16


def mul_494_fun2(north, east, west, south, cent, row, col):
    tmp1 = approxlib.mul_494(north, cent) % 128
    tmp2 = approxlib.mul_494(north, east) % 128
    tmp3 = (tmp1 + tmp2) % 128
    tmp4 = (tmp3 + north) % 128
    tmp5 = tmp4 + 1
    return tmp5 % 16


def mul_24_fun2(north, east, west, south, cent, row, col):
    tmp1 = approxlib.mul_24(north, cent) % 128
    tmp2 = approxlib.mul_24(north, east) % 128
    tmp3 = (tmp1 + tmp2) % 128
    tmp4 = (tmp3 + north) % 128
    tmp5 = tmp4 + 1
    return tmp5 % 16


def mul_41_fun2(north, east, west, south, cent, row, col):
    tmp1 = approxlib.mul_41(north, cent) % 128
    tmp2 = approxlib.mul_41(north, east) % 128
    tmp3 = (tmp1 + tmp2) % 128
    tmp4 = (tmp3 + north) % 128
    tmp5 = tmp4 + 1
    return tmp5 % 16


def mul_89_fun2(north, east, west, south, cent, row, col):
    tmp1 = approxlib.mul_89(north, cent) % 128
    tmp2 = approxlib.mul_89(north, east) % 128
    tmp3 = (tmp1 + tmp2) % 128
    tmp4 = (tmp3 + north) % 128
    tmp5 = tmp4 + 1
    return tmp5 % 16


def mul_89_fun3(north, east, west, south, cent, row, col):
    tmp = approxlib.mul_89(2, north) % 128
    tmp1 = approxlib.mul_89(3, south) % 128
    tmp2 = approxlib.mul_89(4, west) % 128
    tmp3 = approxlib.mul_89(5, east) % 128
    tmp4 = (tmp + tmp1) % 128
    tmp5 = (tmp2 + tmp3) % 128
    tmp6 = (tmp4 + tmp5) % 128
    tmp7 = (tmp6 + cent) % 16
    return tmp7


def mul_498_fun3(north, east, west, south, cent, row, col):
    tmp = approxlib.mul_498(2, north) % 128
    tmp1 = approxlib.mul_498(3, south) % 128
    tmp2 = approxlib.mul_498(4, west) % 128
    tmp3 = approxlib.mul_498(5, east) % 128
    tmp4 = (tmp + tmp1) % 128
    tmp5 = (tmp2 + tmp3) % 128
    tmp6 = (tmp4 + tmp5) % 128
    tmp7 = (tmp6 + cent) % 16
    return tmp7


def mul_24_fun3(north, east, west, south, cent, row, col):
    tmp = approxlib.mul_24(2, north) % 128
    tmp1 = approxlib.mul_24(3, south) % 128
    tmp2 = approxlib.mul_24(4, west) % 128
    tmp3 = approxlib.mul_24(5, east) % 128
    tmp4 = (tmp + tmp1) % 128
    tmp5 = (tmp2 + tmp3) % 128
    tmp6 = (tmp4 + tmp5) % 128
    tmp7 = (tmp6 + cent) % 16
    return tmp7


def mul_498_fun4(north, east, west, south, cent, row, col):
    tmp = north + cent + east
    tmp1 = approxlib.mul_498(north, east) % 128
    tmp2 = approxlib.mul_498(tmp1, west)
    tmp3 = tmp + tmp2
    return (tmp3) % 16


def mul_498_fun2(north, east, west, south, cent, row, col):
    tmp1 = approxlib.mul_498(north, west) % 128
    tmp2 = approxlib.mul_498(north, east) % 128
    tmp3 = (tmp1 + tmp2) % 128
    tmp4 = (tmp3 + north) % 128
    tmp5 = tmp4 + 1
    return tmp5 % 16

def mul_365_fun2(north, east, west, south, cent, row, col):
    tmp1 = approxlib.mul_365(north, west) % 128
    tmp2 = approxlib.mul_365(north, east) % 128
    tmp3 = (tmp1 + tmp2) % 128
    tmp4 = (tmp3 + north) % 128
    tmp5 = tmp4 + 1
    return tmp5 % 16


def mul_317_fun2(north, east, west, south, cent, row, col):
    tmp1 = approxlib.mul_317(north, west) % 128
    tmp2 = approxlib.mul_317(north, east) % 128
    tmp3 = (tmp1 + tmp2) % 128
    tmp4 = (tmp3 + north) % 128
    tmp5 = tmp4 + 1
    return tmp5 % 16

def mul_101_fun3(north, east, west, south, cent, row, col):
    tmp = approxlib.mul_101(2, north) % 128
    tmp1 = approxlib.mul_101(3, south) % 128
    tmp2 = approxlib.mul_101(4, west) % 128
    tmp3 = approxlib.mul_101(5, east) % 128
    tmp4 = (tmp + tmp1) % 128
    tmp5 = (tmp2 + tmp3) % 128
    tmp6 = (tmp4 + tmp5) % 128
    tmp7 = (tmp6 + cent) % 16
    return tmp7


def mul_494_fun3(north, east, west, south, cent, row, col):
    tmp = approxlib.mul_494(2, north) % 128
    tmp1 = approxlib.mul_494(3, south) % 128
    tmp2 = approxlib.mul_494(4, west) % 128
    tmp3 = approxlib.mul_494(5, east) % 128
    tmp4 = (tmp + tmp1) % 128
    tmp5 = (tmp2 + tmp3) % 128
    tmp6 = (tmp4 + tmp5) % 128
    tmp7 = (tmp6 + cent) % 16
    return tmp7

def mul_365_fun3(north, east, west, south, cent, row, col):
    tmp = approxlib.mul_365(2, north) % 128
    tmp1 = approxlib.mul_365(3, south) % 128
    tmp2 = approxlib.mul_365(4, west) % 128
    tmp3 = approxlib.mul_365(5, east) % 128
    tmp4 = (tmp + tmp1) % 128
    tmp5 = (tmp2 + tmp3) % 128
    tmp6 = (tmp4 + tmp5) % 128
    tmp7 = (tmp6 + cent) % 16
    return tmp7

def mul_317_fun3(north, east, west, south, cent, row, col):
    tmp = approxlib.mul_317(2, north) % 128
    tmp1 = approxlib.mul_317(3, south) % 128
    tmp2 = approxlib.mul_317(4, west) % 128
    tmp3 = approxlib.mul_317(5, east) % 128
    tmp4 = (tmp + tmp1) % 128
    tmp5 = (tmp2 + tmp3) % 128
    tmp6 = (tmp4 + tmp5) % 128
    tmp7 = (tmp6 + cent) % 16
    return tmp7


def mul_101_fun4(north, east, west, south, cent, row, col):
    tmp = north + cent + east
    tmp1 = approxlib.mul_101(north, east) % 128
    tmp2 = approxlib.mul_101(tmp1, west)
    tmp3 = tmp + tmp2
    return (tmp3) % 16


def mul_101_fun2(north, east, west, south, cent, row, col):
    tmp1 = approxlib.mul_101(north, cent) % 128
    tmp2 = approxlib.mul_101(north, east) % 128
    tmp3 = (tmp1 + tmp2) % 128
    tmp4 = (tmp3 + north) % 128
    tmp5 = tmp4 + 1
    return tmp5 % 16


def mul_24_fun1(north, east, west, south, cent, row, col):
    tmp1 = approxlib.mul_24(north, cent) % 128
    tmp2 = tmp1 + east
    return tmp2 % 16


def mul_41_fun4(north, east, west, south, cent, row, col):
    tmp = north + cent + east
    tmp1 = approxlib.mul_41(north, east) % 128
    tmp2 = approxlib.mul_41(tmp1, west)
    tmp3 = tmp + tmp2
    return (tmp3) % 16


def mul_89_fun4(north, east, west, south, cent, row, col):
    tmp = north + cent + east
    tmp1 = approxlib.mul_89(north, east) % 128
    tmp2 = approxlib.mul_89(tmp1, west)
    tmp3 = tmp + tmp2
    return (tmp3) % 16

def mul_365_fun4(north, east, west, south, cent, row, col):
    tmp = north + cent + east
    tmp1 = approxlib.mul_365(north, east) % 128
    tmp2 = approxlib.mul_365(tmp1, west)
    tmp3 = tmp + tmp2
    return (tmp3) % 16

def mul_317_fun4(north, east, west, south, cent, row, col):
    tmp = north + cent + east
    tmp1 = approxlib.mul_317(north, east) % 128
    tmp2 = approxlib.mul_317(tmp1, west)
    tmp3 = tmp + tmp2
    return (tmp3) % 16