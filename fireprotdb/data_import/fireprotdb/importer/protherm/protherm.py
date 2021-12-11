# -*- coding: utf-8 -*-
import re

from Bio import Entrez

unit_converter={'kJ/mol':'kJ/mol',
        'kJ/mole':'kJ/mol',
        'kJ/mol/K':'kJ/mol·K',
        'kJ/K mol':'kJ/mol·K',
        'kJ/molK':'kJ/mol·K',
        'kJ/mol K':'kJ/mol·K',
        'kJ/mol/M':'kJ/mol·M',
        'kJ/mol/k':'kJ/mol·K',
        'kJ/K/mol':'kJ/mol·K',
        'kJ/Kmol':'kJ/mol·K',
        'kJ/molK':'kJ/mol·K',
        'kJ/m/K':'kJ/mol·K',
        'kJ/M/mol':'kJ/mol·M',
        'kJmol/M':'kJ/mol·M',
        'kJ/K':'kJ/K',
        'J/K/g':'J/K/g',
        'J/g':'J/g',
        'kcal':'kcal',
        'kcal/mol':'kcal/mol',
        'kcal/mole':'kcal/mol',
        'kal/mol':'kcal/mol',
        'kcla/molK':'kcal/mol·K',
        'kcal/mol/K':'kcal/mol·K',
        'kcal/mol deg':'kcal/mol·K',
        'kcal/mol/M':'kcal/mol·M',
        'kcal/mole/K':'kcal/mol·K',
        'kcal/mol/deg':'kcal/mol·K',
        'Kcal/mol/deg':'kcal/mol·K',
        'kcal/K mol':'kcal/mol·K',
        'kcal/mol M':'kcal/mol·M',
        'kcal/mo/Ml':'kcal/mol·M',
        'Kcal/K mol':'kcal/mol·K',
        'cal/K/mol':'cal/mol·K',
        '1/s':'1/s',
        '1/min':'1/min',
        'mmol/mg/min':'mmol/mg/min',
        '%':'%',
        '(relative)':'(relative)',
        'micro molar':'uM',
        'micro m':'uM',
        'microM':'uM',
        'micro M':'uM',
        '(x 10**6 /M)':'uM', #incorrectly described in paper
        '(micro Molar)':'uM',
        'mM':'mM',
        'nM':'nM',
        'M':'M',
        '1/M':'1/M',
        'mg/mL':'mg/mL',
        'units/mg':'units/mg',
        '(units/mg)':'units/mg',
        'U/mg':'U/mg',
        '1/mg':'1/mg',
        'K':'K',
        'C':'°C',
        'k':'K',

}
#######################################################################################
# Classes
#######################################################################################
class SequenceStructure(object):
    """
    Represents the Sequence and Structural information part of the ProTherm entry

    Has fields PROTEIN, SOURCE, LENGTH, MOL-WEIGHT, PIR_ID, SWISSPROT_ID, E.C.NUMBER, PMD.NO PDB_wild, PDB_mutant, MUTATION, MUTATED_CHAIN, NO_MOLECULE, SEC.STR., ASA

    Information will be parsed into PROTEIN, and SEQUENCE objects in ProtaBank
    """
    def __init__(self, protein, source, length, mol_weight, pir_id, swissprot_id, ec_no, pmd_no, pdb_wild, pdb_mutant, mutation, mutated_chain, no_mol, sec_str, asa):
        self.protein = protein
        self.source = source
        self.length= length
        self.mol_weight = mol_weight
        self.pir_id = pir_id
        self.swissprot_id = swissprot_id
        self.ec_no = ec_no
        self.pmd_no = pmd_no
        self.pdb_wild = pdb_wild.split(',')[0]
        self.pdb_mutant = pdb_mutant.split(',')[0]
        self.mutation = mutation
        self.mutated_chain = mutated_chain
        self.no_mol = no_mol
        self.sec_str = sec_str
        self.asa = asa
        self.pdb_sequence=""

    def __str__(self):
        return self.__dict__()

    def toJSON(self):
        return self.__dict__

    @property
    def swissprot_ac(self):
        return re.search(r'\((.*?)\)', self.swissprot_id).group(1).strip()


