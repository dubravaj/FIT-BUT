from sqlalchemy import Column, Integer, String, ForeignKey
from fireprotdb.importer.db import Base

class ProteinSequenceInterProEntry(Base):
    __tablename__ = 'protein_sequence_interpro_entries'
    sequence_id = Column(Integer, ForeignKey('protein_sequence.sequence_id'), primary_key=True)
    interpro_entry_id = Column(String, ForeignKey('interpro_entries.interpro_entry_id'), primary_key=True)
    order = Column(Integer)
