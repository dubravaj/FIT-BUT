#!/usr/bin/env python
#
# ******************************************************************************
# ****** BiCAS - Bidlo Cellular Automata Simulator pro potreby kurzu BIN *******
# ******************************************************************************
# K funkci vyzaduje nainstalovane prostredi Python s knihovnami NumPy a PyGame.
# Pokud toto nemate nainstalovano, je mozne spustit BiCAS vzdalene po pripojeni
# na server merlin:
#
#                     ssh -l xlogin00 -X merlin.fit.vutbr.cz
#
# --------------------------------- Licence ------------------------------------
# BiCAS je poskytnut v souladu s pravidly "The BSD 3-Clause License", jejiz
# zneni je uvedeno v souboru LICENSE distribuovaneho spolu s programem.
# ------------------------------------------------------------------------------
#
#     (c) 2015-2016, Michal Bidlo, FIT VUT v Brne, <bidlom AT fit vutbr cz>
#
# ******************************************************************************

import approxlib
import argparse
import ca_lib as calib
import os, sys, glob, pygame
from random import randint
import numpy as np
#import matplotlib.pyplot as plt
import matplotlib.mlab as mlab
import plotly.plotly as py
import plotly.graph_objs as go
import time
import math
pygame.init()

# implicitni velikost CA (_SIZE x _SIZE bunek) - modifikovatelne
_SIZE = 100
# velikost okna celularniho prostoru (_WIN x _WIN pixelu) - modif.
_WIN = 600

# nejake barevne konstanty => v tomto pripade CA s max. 13 stavy
white = pygame.Color(0xFFFFFF00)
black = pygame.Color(0x00000000)
red = pygame.Color(0xFF000000)
green = pygame.Color(0x00FF0000)
blue = pygame.Color(0x0000FF00)
cyan = pygame.Color(0x00FFFF00)
magenta = pygame.Color(0xFF00FF00)
yellow = pygame.Color(0xFFFF0000)
gray = pygame.Color(0x64646400)
pink = pygame.Color(0xFF087F00)
brown = pygame.Color(0x825A2C00)
orange = pygame.Color(0xFA680000)
violet = pygame.Color(0xAA00FF00)

color = [ black, red, green, yellow, brown, white, orange, cyan, violet,
          gray, magenta, pink, blue ]


color_codes16 = [
    0x00000000,
    0x66CCCC00,
    0x00000000,
    0x6666CC00,
    0x6600CC00,
    0xCC33CC00,
    0xCC999900,
    0xCCFF6600,
    0xFF336600,
    0xFF993300,
    0xFFFF3300,
    0x00009900,
    0x00999900,
    0x00FF9900,
    0x99336600,
    0x99339900,
    0x33CCCC00
]

colors256 = []
for c, i in zip(color_codes16, range(16)):
    i = pygame.Color(c)
    colors256.append(i)