class ExperimentalCondition(object):
    """
    Represents the Experimental Condition  part of the ProTherm entry

    Has fields temp, pH, BUFFER_NAME, BUFFER_CONC, ION_NAME[], ION_CONC[], ADDITIVES, PROTEIN_CONC, MEASURE, METHOD

    Information will be parsed into ASSAY objects in ProtaBank

    """

    def __init__(self, temp, pH, buffer_name, buffer_conc, ion_name, ion_conc, additives, prot_conc, measure, method):
        self.temp = temp
        self.pH = pH
        self.buffer_name=buffer_name
        self.buffer_conc = buffer_conc
        self.ion_name=ion_name
        self.ion_conc=ion_conc
        self.additives=additives
        self.prot_conc = prot_conc
        self.measure=measure
        self.method=method

    def __str__(self):
        return str(self.__dict__)

    def toJSON(self):
        return self.__dict__

    def create_assay_dict(self, orig_property):
        """
        Create experimental assays for the all the listed data"""
        property_dict = {
            'dG_H2O':{'category':'Stability', 'property':'ΔG', 'units':'kcal/mol'},
            'ddG_H2O':{'category':'Stability', 'property':'ΔG', 'units':'kcal/mol'},
            'dG_H20':{'category':'Stability', 'property':'ΔG', 'units':'kcal/mol'},
            'ddG_H20':{'category':'Stability', 'property':'ΔG', 'units':'kcal/mol'},
            'dG':{'category':'Stability', 'property':'ΔG', 'units':'kcal/mol'},
            'ddG':{'category':'Stability', 'property':'ΔG', 'units':'kcal/mol'},
            'Tm':{'category':'Stability', 'property':'Tm', 'units':'°C'},
            'dTm':{'category':'Stability', 'property':'Tm', 'units':'°C'},
            'dHvH':{'category':'Stability', 'property':'ΔH', 'units':'kcal/mol'},
            'dHcal':{'category':'Stability', 'property':'ΔH', 'units':'kcal/mol'},
            'm':{'category':'Stability', 'property':'m', 'units':'kcal/mol·M'},
            'Cm':{'category':'Stability', 'property':'Cm', 'units':'M'},
            'dCp':{'category':'Stability', 'property':'ΔCp', 'units':'kcal/mol·K'},
            'activity':{'category':'Activity', 'property':'Relative Activity', 'units':"%"},
            'activity_km':{'category':'Activity', 'property':'Km', 'units':"mM"},
            'activity_kcat':{'category':'Activity', 'property':'kcat', 'units':"1/s"},
            'activity_kd':{'category':'Activity', 'property':'Kd', 'units':"mM"},
            }# returns category, technique, property,unit for each prothem category

        technique_dict={
            'Fl':'Fluorescence Spectroscopy',
            'Fluorescence':'Fluorescence Spectroscopy',
            'CD':'Circular Dichroism (CD)',
            'CD(far-UV)':'Circular Dichroism (CD)',
            'CD(near-UV)':'Circular Dichroism (CD)',
            'CD (far-UV)':'Circular Dichroism (CD)',
            'CD (near-UV)':'Circular Dichroism (CD)',
            'far-UV CD':'Circular Dichroism (CD)',
            'near-UV CD':'Circular Dichroism (CD)',
            'DSC':'Differential Scanning Calorimetry (DSC)',
            'DSMC':'Differential Scanning Calorimetry (DSC)', # guessing typo
            'SEC':'Size Exclusion Chromatography',
            'Abs':'Absorbance',
            'Absorption':'Absorbance',
            'Absorbance':'Absorbance',
            'Refraction':'Refraction',
            'EPR':'Electron Paramagnetic Resonance (EPR)',
            'ESR':'Electron Spin Resonance',
            'NMR':'NMR',
            'HPLC':'Chromatography (HPLC, TLC)',
            'FTIR':'Fourier-transform Infrared Spectroscopy (FTIR)',
            'UV':'UV Spectroscopy',
            'UV spectroscopy':'UV Spectroscopy',
            'SAXS':'Small-angle X-ray Scattering (SAXS)',
            'Anisotropy':'Fluorescence Polarization/Anisotropy',
            'ANS':'Fluorescence Polarization/Anisotropy',
            'Fluorescence (ANS)':'Fluorescence Polarization/Anisotropy',
            'ANS binding':'Fluorescence Polarization/Anisotropy',
            'optical':'Optical Density',
            'Optical':'Optical Density',
            'Activity':'Activity',
            'activity':'Activity',
            'Isothermal denaturation':'Isothermal Titration Calorimetry (ITC)',
            'Hydrogen exchange':'Hydrogen Exchange',
            'NMR Hydrogen exchange':'Hydrogen Exchange',
            'NMR amide hydrogen exchange':'Hydrogen Exchange',
            'CD + Fluorescence':'Circular Dichroism (CD); Fluorescence Spectroscopy',
            'Fluorescence, CD':'Circular Dichroism (CD); Fluorescence Spectroscopy',
            'CD, Fluorescence':'Circular Dichroism (CD); Fluorescence Spectroscopy',
            'Light scattering':'Dynamic Light Scattering',
            'Light-scattering':'Dynamic Light Scattering',
            'Fluorescence (Trp)':'Tryptophan Fluorescence',
            'Gel electrophoresis':'Gel Electrophoresis',
        }
        denaturation_dict={
            'Thermal':'Thermal Denaturation',
            'Urea':'Urea Denaturation',
            'GdnHCl':'Guanidinium Denaturation',
        }

        property = orig_property.replace("H20", "H2O")
        new_assay_dict = {
            'name':property,
            'source':'Exp',
            'category':'',
            'units':'',
            'technique':'',
            'property':'',
            'buffers':property_str(self.buffer_name) + ": "+ property_str(self.buffer_conc),
            'details': "Additives "+property_str(self.additives),
            'ionic': property_str(self.ion_name) + ": " + property_str(self.ion_conc)
        }

        if property in property_dict.keys():
            new_assay_dict['category']=property_dict[property]['category']
            new_assay_dict['units']=property_dict[property]['units']
            new_assay_dict['property']=property_dict[property]['property']
        if self.measure in technique_dict.keys():
            new_assay_dict['technique']=technique_dict[self.measure]
        else:
            new_assay_dict['technique']=self.measure
        if self.method in denaturation_dict.keys() and new_assay_dict['category'] == 'Stability' :
            new_assay_dict['technique']+=";"+denaturation_dict[self.method]
        if self.temp != "":
            try:
                new_assay_dict['temp'] = str(float(self.temp)) + " C"
            except:
                new_assay_dict['temp'] = self.temp
        if self.pH !="":
            try:
                new_assay_dict['pH'] = str(float(self.pH))
            except:
                new_assay_dict['pH'] = self.pH
        if self.prot_conc !="":
            new_assay_dict['prot_conc'] = self.prot_conc

        # specify initial and final for dG
        if property  in ['dG', 'dG_H2O', 'dG_H20']:
            new_assay_dict['initial_state'] = "folded"
            new_assay_dict['final_state'] = "unfolded"
        return new_assay_dict

