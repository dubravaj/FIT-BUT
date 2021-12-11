from sqlalchemy import Column, Integer, String, ForeignKey,Numeric
from fireprotdb.importer.db import Base

class StructureResidue(Base):
    __tablename__ = 'structure_residues'
    bio_unit_id = Column(Integer, ForeignKey('biological_unit.bio_unit_id'), primary_key=True)
    position = Column(Integer, ForeignKey('residues.position'), primary_key=True)
    sequence_id = Column(Integer, ForeignKey('protein_sequence.sequence_id'), primary_key=True)
    structure_index = Column(Integer)
    new_chain = Column(String)
    b_factor=Column(Numeric)
    asa=Column(Numeric)
    insertion_code=Column(String)
    secondary_structure=Column(String)