class CA:
    def __init__(self, rows=100, cols=100, states=16, nsize=5):
        self.shelp = 1
        self.age = 0
        self.cell = np.empty((rows+2, cols+2), object)  # object bude str
        self.temp = np.empty((rows+2, cols+2), object)
        self.istt = np.empty((rows+2, cols+2), object)
        for row in range(rows+2):
            for col in range(cols+2):
                self.cell[row][col] = "00"
                self.temp[row][col] = "00"
                self.istt[row][col] = "00"
        self.ltf_dict = {}
        self.freq_dict = {}
        self.ltf_list = [] # seznam souboru s LTF
        self.ltf_index = 0 # index aktualne nactene LTF
        self.ltf_limit = 0 # kolik .tab souboru nacteno
        self.rows = rows
        self.cols = cols
        self.states = states
        self.nsize = nsize # velikost bunecneho okoli
        # nastaveni rozmeru bunek vzhledem k velikosti okna
        self.cell_w = pygame.display.Info().current_w / self.cols
        self.cell_h = pygame.display.Info().current_h / self.rows
        # souradnice pro vypis napovedy
        self.hy = 0
        self.histogram_arr = dict()
        self.filename = ""
        self.used_function = None
        self.used_function_name = ""
        self.iteration = 0

    def set_used_function(self, fun):
        self.used_function = fun

    def set_used_function_name(self, name):
        self.used_function_name = name

    def set_filename(self,name):
        self.filename = name

    def set_iteration(self, iteration):
        self.iteration = iteration

    def init_histogram_arr(self):
        for i in range(self.states):
            self.histogram_arr[i] = 0

    def set_cell(self, row, col, state):
        temp = int(state)
        self.cell[row][col] = "%02d" % temp

    def get_cell(self, row, col):
        return int(self.cell[row][col])

    def set_clicked_cell(self, (pos_x, pos_y)):
        row = pos_y / self.cell_h + 1
        col = pos_x / self.cell_w + 1
        temp = (self.get_cell(row, col) + 1) % self.states
        self.set_cell(row, col, temp)

    def zero_init(self):
        self.age = 0
        for row in range(1, self.rows+1):
            for col in range(1, self.cols+1):
                self.set_cell(row, col, 0)

    def istt_init(self):
        self.age = 0
        for row in range(1, self.rows+1):
            for col in range(1, self.cols+1):
                self.set_cell(row, col, self.istt[row][col])

    def draw_text(self, win, label):
        global _WIN

        font = pygame.font.SysFont("monospace", 24)
        font.set_bold(1)
        text = font.render(label, 1, (255, 255, 0))
        if self.hy == 0:
            win.blit(text, (20, 10))
            self.hy = self.hy + 40
        else:
            win.blit(text, (20, self.hy))
            self.hy = self.hy + 20

    def show_help(self, win):
        self.hy = 0
        self.draw_text(win, "Interaktivni rizeni aplikace BiCAS")
        self.draw_text(win, "SPACE: spust/pozastav vyvoj CA")
        self.draw_text(win, "t: proved jeden vyvojovy krok CA")
        self.draw_text(win, "i: navrat do pocatecniho stavu")
        self.draw_text(win, "c: zapis stavu CA do souboru .png")
        self.draw_text(win, "klik mysi: inkrement stavu bunky")
        self.draw_text(win, "ESC: ukonceni aplikace BiCAS")
        self.draw_text(win, "h: zobrazeni teto napovedy")
        self.draw_text(win, "Detaily viz soubor BiCAS-man.txt.")
        pygame.display.update()

    def make_histogram(self,csv_filename):
        for row in range(1,self.rows+1):
            for col in range(1,self.cols+1):
                state = self.get_cell(row, col)
                self.histogram_arr[state] = self.histogram_arr[state] + 1
        with open(csv_filename,"w") as csv:
            for key in self.histogram_arr.keys():
                cell_state = str(key)+','+str(self.histogram_arr[key])+'\n'
                csv.write(cell_state)
        self.init_histogram_arr()
        csv.close()


    def draw(self, win):
        for row in range(1, self.rows+1):
            for col in range(1, self.cols+1):
                pygame.draw.rect(win, colors256[self.get_cell(row, col)],
                                ((col-1)*self.cell_w, (row-1)*self.cell_h,
                                self.cell_w, self.cell_h), 0)
                pygame.draw.rect(win, colors256[0],
                               ((col-1)*self.cell_w, (row-1)*self.cell_h,
                                self.cell_w, self.cell_h), 1)
        time.sleep(0.1)
        pygame.display.update()
        # po startu aplikace zobraz napovedu
        if self.shelp == 1:
            self.show_help(win)

    def develop(self, win):
        # probehne krok vyvoje vsech bunek podle dane LTF
        for row in range(1, self.rows+1):
            for col in range(1, self.cols+1):
                self.LTF_next(row, col)
        # update stavu celularniho pole
        for row in range(1, self.rows+1):
            for col in range(1, self.cols+1):
                self.set_cell(row, col, self.temp[row][col])
        self.draw(win)
        self.age = self.age + 1


    def LTF_next(self, row, col):
        if self.nsize == 5:
            north=self.cell[row-1][col] if row>1 else self.cell[self.rows][col]
            west =self.cell[row][col-1] if col>1 else self.cell[row][self.cols]
            cent =self.cell[row][col]
            east =self.cell[row][col+1] if col<self.cols else self.cell[row][1]
            south=self.cell[row+1][col] if row<self.rows else self.cell[1][col]
            key =  ''.join([ north, west, cent, east, south ])
            north = int(north)
            west = int(west)
            cent = int(cent)
            east = int(east)
            south = int(south)

            self.temp[row][col] = self.used_function(north, east, west, south, cent, row, col)

        elif self.nsize == 9:
            row_cm = row - 1 if row > 1 else self.rows
            row_cp = row + 1 if row < self.rows else 1
            col_cm = col - 1 if col > 1 else self.cols
            col_cp = col + 1 if col < self.cols else 1
            key =  ''.join([ self.cell[row_cm][col_cm], self.cell[row_cm][col],
                             self.cell[row_cm][col_cp], self.cell[row][col_cm],
                             self.cell[row][col]    , self.cell[row][col_cp],
                             self.cell[row_cp][col_cm], self.cell[row_cp][col],
                             self.cell[row_cp][col_cp] ])
            self.temp[row][col] = self.ltf_dict.get(key, self.cell[row][col])

        else:
            print "Nepodporovane okoli:", self.nsize
            exit(1)

    def read_state(self, sfile):
        istate = []
        for line in sfile.readlines():
            istate.append(line.strip().split(' '))
        sfile.close()
        roff = (self.rows - len(istate)) / 2
        coff = (self.cols - len(istate[0])) / 2
        for r in range(0, len(istate)):
            for s in range(0, len(istate[0])):
                self.istt[r+roff+1][s+coff+1] = istate[r][s]
        self.istt_init()


    def ltf_change_next(self):
        self.ltf_dict.clear()
        if self.ltf_index < self.ltf_limit-1:
            self.ltf_index = self.ltf_index + 1
        else:
            sys.exit(0) # pro indikaci projiti vsech funkci
            self.ltf_index = 0
        self.read_ca(self.ltf_list[self.ltf_index])

    def ltf_change_prev(self):
        self.ltf_dict.clear()
        if self.ltf_index > 0:
            self.ltf_index = self.ltf_index - 1
        else:
            self.ltf_index = self.ltf_limit-1
        self.read_ca(self.ltf_list[self.ltf_index])