class ThermoData(object):
    """Represents the Thermodynamic data part of the ProTherm entry

    Has fields dG_H20, ddG_H2O, dG, ddG, Tm, dTm, dHvH, dHcal, m, Cm, dCp, STATE, REVERSIBILITY, ACTIVITY, ACTIVITY_Km, ACTIVITY_Kcat, AC  ACTIVITY_Kd

    Information will be parsed into DATUM and ASSAY objects in ProtaBank
    """

    def __init__(self, dG_H20, ddG_H2O, dG, ddG, Tm, dTm, dHvH, dHcal, m, Cm, dCp, state, reversibility, activity, activity_km, activity_kcat, activity_kd):
        self.dG_H20 = dG_H20
        self.ddG_H2O = ddG_H2O
        self.dG=dG
        self.ddG=ddG
        self.Tm = Tm
        self.dTm = dTm
        self.dHvH = dHvH
        self.dHcal = dHcal
        self.m =m
        self.Cm = Cm
        self.dCp = dCp
        self.state = state
        self.reversibility = reversibility
        self.activity = activity
        self.activity_km = activity_km
        self.activity_kcat = activity_kcat
        self.activity_kd = activity_kd

    def __str__(self):
        return str(self.__dict__)

    def toJSON(self):
        return self.__dict__

    def non_null_results(self):
        """returns a dictionary of property name and values that are not empty"""
        non_null={}
        for property, value in self.__dict__.items():
            if isinstance(value,list):
                print('Property has multiple values', property, value)
            elif property in ['reversibility', 'state']:
                # skip these for now
                pass
            elif value.strip() != "":
                non_null[property]=value
        return non_null
    # def create_datum_dict(self,property, sequence, assay_id):
    #     """expects a property amoung the ThermoData fields, i.e. dG
    #     passes in a sequence_dict which has the full sequence in sequence and potentially a mut_desc
    #
    #     optionally passes in an assay_dict if it already exists
    #     """
    #
    #     datum_dict = {'mut_desc':sequence_dict['mut_desc'], 'sequence':sequence_dict['sequence'], 'result':getattr(self,property)}
    #     datum_dict['units'] = assay_dict['units']
    #     datum_dict['assay'] = assay_id
    #     return datum_dict

