from sqlalchemy import Column, Integer, String, ForeignKey, Boolean
from fireprotdb.importer.db import Base

class BTCAnnotation(Base):
    __tablename__ = 'btc_annotations'
    btc_mut_id = Column(Integer, primary_key=True)
    frequency = Column(Boolean, default=False)
    ratio = Column(Boolean, default=False)
    mut_id = Column(Integer, ForeignKey('mutations.mut_id'))
