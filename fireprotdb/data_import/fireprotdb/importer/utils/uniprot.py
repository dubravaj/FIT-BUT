import os
import requests

import xml.etree.ElementTree as ET

from fireprotdb.importer.utils import cache_file


class UniprotObsoleteMapping:

    def __init__(self, data={}):
        self.data = data

    def map(self, uniprot_id, pdb_id):
        return self.data[(uniprot_id, pdb_id)] if (uniprot_id, pdb_id) in self.data else uniprot_id


def fetch_uniprot_entry(uniprotid, download_only=False):
    fetch = lambda: requests.get("http://www.uniprot.org/uniprot/{}.xml".format(uniprotid))

    entry_file = cache_file("uniprot/{}.xml".format(uniprotid))
    if not os.path.isfile(entry_file):
        req = fetch()
        if req.status_code != 200:
            print("Unable to fetch UniProt entry {}".format(uniprotid))
            return
        with open(entry_file, 'w') as fh:
            fh.write(req.text)
    if download_only:
        return
    root = ET.parse(entry_file).getroot()

    prot_dict = {}

    prot_dict['id_uniprot_id'] = uniprotid
    try:
        prot_dict['id_name'] = root.find('.//{http://uniprot.org/uniprot}fullName').text
    except:
        prot_dict['id_name'] = None
    try:
        prot_dict['id_sequence'] = root.find('.//{http://uniprot.org/uniprot}sequence').text.replace('\n','')
    except:
        elements = root.findall('.//{http://uniprot.org/uniprot}sequence')
        for element in elements:
            if element.text is not None:
                prot_dict['id_sequence'] = element.text.replace('\n','')
                break
    try:
        prot_dict['ec_number'] = root.find('.//*{http://uniprot.org/uniprot}recommendedName/{http://uniprot.org/uniprot}ecNumber').text
    except:
        prot_dict['ec_number'] = None

    try:
        prot_dict['id_organism'] = root.find('.//*{http://uniprot.org/uniprot}organism/{http://uniprot.org/uniprot}name').text
    except:
        prot_dict['id_organism'] = None
    try:
        prot_dict['id_pdb_id'] = ",".join(set([el.attrib['id'] for el in root.findall(".//*{http://uniprot.org/uniprot}dbReference/[@type='PDB']")]))
    except:
        pass
    prot_dict['pdb_mapping'] = {}
    try:
        for e in root.findall("./{http://uniprot.org/uniprot}entry/{http://uniprot.org/uniprot}dbReference[@type='PDB']"):
            pdb_id = e.attrib["id"]
            method = e.find("{http://uniprot.org/uniprot}property[@type='method']").get('value')

            if method == 'Model':
                continue

            res = e.find("{http://uniprot.org/uniprot}property[@type='resolution']")
            resolution = float(res.get('value')) if res is not None else None

            pdb_entry = {
                "method": method,
                "resolution": resolution,
                "chains": {}
            }
            cstr = e.find("{http://uniprot.org/uniprot}property[@type='chains']").get("value")
            for ccls in cstr.split(","):
                ccls = ccls.strip()
                cs, index_range = ccls.split("=", 2)
                index_from, index_to = index_range.split("-")
                for c in cs.split('/'):
                    pdb_entry['chains'][c] = {
                        "index_from": index_from,
                        "index_to": index_to,
                    }
            prot_dict['pdb_mapping'][pdb_id] = pdb_entry
    except Exception as err:
        print(uniprotid)
        print(err.message)
        pass

    prot_dict['interpro'] = []
    try:
        for e in root.findall("./{http://uniprot.org/uniprot}entry/{http://uniprot.org/uniprot}dbReference[@type='InterPro']"):
            prot_dict['interpro'].append(e.attrib["id"])
    except Exception as err:
        print(uniprotid)
        print(err.message)
        pass
    return prot_dict