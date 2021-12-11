/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.loschmidt.fireprotdb.backend.specifications;

import cz.loschmidt.fireprotdb.backend.dto.OptionsMapEntry;
import cz.loschmidt.fireprotdb.backend.model.MutationExperiments;
import cz.loschmidt.fireprotdb.backend.model.MutationExperiments_;
import cz.loschmidt.fireprotdb.backend.model.Mutations_;
import cz.loschmidt.fireprotdb.backend.repository.MutationExperimentsRepository;
import cz.loschmidt.fireprotdb.backend.repository.SearchCriteriaBuilder;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.Query;
import org.springframework.util.StringUtils;

/**
 * Mutation Criteria API specifications
 *
 * @author Juraj Dubrava
 */
public class MutationSpecifications {

    @Autowired
    static MutationExperimentsRepository experimentsRepository;

    /**
     * All data predicate
     *
     * @param sb search criteria builder
     * @return predicate
     */
    public static Predicate showAll(SearchCriteriaBuilder sb) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();
        return cb.notEqual(root.get(MutationExperiments_.id), 0);
    }

    /**
     * Stabilizing predicate
     *
     * @param sb search criteria builder
     * @param em entity manager
     * @param options additional options
     * @param optionsMap options map
     * @return predicate
     */
    public static Predicate isStabilizing(SearchCriteriaBuilder sb, EntityManager em, List<String> options, Map<String, List<OptionsMapEntry>> optionsMap) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();
        Subquery<MutationExperiments> subquery = sb.getQuery().subquery(MutationExperiments.class);
        Root<MutationExperiments> subRoot = subquery.from(MutationExperiments.class);
        Predicate mutLevel = null;
        Predicate ddgDtmAgree = null;
        Predicate checkPredicate = null;
        // check additional options
        for (String option : options) {
            switch (option) {
                case "apply_mut_level":
                    subquery = sb.getQuery().subquery(MutationExperiments.class);
                    subRoot = subquery.from(MutationExperiments.class);
                    Predicate wrongDdgOrDtm = cb.or(cb.greaterThanOrEqualTo(subRoot.get(MutationExperiments_.ddG), BigDecimal.valueOf(-1)), cb.lessThanOrEqualTo(subRoot.get(MutationExperiments_.dTm), BigDecimal.valueOf(1)));
                    subquery.select(subRoot.get(MutationExperiments_.MUTATION)).where(wrongDdgOrDtm);
                    Expression<String> mutExpression = root.get(MutationExperiments_.MUTATION);
                    mutLevel = mutExpression.in(subquery).not();
                    checkPredicate = mutLevel;
                    break;
                case "ddg_dtm_agree":
                    Query q = em.createNativeQuery("select e.experiment_id from mutation_experiments e where (CASE WHEN e.ddg IS NOT NULL AND e.d_tm IS NOT NULL THEN e.ddg < -1 AND e.d_tm > 1 ELSE e.ddg < -1 OR e.d_tm > 1 END)");
                    List<String> ids = q.getResultList();
                    subquery = sb.getQuery().subquery(MutationExperiments.class);
                    subRoot = subquery.from(MutationExperiments.class);
                    Expression<String> idsExpression = root.get(MutationExperiments_.ID);
                    ddgDtmAgree = idsExpression.in(ids);
                    checkPredicate = ddgDtmAgree;
                    break;
            }
        }

        if (options.size() == 2) {
            checkPredicate = cb.and(mutLevel, ddgDtmAgree);
            return checkPredicate;
        } else if (options.size() == 1) {
            return checkPredicate;
        }

        return cb.isTrue(cb.or(cb.lessThan(root.get(MutationExperiments_.ddG), -1), cb.greaterThan(root.get(MutationExperiments_.dTm), 1)));
    }

    /**
     * Destabilizing predicate
     *
     * @param sb search criteria builder
     * @param em entity manager
     * @param options additional options
     * @param optionsMap options map
     * @return predicate
     */
    public static Predicate isDestabilizing(SearchCriteriaBuilder sb, EntityManager em, List<String> options, Map<String, List<OptionsMapEntry>> optionsMap) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();

        Subquery<MutationExperiments> subquery = sb.getQuery().subquery(MutationExperiments.class);
        Root<MutationExperiments> subRoot = subquery.from(MutationExperiments.class);
        Predicate mutLevel = null;
        Predicate ddgDtmAgree = null;
        Predicate checkPredicate = null;

        for (String option : options) {
            switch (option) {
                case "apply_mut_level":
                    subquery = sb.getQuery().subquery(MutationExperiments.class);
                    subRoot = subquery.from(MutationExperiments.class);
                    Predicate wrongDdgOrDtm = cb.or(cb.lessThanOrEqualTo(subRoot.get(MutationExperiments_.ddG), BigDecimal.valueOf(1)), cb.greaterThanOrEqualTo(subRoot.get(MutationExperiments_.dTm), BigDecimal.valueOf(-1)));
                    subquery.select(subRoot.get(MutationExperiments_.MUTATION)).where(wrongDdgOrDtm);
                    Expression<String> mutExpression = root.get(MutationExperiments_.MUTATION);
                    mutLevel = mutExpression.in(subquery).not();
                    checkPredicate = mutLevel;
                    break;
                case "ddg_dtm_agree":
                    Query q = em.createNativeQuery("select e.experiment_id from mutation_experiments e where (CASE WHEN e.ddg IS NOT NULL AND e.d_tm IS NOT NULL THEN e.ddg > 1 and e.d_tm < -1 ELSE e.ddg > 1 OR e.d_tm < -1 END)");
                    List<String> ids = q.getResultList();
                    subquery = sb.getQuery().subquery(MutationExperiments.class);
                    subRoot = subquery.from(MutationExperiments.class);
                    Expression<String> idsExpression = root.get(MutationExperiments_.ID);
                    ddgDtmAgree = idsExpression.in(ids);
                    checkPredicate = ddgDtmAgree;
                    break;
            }

        }

        if (options.size() == 2) {
            checkPredicate = cb.and(mutLevel, ddgDtmAgree);
            return checkPredicate;
        } else if (options.size() == 1) {
            return checkPredicate;
        }

        return cb.isTrue(cb.or(cb.greaterThan(root.get(MutationExperiments_.ddG), 1), cb.lessThan(root.get(MutationExperiments_.dTm), -1)));
    }

    /**
     * Neutral predicate
     * @param sb search criteria builder
     * @param em entity manager
     * @param options additional options
     * @param optionsMap options map
     * @return predicate
     */
    public static Predicate isNeutral(SearchCriteriaBuilder sb, EntityManager em, List<String> options, Map<String, List<OptionsMapEntry>> optionsMap) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();

        Subquery<MutationExperiments> subquery = sb.getQuery().subquery(MutationExperiments.class);
        Root<MutationExperiments> subRoot = subquery.from(MutationExperiments.class);

        Predicate mutLevel = null;
        Predicate ddgDtmAgree = null;
        Predicate checkPredicate = null;

        for (String option : options) {
            switch (option) {
                case "apply_mut_level":
                    subquery = sb.getQuery().subquery(MutationExperiments.class);
                    subRoot = subquery.from(MutationExperiments.class);
                    Predicate wrongDdg = cb.or(cb.lessThan(subRoot.get(MutationExperiments_.ddG), BigDecimal.valueOf(-1)), cb.greaterThan(subRoot.get(MutationExperiments_.ddG), BigDecimal.valueOf(1)));
                    Predicate wrongDtm = cb.or(cb.lessThan(subRoot.get(MutationExperiments_.dTm), BigDecimal.valueOf(-1)), cb.greaterThan(subRoot.get(MutationExperiments_.dTm), BigDecimal.valueOf(1)));
                    Predicate wrongDdgOrDtm = cb.or(wrongDdg, wrongDtm);
                    subquery.select(subRoot.get(MutationExperiments_.MUTATION)).where(wrongDdgOrDtm);
                    Expression<String> mutExpression = root.get(MutationExperiments_.MUTATION);
                    mutLevel = mutExpression.in(subquery).not();
                    checkPredicate = mutLevel;
                    break;
                case "ddg_dtm_agree":

                    Query q = em.createNativeQuery("select e.experiment_id from mutation_experiments e where (CASE WHEN e.ddg IS NOT NULL AND e.d_tm IS NOT NULL THEN (e.ddg >= -1 AND e.ddg <= 1) and (e.d_tm >= -1 AND e.d_tm <= 1) ELSE (e.ddg >= -1 AND e.ddg <= 1) OR (e.d_tm >= -1 AND e.d_tm <= 1) END)");
                    List<String> ids = q.getResultList();
                    subquery = sb.getQuery().subquery(MutationExperiments.class);
                    subRoot = subquery.from(MutationExperiments.class);
                    Expression<String> idsExpression = root.get(MutationExperiments_.ID);
                    ddgDtmAgree = idsExpression.in(ids);
                    checkPredicate = ddgDtmAgree;
                    break;
            }
        }

        if (options.size() == 2) {
            checkPredicate = cb.and(mutLevel, ddgDtmAgree);
            return checkPredicate;
        } else if (options.size() == 1) {
            return checkPredicate;
        }

        Predicate ddGNeutral = cb.and(cb.greaterThanOrEqualTo(root.get(MutationExperiments_.ddG), -1), cb.lessThanOrEqualTo(root.get(MutationExperiments_.ddG), 1));
        Predicate dTmNeutral = cb.and(cb.greaterThanOrEqualTo(root.get(MutationExperiments_.dTm), -1), cb.lessThanOrEqualTo(root.get(MutationExperiments_.dTm), 1));

        return cb.isTrue(cb.or(ddGNeutral, dTmNeutral));
    }

    /**
     * Mutation id predicate
     * @param sb search criteria builder
     * @param mutationId mutation identifier
     * @return predicate
     */
    public static Predicate hasId(SearchCriteriaBuilder sb, String mutationId) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Join root = sb.getMutationsJoin();
        return cb.equal(root.get(Mutations_.id), mutationId);
    }

    /**
     * Mutated residue predicate
     * @param sb search criteria builder
     * @param aminoAcid mutated amino acid
     * @return predicate
     */
    public static Predicate hasMutatedAminoAcid(SearchCriteriaBuilder sb, String aminoAcid) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Join root = sb.getMutationsJoin();
        return cb.equal(root.get(Mutations_.mutatedAminoAcid), aminoAcid);

    }

    /**
     * Curation predicate
     * @param sb search criteria builder
     * @return predicate
     */
    public static Predicate isCurrated(SearchCriteriaBuilder sb) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();
        return cb.equal(root.get(MutationExperiments_.currated), true);
    }

    /**
     * Not curated experiment predicate
     * @param sb search criteria builder
     * @return predicate
     */
    public static Predicate isNotCurrated(SearchCriteriaBuilder sb) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();
        return cb.equal(root.get(MutationExperiments_.currated), false);
    }

    /**
     * Scan rate existence predicate
     * @param sb search criteria builder
     * @return predicate
     */
    public static Predicate existsScanRate(SearchCriteriaBuilder sb) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();
        return cb.isNotNull(root.get(MutationExperiments_.scanRate));
    }

    /**
     * Scan rate not exists predicate
     * @param sb search criteria builder
     * @return predicate
     */
    public static Predicate notExistsScanRate(SearchCriteriaBuilder sb) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();
        return cb.isNull(root.get(MutationExperiments_.scanRate));
    }

    /**
     * Mutation experiment has scan-rate value predicate
     * @param sb search criteria builder
     * @param scanRate scan-rate value
     * @return predicate
     */
    public static Predicate hasScanRate(SearchCriteriaBuilder sb, String scanRate) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();

        String splitScanRate[] = StringUtils.split(scanRate, " ");

        String operator = "";
        String value = "";

        if (splitScanRate.length == 1) {
            operator = splitScanRate[0];
        } else {
            operator = splitScanRate[0];
            value = splitScanRate[1];
        }

        if (operator.equals(">")) {
            return cb.greaterThan(root.get(MutationExperiments_.scanRate), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals(">=")) {
            return cb.greaterThanOrEqualTo(root.get(MutationExperiments_.scanRate), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals("<")) {
            return cb.lessThan(root.get(MutationExperiments_.scanRate), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals("<=")) {
            return cb.lessThanOrEqualTo(root.get(MutationExperiments_.scanRate), BigDecimal.valueOf(Double.valueOf(value)));
        }
        return cb.equal(root.get(MutationExperiments_.scanRate), BigDecimal.valueOf(Double.valueOf(value)));

    }

    /**
     * Mutation experiment has protein concentration value predicate
     * @param sb search criteria builder
     * @return predicate
     */
    public static Predicate existsProteinConcentration(SearchCriteriaBuilder sb) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();
        return cb.isNotNull(root.get(MutationExperiments_.proteinConcentration));
    }

    /**
     * Mutation experiment does not have protein concentration value predicate
     * @param sb search criteria builder
     * @return predicate
     */
    public static Predicate notExistsProteinConcentration(SearchCriteriaBuilder sb) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();
        return cb.isNull(root.get(MutationExperiments_.proteinConcentration));
    }

    /**
     * Mutation experiment has protein concentration value
     * @param sb search criteria builder
     * @param concentration concentration value
     * @return predicate
     */
    public static Predicate hasProteinConcentration(SearchCriteriaBuilder sb, String concentration) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();

        String splitConcentration[] = StringUtils.split(concentration, " ");

        String operator = "";
        String value = "";

        if (splitConcentration.length == 1) {
            operator = splitConcentration[0];
        } else {
            operator = splitConcentration[0];
            value = splitConcentration[1];
        }

        if (operator.equals(">")) {
            return cb.greaterThan(root.get(MutationExperiments_.proteinConcentration), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals(">=")) {
            return cb.greaterThanOrEqualTo(root.get(MutationExperiments_.proteinConcentration), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals("<")) {
            return cb.lessThan(root.get(MutationExperiments_.proteinConcentration), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals("<=")) {
            return cb.lessThanOrEqualTo(root.get(MutationExperiments_.proteinConcentration), BigDecimal.valueOf(Double.valueOf(value)));
        }

        return cb.equal(root.get(MutationExperiments_.proteinConcentration), BigDecimal.valueOf(Double.valueOf(value)));
    }

    /**
     * Purity of sample value exists
     * @param sb search criteria builder
     * @return predicate
     */
    public static Predicate existsSamplePurity(SearchCriteriaBuilder sb) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();
        return cb.isNotNull(root.get(MutationExperiments_.purityOfSample));
    }

    /**
     * Purity of sample does not exist
     * @param sb search criteria builder
     * @return predicate
     */
    public static Predicate notExistsSamplePurity(SearchCriteriaBuilder sb) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();
        return cb.isNull(root.get(MutationExperiments_.purityOfSample));
    }

    /**
     * Mutation experiment has purity of sample value
     * @param sb search criteria builder
     * @param purityValue purity of sample value
     * @return predicate
     */
    public static Predicate hasSamplePurity(SearchCriteriaBuilder sb, String purityValue) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();

        String splitPurity[] = StringUtils.split(purityValue, " ");
        String operator = "";
        String value = "";

        if (splitPurity.length == 1) {
            operator = splitPurity[0];
        } else {
            operator = splitPurity[0];
            value = splitPurity[1];
        }

        if (operator.equals(">")) {
            return cb.greaterThan(root.get(MutationExperiments_.purityOfSample), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals(">=")) {
            return cb.greaterThanOrEqualTo(root.get(MutationExperiments_.purityOfSample), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals("<")) {
            return cb.lessThan(root.get(MutationExperiments_.purityOfSample), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals("<=")) {
            return cb.lessThanOrEqualTo(root.get(MutationExperiments_.purityOfSample), BigDecimal.valueOf(Double.valueOf(value)));
        }

        return cb.equal(root.get(MutationExperiments_.purityOfSample), BigDecimal.valueOf(Double.valueOf(value)));
    }

    /**
     * Mutation experiment has T offset value
     * @param sb search criteria builder
     * @return predicate
     */
    public static Predicate existsTOffset(SearchCriteriaBuilder sb) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();
        return cb.isNotNull(root.get(MutationExperiments_.tOffset));
    }

    /**
     * Mutation experiment does not have T offset value
     * @param sb search criteria builder
     * @return predicate
     */
    public static Predicate notExistsTOffset(SearchCriteriaBuilder sb) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();
        return cb.isNull(root.get(MutationExperiments_.tOffset));
    }

    /**
     * Mutation experiment has T offset value
     * @param sb search criteria builder
     * @param tOffset T offset value
     * @return predicate
     */
    public static Predicate hasTOffset(SearchCriteriaBuilder sb, String tOffset) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();

        String splitToffset[] = StringUtils.split(tOffset, " ");
        String operator = "";
        String value = "";

        if (splitToffset.length == 1) {
            operator = splitToffset[0];
        } else {
            operator = splitToffset[0];
            value = splitToffset[1];
        }

        if (operator.equals(">")) {
            return cb.greaterThan(root.get(MutationExperiments_.tOffset), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals(">=")) {
            return cb.greaterThanOrEqualTo(root.get(MutationExperiments_.tOffset), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals("<")) {
            return cb.lessThan(root.get(MutationExperiments_.tOffset), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals("<=")) {
            return cb.lessThanOrEqualTo(root.get(MutationExperiments_.tOffset), BigDecimal.valueOf(Double.valueOf(value)));
        }

        return cb.equal(root.get(MutationExperiments_.tOffset), BigDecimal.valueOf(Double.valueOf(value)));
    }

    /**
     * Mutation experiment has pH 
     * @param sb search criteria builder
     * @return predicate
     */
    public static Predicate existsPh(SearchCriteriaBuilder sb) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();
        return cb.isNotNull(root.get(MutationExperiments_.pH));
    }

    /**
     * Mutation experiment does not have pH
     * @param sb search criteria builder
     * @return predicate
     */
    public static Predicate notExistsPh(SearchCriteriaBuilder sb) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();
        return cb.isNull(root.get(MutationExperiments_.pH));
    }

    /**
     * Mutation experiment has pH with certain value
     * @param sb search criteria builder
     * @param ph pH value
     * @return predicate
     */
    public static Predicate hasPh(SearchCriteriaBuilder sb, String ph) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();

        String splitPh[] = StringUtils.split(ph, " ");
        String operator = splitPh[0];
        String value = splitPh[1];
        if (operator.equals(">")) {
            return cb.greaterThan(root.get(MutationExperiments_.pH), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals(">=")) {
            return cb.greaterThanOrEqualTo(root.get(MutationExperiments_.pH), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals("<")) {
            return cb.lessThan(root.get(MutationExperiments_.pH), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals("<=")) {
            return cb.lessThanOrEqualTo(root.get(MutationExperiments_.pH), BigDecimal.valueOf(Double.valueOf(value)));
        }

        return cb.equal(root.get(MutationExperiments_.pH), BigDecimal.valueOf(Double.valueOf(value)));

    }

    /**
     * Mutation experiment has ddG
     * @param sb search criteria builder
     * @return predicate
     */
    public static Predicate existsDDG(SearchCriteriaBuilder sb) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();
        return cb.isNotNull(root.get(MutationExperiments_.ddG));
    }

    /**
     * Mutation experiment does not have ddG
     * @param sb search criteria builder
     * @return predicate
     */
    public static Predicate notExistsDDG(SearchCriteriaBuilder sb) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();
        return cb.isNull(root.get(MutationExperiments_.ddG));
    }

    /**
     * Mutation experiment has ddG with certain value
     * @param sb search criteria builder
     * @param ddg ddG value
     * @param options additional options
     * @return predicate
     */
    public static Predicate hasDDG(SearchCriteriaBuilder sb, String ddg, List<String> options) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();

        String splitDdg[] = StringUtils.split(ddg, " ");
        String operator = "";
        String value = "";
        if (splitDdg.length == 1) {
            operator = splitDdg[0];
        } else {
            operator = splitDdg[0];
            value = splitDdg[1];
        }

        if (operator.equals(">")) {
            if (options.size() > 0) {
                Subquery<MutationExperiments> subquery = sb.getQuery().subquery(MutationExperiments.class);
                Root<MutationExperiments> subRoot = subquery.from(MutationExperiments.class);
                subquery.select(subRoot.get(MutationExperiments_.MUTATION)).where(cb.greaterThan(subRoot.get(MutationExperiments_.ddG), BigDecimal.valueOf(Double.valueOf(value))));
                Expression<String> mutExpression = root.get(MutationExperiments_.MUTATION);
                Predicate predIn = mutExpression.in(subquery).not();
                return cb.and(cb.isNotNull(root.get(MutationExperiments_.ddG)), predIn);

            }

            return cb.greaterThan(root.get(MutationExperiments_.ddG), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals(">=")) {
            if (options.size() > 0) {
                Subquery<MutationExperiments> subquery = sb.getQuery().subquery(MutationExperiments.class);
                Root<MutationExperiments> subRoot = subquery.from(MutationExperiments.class);

                subquery.select(subRoot.get(MutationExperiments_.MUTATION)).where(cb.greaterThan(subRoot.get(MutationExperiments_.ddG), BigDecimal.valueOf(Double.valueOf(value))));
                Expression<String> mutExpression = root.get(MutationExperiments_.MUTATION);
                Predicate predIn = mutExpression.in(subquery).not();
                return cb.and(cb.isNotNull(root.get(MutationExperiments_.ddG)), predIn);
            }

            return cb.greaterThanOrEqualTo(root.get(MutationExperiments_.ddG), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals("<")) {
            if (options.size() > 0) {
                Subquery<MutationExperiments> subquery = sb.getQuery().subquery(MutationExperiments.class);
                Root<MutationExperiments> subRoot = subquery.from(MutationExperiments.class);

                subquery.select(subRoot.get(MutationExperiments_.MUTATION)).where(cb.lessThan(subRoot.get(MutationExperiments_.ddG), BigDecimal.valueOf(Double.valueOf(value))));
                Expression<String> mutExpression = root.get(MutationExperiments_.MUTATION);
                Predicate predIn = mutExpression.in(subquery).not();
                return cb.and(cb.isNotNull(root.get(MutationExperiments_.ddG)), predIn);
            }
            return cb.lessThan(root.get(MutationExperiments_.ddG), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals("<=")) {
            if (options.size() > 0) {
                Subquery<MutationExperiments> subquery = sb.getQuery().subquery(MutationExperiments.class);
                Root<MutationExperiments> subRoot = subquery.from(MutationExperiments.class);
                subquery.select(subRoot.get(MutationExperiments_.MUTATION)).where(cb.lessThanOrEqualTo(subRoot.get(MutationExperiments_.ddG), BigDecimal.valueOf(Double.valueOf(value))));
                Expression<String> mutExpression = root.get(MutationExperiments_.MUTATION);
                Predicate predIn = mutExpression.in(subquery).not();
                return cb.and(cb.isNotNull(root.get(MutationExperiments_.ddG)), predIn);
            }
            return cb.lessThanOrEqualTo(root.get(MutationExperiments_.ddG), BigDecimal.valueOf(Double.valueOf(value)));
        } 
        else {
            // additional options are present
            if (options.size() > 0) {
                Subquery<MutationExperiments> subquery = sb.getQuery().subquery(MutationExperiments.class);
                Root<MutationExperiments> subRoot = subquery.from(MutationExperiments.class);
                Predicate p1 = cb.greaterThan(subRoot.get(MutationExperiments_.ddG), BigDecimal.valueOf(Double.valueOf(value)));
                Predicate p2 = cb.lessThan(subRoot.get(MutationExperiments_.ddG), BigDecimal.valueOf(Double.valueOf(value)));
                Predicate orPred = cb.or(p1, p2);

                subquery.select(subRoot.get(MutationExperiments_.MUTATION)).where(orPred);
                Expression<String> mutExpression = root.get(MutationExperiments_.MUTATION);
                Predicate predIn = mutExpression.in(subquery).not();
                return cb.and(cb.isNotNull(root.get(MutationExperiments_.ddG)), predIn);

            }
            return cb.equal(root.get(MutationExperiments_.ddG), BigDecimal.valueOf(Double.valueOf(value)));
        }
    }

    /**
     * Mutation experiment has dTm
     * @param sb search criteria builder
     * @return predicate
     */
    public static Predicate existsDtm(SearchCriteriaBuilder sb) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();
        return cb.isNotNull(root.get(MutationExperiments_.dTm));
    }

    /**
     * Mutation experiment does not have dTm
     * @param sb search criteria builder
     * @return predicate
     */
    public static Predicate notExistsDtm(SearchCriteriaBuilder sb) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();
        return cb.isNull(root.get(MutationExperiments_.dTm));
    }

    /**
     * Mutation experiment has dTm with certain value
     * @param sb search criteria builder
     * @param dtm dTm value
     * @param options additional options
     * @return
     */
    public static Predicate hasDTM(SearchCriteriaBuilder sb, String dtm, List<String> options) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();

        String splitDtm[] = StringUtils.split(dtm, " ");

        String operator = "";
        String value = "";
        if (splitDtm.length == 1) {
            operator = splitDtm[0];
        } else {
            operator = splitDtm[0];
            value = splitDtm[1];
        }

        if (operator.equals(">")) {
            if (options.size() > 0) {
                Subquery<MutationExperiments> subquery = sb.getQuery().subquery(MutationExperiments.class);
                Root<MutationExperiments> subRoot = subquery.from(MutationExperiments.class);
                subquery.select(subRoot.get(MutationExperiments_.MUTATION)).where(cb.greaterThan(subRoot.get(MutationExperiments_.ddG), BigDecimal.valueOf(Double.valueOf(value))));
                Expression<String> mutExpression = root.get(MutationExperiments_.MUTATION);
                Predicate predIn = mutExpression.in(subquery).not();
                return cb.and(cb.isNotNull(root.get(MutationExperiments_.ddG)), predIn);

            }
            return cb.greaterThan(root.get(MutationExperiments_.dTm), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals(">=")) {
            if (options.size() > 0) {
                Subquery<MutationExperiments> subquery = sb.getQuery().subquery(MutationExperiments.class);
                Root<MutationExperiments> subRoot = subquery.from(MutationExperiments.class);
                subquery.select(subRoot.get(MutationExperiments_.MUTATION)).where(cb.greaterThanOrEqualTo(subRoot.get(MutationExperiments_.ddG), BigDecimal.valueOf(Double.valueOf(value))));
                Expression<String> mutExpression = root.get(MutationExperiments_.MUTATION);
                Predicate predIn = mutExpression.in(subquery).not();
                return cb.and(cb.isNotNull(root.get(MutationExperiments_.ddG)), predIn);

            }
            return cb.greaterThanOrEqualTo(root.get(MutationExperiments_.dTm), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals("<")) {
            if (options.size() > 0) {
                Subquery<MutationExperiments> subquery = sb.getQuery().subquery(MutationExperiments.class);
                Root<MutationExperiments> subRoot = subquery.from(MutationExperiments.class);
                subquery.select(subRoot.get(MutationExperiments_.MUTATION)).where(cb.lessThan(subRoot.get(MutationExperiments_.ddG), BigDecimal.valueOf(Double.valueOf(value))));
                Expression<String> mutExpression = root.get(MutationExperiments_.MUTATION);
                Predicate predIn = mutExpression.in(subquery).not();
                return cb.and(cb.isNotNull(root.get(MutationExperiments_.ddG)), predIn);

            }
            return cb.lessThan(root.get(MutationExperiments_.dTm), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals("<=")) {
            if (options.size() > 0) {
                Subquery<MutationExperiments> subquery = sb.getQuery().subquery(MutationExperiments.class);
                Root<MutationExperiments> subRoot = subquery.from(MutationExperiments.class);
                subquery.select(subRoot.get(MutationExperiments_.MUTATION)).where(cb.lessThanOrEqualTo(subRoot.get(MutationExperiments_.ddG), BigDecimal.valueOf(Double.valueOf(value))));
                Expression<String> mutExpression = root.get(MutationExperiments_.MUTATION);
                Predicate predIn = mutExpression.in(subquery).not();
                return cb.and(cb.isNotNull(root.get(MutationExperiments_.ddG)), predIn);

            }
            return cb.lessThanOrEqualTo(root.get(MutationExperiments_.dTm), BigDecimal.valueOf(Double.valueOf(value)));
        } else {
            if (options.size() > 0) {
                Subquery<MutationExperiments> subquery = sb.getQuery().subquery(MutationExperiments.class);
                Root<MutationExperiments> subRoot = subquery.from(MutationExperiments.class);
                Predicate p1 = cb.greaterThan(subRoot.get(MutationExperiments_.ddG), BigDecimal.valueOf(Double.valueOf(value)));
                Predicate p2 = cb.lessThan(subRoot.get(MutationExperiments_.ddG), BigDecimal.valueOf(Double.valueOf(value)));
                Predicate orPred = cb.or(p1, p2);

                subquery.select(subRoot.get(MutationExperiments_.MUTATION)).where(orPred);
                Expression<String> mutExpression = root.get(MutationExperiments_.MUTATION);
                Predicate predIn = mutExpression.in(subquery).not();
                return cb.and(cb.isNotNull(root.get(MutationExperiments_.ddG)), predIn);
            }
            return cb.equal(root.get(MutationExperiments_.dTm), BigDecimal.valueOf(Double.valueOf(value)));
        }

    }

    /**
     * Mutation experiment has Tm
     * @param sb search criteria builder
     * @return predicate
     */
    public static Predicate existsTm(SearchCriteriaBuilder sb) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();
        return cb.isNotNull(root.get(MutationExperiments_.tm));
    }

    /**
     * Mutation experiment does not have Tm
     * @param sb search criteria builder
     * @return predicate
     */
    public static Predicate notExistsTm(SearchCriteriaBuilder sb) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();
        return cb.isNull(root.get(MutationExperiments_.tm));
    }

    /**
     * Mutation experiment has Tm with certain value
     * @param sb search criteria builder
     * @param tm Tm value
     * @return predicate
     */
    public static Predicate hasTm(SearchCriteriaBuilder sb, String tm) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();

        String splitTm[] = StringUtils.split(tm, " ");
        String operator = "";
        String value = "";

        if (splitTm.length == 1) {
            operator = splitTm[0];
        } else {
            operator = splitTm[0];
            value = splitTm[1];
        }

        if (operator.equals(">")) {
            return cb.greaterThan(root.get(MutationExperiments_.tm), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals(">=")) {
            return cb.greaterThanOrEqualTo(root.get(MutationExperiments_.tm), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals("<")) {
            return cb.lessThan(root.get(MutationExperiments_.tm), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals("<=")) {
            return cb.lessThanOrEqualTo(root.get(MutationExperiments_.tm), BigDecimal.valueOf(Double.valueOf(value)));
        }
        return cb.equal(root.get(MutationExperiments_.tm), BigDecimal.valueOf(Double.valueOf(value)));
    }

    /**
     * Mutation experiment has Cp
     * @param sb search criteria builder
     * @return predicate
     */
    public static Predicate existsCp(SearchCriteriaBuilder sb) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();
        return cb.isNotNull(root.get(MutationExperiments_.cp));
    }

    /**
     * Mutation experiment does not have Cp
     * @param sb search criteria builder
     * @return predicate
     */
    public static Predicate notExistsCp(SearchCriteriaBuilder sb) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();
        return cb.isNull(root.get(MutationExperiments_.cp));
    }

    /**
     * Mutation experiment has Cp with certain value
     * @param sb search criteria builder
     * @param cp Cp value
     * @return predicate
     */
    public static Predicate hasCp(SearchCriteriaBuilder sb, String cp) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();

        String splitCp[] = StringUtils.split(cp, " ");

        String operator = "";
        String value = "";

        if (splitCp.length == 1) {
            operator = splitCp[0];
        } else {
            operator = splitCp[0];
            value = splitCp[1];
        }

        if (operator.equals(">")) {
            return cb.greaterThan(root.get(MutationExperiments_.cp), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals(">=")) {
            return cb.greaterThanOrEqualTo(root.get(MutationExperiments_.cp), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals("<")) {
            return cb.lessThan(root.get(MutationExperiments_.cp), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals("<=")) {
            return cb.lessThanOrEqualTo(root.get(MutationExperiments_.cp), BigDecimal.valueOf(Double.valueOf(value)));
        }

        return cb.equal(root.get(MutationExperiments_.cp), BigDecimal.valueOf(Double.valueOf(value)));
    }

    /**
     * Mutation experiment has half life
     * @param sb search criteria builder
     * @return predicate
     */
    public static Predicate existsHalfLife(SearchCriteriaBuilder sb) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();
        return cb.isNotNull(root.get(MutationExperiments_.halfLife));
    }

    /**
     * Mutation experiment does not have half life
     * @param sb search criteria builder
     * @return predicate
     */
    public static Predicate notExistsHalfLife(SearchCriteriaBuilder sb) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();
        return cb.isNull(root.get(MutationExperiments_.halfLife));
    }

    /**
     * Mutation experiment has half life with certain value
     * @param sb search criteria builder
     * @param halfLife half life value
     * @return predicate
     */
    public static Predicate hasHalfLife(SearchCriteriaBuilder sb, String halfLife) {
        CriteriaBuilder cb = sb.getCriteriaBuilder();
        Root root = sb.getExperimentRoot();

        String splitHalfLife[] = StringUtils.split(halfLife, " ");

        String operator = "";
        String value = "";

        if (splitHalfLife.length == 1) {
            operator = splitHalfLife[0];
        } else {
            operator = splitHalfLife[0];
            value = splitHalfLife[1];
        }

        if (operator.equals(">")) {
            return cb.greaterThan(root.get(MutationExperiments_.halfLife), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals(">=")) {
            return cb.greaterThanOrEqualTo(root.get(MutationExperiments_.halfLife), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals("<")) {
            return cb.lessThan(root.get(MutationExperiments_.halfLife), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (operator.equals("<=")) {
            return cb.lessThanOrEqualTo(root.get(MutationExperiments_.halfLife), BigDecimal.valueOf(Double.valueOf(value)));
        }

        return cb.equal(root.get(MutationExperiments_.halfLife), BigDecimal.valueOf(Double.valueOf(value)));

    }

}
