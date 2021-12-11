import os
import re
import json
from Bio import pairwise2

from fireprotdb.importer.protherm.parsing import parse_protherm_input_to_entry
from fireprotdb.importer.utils import cache_file, data_file
from fireprotdb.importer.utils.alignment import align
from fireprotdb.importer.utils.pdb import load_pdb
from fireprotdb.importer.utils.pubmed import map_pubmed_ids
from fireprotdb.importer.utils.uniprot import UniprotObsoleteMapping, fetch_uniprot_entry

SINGLE_POINT_MUT_RE = r'(?P<wt>[A-Z]) *(?P<pos>[0-9]+) *(?P<mut>[A-Z]) *$'
SINGLE_POINT_MUT_PDB_RE = r'(?P<orig_wt>[A-Z]) *(?P<orig_pos>[0-9]+) *(?P<orig_mut>[A-Z]) *\(PDB: (?P<wt>[A-Z]) *(?P<pos>[0-9]+) *(?P<mut>[A-Z]).*\) *$'
uniprot_mappings = UniprotObsoleteMapping({
    ("P24991", "1A23"): "P0AEG4",
    ("P62990", "1AAR"): "P0CH28",
    ("P17670", "1IDS"): "P9WGE7",
    ("P62988", "1UBQ"): "P0CG48",
    ("P05082", "1ANK"): "P69441",
    ("P06143", "1CEY"): "P0AE67",
    ("Q9R782", "4BLM"): "P00808",
    ("P02928", "3MBP"): "P0AEX9",
    ("P61864", "1OTR"): "P0CG63",
    ("P02934", "1QJP"): "P0A910",
})
problematic_db_mappings = [
    # Mismatch in UniProt entry - different sequence between UniProt entry for PDB and entry
    ("1YEA", "P38909"),
    # Mismatch in UniProt entry - different sequence between UniProt entry for PDB and entry
    ("2IFB", "P55051"),
    # Mismatch in UniProt entry - different sequence between UniProt entry for PDB and entry
    ("1QU7", "P07017"),
    # Mismatch in UniProt entry - different sequence between UniProt entry for PDB and entry
    ("1I5T", "P62898"),
    # Mismatch in UniProt entry - different sequence between UniProt entry for PDB and entry
    ("1LVE", "P01607"),
    # Mismatch in UniProt entry - different sequence between UniProt entry for PDB and entry
    ("1SSO", "P39476"),
    # Mismatch in UniProt entry - different sequence between UniProt entry for PDB and entry
    ("1IOJ", "P08519"),
    # Mismatch in UniProt entry - different sequence between UniProt entry for PDB and entry
    ("1APS", "P07311"),
    # PDB has not UniProt entry, but sequences between PDB and UniProt entry cannot be aligned
    ("2IMM", "P01868"),
    # Mismatch in UniProt entry - different sequence between UniProt entry for PDB and entry
    ("1FXA", "P0A3D9"),
    # Mismatch in UniProt entry - different sequence between UniProt entry for PDB and entry -- can be fixed by replacing UniProt ID??
    ("1FRD", "P0A3D9"),
    # Mismatch in UniProt entry - different sequence between UniProt entry for PDB and entry
    ("1OTR", "P39940"),
    # Mismatch in UniProt entry - different sequence between UniProt entry for PDB and entry
    ("1CYO", "P00157"),
    # Mismatch in UniProt entry - different sequence between UniProt entry for PDB and entry
    ("2CI2", "Q40059"),
    # Mismatch in UniProt entry - different sequence between UniProt entry for PDB and entry
    ("1RHG", "P04141"),
    # Mismatch in UniProt entry - different sequence between UniProt entry for PDB and entry -- only two point mutant...maybe fixed??
    ("1OSI", "P61495"),
    # Mismatch in UniProt entry - different sequence between UniProt entry for PDB and entry
    ("1A43", "Q7LZZ4"),
    # Mismatch in UniProt entry - different sequence between UniProt entry for PDB and entry
    ("1B5M", "P00173"),
    # Mismatch in UniProt entry - different sequence between UniProt entry for PDB and entry
    ("1KDX", "Q92793"),
    # Mismatch in UniProt entry - different sequence between UniProt entry for PDB and entry
    ("1A2I", "P24092"),
    # Mismatch in UniProt entry - different sequence between UniProt entry for PDB and entry
    ("1W99", "Q75QQ5"),
    # Mismatch in UniProt entry - different sequence between UniProt entry for PDB and entry
    ("1RRO", "P02625"),
    # Mismatch in UniProt entry - different sequence between UniProt entry for PDB and entry -- maybe switch UniProt ID to P42773??
    ("1IHB", "P42771"),
    # Mismatch in UniProt entry - different sequence between UniProt entry for PDB and entry
    ("1FNF", "P02752"),
    # Mismatch in UniProt entry - different sequence between UniProt entry for PDB and entry
    ("1W4H", "P0AFG6"),
    # Very weird alignment even in RCSB PDB database (the sequence present is completely different)
    ("1N02", "P81180"),
    # Repetitions in UniProt sequence
    ("1AAR", "P0CH28"),
]

