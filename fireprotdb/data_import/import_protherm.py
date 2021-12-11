import sys
import csv
import yaml

from fireprotdb.importer.db import *
from fireprotdb.importer.utils import cache_file, data_file
from fireprotdb.importer.utils.pubmed import explodepub
from fireprotdb.importer.utils.uniprot import fetch_uniprot_entry

FIREPROT_DATASET_PUBLICATION = {
    "authors": [
        {
            "fore_name": "Milos",
            "initials": "M",
            "last_name": "Musil",
        },
        {
            "fore_name": "Jan",
            "initials": "J",
            "last_name": "Stourac",
        },
        {
            "fore_name": "Jaroslav",
            "initials": "J",
            "last_name": "Bendl",
        },
        {
            "fore_name": "Jan",
            "initials": "J",
            "last_name": "Brezovsky",
        },
        {
            "fore_name": "Zbynek",
            "initials": "Z",
            "last_name": "Prokop",
        },
        {
            "fore_name": "Jaroslav",
            "initials": "J",
            "last_name": "Zendulka",
        },
        {
            "fore_name": "Tomas",
            "initials": "T",
            "last_name": "Martinek",
        },
        {
            "fore_name": "David",
            "initials": "D",
            "last_name": "Bednar",
        },
        {
            "fore_name": "Jiri",
            "initials": "J",
            "last_name": "Damborsky",
        },
    ],
    "issue": '45',
    "journal": "Nucleic Acids Research",
    "pages": "W393-9",
    "pubmed": "28449074",
    "title": "FireProt: web server for automated design of thermostable proteins",
    "year": "2017",
    "volume": "",
    "doi": "10.1093/nar/gkx285"
}

UNMAPPED_PDBS = [
    #Structure(pdb_id="1FLV", method="X-ray", resolution=2.0),
    Structure(pdb_id="1FTG", method="X-ray", resolution=2.0)
]


def explodeall(type, protherm):
    return set([explode(type, m) for m in protherm])

def explode(type, m):
    seq = m['sequence']
    pos = m['uniprot_index']
    struct_pos = m['struct_index']
    uniprot_id = m['uniprot_id']
    uniprot_id_orig = m['uniprot_id_orig']
    protein_name = m['protein_name']
    organism = m['organism']
    ec_number = m['ec_number']
    pdb_id = m['pdb_id']
    chain = m['chain']
    wt = m['wt']
    mut = m['mut']

    if type == "seq":
        return (seq, uniprot_id, protein_name, organism, ec_number)
    elif type == "uniprot":
        return (seq, uniprot_id)
    elif type == "uniprot_orig":
        return (seq, uniprot_id_orig)
    elif type == "pdbid":
        return (pdb_id)
    elif type == "pdb":
        return (pdb_id, seq, chain)
    elif type == "res":
        return (seq, pos, wt)
    elif type == "structres":
        return (pdb_id, seq, pos, struct_pos, chain)
    elif type == "mut":
        return (seq, pos, mut)
    elif type == "pub":
        return explodepub(type, m['publication'])
    elif type == "pubid":
        return explodepub(type, m['publication'])
    else:
        return None


