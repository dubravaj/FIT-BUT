from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.ext.declarative import declarative_base

from fireprotdb.importer.config import config

Base = declarative_base()

from fireprotdb.importer.db.author import Author
from fireprotdb.importer.db.authors_publications import AuthorsPublications
from fireprotdb.importer.db.biological_unit import BiologicalUnit
from fireprotdb.importer.db.bottleneck import Bottleneck
from fireprotdb.importer.db.bottleneck_residues import BottleneckResidues
from fireprotdb.importer.db.catalytic_annotations import CalatyticAnnotations
from fireprotdb.importer.db.catalytic_pocket import CalatyticPocket
from fireprotdb.importer.db.dataset import Dataset
from fireprotdb.importer.db.hsw_jobs import HSWJobs
from fireprotdb.importer.db.interpro_entry import InterProEntry
from fireprotdb.importer.db.mutation import Mutation
from fireprotdb.importer.db.mutation_experiment import MutationExperiment
from fireprotdb.importer.db.mutation_experiment_dataset import MutationExperimentDataset
from fireprotdb.importer.db.protein_sequence import ProteinSequence
from fireprotdb.importer.db.protein_sequence_biological_unit import ProteinSequenceBiologicalUnit
from fireprotdb.importer.db.protein_tunnels import ProteinTunnels
from fireprotdb.importer.db.protein_tunnels_catalytic_pocket import CalatyticPocketTunnel
from fireprotdb.importer.db.publication import Publication
from fireprotdb.importer.db.residue import Residue
from fireprotdb.importer.db.residues_catalytic_pocket import ResiduesCalatyticPocket
from fireprotdb.importer.db.residues_correlation import CorrelatedResidues
from fireprotdb.importer.db.protein_sequence_interpro_entry import ProteinSequenceInterProEntry
from fireprotdb.importer.db.structure import Structure
from fireprotdb.importer.db.structure_residue import StructureResidue
from fireprotdb.importer.db.structure_sequence import StructureSequence
from fireprotdb.importer.db.uniprot import Uniprot


engine = create_engine("mysql://{}:{}@{}:{}/{}".format(config['db']['user'], config['db']['password'], config['db']['host'], config['db']['port'], config['db']['db']), echo=True)
Session = sessionmaker(bind=engine)