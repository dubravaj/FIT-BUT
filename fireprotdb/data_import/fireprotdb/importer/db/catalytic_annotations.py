from sqlalchemy import Column, Integer, String, ForeignKey
from fireprotdb.importer.db import Base

class CalatyticAnnotations(Base):
    __tablename__ = 'catalytic_annotations'
    cat_annotation_id = Column(Integer, primary_key=True)
    position = Column(Integer, ForeignKey('residues.position'))
    sequence_id = Column(Integer, ForeignKey('protein_sequence.sequence_id'))
    source = Column(String)
    accesion_code = Column(String)
    identity = Column(String)
    description = Column(String)
    type = Column(String)
    neighbourhood = Column(String)