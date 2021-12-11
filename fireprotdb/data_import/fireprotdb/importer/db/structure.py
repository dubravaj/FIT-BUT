from sqlalchemy import Column, String, Numeric
from fireprotdb.importer.db import Base

class Structure(Base):
    __tablename__ = 'structures'
    pdb_id = Column(String, primary_key=True)
    method = Column(String)
    resolution = Column(Numeric)
