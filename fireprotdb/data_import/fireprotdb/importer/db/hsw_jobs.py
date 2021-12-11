from sqlalchemy import Column, Integer, String, ForeignKey, Numeric, Date
from fireprotdb.importer.db import Base

class HSWJobs(Base):
    __tablename__ = 'hsw_jobs'
    hsw_job_id = Column(Integer, primary_key=True)
    bio_unit_id = Column(Integer, ForeignKey('biological_unit.bio_unit_id'))
    creation_time = Column(Date)