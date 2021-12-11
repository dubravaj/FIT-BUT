from sqlalchemy import Column, Integer, String, ForeignKey, Numeric
from fireprotdb.importer.db import Base

class AuthorsPublications(Base):
    __tablename__ = 'authors_publications'
    publication_id = Column(Integer, ForeignKey('publications.publication_id'), primary_key=True)
    author_id = Column(Integer, ForeignKey('authors.author_id'), primary_key=True)
    author_order = Column(Integer)