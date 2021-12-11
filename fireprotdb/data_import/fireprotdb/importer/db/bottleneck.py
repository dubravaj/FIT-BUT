from sqlalchemy import Column, Integer, String, ForeignKey, Numeric
from fireprotdb.importer.db import Base

class Bottleneck(Base):
    __tablename__ = 'bottleneck'
    bottleneck_id = Column(Integer, primary_key=True)
    tunnel_id = Column(Integer, ForeignKey('protein_tunnels.tunnel_id'))
    radius = Column(Numeric)
    x_coord = Column(Numeric)
    y_coord = Column(Numeric)
    z_coord = Column(Numeric)
    ball_number = Column(Numeric)