#!/usr/bin/env python


import collections
import os
import csv
import sys
import pandas as pd


def no_states(listname):
    counter = 0
    for key in listname.keys():
        if (listname[key] != 0):
            counter += 1
    return counter


def save_dict(output_path, dictname):
    with open(output_path, "w") as state_csv:
        dict_writer = csv.writer(state_csv, delimiter=",")
        for key in dictname.keys():
            dict_writer.writerow([key, dictname[key]])


def create_histogram(csv_file):
    data = pd.read_csv(csv_file, sep=',', header=None, index_col=0)
    ax = data.plot.hist(bins=16, alpha=1)


def convert_bytes(num):
    num /= 1024.0
    return num


def file_size(file_path):
    if os.path.isfile(file_path):
        file_info = os.stat(file_path)
        num = int(file_info.st_size)
        return num / 1024.0


def states_size(files, output):
    i = 0
    print(output)
    with open(output, "w") as out_states:
        out_csv = csv.writer(out_states)
        for f in files:
            size = file_size(f)
            out_csv.writerow([str(i), str(size)])
            i += 1
    out_states.close()


def main():
    input_path = sys.argv[1]
    output_path = sys.argv[2]
    def_path = input_path
    files_png = []

    dirs = ["state1_f1", "state2_f1", "state3_f1", "state4_f1",
            "state1_f2", "state1_f3", "state1_f4", "state2_f2",
            "state2_f3", "state2_f4", "state3_f2", "state3_f3",
            "state3_f4", "state4_f2", "state4_f3", "state4_f4"]

    for dirname in dirs:
        input_path = input_path + dirname + "/png_compressed"
        output = def_path + dirname + "/" + dirname + ".csv"
        for f in os.listdir(input_path):
            if os.path.isfile(os.path.join(input_path, f)) and f.endswith(".png"):
                files_png.append(os.path.join(input_path, f))
        files_png = sorted(files_png)
        states_size(files_png, output)
        input_path = def_path
        files_png = []


if __name__ == "__main__":
    main()