def main_loop(ca, win, state):
    devel = 0
    capture = 0
    iterations = 0
    while True: # obsluha GUI a rizeni vyvoje CA
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                sys.exit(0)
            elif event.type == pygame.KEYDOWN:
                ca.shelp = 0
                keyb = event.key
                if keyb == pygame.K_SPACE: # pozastavit/spustit vyvoj CA
                    devel = 1 - devel      # priznak, zda bezi vyvoj CA
                elif keyb == pygame.K_c:   # capture - zapis png se stavem CA
                    if devel == 0: # pro zachyceni stavu musi byt CA pozastaven
                        cap_img = "%03d" % capture
                        capture = capture + 1
                        pygame.image.save(win, cap_img + ".png")
# odkomentuj, pokud chces po zapisu stavu automaticky provest jeden dalsi krok
#                        ca.develop(win)
                elif keyb == pygame.K_t:   # proved 1 krok, pokud je CA pozast.
                    if devel == 0:
                        ca.develop(win)
                    print "step %d" % ca.age
                elif keyb == pygame.K_i:   # stop, inicializuj CA z istt.
                    ca.istt_init()
                    ca.draw(win)
                    devel = 0
                    capture = 0
                elif keyb == pygame.K_s:   # stop, vynuluj CA
                    ca.zero_init()
                    ca.draw(win)
                    devel = 0
                    capture = 0
                elif keyb == pygame.K_h:   # pozastav CA, ukaz napovedu
                    if devel == 1:
                        devel = 0
                    ca.shelp = 1
                    ca.draw(win)
                # pokud je v adresari nalezeno vice souboru .tab obsahujicich
                # prechodove funkce CA, lze mezi nimi pomoci sipek prepinat
                elif keyb == pygame.K_DOWN or keyb == pygame.K_RIGHT:
                    if ca.ltf_list:
                        ca.istt_init()
                        ca.draw(win)
                        devel = 0
                        capture = 0
                        ca.ltf_change_next()
                elif keyb == pygame.K_UP or keyb == pygame.K_LEFT:
                    if ca.ltf_list:
                        ca.istt_init()
                        ca.draw(win)
                        devel = 0
                        capture = 0
                        ca.ltf_change_prev()
                elif keyb == pygame.K_ESCAPE: # ukonceni aplikace
                    sys.exit(0)
            # klik mysi na bunku inkrementuje jeji stav - pro hrani si...
            elif event.type == pygame.MOUSEBUTTONDOWN:
                ca.shelp = 0
                ca.set_clicked_cell(pygame.mouse.get_pos())
                ca.draw(win)
        if devel == 1:
            print(iterations)
            ca.develop(win)
            if(iterations < 101):
                if (iterations == 100):
                    return
                cap_img = "%03d" % capture
                if not os.path.exists(str(ca.filename)):
                    os.mkdir(str(ca.filename))
                #ca.make_histogram(str(ca.filename)+str(cap_img)+".csv")
                pygame.image.save(win, str(ca.filename)+cap_img + "_"+str(ca.used_function_name)+"_"+str(ca.iteration)+".png")
                capture = capture + 1
            iterations += 1

def run_parser(): # analyza argumentu zadanych pri spusteni aplikace
    global _SIZE

    ca = None
    size = _SIZE    # implicitni velikost CA
    state = False   # zda zadan pocatecni stav - zatim ne
    ltf = False     # zda zadana prechodova funkce - zatim ne

    ca = CA(size, size)    # vytvorime CA daneho rozmeru
    return ca

