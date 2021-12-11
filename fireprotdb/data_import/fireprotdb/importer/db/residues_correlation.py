from sqlalchemy import Column, Integer, String, ForeignKey, Numeric
from fireprotdb.importer.db import Base

class CorrelatedResidues(Base):
    __tablename__ = 'residues_correlations'
    position = Column(Integer, ForeignKey('residues.position'), primary_key=True)
    position2 = Column(Integer, ForeignKey('residues.position'), primary_key=True)
    sequence_id = Column(Integer, ForeignKey('protein_sequence.sequence_id'), primary_key=True)
    correlation_consensus = Column(Numeric)