from sqlalchemy import Column, Integer, String, ForeignKey, Numeric
from fireprotdb.importer.db import Base

class Mutation(Base):
    __tablename__ = 'mutations'
    mut_id = Column(Integer, primary_key=True)
    position = Column(Integer, ForeignKey('residues.position'))
    sequence_id = Column(Integer, ForeignKey('protein_sequence.sequence_id'))
    mutated_aa = Column(String)