class Literature(object):
    """Represents the Literature data part of the ProTherm entry

    Has fields REFERENCE, PMID, AUTHOR, RELATED_ENTRIES
    (ignoring keywords and remarks...)

    Information will be parsed into PUBLICATION objects in ProtaBank
    """
    def __init__(self, key_words, reference, author, remarks, related_entries):
        self.key_words=""
        if key_words is not None and isinstance(key_words,list):
            self.key_words = ",".join([word for word in key_words if word is not None])
        elif key_words is not None:
            self.key_words = key_words
        self.reference = reference
        self.author = author
        if isinstance(author,list):
            self.author=" ".join(author)
        self.remarks=""
        if remarks is not None and isinstance(remarks,list):
            self.remarks = ",".join([word for word in remarks if word is not None])
        elif remarks is not None:
            self.remarks = remarks
        self.related_entries = []
        if isinstance(related_entries,list):
            self.related_entries = related_entries
        else:
            for entry in related_entries.strip(', ').split(','):
                if entry.strip() != "":
                    try:
                        self.related_entries.append(int(entry))
                    except:
                        print("Could not identify related entry", entry)
                        print(self.reference, related_entries)

    def toJSON(self):
        return self.__dict__

    def __str__(self):
        return str(self.__dict__)

    def __eq__(self,other):
        return (self.reference == other.reference and self.author == other.author)

    def __hash__(self):
        return hash("Literature(%s,%s)"%(self.reference,self.author))

    def get_pmid(self):
        if 'PMID:' in self.reference and len(self.reference) > (self.reference.find('PMID:') + 5):
            return self.reference.split('PMID:')[1].strip()
        else:
            search = re.search(r'^([A-Z ]+)([0-9]+)[., ]*([0-9]+)-([0-9]+).*\(([0-9]+)\).*', self.reference)
            if search:
                lib_dict = {'journal':search.group(1), 'volume':search.group(2), 'pages':search.group(3)+"-"+search.group(4), 'year':search.group(5)}
                lib_dict['authors'] = self.author
                return search_for_pubmedid(lib_dict)
            else:
                print("NO RE MATCH FOUND!", self.reference)
            return None


class ProThermEntry(object):
    """ Represents a ProTherm Entry comprisied of
    SequenceStructure Info, ExperimentalCondition, ThermoData, Literature

    Information will be parsed into STUDY objects in ProtaBank

    """
    def __init__(self, id, sequence_structure, experimental_condition, data, literature):
        self.id=id
        self.sequence_structure=sequence_structure
        self.experimental_condition=experimental_condition
        self.data=data
        self.literature=literature

    def __str__(self):
        return "Entry No." + str(self.number) + str(self.sequence_structure.__dict__) + str(self.experimental_condition.__dict__)+str(self.data.__dict__)+str(self.literature.__dict__)

    def toJSON(self):
        return self.__dict__

    def get_related_entries(self):
        if self.literature is not None:
            return self.literature.related_entries
        else:
            print(str(self))
            return None

def property_str(property):
    if isinstance(property, list):
        return ",".join([p.strip() for p in property])
    elif property is not None:
        return str(property.strip())
    else:
        return ""

def parse_result(result, assay_dict):
    """Parse the protherm result to strip out units and assay conditions and return the result only. Change the units and add any conditions to the assay details of the assay_dict"""
    unit=r'(?P<result>[0-9\.-]+) *(?P<unit>[\w \/\%\(\)\*]+)'
    temp=r'\((?P<val>[0-9]+) degree C\)'
    at_temp=r'(?P<unit>[\w \/\%\(\)\*]+)*at (?P<val>[0-9]+) degree C'
    note=r'(?P<unit>[\w \/\%\(\)\*]+)*\((?P<note>[\w -]+)\)'
    if len(result)==0:
        return result
    else:
        try:
            a=float(result.replace(',',""))
            return result.replace(',','')
        except:
            unit_match = re.match(unit, result)
            if unit_match:
                temp_match = re.match(temp, unit_match.group('unit'))
                at_temp_match = re.match(at_temp, unit_match.group('unit'))
                note_match = re.match(note, unit_match.group('unit'))
                if unit_match.group('unit').strip() in unit_converter.keys():
                    assay_dict['units'] = unit_converter[unit_match.group('unit').strip()]
                elif temp_match:
                    assay_dict['temp'] = temp_match.group('val')+ " degree C"
                elif at_temp_match:
                    if at_temp_match.group('unit') and at_temp_match.group('unit').strip() in unit_converter.keys():
                        assay_dict['units'] = unit_converter[at_temp_match.group('unit').strip()]
                    assay_dict['temp'] = at_temp_match.group('val')+ " degree C"
                elif note_match:
                    if note_match.group('unit') and note_match.group('unit').strip() in unit_converter.keys():
                        assay_dict['units'] = unit_converter[note_match.group('unit').strip()]
                    assay_dict['details']+= " ProTherm noted: "+note_match.group('note')
                return unit_match.group('result')
            return result

def search_for_pubmedid(pub_dict):
    """ match the reference string into journal, issue, year, pages to find the reference """
    Entrez.email='connie.wang@protabit.com'
    term=pub_dict['journal']+"[ta] AND "+pub_dict['volume']+"[vi] AND "+pub_dict['pages']+"[pg]"
    try:
        handle=Entrez.esearch(db="pubmed", term=term)
        record=Entrez.read(handle)
        if record['Count'] == '1':
            return record['IdList'][0]
        else:
            return None
    except:
        print('Exception', term)
        return None