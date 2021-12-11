from sqlalchemy import Column, Integer, String, ForeignKey
from fireprotdb.importer.db import Base

class Residue(Base):
    __tablename__ = 'residues'
    position = Column(Integer, primary_key=True)
    sequence_id = Column(Integer, ForeignKey('protein_sequence.sequence_id'))
    residue = Column(String)
    conservation = Column(Integer)