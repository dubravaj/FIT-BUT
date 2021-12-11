/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.loschmidt.fireprotdb.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.loschmidt.fireprotdb.backend.dto.AminoAcidsSubstitutionsMapDTO;
import cz.loschmidt.fireprotdb.backend.dto.ChartsStatisticsDTO;
import cz.loschmidt.fireprotdb.backend.dto.StatisticsDTO;
import cz.loschmidt.fireprotdb.backend.model.AminoAcidsSubstitution;
import cz.loschmidt.fireprotdb.backend.model.AminoAcidsSubstitutionMap;
import cz.loschmidt.fireprotdb.backend.model.AminoAcidsSubstitutionsReducedMap;
import cz.loschmidt.fireprotdb.backend.model.DdgHistogramEntry;
import cz.loschmidt.fireprotdb.backend.model.DtmHistogramEntry;
import cz.loschmidt.fireprotdb.backend.model.InterproEntries;
import cz.loschmidt.fireprotdb.backend.model.MutationExperiments;
import cz.loschmidt.fireprotdb.backend.model.Mutations;
import cz.loschmidt.fireprotdb.backend.model.OverallStatisticsHistory;
import cz.loschmidt.fireprotdb.backend.model.ProteinSequence;
import cz.loschmidt.fireprotdb.backend.model.ProteinSequenceInterproEntries;
import cz.loschmidt.fireprotdb.backend.model.Residues;
import cz.loschmidt.fireprotdb.backend.model.TopInterProFamilies;
import cz.loschmidt.fireprotdb.backend.model.TopProteins;
import cz.loschmidt.fireprotdb.backend.repository.AminoAcidsSubstitutionMapRepository;
import cz.loschmidt.fireprotdb.backend.repository.AminoAcidsSubstitutionRepository;
import cz.loschmidt.fireprotdb.backend.repository.AminoAcidsSubstitutionsReducedMapRepository;
import cz.loschmidt.fireprotdb.backend.repository.BiologicalUnitRepository;
import cz.loschmidt.fireprotdb.backend.repository.DdgHistogramRepository;
import cz.loschmidt.fireprotdb.backend.repository.DtmHistogramRepository;
import cz.loschmidt.fireprotdb.backend.repository.InterproEntriesRepository;
import cz.loschmidt.fireprotdb.backend.repository.MutationExperimentsRepository;
import cz.loschmidt.fireprotdb.backend.repository.MutationsRepository;
import cz.loschmidt.fireprotdb.backend.repository.OverallStatisticsHistoryRepository;
import cz.loschmidt.fireprotdb.backend.repository.ProteinSequenceRepository;
import cz.loschmidt.fireprotdb.backend.repository.TopInterProFamiliesRepository;
import cz.loschmidt.fireprotdb.backend.repository.TopProteinsRepository;
import java.math.BigDecimal;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Statistics service implementation
 *
 *
 * @author Juraj Dubrava
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Value("${piwik.url}")
    private String piwikUrl;

    @Value("${piwik.token}")
    private String piwikToken;

    @Value("${piwik.siteid}")
    private String piwikSiteId;

    @Autowired
    MutationExperimentsRepository mutationExperimentsRepository;

    @Autowired
    MutationsRepository mutationsRepository;

    @Autowired
    ProteinSequenceRepository proteinSequenceRepository;

    @Autowired
    BiologicalUnitRepository biologicalUnitRepository;

    @Autowired
    TopProteinsRepository topProteinsRepository;
    
    @Autowired
    TopInterProFamiliesRepository topInterProFamiliesRepository;
    
    @Autowired
    InterproEntriesRepository interProFamiliesRepository;

    @Autowired
    OverallStatisticsHistoryRepository overallStatisticsRepository;

    @Autowired
    AminoAcidsSubstitutionRepository aminoAcidsSubstitutionRepository;

    @Autowired
    AminoAcidsSubstitutionMapRepository aminoAcidsSubstitutionMapRepository;
    
    @Autowired
    AminoAcidsSubstitutionsReducedMapRepository aminoAcidsSubstitutionsReducedMapRepository;
    

    @Autowired
    DdgHistogramRepository ddgHistogramRepository;

    @Autowired
    DtmHistogramRepository dtmHistogramRepository;

    private final Logger logger = LoggerFactory.getLogger(StatisticsServiceImpl.class);

    private RestTemplate restTemplate = new RestTemplate();
    
    private final int binsCount = 12;
    
    

    /**
     * Get number of visitors
     *
     * @return number of visitors
     */
    public String getNumberOfVisitorsJSON() {
        if (Objects.isNull(piwikUrl) || Objects.isNull(piwikSiteId) || Objects.isNull(piwikToken)) {
            return "0";
        }

        String url = piwikUrl + "/index.php?module=API&method=VisitsSummary.getVisits&idSite=" + piwikSiteId + "&period=range&date=2013-01-01,today&format=json&token_auth=".concat(piwikToken);
        String response = this.restTemplate.getForObject(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = null;

        try {
            rootNode = objectMapper.readTree(response);
        } catch (JsonProcessingException ex) {
            this.logger.info(ex.getMessage());
            return "0";
        }

        String numberOfVisitors = rootNode.get("value").asText();

        return numberOfVisitors;
    }

    /**
     * Get database statistics
     *
     * @return object with statistics
     */
    public StatisticsDTO getFireprotDBStats() {

        StatisticsDTO dataStatsDTO = new StatisticsDTO();
        dataStatsDTO.setSequencesCount(getSequenceCount());
        dataStatsDTO.setMutationExperimentsCount(getMutationExperimentsCount());
        dataStatsDTO.setCuratedExperimentsCount(getCuratedExperimentsCount());
        dataStatsDTO.setUncuratedExperimentsCount(getUncuratedExperimentsCount());
        dataStatsDTO.setStructuresCount(getBioUnitsCount());
        dataStatsDTO.setMutationsCount(getMutationsCount());
        dataStatsDTO.setNumberOfVisitors(getNumberOfVisitorsJSON());
        return dataStatsDTO;

    }

    /**
     * Get number of biological units in the DB
     *
     * @return number of biological units
     */
    private Long getBioUnitsCount() {
        Long allBioUnits = biologicalUnitRepository.count();
        return allBioUnits;
    }

    /**
     * Get number of proteins in the DB
     *
     * @return number of proteins
     */
    private Long getSequenceCount() {
        Long allSequences = proteinSequenceRepository.count();
        return allSequences;
    }

    /**
     * Get number of mutation in the DB
     *
     * @return number of mutations
     */
    private Long getMutationsCount() {
        Long allMutations = mutationsRepository.count();
        return allMutations;
    }

    /**
     * Get number of experiments in the DB
     *
     * @return number of experiments
     */
    private Long getMutationExperimentsCount() {
        Long allExperiments = mutationExperimentsRepository.count();
        return allExperiments;
    }

    /**
     * Get number of curated experiments in the DB
     *
     * @return number of curated experiments
     */
    private Long getCuratedExperimentsCount() {

        Long curatedExperiments = mutationExperimentsRepository.countByCurrated(true);
        return curatedExperiments;
    }

    /**
     * Get number of uncurated experiments in the DB
     *
     * @return number of uncurated experiments
     */
    private Long getUncuratedExperimentsCount() {
        Long uncuratedExperiments = mutationExperimentsRepository.countByCurrated(false);
        return uncuratedExperiments;
    }

    /**
     * Update data used as statistics data in charts
     * @return 
     */
    public int updateChartStatistics() {

        // update top proteins statistics  
        this.updateTopProteinsStatistics();
        this.updateTopInterProFamilies();
        this.updateOverallStatistics();
        this.updateAminoAcidsSubstitutions();
        this.updateAminoAcidSubstitutionMap();
        this.updateAminoAcidsSubstitutionsReducedMap();
        this.updateDdgHistogram();
        this.updateDtmHistogram();

        return 0;
    }

    /**
     * Get charts statistics data
     * @return charts statistics DTO
     */
    public ChartsStatisticsDTO getChartsStatistics() {

        ChartsStatisticsDTO chartsStatistics = new ChartsStatisticsDTO();

        // get data for top proteins
        List<TopProteins> topProteins = this.topProteinsRepository.findAll();
        chartsStatistics.setTopProteins(topProteins);
        //get data for top families
        List<TopInterProFamilies> topFamilies = this.topInterProFamiliesRepository.findAll();
        chartsStatistics.setTopFamilies(topFamilies);
        // data with overall history
        List<OverallStatisticsHistory> overallStatisticsHistory = this.overallStatisticsRepository.findAll();
        chartsStatistics.setOverallStatisticsHistory(overallStatisticsHistory);
        // histogram data of amino acids substitution
        List<AminoAcidsSubstitution> aminoAcidsSubsitutions = this.aminoAcidsSubstitutionRepository.findAll();
        chartsStatistics.setAminoAcidsSubstitutions(aminoAcidsSubsitutions);
        // 2D map of amino acids substitutions
        List<AminoAcidsSubstitutionMap> aminoAcidsSubstitutionMap = this.aminoAcidsSubstitutionMapRepository.findAll();
        List<AminoAcidsSubstitutionsMapDTO> substitutionEntries = new ArrayList<>();
        
        List<AminoAcidsSubstitutionsReducedMap> aminoAcidsSubstitutionsReducedMap = this.aminoAcidsSubstitutionsReducedMapRepository.findAll();
        List<AminoAcidsSubstitutionsMapDTO> reducedSubstitutionsEntries = new ArrayList<>();
      
        
        
        aminoAcidsSubstitutionMap.forEach(entry -> {
            HashMap<String,Integer> substitutions = new HashMap<>();
            AminoAcidsSubstitutionsMapDTO aa = new AminoAcidsSubstitutionsMapDTO();
            aa.setAminoAcid(entry.getAminoAcid());
            substitutions.put("A", entry.getASubstitutions());
            substitutions.put("C", entry.getCSubstitutions());
            substitutions.put("D", entry.getDSubstitutions());
            substitutions.put("E", entry.getESubstitutions());
            substitutions.put("F", entry.getFSubstitutions());
            substitutions.put("G", entry.getGSubstitutions());
            substitutions.put("H", entry.getHSubstitutions());
            substitutions.put("I", entry.getISubstitutions());
            substitutions.put("K", entry.getKSubstitutions());
            substitutions.put("L", entry.getLSubstitutions());
            substitutions.put("M", entry.getMSubstitutions());
            substitutions.put("N", entry.getNSubstitutions());
            substitutions.put("P", entry.getPSubstitutions());
            substitutions.put("Q", entry.getQSubstitutions());
            substitutions.put("R", entry.getRSubstitutions());
            substitutions.put("S", entry.getSSubstitutions());
            substitutions.put("T", entry.getTSubstitutions());
            substitutions.put("W", entry.getWSubstitutions());
            substitutions.put("Y", entry.getYSubstitutions());
            substitutions.put("V", entry.getVSubstitutions());
            aa.setSubstitutions(substitutions);
            substitutionEntries.add(aa);
        });
        
       
        chartsStatistics.setAminoAcidsSubstitutionsMap(substitutionEntries);
        
        aminoAcidsSubstitutionsReducedMap.forEach(entry -> {
           HashMap<String,Integer> substitutionsReduced = new HashMap<>();
           AminoAcidsSubstitutionsMapDTO reducedEntry = new AminoAcidsSubstitutionsMapDTO();
           reducedEntry.setAminoAcid(entry.getAminoAcid());
           substitutionsReduced.put("A", entry.getAGroupSubstitutions());
           substitutionsReduced.put("I", entry.getIGroupSubstitutions());
           substitutionsReduced.put("F", entry.getFGroupSubstitutions());
           substitutionsReduced.put("X", entry.getXGroupSubstitutions());
           substitutionsReduced.put("N", entry.getNGroupSubstitutions());
           substitutionsReduced.put("S", entry.getSGroupSubstitutions());
           substitutionsReduced.put("G", entry.getGGroupSubstitutions());
           substitutionsReduced.put("P", entry.getPGroupSubstitutions());
           reducedEntry.setSubstitutions(substitutionsReduced);
           reducedSubstitutionsEntries.add(reducedEntry);
        });
        
        
        chartsStatistics.setAminoAcidsSubstitutionsReducedMap(reducedSubstitutionsEntries);
        
        
        // histogram of ddg values
        List<DdgHistogramEntry> ddgHistogram = this.ddgHistogramRepository.findAll();
        chartsStatistics.setDdgHistogram(ddgHistogram);
        // histogram of dtm values
        List<DtmHistogramEntry> dtmHistogram = this.dtmHistogramRepository.findAll();
        chartsStatistics.setDtmHistogram(dtmHistogram);

        return chartsStatistics;

    }

    /**
     * Update statistics of top proteins
     */
    private void updateTopProteinsStatistics() {

        int topNProteins = 8;

        // delete all current data in top proteins table
        this.topProteinsRepository.deleteAll();
        Long dbExperimentsCount = this.mutationExperimentsRepository.count();

        int experimentsCount = 0;
        // find number of experiments for all proteins in the database
        List<TopProteins> topProteins = new ArrayList<TopProteins>();

        for (ProteinSequence s : this.proteinSequenceRepository.findAll()) {
            Double coverage = 0.0;
            TopProteins protein = new TopProteins();
            protein.setProteinName(s.getProteinName());
            experimentsCount = 0;

            // find number of experiments for protein
            for (Residues r : s.getResidues()) {
                for (Mutations m : r.getMutations()) {
                    experimentsCount += m.getMutationExperiments().size();
                }
            }
           
            coverage = (Double.valueOf(experimentsCount) / dbExperimentsCount) * 100;
            protein.setExperimentsCount(experimentsCount);
            protein.setPercentageCoverage(BigDecimal.valueOf(coverage));
            topProteins.add(protein);
            
        }

        // sort top proteins according to experiments count
        topProteins.sort(Comparator.comparing(TopProteins::getExperimentsCount, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
        // get top N proteins
        topProteins = topProteins.subList(0, topNProteins);

        // save updated top proteins to the database
        List<TopProteins> saved = this.topProteinsRepository.saveAll(topProteins);

    }
    
    /**
     * Update statistics of top N InterPro families
     */
    private void updateTopInterProFamilies(){
        int topNFamilies = 10;
        
        this.topInterProFamiliesRepository.deleteAll();
        Long dbExperimentsCount = this.mutationExperimentsRepository.count();
       
        List<TopInterProFamilies> topFamilies = new ArrayList<TopInterProFamilies>();
        
        int experimentsCount = 0;
        Double coverage = 0.0;
        
        for(InterproEntries entry: this.interProFamiliesRepository.findAll()){
            experimentsCount = 0;
            coverage = 0.0;
            
            for(ProteinSequenceInterproEntries psie: entry.getProteinSequenceInterproEntries()){
                ProteinSequence s = psie.getId().getProteinSequence();
                for(Residues r: s.getResidues()){
                    for(Mutations m: r.getMutations()){
                        experimentsCount += m.getMutationExperiments().size();
                    }
                }
                
            }   
            TopInterProFamilies family = new TopInterProFamilies();
            family.setExperimentsCount(experimentsCount);
            family.setFamilyName(entry.getName());
            coverage = (Double.valueOf(experimentsCount) / dbExperimentsCount) * 100;
            family.setPercentageCoverage(BigDecimal.valueOf(coverage));
            
            topFamilies.add(family);   
        }
        
        topFamilies.sort(Comparator.comparing(TopInterProFamilies::getExperimentsCount, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
        // get top N families
        topFamilies = topFamilies.subList(0, topNFamilies);

        // save updated top proteins to the database
        List<TopInterProFamilies> saved = this.topInterProFamiliesRepository.saveAll(topFamilies);
    }
    
    
    

    /**
     * Update overall statistics
     */
    private void updateOverallStatistics() {

        // get current year to be updated  
        Year year = Year.now();
        Integer currentYear = year.getValue();

        // get values from current database state
        long proteinsCount = proteinSequenceRepository.count();
        long mutationsCount = mutationsRepository.count();
        long experimentsCount = mutationExperimentsRepository.count();
        long curatedCount = mutationExperimentsRepository.countByCurrated(true);
        long uncuratedCount = mutationExperimentsRepository.countByCurrated(false);
        
        OverallStatisticsHistory statisticsEntry = null;

        if (!overallStatisticsRepository.existsById(currentYear)) {
            // create entry for current year
            statisticsEntry = new OverallStatisticsHistory();
            statisticsEntry.setYearId(currentYear);
            statisticsEntry.setProteinsCount(proteinsCount);
            statisticsEntry.setMutationsCount(mutationsCount);
            statisticsEntry.setExperimentsCount(experimentsCount);
            statisticsEntry.setCuratedExperimentsCount(curatedCount);
            statisticsEntry.setUncuratedExperimentsCount(uncuratedCount);

        } else {
            // update
            statisticsEntry = overallStatisticsRepository.findById(currentYear).get();
            statisticsEntry.setProteinsCount(proteinsCount);
            statisticsEntry.setMutationsCount(mutationsCount);
            statisticsEntry.setExperimentsCount(experimentsCount);
            statisticsEntry.setCuratedExperimentsCount(curatedCount);
            statisticsEntry.setUncuratedExperimentsCount(uncuratedCount);

        }

        // save entry
        overallStatisticsRepository.save(statisticsEntry);
    }

    /**
     * Update histogram data of amino acids substitutions
     */
    private void updateAminoAcidsSubstitutions() {

        // delete old data
        this.aminoAcidsSubstitutionRepository.deleteAll();

        HashMap<String, Integer> toSubstitutions = new HashMap<String, Integer>();
        HashMap<String, Integer> fromSubstitutions = new HashMap<String, Integer>();

        String[] aminoAcids = {"A", "C", "D", "E", "F", "G", "H", "I", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "W", "Y", "V"};

        // initialize 
        for (String aminoAcid : aminoAcids) {
            toSubstitutions.computeIfAbsent(aminoAcid, value -> 0);
            fromSubstitutions.computeIfAbsent(aminoAcid, value -> 0);
        }

        for (ProteinSequence p : this.proteinSequenceRepository.findAll()) {

            for (Residues r : p.getResidues()) {

                for (Mutations m : r.getMutations()) {
                    for(MutationExperiments e: m.getMutationExperiments()){
                         toSubstitutions.computeIfPresent(m.getMutatedAminoAcid(), (key, value) -> (value + 1));
                        fromSubstitutions.computeIfPresent(r.getResidue(), (key, value) -> (value + 1));
                    }
                    
                    
                   
                }
            }
        }

        // save entry for each residue
        for (String aminoAcid : aminoAcids) {

            Integer toSubstitutionCount = toSubstitutions.get(aminoAcid);
            Integer fromSubstitutionCount = fromSubstitutions.get(aminoAcid);
            AminoAcidsSubstitution entry = new AminoAcidsSubstitution();
            entry.setAminoAcid(aminoAcid);
            entry.setFromSubstitutions(fromSubstitutionCount);
            entry.setToSubstitutions(toSubstitutionCount);

            this.aminoAcidsSubstitutionRepository.save(entry);
        }
    }

    /**
     * Updata data representing 2D map of amino acids substitutions
     */
    private void updateAminoAcidSubstitutionMap() {

        // delete all data at first
        this.aminoAcidsSubstitutionMapRepository.deleteAll();

        String[] aminoAcids = {"A", "C", "D", "E", "F", "G", "H", "I", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "W", "Y", "V"};

        HashMap<String, HashMap<String, Integer>> substitutions = new HashMap<>();
        // initialize 
        for (String aminoAcid : aminoAcids) {
            substitutions.computeIfAbsent(aminoAcid, value -> new HashMap<>());
            for (String aa : aminoAcids) {
                substitutions.get(aminoAcid).computeIfAbsent(aa, value -> 0);
            }
        }
        
        // go through the database
        for (ProteinSequence p : this.proteinSequenceRepository.findAll()) {

            for (Residues r : p.getResidues()) {

                for (Mutations m : r.getMutations()) {
                    for(MutationExperiments e: m.getMutationExperiments()){
                        // compute substitution from resiue aa to mutated aa
                        substitutions.get(r.getResidue()).computeIfPresent(m.getMutatedAminoAcid(), (key, value) -> (value + 1));
                    }
                }
            }
        }

        // create database entries
        substitutions.forEach((key, value) -> {

            AminoAcidsSubstitutionMap aaEntry = new AminoAcidsSubstitutionMap();
            aaEntry.setAminoAcid(key);
            value.forEach((aa, count) -> {
                switch (aa) {
                    case "A":
                        aaEntry.setASubstitutions(count);
                        break;
                    case "C":
                        aaEntry.setCSubstitutions(count);
                        break;
                    case "D":
                        aaEntry.setDSubstitutions(count);
                        break;
                    case "E":
                        aaEntry.setESubstitutions(count);
                        break;
                    case "F":
                        aaEntry.setFSubstitutions(count);
                        break;
                    case "G":
                        aaEntry.setGSubstitutions(count);
                        break;
                    case "H":
                        aaEntry.setHSubstitutions(count);
                        break;
                    case "I":
                        aaEntry.setISubstitutions(count);
                        break;
                    case "K":
                        aaEntry.setKSubstitutions(count);
                        break;
                    case "L":
                        aaEntry.setLSubstitutions(count);
                        break;
                    case "M":
                        aaEntry.setMSubstitutions(count);
                        break;
                    case "N":
                        aaEntry.setNSubstitutions(count);
                        break;
                    case "P":
                        aaEntry.setPSubstitutions(count);
                        break;
                    case "Q":
                        aaEntry.setQSubstitutions(count);
                        break;
                    case "R":
                        aaEntry.setRSubstitutions(count);
                        break;
                    case "S":
                        aaEntry.setSSubstitutions(count);
                        break;
                    case "T":
                        aaEntry.setTSubstitutions(count);
                        break;
                    case "W":
                        aaEntry.setWSubstitutions(count);
                        break;
                    case "Y":
                        aaEntry.setYSubstitutions(count);
                        break;
                    case "V":
                        aaEntry.setVSubstitutions(count);
                        break;
                }

            });

            this.aminoAcidsSubstitutionMapRepository.save(aaEntry);
        });

    }
    
    /**
     * Get representant of amino acids group (reduced alphabet)
     * @param aminoAcid amino acid
     * @return group representant
     */
    private String getRepresentant(String aminoAcid){
           switch(aminoAcid){
               case "A":
               case "L":
               case "M":
                  return "A";
               case "I":
               case "V":
                  return "I";
               case "F":
               case "Y":
               case "W":
                  return "F";
               case "E":
               case "Q":
               case "K":
               case "R":
                   return "X";
               case "D":
               case "N":
                   return "N";
               case "H":
               case "S":
               case "T":
               case "C":
                   return "S";
               case "G":
                   return "G";
               case "P":
                   return "P";
               default:
                   return null;
           }
    }
    
    
    /**
     * Update statistics of substitutions for reduced alphabet of amino acids
     */
    private void updateAminoAcidsSubstitutionsReducedMap(){
        
        // deleta old data
        this.aminoAcidsSubstitutionsReducedMapRepository.deleteAll();
        
        String[] aminoAcids = {"A", "C", "D", "E", "F", "G", "H", "I", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "W", "Y", "V"};
        
        List<String> groupRepresentants = List.of("A","I","F","X","N","S","G","P");
        
        // initialize map of substitutions
        HashMap<String, HashMap<String, Integer>> substitutions = new HashMap<>();
        for (String aminoAcid : groupRepresentants) {
            substitutions.computeIfAbsent(aminoAcid, value -> new HashMap<>());
            for (String aa : groupRepresentants) {
                substitutions.get(aminoAcid).computeIfAbsent(aa, value -> 0);
            }
        }
        
        for(ProteinSequence p: this.proteinSequenceRepository.findAll()){
            for(Residues r: p.getResidues()){
                String originalResidue = r.getResidue();
                for(Mutations m: r.getMutations()){
                    String mutResidue = m.getMutatedAminoAcid();
                    
                    for(MutationExperiments e: m.getMutationExperiments()){
                        substitutions.get(this.getRepresentant(originalResidue)).computeIfPresent(this.getRepresentant(mutResidue), (key,value) -> (value + 1));
                        
                        
                        
                    }
                }
            }
        }
       
        // create database entries
        substitutions.forEach((key, value) -> {

            AminoAcidsSubstitutionsReducedMap aaEntry = new AminoAcidsSubstitutionsReducedMap();
            
            aaEntry.setAminoAcid(key);
            value.forEach((aa, count) -> {
                switch (aa) {
                    case "A":
                        aaEntry.setAGroupSubstitutions(count);
                    case "I":
                        aaEntry.setIGroupSubstitutions(count);
                        break;
                    case "F":
                        aaEntry.setFGroupSubstitutions(count);
                        break;
                    case "X":
                        aaEntry.setXGroupSubstitutions(count);
                        break;
                    case "N":
                        aaEntry.setNGroupSubstitutions(count);
                        break;
                    case "S":
                        aaEntry.setSGroupSubstitutions(count);
                        break;
                    case "G":
                        aaEntry.setGGroupSubstitutions(count);
                        break;
                    case "P":
                        aaEntry.setPGroupSubstitutions(count);
                        break;
                   
                }});

            this.aminoAcidsSubstitutionsReducedMapRepository.save(aaEntry);
        }); 
        
    }
    
    /**
     * Update ddg histogram data
     */
    public void updateDdgHistogram() {

        this.ddgHistogramRepository.deleteAll();
            
        // array of all ddg value in experiments
        List<Double> ddGValues = new ArrayList<Double>();
        for (MutationExperiments e : this.mutationExperimentsRepository.findAll()) {
            BigDecimal ddg = e.getDdG();

            if (ddg != null) {
                ddGValues.add(ddg.doubleValue());
            }
        }
        ddGValues.sort(Comparator.naturalOrder());

        double[] ddgArr = ddGValues.stream().mapToDouble(Double::doubleValue).toArray();

         int intervals = this.binsCount; 
        double max = Arrays.stream(ddgArr).max().getAsDouble();
        double min = Arrays.stream(ddgArr).min().getAsDouble();
        long intervalWidth = Math.round(max - min) / intervals;
        long n = 0;
        List<Long> upperBounds = new ArrayList<Long>();
        
        for(int i=0; i < intervals; i++){
           
            if (i == 0){
                n = Math.round(min) + intervalWidth;
            }
            else{
                n = n + intervalWidth;
            }
            upperBounds.add(n);
        }
             
        for(int i=0; i <  upperBounds.size(); i++){
            DdgHistogramEntry entry = new DdgHistogramEntry();
            entry.setId(i);
            
            long count = 0;
            long upBorder =  upperBounds.get(i);
            long lowBorder = i == 0 ?  upperBounds.get(i) : upperBounds.get(i - 1);
            
            if(i == 0){
                count = ddGValues.stream().filter(num -> (num <= upBorder)).count();
            }
            else if(i ==  upperBounds.size() - 1){
                 count = ddGValues.stream().filter(num -> (num > upBorder)).count();
            }
            else{
                 count = ddGValues.stream().filter(num -> (num > lowBorder && num <= upBorder)).count();
            }
            
            entry.setIntervalMin(String.valueOf(lowBorder));
            entry.setIntervalMax(String.valueOf(upBorder));
            entry.setCount(count);
            
            this.ddgHistogramRepository.save(entry);
        }
        
      
  

    }


    /**
     * Update dtm histogram data
     */
    public void updateDtmHistogram() {

        this.dtmHistogramRepository.deleteAll();
        
        
        // array of all ddg value in experiments
        List<Double> dtmValues = new ArrayList<Double>();
        for (MutationExperiments e : this.mutationExperimentsRepository.findAll()) {
            BigDecimal dtm = e.getdTm();

            if (dtm != null) {
                dtmValues.add(dtm.doubleValue());
            }
        }

        dtmValues.sort(Comparator.naturalOrder());

        double[] dtmArr = dtmValues.stream().mapToDouble(Double::doubleValue).toArray();
        
        int intervals = this.binsCount; 
        double max = Arrays.stream(dtmArr).max().getAsDouble();
        double min = Arrays.stream(dtmArr).min().getAsDouble();
        long intervalWidth = Math.round(max - min) / intervals;
        long n = 0;
        
        // custom bounds for main page statistics
        List<Long> upperBounds = new ArrayList<Long>(List.of(
                Long.valueOf("-15"),
                Long.valueOf("-10"),
                Long.valueOf("-5"),
                Long.valueOf("0"),
                Long.valueOf("5"),
                Long.valueOf("10"),
                Long.valueOf("15")));
        
        
        for(int i=0; i <= upperBounds.size(); i++){
            DtmHistogramEntry entry = new DtmHistogramEntry();
            entry.setId(i);
            
            long count = 0;
            long upBorder = i == upperBounds.size() ? upperBounds.get(i - 1) : upperBounds.get(i);
            long lowBorder = i == 0 ?  upperBounds.get(i) : upperBounds.get(i - 1);
            
            if(i == 0){
                count = dtmValues.stream().filter(num -> (num <= upBorder)).count();
                entry.setIntervalMin(String.valueOf(lowBorder));
            }
            else if(i ==  upperBounds.size()){
                 count = dtmValues.stream().filter(num -> (num > upBorder)).count();
                  entry.setIntervalMin(String.valueOf(upBorder));
            }
            else{
                 count = dtmValues.stream().filter(num -> (num > lowBorder && num <= upBorder)).count();
                 entry.setIntervalMin(String.valueOf(lowBorder));
            }
            
            
            entry.setIntervalMax(String.valueOf(upBorder));
            entry.setCount(count);
            
            this.dtmHistogramRepository.save(entry);
        }  
    }
}
