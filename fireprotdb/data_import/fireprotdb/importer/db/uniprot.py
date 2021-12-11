from sqlalchemy import Column, Integer, String, ForeignKey
from fireprotdb.importer.db import Base

class Uniprot(Base):
    __tablename__ = 'uniprot'
    uniprot_id = Column(Integer, primary_key=True)
    sequence_id = Column(Integer, ForeignKey('protein_sequence.sequence_id'))
    relation = Column(String)