from sqlalchemy import Column, Integer, String, ForeignKey
from fireprotdb.importer.db import Base

class BottleneckResidues(Base):
    __tablename__ = 'bottleneck_residues'
    bottleneck_id = Column(Integer, ForeignKey('bottleneck.bottleneck_id'), primary_key=True)
    tunnel_id = Column(Integer, ForeignKey('protein_tunnels.tunnel_id'), primary_key=True)
    position = Column(Integer, ForeignKey('residues.position'), primary_key=True)
    sequence_id = Column(Integer, ForeignKey('protein_sequence.sequence_id'), primary_key=True)
    sidechain = Column(Integer)