from sqlalchemy import Column, Integer, String
from fireprotdb.importer.db import Base

class Publication(Base):
    __tablename__ = 'publications'
    publication_id = Column(Integer, primary_key=True)
    title = Column(String)
    journal = Column(String)
    volume = Column(String)
    issue = Column(String)
    year = Column(String)
    pages = Column(String)
    doi = Column(String)
    pmid = Column(String)