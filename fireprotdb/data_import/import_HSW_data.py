import os,glob, requests,re, glob
import xml.etree.ElementTree as ET

from fireprotdb.importer.db import *
from fireprotdb.importer.db.bottleneck import Bottleneck
from fireprotdb.importer.db.bottleneck_residues import BottleneckResidues
from fireprotdb.importer.db.btc_annotations import BTCAnnotation
from fireprotdb.importer.db.catalytic_annotations import CalatyticAnnotations
from fireprotdb.importer.db.catalytic_pocket import CalatyticPocket
from fireprotdb.importer.db.hsw_jobs import HSWJobs
from fireprotdb.importer.db.protein_tunnels import ProteinTunnels
from fireprotdb.importer.db.protein_tunnels_catalytic_pocket import CalatyticPocketTunnel
from fireprotdb.importer.db.residues_catalytic_pocket import ResiduesCalatyticPocket
from fireprotdb.importer.db.residues_correlation import CorrelatedResidues
from fireprotdb.importer.utils import cache_file, data_file
from fireprotdb.importer.utils.pdb import load_pdb
from fireprotdb.importer.utils.uniprot import UniprotObsoleteMapping, fetch_uniprot_entry
from Bio import pairwise2

aa = ["A", "R", "N", "D", "C", "E", "Q", "G", "H", "I", "L", "K", "M", "F", "P", "S", "T", "W", "Y", "V"]
trans = {"Ala":"A","Arg":"R","Asn":"N","Asp":"D","Cys":"C","Glu":"E","Gln":"Q","Gly":"G","His":"H","Ile":"I","Leu":"L",
         "Lys":"K","Met":"M","Phe":"F","Pro":"P","Ser":"S","Thr":"T","Trp":"W","Tyr":"Y","Val":"V"}

uniprot_mappings = UniprotObsoleteMapping({
    ("P51698", "1MJ5"): "D4Z2G1",
})

problematic_res = list()

def convert_indexes(act, indexes):
    for index in indexes:
        if int(index[0]) == int(act):
            return index[1]
    return None

session = Session()
#HSW jobs
hswJobs = list()
ids = open("./cache/actPDB2.txt")
for line in ids.readlines():
    line = line[0:-1]
    parts = re.split("\\s+", line)
    if len(parts) == 3:
        hswJobs.append([parts[0], parts[1], parts[2]])

for job in hswJobs:
    bio_unit = session.query(BiologicalUnit).filter(BiologicalUnit.pdb_id == job[0])[0]
    data = session.query(HSWJobs).filter(HSWJobs.hsw_job_id==job[2])
    if data.count() == 0:
        newJob = HSWJobs(hsw_job_id=job[2], bio_unit_id= bio_unit.bio_unit_id)
        session.add(newJob)
        session.commit()

