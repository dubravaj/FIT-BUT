from sqlalchemy import Column, Integer, String, ForeignKey
from fireprotdb.importer.db import Base

class Dataset(Base):
    __tablename__ = 'datasets'
    dataset_id = Column(Integer, primary_key=True)
    name = Column(String)
    version = Column(String)
    publication_id = Column(Integer, ForeignKey('publications.publication_id'))
