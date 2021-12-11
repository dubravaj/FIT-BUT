from sqlalchemy import Column, Integer, String, ForeignKey
from fireprotdb.importer.db import Base

class StructureSequence(Base):
    __tablename__ = 'structure_sequence'
    pdb_id = Column(String, ForeignKey('structures.pdb_id'), primary_key=True)
    chain = Column(String, primary_key=True)
    sequence_id = Column(Integer, ForeignKey('protein_sequence.sequence_id'), primary_key=True)
    index_mapping = Column(String)
