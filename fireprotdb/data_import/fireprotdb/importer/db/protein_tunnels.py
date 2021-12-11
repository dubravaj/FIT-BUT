from sqlalchemy import Column, Integer, String, ForeignKey, Numeric
from fireprotdb.importer.db import Base

class ProteinTunnels(Base):
    __tablename__ = 'protein_tunnels'
    tunnel_id = Column(Integer, primary_key=True)
    priority = Column(Integer)
    length = Column(Numeric)
    distance_to_surface = Column(Numeric)
    curvature = Column(Numeric)
    throughput = Column(Numeric)