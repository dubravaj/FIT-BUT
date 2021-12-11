from sqlalchemy import Column, Integer, String, ForeignKey, Numeric
from fireprotdb.importer.db import Base

class CalatyticPocket(Base):
    __tablename__ = 'catalytic_pocket'
    pocket_id = Column(Integer, primary_key=True)
    bio_unit_id = Column(Integer, ForeignKey('biological_unit.bio_unit_id'))
    relevance = Column(Numeric)
    volume = Column(Numeric)
    drugability = Column(Numeric)