import os,glob, requests,re, glob
import yaml
from fireprotdb.importer.db import *
from fireprotdb.importer.utils.pdb import load_pdb
from fireprotdb.importer.utils.uniprot import UniprotObsoleteMapping

uniprot_mappings = UniprotObsoleteMapping({
    ("P51698", "1MJ5"): "D4Z2G1",
})
uniprot_ids = dict()

storedProteins = dict()
dataStore = open("./data/varibench/errors_to_check.csv", "w")

datasets_pubs = list()
pubs = open("./data/varibench/publications.csv")
data_names = dict()
dataset_journal = dict()
mutations_dict = dict()
no_chain = dict()
no_value = dict()
right = 0
oposite = 0
errors_list = list()

for line in pubs.readlines():
    parts = re.split(";", line)
    if len(parts) > 1:
        datafile = parts[0]
        name = parts[1]
        article = parts[2]
        journal = parts[3]
        volume = parts[4]
        issue = parts[5]
        year = parts[6]
        pages = parts[7]
        doi = parts[8]
        pmid = parts[9]
        authors = list()
        for auth in parts[10:]:
            if len(auth) > 2:
                name_full = re.split(" ", auth)
                last = name_full[0]
                init = name_full[1]
                if len(init) == 1:
                    first = init
                else:
                    first = init[0] + " " + init[1]
                authors.append([last, first, init])
        datasets_pubs.append([datafile, name, article, journal, volume, issue, year, pages, doi, pmid, authors])
        data_names[datafile] = name
        dataset_journal[name] = [article, journal, volume, issue, year, pages, doi, pmid, authors]

datasets = list()
dts = open("./data/varibench/data_columns.csv")
first = True
for line in dts.readlines():
    if first:
        first = False
        continue

    parts = re.split(",", line)
    if len(parts) > 1:
        datafile = parts[0]
        pdb = parts[1]
        chain = parts[2]
        position = parts[3]
        ref = parts[4]
        alt = parts[5]
        dtm = parts[6]
        ddg = parts[7]
        ph = parts[8]
        tm = parts[9]
        inverse = parts[17]
        datasets.append([datafile, pdb, chain, position, ref, alt, dtm, ddg, ph, tm, inverse])

