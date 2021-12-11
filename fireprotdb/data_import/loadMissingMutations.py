import re

from fireprotdb.importer.db import *
from fireprotdb.importer.utils.pdb import load_pdb
from fireprotdb.importer.utils.uniprot import UniprotObsoleteMapping

uniprot_mappings = UniprotObsoleteMapping({
    ("P51698", "1MJ5"): "D4Z2G1",
})

data = open("./data/varibench/allMuts.csv")

problems = list()
mutations = dict()

for line in data.readlines():
    line = line.strip()
    parts = re.split(",", line)

    if parts[0] in mutations:
        mutations[parts[0]].append([parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7]])
    else:
        mutations[parts[0]] = list()
        mutations[parts[0]].append([parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7]])

session = Session()
vbCounter = 0
for pdb in mutations:
    try:
        chainSeqMap = dict()
        bio_id = session.query(BiologicalUnit).filter(BiologicalUnit.pdb_id == pdb).first()
        if not bio_id:
            continue
        seqID = session.query(ProteinSequenceBiologicalUnit).filter(ProteinSequenceBiologicalUnit.bio_unit_id == bio_id.bio_unit_id)
        if seqID.count() == 0:
            continue
        else:
            for actSeq in seqID:
                chainSeqMap[actSeq.new_chain] = actSeq.sequence_id

        print(chainSeqMap)

        seqs, uniprot_data, struct_index_mapping, missing_residues, seqres = load_pdb(pdb, uniprot_mappings)

        for mut in mutations[pdb]:
            chain = mut[0]
            if chain == "@":
                chain = "A"
            if chain == "":
                chain = "A"
            position = int(mut[2])
            try:
                position = struct_index_mapping[chain][int(mut[2])]
            except Exception as e:
                problems.append(pdb + "," + str(mutations[pdb]))
                continue

            ref = mut[1]
            alt = mut[3]
            ddg = ""
            dtm = ""
            actddg = re.findall(r"[-+]?\d*\.\d+|\d+", mut[4])
            if len(actddg) > 0:
                ddg = float(actddg[0])
            actdtm = re.findall(r"[-+]?\d*\.\d+|\d+", mut[5])
            if len(actdtm) > 0:
                dtm = float(actdtm[0])
            if ddg != "":
                ddg = ddg * float(mut[6])

            if not chain in chainSeqMap:
                continue

            newRes = session.query(Residue).filter(Residue.sequence_id==chainSeqMap[chain]).filter(Residue.position==position).first()
            if not newRes:
                newRes = Residue(position=position, residue=seqs[chain][position], sequence_id=chainSeqMap[chain])
                session.add(newRes)
                session.commit()

            newMut = session.query(Mutation).filter(Mutation.sequence_id==chainSeqMap[chain]).filter(Mutation.position==position).filter(Mutation.mutated_aa==alt).first()
            if not newMut:
                newMut = Mutation(position=position, sequence_id=chainSeqMap[chain], mutated_aa=alt)
                session.add(newMut)
                session.commit()

            mutex = session.query(MutationExperiment).filter(MutationExperiment.mut_id==newMut.mut_id).first()
            if mutex:
                continue
                if mutex.ddg == ddg and mutex.d_tm == dtm:
                    continue
                elif mutex.ddg == ddg and mutex.d_tm != None:
                    mutex.d_tm = dtm
                    session.commit()
                elif mutex.ddg != None and mutex.d_tm == dtm:
                    mutex.ddg = ddg
                    session.commit()
                else:
                    if ddg != "" and dtm != "":
                        mutex = MutationExperiment(mut_id=newMut.mut_id, ddg=ddg, d_tm=dtm, currated=0, experiment_id="VB"+str(vbCounter))
                    elif ddg != "":
                        mutex = MutationExperiment(mut_id=newMut.mut_id, ddg=ddg, currated=0, experiment_id="VB"+str(vbCounter))
                    else:
                        mutex = MutationExperiment(mut_id=newMut.mut_id, d_tm=dtm, currated=0, experiment_id="VB"+str(vbCounter))
                    session.add(mutex)
                    session.commit()
                    vbCounter += 1
            else:
                if ddg != "" and dtm != "":
                    mutex = MutationExperiment(mut_id=newMut.mut_id, ddg=ddg, d_tm=dtm, currated=0, experiment_id="VB"+str(vbCounter))
                elif ddg != "":
                    mutex = MutationExperiment(mut_id=newMut.mut_id, ddg=ddg, currated=0, experiment_id="VB"+str(vbCounter))
                else:
                    mutex = MutationExperiment(mut_id=newMut.mut_id, d_tm=dtm, currated=0, experiment_id="VB"+str(vbCounter))
                session.add(mutex)
                session.commit()
                vbCounter += 1
    except:
        problems.append(pdb + "," + str(mutations[pdb]))

print("number of problems: " + str(len(problems)))