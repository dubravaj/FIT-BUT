import requests
from Bio import pairwise2

data = open("/home/xodar/PycharmProjects/Protherm_refined/dataset_cleaned_pokus3.csv")
names = open("/home/xodar/PycharmProjects/Protherm_refined/renames.csv")
output = open("/home/xodar/PycharmProjects/Protherm_refined/reverted_dataset.csv", "w")
probMuts = open("/home/xodar/PycharmProjects/Protherm_refined/problem_dataset.csv", "w")

trans = {"ALA":"A","ARG":"R","ASN":"N","ASP":"D","CYS":"C","GLU":"E","GLN":"Q","GLY":"G","HIS":"H","ILE":"I","LEU":"L",
         "LYS":"K","MET":"M","PHE":"F","PRO":"P","SER":"S","THR":"T","TRP":"W","TYR":"Y","VAL":"V"}

problems = list()
mutations_to_check = list()
origData = dict()
for line in data.readlines():
    line = line[0:-1]
    parts = line.split(",")
    if parts[0] in origData:
        origData[parts[0]].append(parts[1:])
    else:
        origData[parts[0]] = list()
        origData[parts[0]].append(parts[1:])

name_dict = dict()
for line in names.readlines():
    line = line[0:-1]
    parts = line.split(",")
    name_dict[parts[1]] = parts[0]

for name in name_dict:
    new_coords_dict = dict()
    new_coords = list()
    bias = -9999
    new_PDB = requests.get("https://files.rcsb.org/download/" + name + ".pdb").text
    lines = new_PDB.split("\n")
    actPos = ""
    for line in lines:
        if line[0:4] == "ATOM":
            aa = line[17:20]
            chain = line[21]
            position = int(line[22:26])
            if bias == -9999:
                bias = 1 - int(position)
            if aa in trans:
                aa = trans[aa]
            else:
                aa = "X"
            if actPos != position:
                actPos = position
                new_coords.append([chain, position, aa])
                new_coords_dict[int(position)] = aa

    old_coords = list()
    old_coords_dict = dict()
    old_PDB = requests.get("https://files.rcsb.org/download/" + name_dict[name] + ".pdb").text
    lines = old_PDB.split("\n")
    actPos = ""
    for line in lines:
        if line[0:4] == "ATOM":
            aa = line[17:20]
            chain = line[21]
            position = int(line[22:26])
            if aa in trans:
                aa = trans[aa]
            else:
                aa = "X"
            if actPos != position:
                actPos = position
                old_coords.append([chain, position, aa])
                old_coords_dict[int(position)] = aa

    print("actual protein: " + name + ", bias: " + str(bias))

    new_seq = ""
    for coord in new_coords:
        new_seq += coord[2]
    old_seq = ""
    for coord in old_coords:
        old_seq += coord[2]
    alignment = pairwise2.align.globalxx(new_seq, old_seq)
    al1 = alignment[0][0]
    al2 = alignment[0][1]
    counter1 = 1
    counter2 = 1
    index_mapping = dict()

    for i in range(len(al1)):
        if al1[i] != "-" and al2[i] != "-":
            index_mapping[counter1] = counter2
        if al1[i] != "-":
            counter1 += 1
        if al2[i] != "-":
            counter2 += 1

    if name not in origData:
        continue
    actProblem = False
    for mutation in origData[name]:
        actMut = int(mutation[1]) - bias
        try:
            #output.write(name_dict[name] + "," + mutation[0] + "," + str(index_mapping[int(mutation[1])]) + "," + mutation[2] + "," + mutation[3] + "," + mutation[4] + "\n")
            if new_coords_dict[actMut] != old_coords_dict[index_mapping[actMut]]:
                actProblem = True
                print("problem: " + str(actMut))
            else:
                print("ok")
        except:
            problems.append([name, actMut])
            print("shit")
            actProblem = True
    for mut in origData[name]:
        actMut = int(mut[1]) - bias
        if not actProblem:
            output.write(name_dict[name] + "," + mut[0] + "," + str(index_mapping[actMut]) + "," + mut[2] + "," + mut[3] + "," + mut[4] + "\n")
        else:
            probMuts.write(name + "," + name_dict[name] + "," + mut[0] + "," + str(actMut) + "," + mut[2] + "\n")