ids = open("./cache/actPDB2.txt")
problems = list()
for line in ids.readlines():
    line = line[0:-1]
    parts = re.split(" ", line)
    pdb_id = parts[0]

    try:
        tree = ET.parse("./cache/hswJana/" + parts[0] + "/results.xml")
    except:
        problems.append(parts[0] + ": Unable to open HSW results (file does not exist)")
        continue

    root = tree.getroot()

    residuesData = list()
    correlatedData = list()
    pocketData = list()
    tunnelData = dict()
    indexes_struct_seq = dict()
    indexes_seq_struct = dict()
    catPockets = list()
    btcMutations = list()
    pocketList = list()

    for chains in root.findall("chains"):
        for chain in chains.findall("chain"):
            actChain = chain.attrib["id"]

            index_convert = list()
            counter1 = 0
            counter2 = 0
            actSequence = chain.find("sequence").text

            pdb_seq_id = session.query(StructureSequence).filter(StructureSequence.pdb_id==parts[0]).filter(StructureSequence.chain==actChain)
            if pdb_seq_id.count() == 0:
                pdb_seq_id = session.query(StructureSequence).filter(StructureSequence.pdb_id == parts[0])
                if pdb_seq_id.count() == 0:
                    problems.append(pdb_id + ": sequence not found in the database")
                    continue
                else:
                    problems.append("(Warning) " + pdb_id + ": unable to map structure with the specified chain")

            seq_uni_row = session.query(ProteinSequence).filter(ProteinSequence.sequence_id==pdb_seq_id.first().sequence_id)
            if seq_uni_row.count() != 0:
                alignment = pairwise2.align.globalxx(actSequence, seq_uni_row.first().protein_seq)
                orig = alignment[0][0]
                uniseq = alignment[0][1]
                for i in range(len(orig)):
                    if orig[i] != '-':
                        counter1 += 1
                    if uniseq[i] != '-':
                        counter2 += 1
                    index_convert.append([counter1 if orig[i] != '-' else -1, counter2 if uniseq[i] != '-' else -1])
            else:
                problems.append(pdb_id + ": no uniprot sequence to map to")
                continue

            actCatPocket = chain.find("catalyticPocket")
            if actCatPocket != None:
                catPockets.append(actCatPocket.text)

            actCorrelatedData = list()
            actCorrelated = chain.find("correlatedMutations")
            if actCorrelated != None:
                for cor in actCorrelated.findall("pair"):
                    pos1 = convert_indexes(cor.attrib["pos1"], index_convert)
                    pos2 = convert_indexes(cor.attrib["pos2"], index_convert)
                    if pos1 == None or pos2 == None:
                        problems.append(pdb_id + ": cannot find index for correlated residues - " + str(cor.attrib["pos1"]) + "," + str(cor.attrib["pos2"]))
                        continue
                    sum = 0.
                    num = 0.
                    for sc in cor.findall("score"):
                        sum += float(sc.attrib["zScore"])
                        num += 1
                    actCorrelatedData.append([pos1, pos2, str(float(sum) / float(num))])
            correlatedData.append([actChain, actCorrelatedData])

            for residues in chain.findall("residues"):
                for residue in residues.findall("residue"):
                    index_seq = convert_indexes(residue.attrib["index"], index_convert)
                    if index_seq == None:
                        problems.append(pdb_id + ": unable to map pdb and uniprot sequence index on position " + str(residue.attrib["index"]))
                        continue
                    try:
                        index_struct = residue.attrib["structIndex"]
                    except:
                        problems.append(pdb_id + ": unable to obtain structural index at position " + str(residue.attrib["index"]))
                        continue
                    indexes_seq_struct[index_seq] = index_struct
                    indexes_struct_seq[index_struct] = index_seq
                    if not residue.attrib["chemComp"] in trans:
                        comp = 'X'
                    else:
                        comp = trans[residue.attrib["chemComp"]]
                    try:
                        secondary_structure = str(residue.find("secondaryStructure").text)
                    except:
                        secondary_structure = None
                    try:
                        asa = str(residue.find("asa").text)
                    except:
                        asa = None
                    try:
                        bfactor = str(residue.find("averageBFactor").text)
                    except:
                        bfactor = 0.
                    try:
                        conservation = str(10 - int(residue.find("mutability").attrib["grade"]))
                    except:
                        conservation = None

                    actBTC = residue.find("backToConsensus")
                    if actBTC:
                        for act in actBTC.findall("mutation"):
                            btcMutations.append([chain, index_seq, act.attrib["byMajority"], act.attrib["byRatio"], act.text])

                    pockets = list()
                    pocketTag = residue.find("pockets")
                    if pocketTag:
                        for act in pocketTag.findall("pocket"):
                            pockets.append(act.attrib["id"])
                    tunnels = list()
                    tunnelTag = residue.find("tunnels")
                    if tunnelTag:
                        for act in tunnelTag.findall("tunnel"):
                            tunnels.append([act.attrib["set"], act.attrib["id"], act.attrib["isBottleneck"], act.attrib["includesSidechain"]])

                    annotationsData = list()
                    annotTag = residue.find("functionAnnotations")
                    if annotTag:
                        annotations = annotTag.findall("annotation")
                        for annotation in annotations:
                            ac = annotation.attrib["ac"]
                            source = annotation.attrib["source"]
                            identity = annotation.attrib["identity"]
                            type = annotation.find("type").text
                            try:
                                description = annotation.find("description").text
                            except:
                                description = ""
                            neighbourhood = annotation.find("neighbourhood").find("query").text
                            annotationsData.append([source,ac,identity,type,description,neighbourhood])

                    residuesData.append([actChain, index_struct, index_seq, comp, secondary_structure, asa, bfactor, conservation, pockets, tunnels, annotationsData])

    for pocket in root.find("pockets").findall("pocket"):
        id = pocket.attrib["id"]
        score = pocket.find("score").text
        area = pocket.find("area").text
        volume = pocket.find("volume").text
        drug = pocket.find("druggability").text

        if id in catPockets:
            pocketData.append([id, score, area, volume, drug])
            pocketList.append(id)

    for set in root.find("tunnels").findall("set"):
        setid = set.attrib["id"]
        startingPocket = set.attrib["startingPocket"]
        startingPointX = set.attrib["startingPointX"]
        startingPointY = set.attrib["startingPointY"]
        startingPointZ = set.attrib["startingPointZ"]
        setData = list()
        for tunnel in set.findall("tunnel"):
            id = tunnel.attrib["id"]
            throughput = tunnel.find("throughput").text
            cost = tunnel.find("cost").text
            curvature = tunnel.find("curvature").text
            length = tunnel.find("length").text
            bottleneck = tunnel.find("bottleneck").text
            centerPoint = list()
            for point in tunnel.find("centerline").findall("point"):
                centerPoint.append([point.attrib["distance"], point.attrib["length"], point.attrib["order"], point.attrib["radius"], point.attrib["x"], point.attrib["y"], point.attrib["z"]])
            setData.append([id, throughput, cost, curvature, length, bottleneck, centerPoint])
        tunnelData[setid] = [startingPocket, startingPointX, startingPointY, startingPointZ, setData]

    bio_unit = session.query(BiologicalUnit).filter(BiologicalUnit.pdb_id==pdb_id).first().bio_unit_id
    sequence_id = session.query(ProteinSequenceBiologicalUnit).filter(ProteinSequenceBiologicalUnit.bio_unit_id==bio_unit).filter(ProteinSequenceBiologicalUnit.old_chain==actChain)
    if sequence_id.count() == 0:
        problems.append(pdb_id + ": not able to map bio unit with the corresponding chain")
        sequence_id = session.query(ProteinSequenceBiologicalUnit).filter(ProteinSequenceBiologicalUnit.bio_unit_id == bio_unit)
        if sequence_id.count() == 0:
            problems.append(pdb_id + ": not able to map bio unit to the target sequence")
    seq_id = sequence_id.first().sequence_id

    #filling up residues table
    for residue in residuesData:
        data = session.query(Residue).filter(Residue.sequence_id==seq_id).filter(Residue.position==residue[2])
        if data.count() == 0:
            if residue[7] != None and residue[7].isnumeric():
                actRes = Residue(position=residue[2], sequence_id=seq_id, residue=residue[3], conservation=residue[7])
            else:
                actRes = Residue(position=residue[2], sequence_id=seq_id, residue=residue[3])
            session.add(actRes)
            session.commit()
        else:
            if residue[7] != None and residue[7].isnumeric():
                if data.first().residue != residue[3]:
                    problems.append(pdb_id + "missmatch in residues, position " + str(data.first().position) + " " + str(data.first().residue) + " " + str(residue[3]))
                session.query(Residue).filter(Residue.sequence_id==seq_id).filter(Residue.position==residue[2]).update({Residue.conservation: residue[7]})
                session.commit()

    #filling up structure residues
    for residue in residuesData:
        data = session.query(StructureResidue).filter(StructureResidue.sequence_id==seq_id).filter(StructureResidue.position==residue[2]).filter(StructureResidue.bio_unit_id==bio_unit)
        if data.count() == 0:
            actRes = StructureResidue(position=residue[2],bio_unit_id=bio_unit, sequence_id=seq_id, b_factor=residue[6], secondary_structure=residue[4], asa=residue[5], structure_index=residue[1], new_chain=residue[0], insertion_code="NULL")
            session.add(actRes)
            session.commit()
        else:
            session.query(StructureResidue).filter(StructureResidue.sequence_id == seq_id).filter(StructureResidue.position == residue[2]).filter(StructureResidue.bio_unit_id == bio_unit).update({StructureResidue.b_factor: residue[6], StructureResidue.secondary_structure: residue[4], StructureResidue.asa: residue[5], StructureResidue.structure_index: residue[1], StructureResidue.new_chain: residue[0], StructureResidue.insertion_code: "NULL"})
            session.commit()

    #filling up pockets
    for pocket in pocketData:
        data = session.query(CalatyticPocket).filter(CalatyticPocket.pocket_id==pocket[0]).filter(CalatyticPocket.bio_unit_id==bio_unit)
        if data.count() == 0:
            newPocket = CalatyticPocket(pocket_id=pocket[0], bio_unit_id=bio_unit, relevance=pocket[1], volume=pocket[3], drugability=pocket[4])
            session.add(newPocket)
            session.commit()

    #filling up residues_pockets
    for residue in residuesData:
        position = residue[2]
        for pocket in residue[8]:
            if not pocket in pocketList:
                continue
            data = session.query(ResiduesCalatyticPocket).filter(ResiduesCalatyticPocket.pocket_id == pocket).filter(ResiduesCalatyticPocket.sequence_id == seq_id).filter(ResiduesCalatyticPocket.position == position)
            if data.count() == 0:
                newPocket = ResiduesCalatyticPocket(position=position, pocket_id=pocket, sequence_id=seq_id)
                session.add(newPocket)
                session.commit()

    #filling up annotations
    for residue in residuesData:
        position = residue[2]
        for annotation in residue[10]:
            data = session.query(CalatyticAnnotations).filter(CalatyticAnnotations.position == position).filter(CalatyticAnnotations.sequence_id == seq_id).filter(CalatyticAnnotations.accesion_code == annotation[1])
            if data.count() == 0:
                newAnnotation = CalatyticAnnotations(source=annotation[0], accesion_code=annotation[1], identity=annotation[2], description=annotation[4], type=annotation[3], neighbourhood=annotation[5], position=position, sequence_id=seq_id)
                session.add(newAnnotation)
                session.commit()

    #tunnels + bottleneck
    data = session.query(CalatyticPocketTunnel).filter(CalatyticPocketTunnel.bio_unit_id==bio_unit)
    if data.count() == 0:
        for set in tunnelData:
            actSet = tunnelData[set]
            stPocket = actSet[0]
            stX = actSet[1]
            stY = actSet[2]
            stZ = actSet[3]
            for tunnel in actSet[4]:
                priority = tunnel[0]
                throughput = tunnel[1]
                cost = tunnel[2]
                curvature = tunnel[3]
                length = tunnel[4]
                bottleneck = tunnel[5]
                bottleneckPos = None
                for point in tunnel[6]:
                    if bottleneckPos == None:
                        bottleneckPos = point
                    else:
                        if float(bottleneckPos[3]) > float(point[3]):
                            bottleneckPos = point
                residues = list()
                for residue in residuesData:
                    for tn in residue[9]:
                        if tn[1] == priority and tn[0] == set and tn[2] == "true":
                            if(tn[3] == "true"):
                                residues.append([residue[2], 1])
                            else:
                                residues.append([residue[2], 0])
                newTunnel = ProteinTunnels(priority=priority, throughput=throughput, distance_to_surface=cost, length=length, curvature=curvature)
                session.add(newTunnel)
                session.commit()

                newBottleneck = Bottleneck(tunnel_id=newTunnel.tunnel_id, radius=bottleneckPos[3], x_coord=bottleneckPos[4], y_coord=bottleneckPos[5], z_coord=bottleneckPos[6], ball_number=bottleneckPos[2])
                session.add(newBottleneck)
                session.commit()

                for res in residues:
                    data = session.query(BottleneckResidues).filter(BottleneckResidues.tunnel_id == newTunnel.tunnel_id).filter(BottleneckResidues.position==res[0]).filter(BottleneckResidues.sequence_id==seq_id).filter(BottleneckResidues.bottleneck_id==newBottleneck.bottleneck_id)
                    if data.count() == 0:
                        newBottleResidue = BottleneckResidues(bottleneck_id=newBottleneck.bottleneck_id, tunnel_id=newTunnel.tunnel_id, position=res[0], sequence_id=seq_id, sidechain=res[1])
                        session.add(newBottleResidue)
                session.commit()

                newTunnelPocket = CalatyticPocketTunnel(tunnel_id=newTunnel.tunnel_id, pocket_id=stPocket, bio_unit_id=bio_unit, x_start=stX, y_start=stY, z_start=stZ)
                session.add(newTunnelPocket)
                session.commit()

   #filling up btc mutations
    for btc in btcMutations:
        majority = False
        ratio = False
        if btc[2] == "true":
            majority = True
        if btc[3] == "true":
            ratio = True
        actMut = (session.query(Mutation)).filter(Mutation.position==btc[1]).filter(Mutation.sequence_id==seq_id).filter(Mutation.mutated_aa==btc[4])
        try:
            if actMut.count() == 0:
                actMut = Mutation(position=btc[1], mutated_aa=btc[4], sequence_id=seq_id)
                session.add(actMut)
                session.commit()
            else:
                actMut = actMut.first()
            actBTC = (session.query(BTCAnnotation)).filter(BTCAnnotation.mut_id==actMut.mut_id).filter(BTCAnnotation.frequency==majority).filter(BTCAnnotation.ratio==ratio)
            if actBTC.count() == 0:
                actBTC = BTCAnnotation(mut_id=actMut.mut_id, frequency=majority, ratio=ratio)
                session.add(actBTC)
                session.commit()
        except:
            problems.append(pdb_id + ": unable to map btc mutation")
            session.rollback()

    #filling up correlated table
    for correlated in correlatedData:
        for cor in correlated[1]:
            try:
                data = (session.query(CorrelatedResidues)).filter(CorrelatedResidues.position==cor[0]).filter(CorrelatedResidues.position2==cor[1]).filter(CorrelatedResidues.sequence_id==seq_id)
                dataInv = (session.query(CorrelatedResidues)).filter(CorrelatedResidues.position==cor[1]).filter(CorrelatedResidues.position2==cor[0]).filter(CorrelatedResidues.sequence_id==seq_id)
                if data.count() == 0 and dataInv.count() == 0:
                    corRes = CorrelatedResidues(position=cor[0], position2=cor[1], sequence_id=seq_id, correlation_consensus=cor[2])
                    session.add(corRes)
                    session.commit()
            except:
                problems.append(pdb_id + ": unable to map correlated mutation")
                session.rollback()


errors = open("./data/errors_list.txt", "w")
for prob in problems:
    errors.write(prob + "\n")