from sqlalchemy import Column, Integer, String, ForeignKey, Numeric
from fireprotdb.importer.db import Base

class ResiduesCalatyticPocket(Base):
    __tablename__ = 'residues_catalytic_pocket'
    position = Column(Integer, ForeignKey('residues.position'), primary_key=True)
    pocket_id = Column(Integer, ForeignKey('catalytic_pocket.pocket_id'), primary_key=True)
    sequence_id = Column(Integer, ForeignKey('protein_sequence.sequence_id'), primary_key=True)