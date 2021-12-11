import re
from fireprotdb.importer.db import *
from fireprotdb.importer.utils.pdb import load_pdb
from fireprotdb.importer.utils.uniprot import UniprotObsoleteMapping, fetch_uniprot_entry

uniprot_mappings = UniprotObsoleteMapping({
    ("P51698", "1MJ5"): "D4Z2G1",
})

pdbList = list()
datasets = open("./data/varibench/data_collumns.csv")

#datFirst = True
#for line in datasets.readlines():
#    if datFirst:
#        datFirst = False
#        continue
#    line = line[0:-1]
#    parts = re.split(",", line)
#    if parts[17] != "0":
#        actData = open("./data/varibench/" + parts[0])
#        splitPDB = re.split(";", parts[1])
#        pdbCol = int(splitPDB[0])
#        pdbStart = None
#        pdbStop = None
#        if len(splitPDB) > 1:
#            print("jsem tu")
#            pdbStart = int(splitPDB[1])
#            pdbStop =int(splitPDB[2])
#
#        first = True
#        for row in actData.readlines():
#            row = row[0:-1]
#            if first:
#                first = False
#                continue
#            else:
#                pt = re.split(",", row)
#                if pdbStart == None:
#                    pdb = pt[pdbCol]
#                else:
#                    pdb = pt[pdbCol][pdbStart:pdbStop]
#                if not pdb in pdbList:
#                    pdb = pdb.upper().strip()
#                    if len(pdb) == 4:
#                        pdbList.append(pdb)

#missingPDBs = list()
#notMissingPDBs = list()
#session = Session()
#for pdb in pdbList:
#    act = session.query(BiologicalUnit).filter(BiologicalUnit.pdb_id == pdb).first()
#    if not act:
#        if not pdb in missingPDBs:
#            missingPDBs.append(pdb)
#    else:
#        if not pdb in notMissingPDBs:
#            notMissingPDBs.append(pdb)

#print(missingPDBs)
session = Session()
missingPDBs = ['1AQH', '1B5M', '1BCX', '1BRF', '1RRO', '1SSO', '1W4E', '3HHR', '1A2P', '1ANF', '1AYE', '1BF4', '1BKS', '1DYJ', '1FKB', '1FMK', '1FXA', '1HGU', '1HZ6', '1IFC', '1LMB', '1QHE', '1REX', '1YPC', '2CHF', '3CHY', '8TIM', '1WSY', '2MBP', '2BQA', '1SAR', '3GAP', '6ICB', 'DSBA', '1W4H', '1EM7', '1OTR', '1FGA', 'NULL', '1SPD', '1FYN', '1CIQ', '1KIV', '1WEJ', '2EZM', '2IFB', '1APS', '2IMM', '1A43', '1RHG', '1I5T', '1FRD', '1LVE', '2MM1', '1YEA', '2HID', '1KDX', 'CCBY', '1AEP', '1AKY', '1B8E', '1CSE', '1CUN', '1E65', '1EY0', '1FNA', '1FVK', '1G4I', '1HMK', '1HMS', '1HUU', '1IET', '1IHB', '1JIW', '1KCQ', '1KE4', '1LNI', '1LUC', '1PDO', '1QM4', '1QND', '1RG8', '1TTQ', '1TYV', '1YU5', '1YYJ', '1ZG4', '2A01', '2H61', '2NVH', '2OCJ', '2TRT', '3ECA', '3GLY', '3SIL', '5DFR', '5PTI', '1FNF', '1W3D', '1YYX', '2GB1', '1AVR', '1C52', '1C8C', '1E0W', '1ESF', '1EZM', '1F6R', '1FHL', '1G5A', '1GV5', '1JNX', '1KF2', '1KF3', '1KF5', '1LHM', '1T69', '1ZYM', '1ZDR', '2CNC', '2CTH', '2HBB', '2HIP', '2OV0', '3D2C', '3KS3', '3UUE', '4U2B', '1IOJ', '1N02']
#missingPDBs = ["1CQW","1BFG","1MJ5","1PX0","5ZYR","6G4B","1M21","6TQ3","1PGA","2O9P","6BQG","3TGL","4E5K","2JK2","1QGD","3PG4","3PG0","3VUB","1BTL","1BYW","1TUX","4FMP","1DPM","4ZLU","1ISP","1H0C","2UXY","1TDJ","2JIE","3WP4","1DDR","1SVX","1XPB","4EY2","6JHM"]

problems = list()
for pdb in missingPDBs:
    try:
        seqs, uniprot_data, struct_index_mapping, missing_residues, seqres = load_pdb(pdb, uniprot_mappings)

        newBioUnit = session.query(BiologicalUnit).filter(BiologicalUnit.pdb_id==pdb).first()
        if not newBioUnit:
            newBioUnit = BiologicalUnit(pdb_id=pdb, pdb_unit_number=1)
            session.add(newBioUnit)
            session.commit()

        for key in seqs:
            if key != " " and seqs[key] != " " and key != "" and seqs[key] != "":

                newSequence = None
                if not newSequence:
                    uniprotData = None
                    for unkey in uniprot_data:
                        if key in uniprot_data[unkey]:
                            uniprotData = fetch_uniprot_entry(unkey)
                            break
                    id = None
                    name = None
                    ec = None
                    organism = None
                    seq = None
                    method = ""
                    resolution = None
                    if uniprotData:
                        id = uniprotData.get("id_uniprot_id")
                        name = uniprotData.get("id_name")
                        ec = uniprotData.get("ec_number")
                        organism = uniprotData.get("id_organism")
                        mapping = uniprotData.get("pdb_mapping")
                        seq = uniprotData.get("id_sequence")
                        if mapping.get(pdb):
                            method = mapping.get(pdb).get("method")
                            resolution = mapping.get(pdb).get("resolution")

                    newSequence = session.query(ProteinSequence).filter(ProteinSequence.uniprot_id == id).first()
                    if not newSequence:
                        newSequence = ProteinSequence(uniprot_id=id, protein_name=name, species=organism, ec_number=ec, protein_seq=seq)
                        session.add(newSequence)
                    newStructure = session.query(Structure).filter(Structure.pdb_id==pdb).first()
                    if not newStructure:
                        newStructure = Structure(pdb_id=pdb, method=method, resolution=resolution)
                        session.add(newStructure)
                        session.commit()
            newSequenceBio = session.query(ProteinSequenceBiologicalUnit).filter(ProteinSequenceBiologicalUnit.bio_unit_id==int(newBioUnit.bio_unit_id)).filter(ProteinSequenceBiologicalUnit.sequence_id==int(newSequence.sequence_id)).first()
            if not newSequenceBio:
                newSequenceBio = ProteinSequenceBiologicalUnit(new_chain=key, old_chain=key, bio_unit_id=int(newBioUnit.bio_unit_id), sequence_id=int(newSequence.sequence_id))
                session.add(newSequenceBio)
                session.commit()

            for i in range(len(seqs[key])):
                newResidue = session.query(Residue).filter(Residue.sequence_id==int(newSequence.sequence_id)).filter(Residue.position==i).first()
                if not newResidue:
                    newResidue = Residue(position=i, residue=seqs[key][i], sequence_id=int(newSequence.sequence_id))
                    session.add(newResidue)
            session.commit()

    except Exception as e:
        problems.append(pdb)
        print(e)

print(problems)

