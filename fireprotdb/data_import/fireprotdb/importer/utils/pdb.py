import os
import re
import csv
import requests

from Bio.PDB import *

from fireprotdb.importer.utils import cache_file
from fireprotdb.importer.utils.uniprot import UniprotObsoleteMapping


def three_to_one(aa):
    try:
        return Polypeptide.three_to_one(aa)
    except:
        if aa == "DSN":
            return "S"
        return None


def fill_missing_residues(seqs, struct_index_mapping, missing_residues, chain, index_from, index_to):
    if chain not in missing_residues or not index_from or not index_to:
        return
    for i in range(index_from, index_to):
        if i in missing_residues[chain]:
            if not three_to_one(missing_residues[chain][i]):
                continue
            seqs[chain].append(missing_residues[chain][i])
            struct_index_mapping[chain][i] = len(seqs[chain])


def load_pdb(pdb_id, uniprot_mapping=UniprotObsoleteMapping()):
    entry_file = cache_file("pdb/{}.pdb".format(pdb_id))
    if not os.path.isfile(entry_file):
        req = requests.get("https://files.rcsb.org/download/{}.pdb".format(pdb_id))
        if req.status_code != 200:
            print("Error on entry {} with pdb id {}".format(k, pdb_id))
            return None
        with open(entry_file, 'w') as fh:
            fh.write(req.text)

    seqs = {}
    seqres = {}
    struct_index_mapping = {}
    ters = []
    uniprot_data = {}
    missing_residues = {}
    curr_resindex = None

    for line in open(entry_file):
        line = line.strip()
        if line.startswith("REMARK 465 "):
            m = re.match('^REMARK 465\\s+([A-Z]{3})\\s+([A-Z0-9])\\s+(\\d+)\\s*$', line)
            if not m:
                continue
            res = m.group(1)
            chain = m.group(2)
            pos = int(m.group(3))

            if chain not in missing_residues:
                missing_residues[chain] = {}

            missing_residues[chain][pos] = res
        if line.startswith("DBREF"):
            t = line.split()
            if t[5] != "UNP":
                continue
            chain = t[2]
            uid = uniprot_mapping.map(t[6], pdb_id)
            if uid not in uniprot_data:
                uniprot_data[uid] = []
            uniprot_data[uid].append(chain)
        if line.startswith("SEQRES"):
            t = line.split()
            chain = t[2]
            if chain not in seqres:
                seqres[chain] = []
            seqres[chain] += map(three_to_one, t[4:])
        if line.startswith("ATOM") or line.startswith("HETATM"):
            chain = line[21]
            resindex = int(line[22:26].strip())
            aa = line[17:20].strip()

            if chain in ters:
                continue

            if chain not in seqs:
                seqs[chain] = []

            if chain not in struct_index_mapping:
                struct_index_mapping[chain] = {}

            if curr_resindex == resindex:
                continue

            if not curr_resindex:
                fill_missing_residues(seqs, struct_index_mapping, missing_residues, chain, min(missing_residues[chain].keys()) if chain in missing_residues else resindex, resindex)
            elif resindex - curr_resindex > 1:
                fill_missing_residues(seqs, struct_index_mapping, missing_residues, chain, curr_resindex + 1, resindex)

            if not three_to_one(aa):
                continue
            seqs[chain].append(aa)
            struct_index_mapping[chain][resindex] = len(seqs[chain])
            curr_resindex = resindex
        if line.startswith("TER"):
            fill_missing_residues(seqs, struct_index_mapping, missing_residues, chain, curr_resindex, (max(missing_residues[chain].keys()) + 1) if chain in missing_residues else curr_resindex)
            ters.append(line[21])
            curr_resindex = None

    seqs = {c: "".join([three_to_one(r) for r in s if three_to_one(r)]) for (c, s) in seqs.items()}

    seqres = {c: "".join(map(lambda x: x if x else 'X', s)) for (c, s) in seqres.items()}

    return seqs, uniprot_data, struct_index_mapping, missing_residues, seqres


def fetch_pdb_metadata(pdb_id):
    url = "http://www.rcsb.org/pdb/rest/customReport.csv?pdbids={}&customReportColumns=structureTitle,resolution,experimentalTechnique,ecNo&service=wsfile&format=csv".format(pdb_id)
    prot_dict={}
    r = requests.get(url)
    if r.status_code != 200:
        print("Unable to fetch metadata about PDB {}".format(pdb_id))
        return None
    lines = [l for l in csv.reader(r.text.split("\n"), quotechar='"', delimiter=',', quoting=csv.QUOTE_ALL)]
    if len(lines) > 1:
        try:
            prot_dict['pdb_id'] = pdb_id
            prot_dict['structure_title'] = lines[1][2].strip('"')
            prot_dict['resolution'] = float(lines[1][3].strip('"')) if len(lines[1][3].strip('"')) > 0 else None
            exp_tech = lines[1][4].strip('"').upper()
            if exp_tech.startswith("X-RAY"):
                prot_dict['method'] = "X-ray"
            elif exp_tech == "ELECTRON MICROSCOPY":
                prot_dict['method'] = "EM"
            elif "NMR" in exp_tech:
                prot_dict['method'] = "NMR"
            else:
                prot_dict['method'] = None
        except:
            pass
    return prot_dict