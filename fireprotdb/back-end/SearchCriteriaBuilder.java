/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.loschmidt.fireprotdb.backend.repository;

import cz.loschmidt.fireprotdb.backend.model.BiologicalUnit;
import cz.loschmidt.fireprotdb.backend.model.Datasets;
import cz.loschmidt.fireprotdb.backend.model.MutationExperiments;
import cz.loschmidt.fireprotdb.backend.model.Mutations;
import cz.loschmidt.fireprotdb.backend.model.Mutations_;
import cz.loschmidt.fireprotdb.backend.model.ProteinSequence;
import cz.loschmidt.fireprotdb.backend.model.ProteinSequence_;
import cz.loschmidt.fireprotdb.backend.model.Publications;
import cz.loschmidt.fireprotdb.backend.model.Residues;
import cz.loschmidt.fireprotdb.backend.model.Residues_;
import cz.loschmidt.fireprotdb.backend.model.InterproEntries;
import cz.loschmidt.fireprotdb.backend.model.MutationExperiments_;
import cz.loschmidt.fireprotdb.backend.model.ProteinSequenceBioUnitPK;
import cz.loschmidt.fireprotdb.backend.model.ProteinSequenceBioUnitPK_;
import cz.loschmidt.fireprotdb.backend.model.ProteinSequenceBiologicalUnit;
import cz.loschmidt.fireprotdb.backend.model.ProteinSequenceBiologicalUnit_;
import cz.loschmidt.fireprotdb.backend.model.ProteinSequenceInterproEntries;
import cz.loschmidt.fireprotdb.backend.model.ProteinSequenceInterproEntriesPK;
import cz.loschmidt.fireprotdb.backend.model.ProteinSequenceInterproEntriesPK_;
import cz.loschmidt.fireprotdb.backend.model.ProteinSequenceInterproEntries_;
import cz.loschmidt.fireprotdb.backend.model.ResiduesPK;
import cz.loschmidt.fireprotdb.backend.model.ResiduesPK_;
import cz.loschmidt.fireprotdb.backend.model.StructureResidues;
import cz.loschmidt.fireprotdb.backend.model.StructureSequence;
import cz.loschmidt.fireprotdb.backend.model.StructureSequencePK;
import cz.loschmidt.fireprotdb.backend.model.StructureSequencePK_;
import cz.loschmidt.fireprotdb.backend.model.StructureSequence_;
import cz.loschmidt.fireprotdb.backend.model.Structures;
import cz.loschmidt.fireprotdb.backend.model.Uniprot;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Search criteria builder class
 *
 * @author Juraj Dubrava
 */
public class SearchCriteriaBuilder {

    private final CriteriaBuilder cb;
    // query object
    private final CriteriaQuery<MutationExperiments> query;
    // root entity used in FROM clause
    private final Root<MutationExperiments> rootExperiments;
    // joins of entities representing tables
    private Join<MutationExperiments, Mutations> mutationsJoin;
    private Join<Mutations, Residues> residuesJoin;
    private Join<Residues, StructureResidues> structureResiduesJoin;
    private Join<MutationExperiments, Publications> publicationsJoin;
    private Join<MutationExperiments, Datasets> datasetsJoin;
    private Join<ResiduesPK, ProteinSequence> sequenceJoin;
    private Join<ProteinSequence, Uniprot> uniprotJoin;
    private Join<ProteinSequence, ProteinSequenceBiologicalUnit> sequenceBioUnitJoin;
    private Join<ProteinSequenceBioUnitPK, BiologicalUnit> bioUnitJoin;
    private Join<ProteinSequenceBiologicalUnit, ProteinSequenceBioUnitPK> proteinSeqBioUnitPkJoin;
    private Join<Residues, ResiduesPK> residuesPKJoin;
    private Join<ProteinSequence, StructureSequence> proteinSeqStrucJoin;
    private Join<StructureSequence, StructureSequencePK> proteinSeqStrucPKJoin;
    private Join<StructureSequencePK, Structures> structuresJoin;
    private Join<ProteinSequence, ProteinSequenceInterproEntries> seqInterProJoin;
    private Join<ProteinSequenceInterproEntries, ProteinSequenceInterproEntriesPK> interproPKJoin;
    private Join<ProteinSequenceInterproEntriesPK, InterproEntries> interProJoin;

    private List<Predicate> searchPredicates;

    /**
     * Constructor
     *
     * @param criteriaBuilder criteria Builder object
     */
    public SearchCriteriaBuilder(CriteriaBuilder criteriaBuilder) {
        this.cb = criteriaBuilder;
        this.query = cb.createQuery(MutationExperiments.class);
        this.searchPredicates = new ArrayList<>();
        this.rootExperiments = query.from(MutationExperiments.class);
    }

    /**
     * Get criteria builder
     *
     * @return criteria builder
     */
    public CriteriaBuilder getCriteriaBuilder() {
        return this.cb;
    }

    /**
     * Create SQL query with Criteria API
     *
     * @param specification predicate in WHERE clause
     * @return criteria query
     */
    public CriteriaQuery<MutationExperiments> buildQuery(Predicate specification) {

        query.select(rootExperiments).where(specification);
        return query.distinct(true);
    }