def import_protherm():
    with open(cache_file('protherm.yaml')) as fh:
        protherm = yaml.safe_load(fh)

    with open(data_file('fireprot_dataset.csv')) as fh:
        reader = csv.reader(fh)
        fireprot_dataset = [r for r in reader]

    session = Session()

    seqs = {seq: ProteinSequence(protein_seq=seq, protein_name=protein_name, uniprot_id=uniprot_id, species=organism, ec_number=ec_number) for (seq, uniprot_id, protein_name, organism, ec_number) in explodeall("seq", protherm)}

    session.add_all(seqs.values())
    session.commit()

    uniprot_mappings = {uniprot_id_orig: Uniprot(uniprot_id=uniprot_id_orig, sequence_id=seqs[seq].sequence_id, relation='superseded') for (seq, uniprot_id_orig) in explodeall("uniprot_orig", protherm) if uniprot_id_orig}

    session.add_all(uniprot_mappings.values())
    session.commit()

    uniprot_pdb_mappings = {(seq): fetch_uniprot_entry(uniprot_id) for (seq, uniprot_id) in explodeall("uniprot", protherm)}

    protherm_pdbs = explodeall("pdbid", protherm)
    pdbs = {}
    pdb_seq_mapping = []
    for (seq, uniprot_entry) in uniprot_pdb_mappings.items():
        for (pdb_id, pdb_entry) in uniprot_entry['pdb_mapping'].items():
            if pdb_id not in pdbs:
                if pdb_id in protherm_pdbs:
                    protherm_pdbs.remove(pdb_id)
                pdbs[pdb_id] = Structure(pdb_id=pdb_id, method=pdb_entry['method'], resolution=pdb_entry['resolution'])
            pdb_seq_mapping += [StructureSequence(pdb_id=pdb_id, sequence_id=seqs[seq].sequence_id, chain=pdb_chain) for pdb_chain in pdb_entry['chains'].keys()]

    session.add_all(UNMAPPED_PDBS)
    for p in UNMAPPED_PDBS:
        if p.pdb_id in protherm_pdbs:
            protherm_pdbs.remove(p.pdb_id)

    if len(protherm_pdbs) > 0:
        print(protherm_pdbs)
        sys.exit(1)

    session.add_all(pdbs.values())
    session.commit()
    session.add_all(pdb_seq_mapping)
    session.commit()

    bio_units = {pdb_id: BiologicalUnit(pdb_id=pdb_id, pdb_unit_number=1) for pdb_id in explodeall("pdbid", protherm)}

    session.add_all(bio_units.values())
    session.commit()

    pdb_mappings = {(pdb_id, seq): ProteinSequenceBiologicalUnit(sequence_id=seqs[seq].sequence_id, bio_unit_id=bio_units[pdb_id].bio_unit_id, old_chain=chain, new_chain=chain) for (pdb_id, seq, chain) in explodeall("pdb", protherm)}

    session.add_all(pdb_mappings.values())
    session.commit()

    res = {(seq, pos): Residue(position=pos, sequence_id=seqs[seq].sequence_id, residue=wt) for (seq, pos, wt) in explodeall("res", protherm)}

    session.add_all(res.values())
    session.commit()

    struct_res = {(seq, struct_pos): StructureResidue(bio_unit_id=bio_units[pdb_id].bio_unit_id, position=res[(seq, pos)].position, sequence_id=seqs[seq].sequence_id, structure_index=struct_pos, new_chain=chain) for (pdb_id, seq, pos, struct_pos, chain) in explodeall("structres", protherm)}

    session.add_all(struct_res.values())
    session.commit()

    muts = {(seq, pos, mut): Mutation(position=res[(seq, pos)].position, sequence_id=seqs[seq].sequence_id, mutated_aa=mut) for (seq, pos, mut) in explodeall("mut", protherm)}

    session.add_all(muts.values())
    session.commit()

    pubs = {(pmid, doi): Publication(doi=doi, pmid=pmid, title=title, journal=journal, volume=volume, issue=issue, year=year, pages=pages) for (pmid, doi, title, journal, volume, issue, year, pages) in explodeall("pub", protherm)}

    pubs.update({(pmid, doi): Publication(doi=doi, pmid=pmid, title=title, journal=journal, volume=volume, issue=issue, year=year, pages=pages) for (pmid, doi, title, journal, volume, issue, year, pages) in [explodepub('pub', FIREPROT_DATASET_PUBLICATION)]})

    session.add_all(pubs.values())
    session.commit()

    authors = {}
    for m in protherm + [{"publication": FIREPROT_DATASET_PUBLICATION}]:
        for a in m['publication']['authors']:
            (last_name, fore_name, initials) = (a['last_name'], a['fore_name'], a['initials'])
            authors[(last_name, fore_name, initials)] = Author(last_name=last_name, fore_name=fore_name, initials=initials)

    authors[(last_name, fore_name, initials)] = Author(last_name=last_name, fore_name=fore_name, initials=initials)
    session.add_all(authors.values())
    session.commit()

    mapped_pubs = []
    for m in protherm + [{"publication" : FIREPROT_DATASET_PUBLICATION}]:
        pub = explodepub('pubid', m['publication'])
        if pub in mapped_pubs:
            continue
        author_order = 1
        for a in m['publication']['authors']:
            aid = (a['last_name'], a['fore_name'], a['initials'])
            session.add(AuthorsPublications(publication_id=pubs[pub].publication_id, author_id=authors[aid].author_id, author_order=author_order))
            author_order += 1
        mapped_pubs.append(pub)

    session.commit()

    exps = {}
    for m in protherm:
        mut = muts[explode("mut", m)]
        ddg = m['data']['ddG'] if m['data']['ddG'] else m['data']['ddG_H2O']
        if ddg:
            # !!!!IMPORTANT!!!! Store opposite value of ddG since ProTherm contains ddG of UNFOLDING (which is opposite value from FOLDING)
            ddg = -ddg

        for row in fireprot_dataset:
            if m['pdb_id'] == row[0].upper() and \
                    m['chain'] == row[1] and \
                    m['struct_index'] == int(row[2]) and \
                    m['wt'] == row[3] and \
                    m['mut'] == row[4] and \
                    ddg == float(row[5]):
                m['curated'] = True
                break

        dm = MutationExperiment(
            experiment_id="PT{:06d}".format(m['id']),
            mut_id=mut.mut_id,
            publication_id=pubs[explode('pubid', m)].publication_id,
            ddg=ddg,
            d_tm=m['data']['dTm'],
            tm=m['data']['Tm'],
            currated='curated' in m,
            pH=m['experimental_condition']['pH'],
            method=m['experimental_condition']['method']['name'],
            method_details=m['data']['method']['details'] if 'method' in m['experimental_condition']['method'] else m['experimental_condition']['method']['name'],
            technique=m['experimental_condition']['measure']['name'],
            technique_details=m['experimental_condition']['measure']['details'] if 'details' in m['experimental_condition']['measure'] else m['experimental_condition']['measure']['name'],
            notes=m['remarks']
        )
        exps[m['id']] = dm

    session.add_all(exps.values())

    fireprot_dataset = Dataset(name="FireProt", version="1", publication_id=pubs[explodepub('pubid', FIREPROT_DATASET_PUBLICATION)].publication_id)
    session.add(fireprot_dataset)

    session.commit()

    for m in protherm:
        if 'curated' in m and m['curated']:
            session.add(MutationExperimentDataset(experiment_id=exps[m['id']].experiment_id, dataset_id=fireprot_dataset.dataset_id))

    session.commit()

if __name__ == "__main__":
    import_protherm()