for dataset in datasets:
    if int(dataset[-1]) == 0:
        continue

    data = open("./data/varibench/" + dataset[0])

    pdb_col = ""
    pdb_start = ""
    pdb_end = ""
    if len(dataset[1]) > 0:
        if len(dataset[1]) <= 2:
            pdb_col = int(dataset[1])
        else:
            pts = re.split(";", dataset[1])
            pdb_col = int(pts[0])
            pdb_start = int(pts[1])
            pdb_end = int(pts[2])
    chain_col = ""
    chain_char = ""
    if len(dataset[2]) > 0:
        if len(dataset[2]) > 1:
            parts = dataset[2].split(";")
            chain_col = int(parts[0])
            chain_char = int(parts[1])
        else:
            chain_col = int(dataset[2])
    position_col = ""
    position_start = ""
    position_end = ""
    if len(dataset[3]) > 0:
        if len(dataset[3]) > 1:
            parts = dataset[3].split(";")
            position_col = int(parts[0])
            position_start = int(parts[1])
            position_end = int(parts[2])
        else:
            position_col = int(dataset[3])
    ref_col = ""
    ref_char = ""
    if len(dataset[4]) > 0:
        if len(dataset[4]) > 1:
            parts = dataset[4].split(";")
            ref_col = int(parts[0])
            ref_char = int(parts[1])
        else:
            ref_col = int(dataset[4])
    alt_col = ""
    alt_char = ""
    if len(dataset[5]) > 0:
        if len(dataset[5]) > 1:
            parts = dataset[5].split(";")
            alt_col = int(parts[0])
            alt_char = int(parts[1])
        else:
            alt_col = int(dataset[5])
    if len(dataset[6]) > 0:
        dtm = int(dataset[6])
    else:
        dtm = ""
    if len(dataset[7]) > 0:
        ddg = int(dataset[7])
    else:
        ddg = ""
    if len(dataset[8]) > 0:
        ph = int(dataset[8])
    else:
        ph = ""
    if len(dataset[9]) > 0:
        tm = int(dataset[9])
    else:
        tm = ""

    first = True
    for line in data.readlines():

        if first:
            first = False
            continue

        line = line[0:-1]
        line = line.strip()

        parts = line.split(",")
        if dataset[10] == "0":
            continue

        for part in parts:
            part = part.strip()
        if pdb_col == "":
            continue
        if pdb_start == "":
            pdb = parts[pdb_col].strip()
        else:
            pdb = parts[pdb_col][pdb_start:pdb_end]
        pdb = pdb.upper()

        chain = ""
        if chain_col != "":
            if chain_char != "":
                try:
                     chain = parts[chain_col][chain_char]
                except:
                    print(dataset[0] + ":" + line)
                    exit()
            else:
                chain = parts[chain_col]

        if not pdb in uniprot_ids:
            uniprot_ids[pdb] = chain

        position = ""
        if position_col != "":
            if position_start != "":
                try:
                    position = int(parts[position_col][position_start:position_end])
                except Exception as e:
                    try:
                        position = int(''.join(filter(str.isdigit, parts[position_col][position_start:position_end])))
                    except:
                        print(e)
                        print(dataset[0] + "," + str(position_col) + "," + str(position_start) + "," + str(position_end))
                        print(line)
                        exit()
            else:
                position = int(parts[position_col])
        if position == "":
            continue
        ref = ""
        if ref_col != "":
            if ref_char != "":
                ref = parts[ref_col][ref_char]
            else:
                ref = parts[ref_col]
        alt = ""
        if alt_col != "":
            if alt_char != "":
                alt = parts[alt_col][alt_char]
            else:
                alt = parts[alt_col]
        ref = ref.upper()
        alt = alt.upper()

        actDDG = ""
        if ddg != "" and parts[ddg] != "" and parts[ddg] != "-":
            try:
                parts[ddg] = parts[ddg].replace("(a)", "").replace("(b)", "").replace("(d)", "").replace("(c)", "")
                actDDG = str(round(float(parts[ddg]) * float(dataset[-1]), 1))
            except Exception as e:
                print(e)
                print(dataset[0] + ":" + line)
        actDTM = ""
        if dtm != "" and parts[dtm] != "" and parts[dtm] != "-":
            parts[dtm] = parts[dtm].replace("\(.*\)", "")
            actDTM = str(round(float(parts[dtm]), 1))

        actPH = ""
        if ph != "":
            try:
                actPH = str(round(float(parts[ph]),1))
            except:
                actPH = parts[ph]
        actTM = ""
        if tm != "":
            try:
                actTM = str(round(float(parts[tm]),1))
            except:
                actTM = parts[tm]

        if chain == "@":
            chain = ""

        if ref.isnumeric() or len(pdb) != 4:
            continue
        if not ref.isalpha() or not alt.isalpha():
            continue
        if actDDG == "" and actDTM == "":
            continue

        actMut = str(pdb) + "," + str(chain) + "," + str(ref) + str(position) + str(alt) + "," + str(actDDG) + "," + str(actDTM) + "," + str(actPH) + "," + str(actTM)
        actMutNoChain = str(pdb) + "," + str(ref) + str(position) + str(alt) + "," + str(actDDG) + "," + str(actDTM)
        actNoValue = str(pdb) + "," + str(ref) + str(position) + str(alt)
        exclude = False
        if pdb in storedProteins:
            found = False
            for mut in storedProteins[pdb]:
                if mut[0] == ref and mut[1] == position and mut[2] == alt:
                    if mut[3] != "" and actDDG != "" and float(mut[3]) == float(actDDG)*(-1.):
                        found = True
                        exclude = True
                    else:
                        found = True
            if not found:
                storedProteins[pdb] = list()
                storedProteins[pdb].append([ref, position, alt, actDDG])
        else:
            storedProteins[pdb] = list()
            storedProteins[pdb].append([ref, position, alt, actDDG])

        if actMut in mutations_dict:
            if not dataset[0][0:-4] in mutations_dict[actMut] and not exclude:
                all_dataset = mutations_dict[actMut]
                all_dataset.append(dataset[0][0:-4])
                mutations_dict[actMut] = all_dataset
                no_chain[actMutNoChain] = actMut
        elif not exclude:
            mutations_dict[actMut] = [dataset[0][0:-4]]
            no_chain[actMutNoChain] = actMut
            no_value[actNoValue] = actMut

protherm_data = open("./cache/protherm.yaml")
protherm = yaml.safe_load(protherm_data)
protherm_included = list()
protherm_included_novalue = list()

for id in uniprot_ids:
    try:
        seqs, uniprot_data, struct_index_mapping, missing_residues, seqres = load_pdb(id, uniprot_mappings)

        if len(uniprot_data) > 0:
            default = ""
            for act in uniprot_data:
                default = act
                if uniprot_ids[id] in uniprot_data[act]:
                    uniprot_ids[id] = act
                    break
            uniprot_ids[id] = default
    except Exception as e:
        None


