from sqlalchemy import Column, Integer, String, ForeignKey, Numeric
from fireprotdb.importer.db import Base

class CalatyticPocketTunnel(Base):
    __tablename__ = 'protein_tunnels_catalytic_pocket'
    tunnel_id = Column(Integer, ForeignKey('protein_tunnels.tunnel_id'), primary_key=True)
    pocket_id = Column(Integer, ForeignKey('catalytic_pocket.pocket_id'), primary_key=True)
    bio_unit_id = Column(Integer, ForeignKey('biological_unit.bio_unit_id'), primary_key=True)
    x_start = Column(Numeric)
    y_start = Column(Numeric)
    z_start = Column(Numeric)