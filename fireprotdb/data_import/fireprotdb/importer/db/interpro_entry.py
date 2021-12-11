from sqlalchemy import Column, String

from fireprotdb.importer.db import Base

class InterProEntry(Base):
    __tablename__ = 'interpro_entries'
    interpro_entry_id = Column(String, primary_key=True)
    type = Column(String)
    name = Column(String)
