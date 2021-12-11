from sqlalchemy import Column, Integer, String
from fireprotdb.importer.db import Base

class Author(Base):
    __tablename__ = 'authors'
    author_id = Column(Integer, primary_key=True)
    last_name = Column(String)
    fore_name = Column(String)
    initials = Column(String)