UNMAPPABLE_PUBS = {
    "J AM CHEM SOC  115,     8523-8526 (1993) PMID:": {
        "doi": "10.1021/ja00072a001",
        "title": "Phospholipase A2 engineering. 10. The aspartate...histidine catalytic diad also plays an important structural role",
        "journal": "Journal of the American Chemical Society",
        "volume": "115",
        "issue": "19",
        "year": "1993",
        "pages": "8523-6",
        "authors": [
            {
                "last_name": "Li",
                "fore_name": "Y",
                "initials": "Yishan"
            },
            {
                "last_name": "Tsai",
                "fore_name": "M D",
                "initials": "Ming Daw"
            }
        ],
    },
    "J MOL BIOL  224,       819-835 (1992)": {
        "pubmed": "1569559",
        "doi": "10.1016/0022-2836(92)90564-z",
        "title": "The folding of an enzyme. IV. Structure of an intermediate in the refolding of barnase analysed by a protein engineering procedure",
        "journal": "Journal of Molecular Biology",
        "volume": "224",
        "issue": "3",
        "year": "1992",
        "pages": "819-35",
        "authors": [
            {
                "last_name": "Matoushek",
                "fore_name": "A",
                "initials": "A"
            },
            {
                "last_name": "Serrano",
                "fore_name": "L",
                "initials": "L"
            },
            {
                "last_name": "Fersht",
                "fore_name": "AR",
                "initials": "AR"
            }
        ],
    },
    "PROTEIN SCI        6,   2196-2202 (1997)": {
        "pubmed": "9336842",
        "doi": "10.1002/pro.5560061014",
        "title": "DSC studies of the conformational stability of barstar wild-type",
        "journal": "Protein Science",
        "volume": "6",
        "issue": "10",
        "year": "1997",
        "pages": "2196-202",
        "authors": [
            {
                "last_name": "Matoushek",
                "fore_name": "A",
                "initials": "A"
            },
            {
                "last_name": "Serrano",
                "fore_name": "L",
                "initials": "L"
            },
            {
                "last_name": "Fersht",
                "fore_name": "AR",
                "initials": "AR"
            }
        ],
    },
}

