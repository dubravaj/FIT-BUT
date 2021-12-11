import os,glob, requests,re, glob
import xml.etree.ElementTree as ET

from fireprotdb.importer.db import *
from fireprotdb.importer.db.bottleneck import Bottleneck
from fireprotdb.importer.db.bottleneck_residues import BottleneckResidues
from fireprotdb.importer.db.catalytic_annotations import CalatyticAnnotations
from fireprotdb.importer.db.catalytic_pocket import CalatyticPocket
from fireprotdb.importer.db.hsw_jobs import HSWJobs
from fireprotdb.importer.db.protein_tunnels import ProteinTunnels
from fireprotdb.importer.db.protein_tunnels_catalytic_pocket import CalatyticPocketTunnel
from fireprotdb.importer.db.residues_catalytic_pocket import ResiduesCalatyticPocket
from fireprotdb.importer.db.residues_correlation import CorrelatedResidues
from fireprotdb.importer.utils import cache_file, data_file
from fireprotdb.importer.utils.pdb import load_pdb
from fireprotdb.importer.utils.uniprot import UniprotObsoleteMapping

uniprot_mappings = UniprotObsoleteMapping({
    ("P51698", "1MJ5"): "D4Z2G1",
})

session = Session()

dataStore = open("./data/varibench/allMuts.csv", "w")

datasets = list()
pubs = open("./data/varibench/publications.csv")
data_names = dict()

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
        datasets.append([datafile, name, article, journal, volume, issue, year, pages, doi, pmid, authors])
        data_names[datafile] = name

for data in datasets:
    authDict = dict()
    for auth in data[10]:
        actAuthor = None
        act = session.query(Author).filter(Author.last_name == auth[0])
        if act.count() != 0:
            actAuthor = act[0]
        else:
            actAuthor = Author(last_name=auth[0], fore_name=auth[1], initials=auth[2])
            session.add(actAuthor)
            session.commit()
        authDict[auth[0]] = actAuthor

    actPublication = None
    act = session.query(Publication).filter(Publication.pmid==data[9])
    if act.count() != 0:
        actPublication = act[0]
    else:
        actPublication = Publication(title=data[2], journal=data[3], volume=data[4], issue=data[5], year=data[6], pages=data[7], doi=data[8], pmid=data[9])
        session.add(actPublication)
        session.commit()

    authCount = 0
    for auth in authDict:
        actAuthPub = session.query(AuthorsPublications).filter(AuthorsPublications.publication_id==actPublication.publication_id).filter(AuthorsPublications.author_id==authDict[auth].author_id)
        if actAuthPub.count() == 0:
            authPub = AuthorsPublications(publication_id=actPublication.publication_id, author_id=authDict[auth].author_id, author_order=authCount)
            session.add(authPub)
            session.commit()
            authCount += 1

    actDat = session.query(Dataset).filter(Dataset.name==data[1]).filter(Dataset.version=="1")
    if actDat.count() == 0:
        actDataset = Dataset(name=data[1], version="1", publication_id=actPublication.publication_id)
        session.add(actDataset)
        session.commit()

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
    if dataset[-1] == 0:
        continue

    data = open("./data/varibench/" + dataset[0])
    actDataset = session.query(Dataset).filter(Dataset.name == data_names[dataset[0]])
    if actDataset.count() == 0:
        continue

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

        chain = ""
        if chain_col != "":
            if chain_char != "":
                try:
                    chain = parts[chain_col][chain_char]
                except:
                    continue
            else:
                try:
                    chain = parts[chain_col]
                except:
                    continue
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

        actDDG = ""
        if ddg != "":
            actDDG = parts[ddg]
        actDTM = ""
        if dtm != "":
            actDTM = parts[dtm]

        dataStore.write(str(pdb) + "," + str(chain) + "," + str(ref) + "," + str(position) + "," + str(alt) + "," + str(actDDG) + "," + str(actDTM) + "," + str(int(dataset[10].strip())) + "\n")

        if chain != "":
            actSeq = session.query(StructureSequence).filter(StructureSequence.pdb_id==pdb).filter(StructureSequence.chain==chain)
        else:
            actSeq = session.query(StructureSequence).filter(StructureSequence.pdb_id==pdb)
        if actSeq.count() > 0:
            seq = actSeq[0].sequence_id
            seqPos = session.query(StructureResidue).filter(StructureResidue.sequence_id==seq).filter(StructureResidue.structure_index==position)
            if seqPos.count() > 0:
                actMut = session.query(Mutation).filter(Mutation.sequence_id==seq).filter(Mutation.position==seqPos[0].position)
                if actMut.count() > 0:
                    mutID = actMut[0].mut_id
                    actExp = session.query(MutationExperiment).filter(MutationExperiment.mut_id==mutID)
                    if actExp.count() > 0:
                        expID = actExp[0].experiment_id
                        testDataset = session.query(MutationExperimentDataset).filter(MutationExperimentDataset.experiment_id==expID).filter(MutationExperimentDataset.dataset_id==actDataset[0].dataset_id)
                        if testDataset.count() == 0:
                            newDataset = MutationExperimentDataset(experiment_id=expID, dataset_id=actDataset[0].dataset_id)
                            session.add(newDataset)
                            session.commit()
