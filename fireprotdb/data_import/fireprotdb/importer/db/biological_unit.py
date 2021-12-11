from sqlalchemy import Column, Integer, String, Numeric
from fireprotdb.importer.db import Base

class BiologicalUnit(Base):
    __tablename__ = 'biological_unit'
    bio_unit_id = Column(Integer, primary_key=True)
    pdb_id = Column(String)
    pdb_unit_number = Column(Integer)