def usage():
    print "Usage:"
    print "-------------------------------------------------------"
    print "PyCA cislo_funkcie pociatocny_stav_CA pociatocny_stav_bunky vystupny_priecinok"
    print "-------------------------------------------------------"

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

def state1(ca,val):
    ca.set_cell(50,50,val)
def state2(ca,val):
    ca.set_cell(50, 50, val)
    ca.set_cell(50, 51, val)
    ca.set_cell(49, 50, val)
    ca.set_cell(49, 51, val)
def state3(ca,val):
    ca.set_cell(50, 50, val)
    ca.set_cell(50, 51, val)
    ca.set_cell(50, 52, val)
def state4(ca,val):
    ca.set_cell(50, 50, val)
    ca.set_cell(49, 50, val)
    ca.set_cell(48, 50, val)
    ca.set_cell(50, 51, val)
    ca.set_cell(50, 52, val)
    ca.set_cell(50, 49, val)
    ca.set_cell(50, 48, val)
    ca.set_cell(51, 50, val)
    ca.set_cell(52, 50, val)

def generate_initial_state(ca,val):
    no_cells = randint(1,4)
    print(no_cells)
    if(no_cells == 1):
        row = randint(1,101)
        col = randint(1,101)
        ca.set_cell(row,col, val)
    if(no_cells == 2):
        row = randint(1, 101)
        col = randint(1, 101)
        ca.set_cell(row, col, val)
        row = randint(1, 101)
        col = randint(1, 101)
        ca.set_cell(row, col, val)
        row = randint(1, 101)
        col = randint(1, 101)
        ca.set_cell(row, col, val)
        row = randint(1, 101)
        col = randint(1, 101)
        ca.set_cell(row, col, val)
    if(no_cells == 3):
        row = randint(1, 101)
        col = randint(1, 101)
        ca.set_cell(row, col, val)
        row = randint(1, 101)
        col = randint(1, 101)
        ca.set_cell(row, col, val)
        row = randint(1, 101)
        col = randint(1, 101)
        ca.set_cell(row, col, val)
    if(no_cells == 4):
        row = randint(1, 101)
        col = randint(1, 101)
        ca.set_cell(row, col, val)
        row = randint(1, 101)
        col = randint(1, 101)
        ca.set_cell(row, col, val)
        row = randint(1, 101)
        col = randint(1, 101)
        ca.set_cell(row, col, val)
        row = randint(1, 101)
        col = randint(1, 101)
        ca.set_cell(row, col, val)
        row = randint(1, 101)
        col = randint(1, 101)
        ca.set_cell(row, col, val)
        row = randint(1, 101)
        col = randint(1, 101)
        ca.set_cell(row, col, val)
        row = randint(1, 101)
        col = randint(1, 101)
        ca.set_cell(row, col, val)
        row = randint(1, 101)
        col = randint(1, 101)
        ca.set_cell(row, col, val)
        row = randint(1, 101)
        col = randint(1, 101)
        ca.set_cell(row, col, val)

def main():
    global _WIN

    parser = argparse.ArgumentParser()
    parser.add_argument('-f', required=True)
    parser.add_argument('-s', required=True)
    parser.add_argument('-i', required=True)
    parser.add_argument('-d', required=True)
    args = vars(parser.parse_args())
    print(args)

    func = int(args["f"])
    state = int(args["s"])
    initial_val_state = int(args["i"])
    filename = args["d"]
    usage()
    ca_functions = []
    if(func == 1):
        ca_functions = init_function1_demo()
    elif (func == 2):
        ca_functions = init_function2_demo()
    elif (func == 3):
        ca_functions = init_function3_demo()
    elif (func == 4):
        ca_functions = init_function4_demo()

    win = pygame.display.set_mode((_WIN, _WIN))

    i = 0
    val = initial_val_state
    for f in ca_functions:
        f_name = f.__name__
        ca = run_parser()
        ca.draw(win)
        ca.init_histogram_arr()
        ca.set_filename(filename)
        ca.set_used_function(f)
        ca.set_used_function_name(f_name)
        ca.set_iteration(str(state))
        if state == 1:
            state1(ca,val)
        elif state == 2:
            state2(ca,val)
        elif state == 3:
            state3(ca,val)
        elif state == 4:
            state4(ca,val)
        elif state > 4:
            generate_initial_state(ca,val)
        #pygame.display.flip()
        main_loop(ca, win, state)
        i +=1

if __name__ == "__main__":
    main()