MEASURE_MAPPING = {
    "Abs": {
        "name": "Absorbance",
    },
    "Abs, CD, Fluorescence": {
        "name": "Absorbance, CD, Fluorescence",
    },
    "Absorbance": {
        "name": "Absorbance",
    },
    "Absorption": {
        "name": "Absorbption",
    },
    "activity": {
        "name": "Activity",
    },
    "Activity": {
        "name": "Activity",
    },
    "Activity assay": {
        "name": "Activity",
    },
    "Anisotropy": {
        "name": "Anisotropy",
    },
    "ANS binding": {
        "name": "ANS binding",
    },
    "Capillary electrophoresis": {
        "name": "Capillary electrophoresis",
    },
    "CD": {
        "name": "CD",
    },
    "CD (far-UV)": {
        "name": "CD",
        "details": "CD (far-UV)",
    },
    "CD(far-UV)": {
        "name": "CD",
        "details": "CD (far-UV)",
    },
    "CD + Fluorescence": {
        "name": "CD, Fluorescence",
    },
    "CD, Fluorescence": {
        "name": "CD, Fluorescence",
    },
    "CD, Fluorescence, Thiol reactivity": {
        "name": "CD, Fluorescence, Thiol reactivity",
    },
    "CD (near-UV)": {
        "name": "CD",
        "details": "CD (near-UV)",
    },
    "CD(near-UV)": {
        "name": "CD",
        "details": "CD (near-UV)",
    },
    "Chromatography": {
        "name": "Chromatography",
    },
    "DSC": {
        "name": "DSC",
    },
    "DSMC": {
        "name": "DSMC",
    },
    "Emission": {
        "name": "Emission",
    },
    "enzyme activity": {
        "name": "Activity",
    },
    "Enzyme activity": {
        "name": "Activity",
    },
    "Enzyme assay": {
        "name": "Enzyme assay",
    },
    "EPR": {
        "name": "EPR",
        #"details": "Electron paramagnetic resonance"
    },
    "ESR": {
        "name": "ESR",
    },
    "far-UV CD": {
        "name": "CD",
        "details": "CD (far-UV)",
    },
    "Fluorescence": {
        "name": "Fluorescence",
    },
    "Fluorescence (ANS)": {
        "name": "Fluorescence",
        "details": "Fluorescence (ANS)",
    },
    "Fluorescence, CD": {
        "name": "CD, Fluorescence",
    },
    "Fluorescence (Trp)": {
        "name": "Fluorescence",
        "details": "Fluorescence (Trp)",
    },
    "FTIR": {
        "name": "FTIR",
    },
    "Gel electrophoresis": {
        "name": "Gel electrophoresis",
    },
    "HPLC": {
        "name": "HPLC",
    },
    "Hydrogen exchange": {
        "name": "Hydrogen exchange",
    },
    "IATC": {
        "name": "IATC",
    },
    "IR spectroscopy": {
        "name": "IR spectroscopy",
    },
    "Isothermal denaturation": {
        "name": "Isothermal denaturation",
    },
    "Light scattering": {
        "name": "Light-scattering",
    },
    "Light-scattering": {
        "name": "Light-scattering",
    },
    "Magnetic Relaxation Dispersion": {
        "name": "Magnetic Relaxation Dispersion",
    },
    "near-UV CD": {
        "name": "CD",
        "details": "CD (near-UV)",
    },
    "NMR": {
        "name": "NMR",
    },
    "NMR amide hydrogen exchange": {
        "name": "NMR",
        "details": "NMR (amide hydrogen exchange)",
    },
    "NMR hydrogen exchange": {
        "name": "NMR",
        "details": "NMR (hydrogen exchange)",
    },
    "NMR Hydrogen exchange": {
        "name": "NMR",
        "details": "NMR (hydrogen exchange)",
    },
    "optical": {
        "name": "Optical",
    },
    "Optical Density": {
        "name": "Optical density",
    },
    "Pulse Protolysis": {
        "name": "Pulse protolysis",
    },
    "Quantitative cysteine reactivity": {
        "name": "Quantitaive cysteine reactivity",
    },
    "Refraction": {
        "name": "Refraction",
    },
    "SAXS": {
        "name": "SAXS",
    },
    "SEC": {
        "name": "SEC",
    },
    "SUPREX": {
        "name": "SUPREX",
    },
    "UV spectroscopy": {
        "name": "UV spectroscopy",
    },
}

METHOD_MAPPING = {
    "2-Propanol": {
        "name": "2-Propanol",
    },
    "Acid": {
        "name": "Acid",
    },
    "Activity": {
        "name": "Activity",
    },
    "DimethylUrea": {
        "name": "DimethylUrea",
    },
    "Dynamic fluctuation": {
        "name": "Dynamic fluctuation",
    },
    "GdnHCl": {
        "name": "GdnHCl",
    },
    "GdnHSCN": {
        "name": "GdnHSCN",
    },
    "GdnSCN": {
        "name": "GdnSCN",
    },
    "GSSG, GSH": {
        "name": "GSSG, GSH",
    },
    "HClO4": {
        "name": "HClO4",
    },
    "heat treatment": {
        "name": "Heat treatment",
    },
    "Heat treatment": {
        "name": "Heat treatment",
    },
    "KSCN": {
        "name": "KSCN",
    },
    "LiCl": {
        "name": "LiCl",
    },
    "NaCl": {
        "name": "NaCl",
    },
    "NaClO4 titration": {
        "name": "NaClO4 titration",
    },
    "pressure": {
        "name": "Pressure",
    },
    "Pressure": {
        "name": "Pressure",
    },
    "Pressure denaturation": {
        "name": "Pressure",
    },
    "SDS": {
        "name": "SDS",
    },
    "TFE": {
        "name": "TFE",
    },
    "Thermal": {
        "name": "Thermal",
    },
    "THERMAL": {
        "name": "Thermal",
    },
    "Urea": {
        "name": "Urea",
    },
    "Urea/GdnHCl(0.5 M)": {
        "name": "Urea/GdnHCl",
        "details": "Urea/GdnHCl (0.5 M)",
    },
    "Urea/GdnHCl(0.9 M)": {
        "name": "Urea/GdnHCl",
        "details": "Urea/GdnHCl (0.9 M)",
    },
    "Urea/GdnHCl(1.35 M)": {
        "name": "Urea/GdnHCl",
        "details": "Urea/GdnHCl (1.35 M)",
    },
}


