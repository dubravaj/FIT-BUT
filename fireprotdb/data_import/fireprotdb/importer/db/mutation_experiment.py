from sqlalchemy import Column, Integer, String, ForeignKey, Numeric, Boolean
from fireprotdb.importer.db import Base

class MutationExperiment(Base):
    __tablename__ = 'mutation_experiments'
    experiment_id = Column(Integer, primary_key=True)
    mut_id = Column(Integer, ForeignKey('mutations.mut_id'))
    publication_id = Column(Integer, ForeignKey('publications.publication_id'))
    tm = Column(Numeric)
    scan_rate = Column(Numeric)
    protein_concentration = Column(Numeric)
    purity_of_sample = Column(Numeric)
    t_offset = Column(Numeric)
    pH = Column(Numeric)
    method = Column(String)
    method_details = Column(String)
    technique = Column(String)
    technique_details = Column(String)
    notes = Column(String)
    ddg = Column(Numeric)
    d_tm = Column(Numeric)
    cp = Column(Numeric)
    half_life = Column(Numeric)
    currated = Column(Boolean)