for item in protherm:
    ddgh2o = str(item["data"]["ddG_H2O"])
    ddg = str(item["data"]["ddG"])
    dtm = str(item["data"]["dTm"])
    ph = str(item["experimental_condition"]["pH"])
    tm = str(item["experimental_condition"]["temp"])
    pdb = str(item["pdb_id"]).upper()
    mut = str(item["mut"]).upper()
    pos = str(item["struct_index"])
    wt = str(item["wt"]).upper()

    actDDG = ""
    if ddg != None and ddg != "" and ddg != "None":
        actDDG = str(round(float(ddg), 1))
    elif ddgh2o != None and ddgh2o != "" and ddgh2o != "None":
        actDDG = str(round(float(ddgh2o), 1))
    actDTM = ""
    if dtm != None and dtm != "" and dtm != "None":
        actDTM = str(round(float(dtm), 1))

    actPH = ""
    if ph != "" and ph != None and ph != "None":
        try:
            actPH = str(round(float(ph),1))
        except:
            actPH = ph
    actTM = ""
    if tm != "" and tm != None and tm != "None":
        try:
            actTM = str(round(float(tm),1))
        except:
            actTM = tm

    invDDG = ""
    try:
        invDDG = str(float(actDDG) * (-1.))
    except:
        None
    protMut = str(pdb) + "," + str(wt) + str(pos) + str(mut) + "," + str(actDDG) + "," + str(actDTM)
    invprotMut = str(pdb) + "," + str(wt) + str(pos) + str(mut) + "," + str(invDDG) + "," + str(actDTM)
    actNoValue = str(pdb) + "," + str(wt) + str(pos) + str(mut)
    if protMut in no_chain or invprotMut in no_chain:
        protherm_included.append(protMut)
        protherm_included.append(invprotMut)
    if protMut in no_chain:
        #for err in mutations_dict[no_chain[protMut]]:
        #    if err in errors_dict:
        #        act_err = errors_dict[err] + 1
        #        errors_dict[err] = act_err
        #    else:
        #        errors_dict[err] = 1
        #dataStore.write(no_chain[protMut] + "\n")
        errors_list.append(no_chain[protMut])
        right += 1
    #if invprotMut in no_chain:
    #    protherm_included.append(invprotMut)

    #if actNoValue in no_value:
    #    protherm_included_novalue.append(actNoValue)

unique_data = list()
for act in no_chain:
    if not act in protherm_included:
        if not act in unique_data:
            unique_data.append(act)

#unique_data = list()
#for act in no_value:
#    if not act in protherm_included_novalue:
#        if not act in unique_data:
#            unique_data.append(act)

output = open("./data/varibench/allData_filtered.csv", "w")
derived = open("./data/varibench/allData_derived.csv", "w")

#for act in unique_data:
#    print(str(no_chain[act]) + ":" + str(mutations_dict[no_chain[act]]))

counter = 1
counterDR = 1
for actMut in unique_data:
    uniID = ""
    mut = no_chain[actMut]
    sources = ""
    for source in mutations_dict[mut]:
        sources += source + ";"
    sources = sources[0:-1]
    parts = mut.split(",")
    if uniprot_ids[parts[0]] != None:
        uniID = uniprot_ids[parts[0]]
    if sources == "potapov_with_uniprot_mapping":
        derived.write("DR" + format(counterDR, '05') + ",,," + uniID + "," + parts[0] + "," + parts[1] + ",," + parts[2] + "," + sources + "," + str(parts[3]) + "," + str(parts[4]) + "," + str(parts[6]) + ",,,,,,,,,," + str(parts[5]) + "\n")
        counterDR += 1
        #derived.write(mut + "," + sources + "\n")
    else:
        #output.write(mut + "," + sources + "\n")
        output.write("VB" + format(counter, '05') + ",,," + uniID + "," + parts[0] + "," + parts[1] + ",," + parts[2] + "," + sources + "," + str(parts[3]) + "," + str(parts[4]) + "," + str(parts[6]) + ",,,,,,,,,," + str(parts[5]) + "\n")
        counter += 1

errCounter = 1
for error in errors_list:
    uniID = ""
    for source in mutations_dict[mut]:
        sources += source + ";"
    sources = sources[0:-1]
    parts = error.split(",")
    if uniprot_ids[parts[0]] != None:
        uniID = uniprot_ids[parts[0]]
    dataStore.write("ER" + format(errCounter, '05') + ",,," + uniID + "," + parts[0] + "," + parts[1] + ",," + parts[2] + "," + sources + "," + str(parts[3]) + "," + str(parts[4]) + "," + str(parts[6]) + ",,,,,,,,,," + str(parts[5]) + "\n")
    errCounter += 1


#print("right: " + str(right))
#print("opossite: " + str(oposite))

#for err in errors_dict:
#    print(err + ": " + str(errors_dict[err]))
