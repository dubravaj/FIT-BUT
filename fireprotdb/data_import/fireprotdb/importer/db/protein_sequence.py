from sqlalchemy import Column, Integer, String
from fireprotdb.importer.db import Base

class ProteinSequence(Base):
    __tablename__ = 'protein_sequence'
    sequence_id = Column(Integer, primary_key=True)
    uniprot_id = Column(String)
    protein_name = Column(String)
    species = Column(String)
    ec_number = Column(String)
    protein_seq = Column(String)