def _dev_print_nonperfect_alignments(best_aln):
    # check procedure
    should_print = False
    if re.sub('[-]*$', '', re.sub('^[-]*', '', best_aln.seqA)).count('-') == 0:
        for i in range(len(best_aln.seqA)):
            if best_aln.seqA[i] != "-" and best_aln.seqB[i] != "-" and best_aln.seqA[i] != best_aln.seqB[i]:
                should_print = True
                break
    else:
        should_print = True

    if should_print:
        print(pairwise2.format_alignment(*best_aln))
        input()


def is_not_indel(v):
    t = v.sequence_structure.mutation.strip().split(' ')
    return len(t[0]) == 1 and len(t[2]) == 1


NUMBER_REGEXP = '([-]?\\d+\\.\\d+|[-]?\\d+)\\s*'


def convert_units(v, temp_abs=False):
    v = v.strip()
    if len(v) == 0:
        return None

    if v == "Unknown" or v == "unknown":
        return None

    base_str = NUMBER_REGEXP
    m = re.match('^{}$'.format(base_str), v)
    if m:
        return float(m.group(1))

    kcal_res = [
        '^{}kcal/mol\\s*$',
        '^{}kal/mol\\s*$',
        '^{}kcal\\(average\\)'
    ]
    for kcal_re in kcal_res:
        m = re.match(kcal_re.format(base_str), v)
        if m:
            return float(m.group(1))

    kj_res = [
        '^{}kJ/mol\\s*$'
    ]
    for kj_re in kj_res:
        m = re.match(kj_re.format(base_str), v)
        if m:
            return float(m.group(1)) * .239

    c_res = [
        '^{}C\\s*$'
    ]

    for c_re in c_res:
        m = re.match(c_re.format(base_str), v)
        if m:
            return float(m.group(1))

    k_res = [
        '^{}K\\s*$'
    ]

    for tm_re in k_res:
        m = re.match(tm_re.format(base_str), v)
        if m:
            return float(m.group(1)) + (-272.15 if temp_abs else 0)

    raise IOError('Unparsable input \'{}\''.format(v))


def convert_tm(v):
    res = [
        '^\\< {}$',
        '^{}\\s*\\(dimer\\)$'
    ]

    for mre in res:
        m = re.match(mre.format(NUMBER_REGEXP), v)
        if m:
            return m.group(1)

    return v


def convert_ddg(v):
    res = [
        '^\\< {}$',
    ]

    for mre in res:
        m = re.match(mre.format(NUMBER_REGEXP), v)
        if m:
            return m.group(1)

    return v


def fetch_pub_ids(protherm_entries):
    pubs_orig = {v.literature.reference: v.literature for v in protherm_entries.values()}
    pmids = []
    pubs_mapping = {}
    for p in pubs_orig.values():
        pmid = p.get_pmid()
        if pmid is None:
            print("{} has no PMID mapping".format(p.reference))
            continue
        pmids.append(pmid)
        pubs_mapping[p.reference] = pmid

    pubs = map_pubmed_ids(pmids)

    pubs_final = {}
    for (k, v) in pubs_mapping.items():
        if v not in pubs:
            print("Unable to find publication for {}".format(v))
            continue
        pubs_final[k] = pubs[v]
        pubs_final[k]["mapping"] = v

    return pubs_final


