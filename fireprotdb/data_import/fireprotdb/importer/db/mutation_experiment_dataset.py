from sqlalchemy import Column, Integer, String, ForeignKey, Numeric
from fireprotdb.importer.db import Base

class MutationExperimentDataset(Base):
    __tablename__ = 'mutation_experiments_datasets'
    experiment_id = Column(Integer, ForeignKey('mutation_experiments.experiment_id'), primary_key=True)
    dataset_id = Column(Integer, ForeignKey('datasets.dataset_id'), primary_key=True)