from sqlalchemy import Column, Integer, String, ForeignKey, Numeric
from fireprotdb.importer.db import Base

class ProteinSequenceBiologicalUnit(Base):
    __tablename__ = 'protein_sequence_biological_unit'
    sequence_id = Column(Integer, ForeignKey('protein_sequence.sequence_id'), primary_key=True)
    bio_unit_id = Column(Integer, ForeignKey('biological_unit.bio_unit_id'), primary_key=True)
    old_chain = Column(String)
    new_chain = Column(String)