import yaml, re, csv

VARIANT_FORMAT_RE = "p\\.([A-Z])(\d+)([A-Z])"

class VariBenchEntry:

    def __init__(self, row):
        self.pdb_id = row[0]
        self.pdb_chain = row[1]
        variant = re.match(VARIANT_FORMAT_RE, row[4])
        self.wt = variant.group(1)
        self.struct_index = int(variant.group(2))
        self.mut = variant.group(3)
        self.ddg = float(row[6])
        self.uniprot_id = row[7]
        uniprot_variant = re.match(VARIANT_FORMAT_RE, row[8])
        if uniprot_variant:
            if uniprot_variant.group(1) != self.wt or uniprot_variant.group(3) != self.mut:
                print("Wild-type or mutation amino acids do not match between uniprot and pdb (in VariBench)")
            self.uniprot_index = int(uniprot_variant.group(2))
        else:
            self.uniprot_index = None


def load_varibench(f):
    with open(f) as fh:
        reader = csv.reader(fh)
        # skip header
        next(reader)
        data = []
        for row in reader:
            try:
                data.append(VariBenchEntry(row))
            except:
                pass

        return data


def match_protherm(ve, pe):
    return ve.pdb_id == pe["pdb_id"] and \
        ve.pdb_chain == pe["chain"] and \
        ve.wt == pe["wt"] and \
        ve.struct_index == pe["struct_index"] and \
        ve.mut == pe["mut"]
        #ve.uniprot_id == pe["uniprot_id"]

#
# for d in datasets_data:
#     if 'file' not in d:
#         continue
#     for ve in load_varibench("data/varibench/" + d['file']):
#         matches = []
#         for pe in protherm:
#             if match_protherm(ve, pe):
#                 matches.append(pe)
#         if len(matches) == 0:
#             print("no match for entry {}".format(ve.__dict__))
#         elif len(matches) > 1:
#             print("too many matches for entry {}".format(ve.__dict__))
#         else:
#             if 'datasets' not in matches[0]:
#                 matches[0]['datasets'] = set()
#             matches[0]['datasets'].add(datasets[(d['name'], d['version'])].dataset_id)
#
# for m in protherm:
#     if 'datasets' not in m:
#         continue
#
#     for d in m['datasets']:
#         session.add(MutationExperimentDataset(experiment_id=exps[m['id']].experiment_id, dataset_id=d))