    // create query to show all objects in mutation experiments table
    public CriteriaQuery<MutationExperiments> queryAll() {
        return query.select(rootExperiments);

    }

    // get query
    public CriteriaQuery<MutationExperiments> getQuery() {
        return this.query;
    }

    // get search predicates
    public List<Predicate> getPredicates() {
        return this.searchPredicates;
    }

    // set predicates
    public void setPredicates(List<Predicate> predicates) {
        this.searchPredicates = predicates;
    }

    // get root
    public Root getExperimentRoot() {
        return this.rootExperiments;
    }

    public Join<MutationExperiments, Mutations> getMutationsJoin() {
        if (mutationsJoin == null) {
            mutationsJoin = getExperimentRoot().join(MutationExperiments_.mutation);
        }
        return mutationsJoin;
    }

    public Join<Mutations, Residues> getResiduesJoin() {
        if (residuesJoin == null) {
            residuesJoin = getMutationsJoin().join(Mutations_.residue);
        }
        return residuesJoin;
    }

    public Join<Residues, ResiduesPK> getResiduesPKJoin() {
        if (residuesPKJoin == null) {
            residuesPKJoin = getResiduesJoin().join(Residues_.id);

        }
        return residuesPKJoin;
    }

    public Join<ResiduesPK, ProteinSequence> getSequenceJoin() {
        if (sequenceJoin == null) {
            sequenceJoin = getResiduesPKJoin().join(ResiduesPK_.proteinSequence);
        }
        return sequenceJoin;
    }

    public Join<ProteinSequence, Uniprot> getUniprotJoin() {
        if (uniprotJoin == null) {
            uniprotJoin = getSequenceJoin().join(ProteinSequence_.uniprotRecords, JoinType.LEFT);
        }
        return uniprotJoin;
    }

    public Join<MutationExperiments, Publications> getPublicationsJoin() {
        if (publicationsJoin == null) {
            publicationsJoin = getExperimentRoot().join(MutationExperiments_.publication);
        }
        return publicationsJoin;
    }

    public Join<MutationExperiments, Datasets> getDatasetJoin() {
        if (datasetsJoin == null) {

            datasetsJoin = getExperimentRoot().join(MutationExperiments_.datasets, JoinType.LEFT);
        }

        return datasetsJoin;
    }

    public Join<Residues, StructureResidues> getStructureResiduesJoin() {
        if (structureResiduesJoin == null) {
            structureResiduesJoin = getResiduesJoin().join(Residues_.structureResidues);
        }
        return structureResiduesJoin;
    }

    public Join<ProteinSequence, ProteinSequenceBiologicalUnit> getSequenceBioUnitJoin() {
        if (sequenceBioUnitJoin == null) {
            sequenceBioUnitJoin = getSequenceJoin().join(ProteinSequence_.proteinSeqBioUnit);
        }
        return sequenceBioUnitJoin;
    }

    public Join<ProteinSequenceBiologicalUnit, ProteinSequenceBioUnitPK> getProteinSeqBioUnitPkJoin() {
        if (proteinSeqBioUnitPkJoin == null) {
            proteinSeqBioUnitPkJoin = getSequenceBioUnitJoin().join(ProteinSequenceBiologicalUnit_.id);
        }
        return proteinSeqBioUnitPkJoin;
    }

    public Join<ProteinSequenceBioUnitPK, BiologicalUnit> getBioUnitJoin() {
        if (bioUnitJoin == null) {
            bioUnitJoin = getProteinSeqBioUnitPkJoin().join(ProteinSequenceBioUnitPK_.bioUnit);
        }
        return bioUnitJoin;
    }

    public Join<ProteinSequence, StructureSequence> getSequenceStrucJoin() {
        if (proteinSeqStrucJoin == null) {
            proteinSeqStrucJoin = getSequenceJoin().join(ProteinSequence_.structureSequence);
        }
        return proteinSeqStrucJoin;
    }

    public Join<StructureSequence, StructureSequencePK> getStructurePKJoin() {
        if (proteinSeqStrucPKJoin == null) {
            proteinSeqStrucPKJoin = getSequenceStrucJoin().join(StructureSequence_.id);
        }
        return proteinSeqStrucPKJoin;
    }

    public Join<StructureSequencePK, Structures> getStructureJoin() {
        if (structuresJoin == null) {
            structuresJoin = getStructurePKJoin().join(StructureSequencePK_.structure);
        }
        return structuresJoin;
    }

    public Join<ProteinSequence, ProteinSequenceInterproEntries> getSequenceInterProJoin() {
        if (seqInterProJoin == null) {
            seqInterProJoin = getSequenceJoin().join(ProteinSequence_.proteinSequenceInterproEntries);
        }
        return seqInterProJoin;
    }

    public Join<ProteinSequenceInterproEntries, ProteinSequenceInterproEntriesPK> getInterProPKJoin() {
        if (interproPKJoin == null) {
            interproPKJoin = getSequenceInterProJoin().join(ProteinSequenceInterproEntries_.id);
        }
        return interproPKJoin;
    }

    public Join<ProteinSequenceInterproEntriesPK, InterproEntries> getInterProJoin() {
        if (interProJoin == null) {
            interProJoin = getInterProPKJoin().join(ProteinSequenceInterproEntriesPK_.interproEntry);

        }
        return interProJoin;
    }

}