class ProThermMutation:

    def __init__(self):
        self.id = 0
        self.uniprot_id = None
        self.uniprot_id_orig = None
        self.protein_name = None
        self.ec_number = None
        self.organism = None
        self.pdb_id = None
        self.sequence = None
        self.chain = None
        self.struct_index = None
        self.seq_index = None
        self.uniprot_index = None
        self.wt = None
        self.mut = None
        self.alignment = None
        self.data = None
        self.experimental_condition = None
        self.publication = None
        self.remarks = None


class ProThermConverter:

    def __init__(self):
        self.force_pubs_fetch = False

    def run(self):
        protherm_entries = parse_protherm_input_to_entry(data_file("protherm/ProTherm.dat"), False)
        print("Total number of entries: {}".format(len(protherm_entries)))

        protherm_entries = {k: v for (k, v) in protherm_entries.items() if len(
            v.sequence_structure.mutation.strip()) != 0 and not v.sequence_structure.mutation.strip().startswith(
            'wild')}
        print("Without wild-types: {}".format(len(protherm_entries)))

        protherm_entries = {k: v for (k, v) in protherm_entries.items() if is_not_indel(v)}
        print("Without indels: {}".format(len(protherm_entries)))

        protherm_entries = {k: v for (k, v) in protherm_entries.items() if
                            len(v.sequence_structure.swissprot_id.strip()) and len(
                                v.sequence_structure.pdb_wild.strip())}
        print("With SwissProt/PDB ids: {}".format(len(protherm_entries)))

        protherm_entries = {k: v for (k, v) in protherm_entries.items() if
                            len(v.sequence_structure.mutation.split(',')) == 1}
        print("Single points: {}".format(len(protherm_entries)))

        protherm_entries = {k: v for (k, v) in protherm_entries.items() if
                            len(v.data.ddG_H2O.strip()) or len(v.data.ddG.strip()) or len(v.data.dTm.strip())}
        not_everything_unknown = lambda x: x.count("") + x.count("Unknown") + x.count("unknown") < len(x)
        protherm_entries = {k: v for (k, v) in protherm_entries.items() if
                            not_everything_unknown([v.data.ddG_H2O.strip(), v.data.ddG.strip(), v.data.dTm.strip()])}
        print("With stability data: {}".format(len(protherm_entries)))

        protherm_entries = {k: v for (k, v) in protherm_entries.items() if (
        v.sequence_structure.pdb_wild, v.sequence_structure.swissprot_ac) not in problematic_db_mappings}
        print("Without problematic database mappings: {}".format(len(protherm_entries)))

        pubs = cache_file("protherm/pubs.json")
        if not os.path.isfile(pubs) or self.force_pubs_fetch:
            with open(pubs, 'w') as outfile:
                json.dump(fetch_pub_ids(protherm_entries), outfile, indent=4)

        with open(pubs, 'r') as fh:
            pubs = json.load(fh)

        protherm_entries = {k: v for (k, v) in protherm_entries.items() if v.literature.reference in pubs}
        print("Without problematic publication mapping: {}".format(len(protherm_entries)))

        final_mutations = []

        for (k, v) in protherm_entries.items():
            uniprot_id = v.sequence_structure.swissprot_ac
            pdb_id = v.sequence_structure.pdb_wild
            pdb_chain = v.sequence_structure.mutated_chain if v.sequence_structure.mutated_chain.isalpha() else None
            uniprot_mapper = lambda x: uniprot_mappings.map(x, pdb_id)
            my_uniprot_query = lambda x: fetch_uniprot_entry(uniprot_mappings.map(x, pdb_id))
            real_uniprot_id = uniprot_mapper(uniprot_id)

            # FIXME: tmp
            if pdb_id in ["3HHR"]:
                continue

            print("Processing entry {} with pdb id {}".format(v.id, pdb_id))

            uniprot_entry = my_uniprot_query(uniprot_id)
            seqs, uniprot_data, struct_index_mapping, missing_residues, seqres = load_pdb(pdb_id, uniprot_mappings)

            if real_uniprot_id not in uniprot_data:
                at_least_one = False
                for uniprot_id2 in uniprot_data:
                    uniprot_entry2 = my_uniprot_query(uniprot_id2)
                    if uniprot_entry['id_sequence'] == uniprot_entry2['id_sequence']:
                        at_least_one = True
                        real_uniprot_id = uniprot_id2
                        break
                if not at_least_one:
                    print("Entry {} has uniprot mismatch (orig: {}, in pdb: {})".format(k, real_uniprot_id,
                                                                                        uniprot_data.keys()))
                    input()

            if pdb_chain and pdb_chain not in uniprot_data[real_uniprot_id]:
                pdb_chain = uniprot_data[real_uniprot_id][0]
                print("Changing pdb chain to {}".format(pdb_chain))

            if len(seqs) > 1 and not pdb_chain:
                if real_uniprot_id in uniprot_data:
                    print("Selecting chain {} for entry {}".format(uniprot_data[real_uniprot_id][0], k))
                    pdb_chain = uniprot_data[real_uniprot_id][0]

            pdb_chain = pdb_chain if pdb_chain else next(iter(seqs.keys()))
            seq = seqs[pdb_chain]

            aln = align(seq, uniprot_entry['id_sequence'])

            m = re.search(SINGLE_POINT_MUT_PDB_RE, v.sequence_structure.mutation)
            if not m:
                m = re.search(SINGLE_POINT_MUT_RE, v.sequence_structure.mutation)
                if not m:
                    print("Mutation is not parseable")
                    continue

            wt = m.group('wt')
            struct_pos = int(m.group('pos'))
            seq_pos = struct_index_mapping[pdb_chain][struct_pos] if struct_pos in struct_index_mapping[
                pdb_chain] else None
            mut = m.group('mut')

            if not seq_pos:
                print("Cannot get sequence position {} {} {} {} {}".format(struct_pos, pdb_chain, wt, struct_pos, mut))
                continue

            if seq[seq_pos - 1] != wt:
                print("Wild-type residue does not match {} {} {} {} {} {}".format(struct_pos, seq_pos, pdb_chain, wt,
                                                                                  seq[seq_pos - 1],
                                                                                  seq[seq_pos - 5:seq_pos + 5]))
                continue

            up_pos = aln.one_two(seq_pos)
            if up_pos is None or uniprot_entry['id_sequence'][up_pos - 1] != wt:
                print("Mapped uniprot residue do not match {} {} {} {}".format(pdb_chain, wt, up_pos, seq_pos))
                continue

            # _dev_print_nonperfect_alignments(best_aln)

            def to_float(x): return float(x) if len(x) > 0 else None
            pm = ProThermMutation()
            pm.id = v.id
            pm.uniprot_id = real_uniprot_id
            pm.uniprot_id_orig = uniprot_id if uniprot_mapper(uniprot_id) != uniprot_id else None
            pm.protein_name = uniprot_entry['id_name']
            pm.ec_number = uniprot_entry['ec_number']
            pm.organism = uniprot_entry['id_organism']
            pm.pdb_id = pdb_id
            pm.sequence = uniprot_entry['id_sequence']
            pm.chain = pdb_chain
            pm.struct_index = struct_pos
            pm.seq_index = seq_pos
            pm.uniprot_index = up_pos
            pm.wt = wt
            pm.mut = mut
            pm.alignment = aln
            pm.data = v.data
            pm.data.Tm = convert_units(convert_tm(pm.data.Tm), temp_abs=True)
            pm.data.dTm = convert_units(convert_tm(pm.data.dTm))
            pm.data.ddG = convert_units(convert_ddg(pm.data.ddG))
            pm.data.ddG_H2O = convert_units(convert_ddg(pm.data.ddG_H2O))

            if not pm.data.dTm and not pm.data.ddG and not pm.data.ddG_H2O:
                continue

            pm.experimental_condition = v.experimental_condition
            pm.experimental_condition.pH = to_float(pm.experimental_condition.pH)
            pm.experimental_condition.measure = MEASURE_MAPPING[pm.experimental_condition.measure] if len(pm.experimental_condition.measure) else None
            pm.experimental_condition.method = METHOD_MAPPING[pm.experimental_condition.method] if len(pm.experimental_condition.method) else None
            pm.experimental_condition.temp = convert_units(pm.experimental_condition.temp, temp_abs=True)
            pm.publication = pubs[v.literature.reference]
            pm.publication["orig_reference"] = v.literature.reference
            pm.remarks = v.literature.remarks

            final_mutations.append(pm)

        print("Final size of dataset: {}".format(len(final_mutations)))

        return final_mutations

