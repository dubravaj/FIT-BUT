/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.loschmidt.fireprotdb.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.loschmidt.fireprotdb.backend.dto.ExpressionItem;
import cz.loschmidt.fireprotdb.backend.dto.OptionDTO;
import cz.loschmidt.fireprotdb.backend.dto.OptionsMapEntry;
import cz.loschmidt.fireprotdb.backend.dto.SearchMutationDTO;
import cz.loschmidt.fireprotdb.backend.dto.SearchMutationPageDTO;
import cz.loschmidt.fireprotdb.backend.dto.SearchOptionValue;
import cz.loschmidt.fireprotdb.backend.dto.SearchResultsStatisticsDTO;
import cz.loschmidt.fireprotdb.backend.elasticrepository.EsAuthorsRepository;
import cz.loschmidt.fireprotdb.backend.elasticrepository.EsDatasetsRepository;
import cz.loschmidt.fireprotdb.backend.elasticrepository.EsInterProEntriesRepository;
import cz.loschmidt.fireprotdb.backend.elasticrepository.EsMutationExperimentsRepository;
import cz.loschmidt.fireprotdb.backend.elasticrepository.EsProteinSequenceRepository;
import cz.loschmidt.fireprotdb.backend.elasticrepository.EsPublicationsRepository;
import cz.loschmidt.fireprotdb.backend.elasticrepository.EsStructuresRepository;
import cz.loschmidt.fireprotdb.backend.esmodel.EsInterproEntry;
import cz.loschmidt.fireprotdb.backend.esmodel.EsProteinSequence;
import cz.loschmidt.fireprotdb.backend.esmodel.EsStructure;
import cz.loschmidt.fireprotdb.backend.model.AminoAcidsSubstitution;
import cz.loschmidt.fireprotdb.backend.model.Authors;
import cz.loschmidt.fireprotdb.backend.model.AuthorsPublications;
import cz.loschmidt.fireprotdb.backend.model.Datasets;
import cz.loschmidt.fireprotdb.backend.model.DdgHistogramEntry;
import cz.loschmidt.fireprotdb.backend.model.DtmHistogramEntry;
import cz.loschmidt.fireprotdb.backend.model.InterproEntries;
import cz.loschmidt.fireprotdb.backend.model.MutationExperiments;
import cz.loschmidt.fireprotdb.backend.model.MutationExperimentsSearch;
import cz.loschmidt.fireprotdb.backend.model.Mutations;
import cz.loschmidt.fireprotdb.backend.model.ProteinSequence;
import cz.loschmidt.fireprotdb.backend.model.Publications;
import cz.loschmidt.fireprotdb.backend.model.Residues;
import cz.loschmidt.fireprotdb.backend.model.Structures;
import cz.loschmidt.fireprotdb.backend.model.TopProteins;
import cz.loschmidt.fireprotdb.backend.repository.AuthorsRepository;
import cz.loschmidt.fireprotdb.backend.repository.DatasetsRepository;
import cz.loschmidt.fireprotdb.backend.repository.InterproEntriesRepository;
import cz.loschmidt.fireprotdb.backend.repository.MutationExperimentsRepository;
import cz.loschmidt.fireprotdb.backend.repository.MutationExperimentsSearchRepository;
import cz.loschmidt.fireprotdb.backend.repository.MutationsRepository;
import cz.loschmidt.fireprotdb.backend.repository.ProteinSequenceRepository;
import cz.loschmidt.fireprotdb.backend.repository.PublicationsRepository;
import cz.loschmidt.fireprotdb.backend.repository.SearchCriteriaBuilder;
import cz.loschmidt.fireprotdb.backend.repository.StructuresRepository;
import cz.loschmidt.fireprotdb.backend.specifications.BiologicalUnitSpecifications;
import cz.loschmidt.fireprotdb.backend.specifications.DatasetSpecifications;
import cz.loschmidt.fireprotdb.backend.specifications.MutationSpecifications;
import cz.loschmidt.fireprotdb.backend.specifications.ProteinSequenceSpecifications;
import cz.loschmidt.fireprotdb.backend.specifications.PublicationSpecifications;
import cz.loschmidt.fireprotdb.backend.specifications.ResiduesSpecifications;
import cz.loschmidt.fireprotdb.backend.specifications.SpecificationsNames;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

/**
 * Search service implementation
 *
 * @author Juraj Dubrava
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Inject
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    EntityManager em;

    @Autowired
    DatasetsRepository datasetsRepository;

    @Autowired
    EsMutationExperimentsRepository esMutationExperimentsRepository;

    @Autowired
    EsDatasetsRepository esDatasetsRepository;

    @Autowired
    MutationExperimentsRepository mutationExperimentsRepository;

    @Autowired
    ProteinSequenceRepository proteinSequenceRepository;

    @Autowired
    EsProteinSequenceRepository esProteinSequenceRepository;

    @Autowired
    PublicationsRepository publicationsRepository;

    @Autowired
    EsPublicationsRepository esPublicationsRepository;

    @Autowired
    StructuresRepository structuresRepository;

    @Autowired
    EsStructuresRepository esStructuresRepository;

    @Autowired
    AuthorsRepository authorsRepository;

    @Autowired
    EsAuthorsRepository esAuthorsRepository;

    @Autowired
    InterproEntriesRepository interproRepository;

    @Autowired
    MutationsRepository mutationsRepository;

    @Autowired
    MutationExperimentsSearchRepository experimentsSearchRepository;

    @Autowired
    InterproEntriesRepository interproEntriesRepository;

    @Autowired
    EsInterProEntriesRepository esInterProEntriesRepository;

    private CriteriaBuilder cb;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);


    /**
     * Search for datasets in Elasticsearch
     * @param query full-text query
     * @return list of found datasets
     */
    @Transactional
    public List<Datasets> elasticSearchDatasets(String query) {
        QueryBuilder queryBuilderDatasets = QueryBuilders.boolQuery().should(
                QueryBuilders.multiMatchQuery(query).field("name")
        );

        List<Datasets> datasetQueryResults = new ArrayList<>();
        try{
            esDatasetsRepository.search(queryBuilderDatasets).forEach(datasetQueryResults::add);
        }
        catch(ElasticsearchStatusException e ){
            logger.info(e.getMessage());
        }
        return datasetQueryResults;

    }

    /**
     * Get mutation experiments related to found datasets
     * @param datasets found datasets in search
     * @return list of related experiments
     */
    private List<MutationExperiments> getDatasetExperiments(List<Datasets> datasets) {
        List<Datasets> dbDatasets = new ArrayList<>();
        List<MutationExperiments> experiments = new ArrayList<>();
        for (Datasets dataset : datasets) {
            dbDatasets.add(datasetsRepository.findById(dataset.getId()).get());
        }

        for (Datasets dataset : dbDatasets) {
            for (MutationExperiments exp : dataset.getMutationExperiments()) {
                experiments.add(exp);
            }
        }
        return experiments;
    }

    /**
     * Search for structures in Elasticsearch
     * @param query full-text query
     * @return list of found structures
     */
    @Transactional
    public List<EsStructure> elasticSearchStructures(String query) {

        QueryBuilder queryBuilderStructures = QueryBuilders.boolQuery().should(
                QueryBuilders.multiMatchQuery(query, "pdbId")
        );
        List<EsStructure> structuresQueryResults = new ArrayList<>();
        try{
            esStructuresRepository.search(queryBuilderStructures).forEach(structuresQueryResults::add);
        }
        catch(ElasticsearchStatusException e ){
            logger.info(e.getMessage());
        }

        return structuresQueryResults;
    }

    /**
     * Get mutation experiments related to structures
     * @param structures found structures in search
     * @return list of experiments
     */
    private List<MutationExperiments> getStructuresExperiments(List<EsStructure> structures) {
        List<Structures> dbStructures = new ArrayList<>();
        List<MutationExperiments> experiments = new ArrayList<>();

        for (EsStructure structure : structures) {
            dbStructures.add(structuresRepository.findById(structure.getId()).get());
        }

        dbStructures.forEach((structure) -> {
            structure.getStructureSequence().stream().map((structureSequence) -> structureSequence.getId().getProteinSequence()).forEachOrdered((sequence) -> {
                sequence.getResidues().forEach((residue) -> {
                    residue.getMutations().forEach((mutation) -> {
                        mutation.getMutationExperiments().forEach((exp) -> {
                            experiments.add(exp);
                        });
                    });
                });
            });
        });
        return experiments;
    }

    /**
     * Search protein sequence in Elasticsearch
     * @param query full-text query
     * @return list of found protein sequences
     */
    @Transactional
    public List<EsProteinSequence> elasticSearchSequence(String query) {
        QueryBuilder queryBuilderProteinSequence = QueryBuilders.boolQuery().should(
                QueryBuilders.multiMatchQuery(query, "uniprotId", "obsoleteUniprotId", "species", "ecNumber", "proteinName")
        );

        List<EsProteinSequence> sequenceQueryResults = new ArrayList<>();
        try{
            esProteinSequenceRepository.search(queryBuilderProteinSequence).forEach(sequenceQueryResults::add);
        }
        catch(ElasticsearchStatusException e){
            logger.info(e.getMessage());
        }

        return sequenceQueryResults;
    }

    /**
     * Get experiments related to protein sequences
     * @param sequences list of found protein sequences
     * @return list of experiments
     */
    private List<MutationExperiments> getSequenceExperiments(List<EsProteinSequence> sequences) {
        List<ProteinSequence> dbProteinSequences = new ArrayList<>();
        List<MutationExperiments> experiments = new ArrayList<>();
        sequences.forEach((sequence) -> {
            dbProteinSequences.add(proteinSequenceRepository.findById(Integer.valueOf(sequence.getId())).get());
        });

        dbProteinSequences.forEach((ProteinSequence sequence) -> {
            sequence.getResidues().forEach((Residues residue) -> {
                residue.getMutations().forEach((Mutations mutation) -> {
                    mutation.getMutationExperiments().forEach((MutationExperiments exp) -> {
                        experiments.add(exp);
                    });
                });
            });
        });
        return experiments;
    }

    /**
     * Search for authors in Elasticsearch
     * @param query full-text query
     * @return list of found authors
     */
    @Transactional
    public List<Authors> elasticSearchAuthors(String query) {
        QueryBuilder queryBuilderAuthors = QueryBuilders.boolQuery().should(
                QueryBuilders.multiMatchQuery(query, "foreName", "lastName", "initials")
        );
        List<Authors> authorsQueryResults = new ArrayList<>();
        try{
            esAuthorsRepository.search(queryBuilderAuthors).forEach(authorsQueryResults::add);
        }
        catch(ElasticsearchStatusException e ){
            logger.info(e.getMessage());
        }

        return authorsQueryResults;
    }

    /**
     * Get experiments related to authors
     *
     * @param authors list of found authors
     * @return list of experiments
     */
    private List<MutationExperiments> getAuthorsExperiments(List<Authors> authors) {
        List<Authors> dbAuthors = new ArrayList<>();
        List<MutationExperiments> experiments = new ArrayList<>();
        for (Authors author : authors) {
            dbAuthors.add(authorsRepository.findById(author.getId()).get());
        }

        for (Authors author : dbAuthors) {
            for (Datasets dataset : author.getDatasets()) {
                for (MutationExperiments exp : dataset.getMutationExperiments()) {
                    experiments.add(exp);
                }
            }

            for (AuthorsPublications authorPublication : author.getAuthorsPublications()) {
                Publications publication = authorPublication.getId().getPublication();
                for (MutationExperiments exp : publication.getMutationExperiment()) {
                    experiments.add(exp);
                }
            }
            /*for(Publications publication: author.getPublications()){
                for(MutationExperiments exp: publication.getMutationExperiment()){
                    experiments.add(exp);
                 }
            }*/

        }

        return experiments;
    }

    /**
     * Search for interpro entries in Elasticsearch
     * @param query full-text query
     * @return list of found interpro entries
     */
    @Transactional
    public List<EsInterproEntry> elasticSearchInterproEntries(String query) {
        QueryBuilder queryBuilderInterproEntries = QueryBuilders.boolQuery().should(
                QueryBuilders.multiMatchQuery(query, "interproId", "type", "name")
        );
        List<EsInterproEntry> interproQueryResults = new ArrayList<>();
        try{
            esInterProEntriesRepository.search(queryBuilderInterproEntries).forEach(interproQueryResults::add);
        }
        catch(ElasticsearchStatusException e ){
            logger.info(e.getMessage());
        }

        return interproQueryResults;
    }

    /**
     * Get experiments related to interpro entries
     * @param interproEntries list of found interpro entries
     * @return list of experiments
     */
    private List<MutationExperiments> getInterproExperiments(List<EsInterproEntry> interproEntries) {
        List<InterproEntries> dbInterproEntries = new ArrayList<>();
        List<MutationExperiments> experiments = new ArrayList<>();

        for (EsInterproEntry interproEntry : interproEntries) {
            dbInterproEntries.add(interproEntriesRepository.findById(interproEntry.getId()).get());
        }

        dbInterproEntries.forEach(entry -> {
            entry.getProteinSequenceInterproEntries().forEach(seqInterprot -> {
                seqInterprot.getId().getProteinSequence().getResidues().forEach(r -> {
                    r.getMutations().forEach(m -> {
                        m.getMutationExperiments().forEach(e -> {
                            experiments.add(e);
                        });
                    });
                });
            });
        });

        return experiments;
    }
    
    
    /**
     * Get search experiments according to search type
     * @param request
     * @param searchType
     * @return list fo mutation experiment search
     */
    private List<MutationExperiments> getSearchExperiments(String request, String searchType){
        
        
        SearchCriteriaBuilder sb = new SearchCriteriaBuilder(em.getCriteriaBuilder());
        List<MutationExperiments> foundMutations;
        
        if(searchType.equals("advanced")){
            
            foundMutations = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = null;
            JsonNode searchDataNode = null;
            try {
                rootNode = objectMapper.readTree(request);
                searchDataNode = rootNode.get("searchData");
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            }
            TypedQuery<MutationExperiments> query;
            Map<String, List<OptionsMapEntry>> optionsMap = new HashMap<>();
            Predicate searchSpecification = null;

            Predicate p = null;
            try {
                p = parseBracketQuery(sb, searchDataNode,optionsMap);
            } catch (JsonProcessingException ex) {
                Logger.getLogger(SearchServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }

            if(p != null){
               searchSpecification = p;
            }
            if (searchSpecification == null) {
                query = em.createQuery(sb.queryAll());
            } else {
                query = em.createQuery(sb.buildQuery(searchSpecification));
            }

            foundMutations = query.getResultList();


          
        }
        else{
            foundMutations = new ArrayList<MutationExperiments>();
            try {
                List<Datasets> datasetExperiments = CompletableFuture.supplyAsync(() -> elasticSearchDatasets(request)).get();
                foundMutations.addAll(getDatasetExperiments(datasetExperiments));
                List<EsStructure> searchedStructures = CompletableFuture.supplyAsync(() -> elasticSearchStructures(request)).get();
                foundMutations.addAll(getStructuresExperiments(searchedStructures));
                List<EsProteinSequence> searchedSequences = CompletableFuture.supplyAsync(() -> elasticSearchSequence(request)).get();
                foundMutations.addAll(getSequenceExperiments(searchedSequences));
                List<Authors> searchedAuthors = CompletableFuture.supplyAsync(() -> elasticSearchAuthors(request)).get();
                foundMutations.addAll(getAuthorsExperiments(searchedAuthors));
                List<EsInterproEntry> searchedInterproEntries = CompletableFuture.supplyAsync(() -> elasticSearchInterproEntries(request)).get();
                foundMutations.addAll(getInterproExperiments(searchedInterproEntries));

            } catch (InterruptedException ex) {
                Logger.getLogger(SearchServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(SearchServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            foundMutations = foundMutations.stream().distinct().collect(Collectors.toList());
  

        }
        
        
        /*foundMutations.forEach(exp -> ids.add(exp.getId()));
        for (MutationExperimentsSearch exp : experimentsSearchRepository.findAll()) {
            if (ids.contains(exp.getExperimentId())) {
                searchExperiments.add(exp);
            }
        }*/
        
        return foundMutations;
    }
    
    
    
    
    /**
     * Export results from advanced search
     * @param request search request query
     * @param page page number
     * @param size page size
     * @return list of experiments
     */
    public List<MutationExperiments> searchExport(String request, Integer page, Integer size) {

        List<MutationExperiments> foundMutations = this.getSearchExperiments(request, "advanced");

        return foundMutations;
    }

    /**
     * Get results from advanced search query
     * @param request search request
     * @param page page number
     * @param size size of page
     * @return page of search experiments
     */
    public SearchMutationPageDTO search(String request, Integer page, Integer size) {

        SearchCriteriaBuilder sb = new SearchCriteriaBuilder(em.getCriteriaBuilder());
        cb = sb.getCriteriaBuilder();

        Pageable searchPage = PageRequest.of(page, size);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = null;
        JsonNode filterNode = null;
        JsonNode searchDataNode = null;
        try {
            rootNode = objectMapper.readTree(request);
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
       
        filterNode = rootNode.get("filter");
        searchDataNode = rootNode.get("searchData");

        

        List<MutationExperiments> foundMutations = this.getSearchExperiments(request, "advanced");//query.getResultList();
        List<String> ids = new ArrayList<>();
        foundMutations.forEach(exp -> ids.add(exp.getId()));
        List<MutationExperimentsSearch> searchExperiments = new ArrayList<>();
        
        for (MutationExperimentsSearch exp : experimentsSearchRepository.findAll()) {
            if (ids.contains(exp.getExperimentId())) {
                searchExperiments.add(exp);
            }
        }

        // filter results according to specified filter key
        String sortKey = filterNode.get("filterKey").asText();
        String sortOrder = filterNode.get("order").asText();
        
        searchExperiments = this.sortExperimentsByKey(searchExperiments, sortKey, sortOrder);
        

        // extract results representing data for specified page
        int start = (int) searchPage.getOffset();
        int end = (start + searchPage.getPageSize()) > foundMutations.size() ? foundMutations.size() : (start + searchPage.getPageSize());

        Page<MutationExperimentsSearch> resultPage = null;
        List<MutationExperimentsSearch> pageContent = new ArrayList<>();
        SearchMutationPageDTO resultPageDTO = new SearchMutationPageDTO();
        List<SearchMutationDTO> searchResults = new ArrayList<>();

        resultPage = new PageImpl<MutationExperimentsSearch>(searchExperiments.subList(start, end), searchPage, foundMutations.size());
        pageContent = resultPage.getContent();

        for (MutationExperimentsSearch m : pageContent) {
            SearchMutationDTO sm = new SearchMutationDTO();
            sm.setDdG(m.getDdg() == null ? null : BigDecimal.valueOf(m.getDdg()));
            sm.setDtM(m.getDtm() == null ? null : BigDecimal.valueOf(m.getDtm()));
            sm.setCurated(m.isCurated());
            sm.setHalfLife(m.getHalfLife() == null ? null : BigDecimal.valueOf(m.getHalfLife()));
            sm.setId(m.getMutId());
            sm.setExperimentId(m.getExperimentId());
            sm.setMutation(m.getWildType().concat(m.getPosition().toString()).concat(m.getMutation()));
            sm.setPH(m.getPh() == null ? null : BigDecimal.valueOf(m.getPh()));
            sm.setProtein(m.getProtein());
            sm.setProteinConcentration(m.getProteinConcentration() == null ? null : BigDecimal.valueOf(m.getProteinConcentration()));
            sm.setPurityOfSample(m.getPurityOfSample() == null ? null : BigDecimal.valueOf(m.getPurityOfSample()));
            sm.setScanRate(m.getScanRate() == null ? null : BigDecimal.valueOf(m.getScanRate()));
            sm.setSequenceId(m.getSequenceId());
            searchResults.add(sm);

        }

        resultPageDTO.setSearchResults(searchResults);
        resultPageDTO.setCurrentPage(resultPage.getNumber());
        resultPageDTO.setSearchResultsCount(foundMutations.size());
        resultPageDTO.setTotalPageCount(resultPage.getTotalPages());

        return resultPageDTO;

    }

    /**
     * Create search predicate from search query object
     * @param sb search criteria builder
     * @param node root JSON node
     * @param predicates stack of predicates
     * @param operators stack of operators
     * @param predStack compound predicates
     * @param optionsMap options 
     */
    private void createQuery(SearchCriteriaBuilder sb, JsonNode node, Stack<JsonNode> predicates, Stack<String> operators, Stack<Predicate> predStack, Map<String,List<OptionsMapEntry>> optionsMap){

        if(node.get("type").asText().equals("or") || node.get("type").asText().equals("and")){
               operators.push(node.get("type").asText());
               for(JsonNode n: node.get("options")){
                    createQuery(sb,n,predicates, operators,predStack,optionsMap);
               }
        }
        else{
            //expr object
            predStack.add(toSpecification(sb,node,optionsMap));
            predicates.push(node);
            return;
        }
        if(predStack.size() >= 2){
            Predicate pred1 = predStack.pop();
            Predicate pred2 = predStack.pop();
            Predicate p = createOpSpecification(operators.pop(), pred1, pred2);
            predStack.push(p);
        }
    }
    /**
     * Parse search query with brackets support
     * @param sb search criteria builder
     * @param node root node
     * @param optionsMap options
     * @return predicate from search query
     * @throws JsonProcessingException 
     */
    public Predicate parseBracketQuery(SearchCriteriaBuilder sb, JsonNode node,Map<String, List<OptionsMapEntry>> optionsMap) throws JsonProcessingException{
       
        JsonNode rootNode = node;
        Stack<JsonNode> predicates = new Stack<>();
        Stack<Predicate> predStack = new Stack<>();
        Stack<String> operators = new Stack<>();
        createQuery(sb, rootNode,predicates, operators, predStack,optionsMap);
        
        if(predStack.size() == 1){
            return predStack.pop();
        }
        else{
            while(predStack.size() > 1){
                Predicate p = createOpSpecification(operators.pop(), predStack.pop(), predStack.pop());
                predStack.push(p);
            }

            if(predStack.peek() != null){
                return predStack.pop();
            }
        }
        return null;
    }

    
    /**
     * Create Criteria API specification 
     * @param sb search builder
     * @param node root node
     * @param optionsMap options
     * @return predicate
     */
    private Predicate toSpecification(SearchCriteriaBuilder sb, JsonNode node, Map<String, List<OptionsMapEntry>> optionsMap) {

        String val = node.get("key").asText();
        val = val.replace('.', '_');
        String nodeValue = node.get("value").asText();
        List<String> checkOptions = new ArrayList<>();
        for (JsonNode n : node.get("checkOptions")) {
            checkOptions.add(n.get("value").asText());
        }
        OptionsMapEntry e = new OptionsMapEntry();
        e.setOptions(checkOptions);
        e.setValue(nodeValue);
        optionsMap.computeIfAbsent(val, k -> new ArrayList<>()).add(e);

        switch (SpecificationsNames.valueOf(val)) {
            case all:
                return null;
            case sequence_name:
                return ProteinSequenceSpecifications.hasProteinName(sb, nodeValue);
            case sequence_length:
                return ProteinSequenceSpecifications.hasLength(sb, nodeValue);
            case sequence_uniprotId:
                return ProteinSequenceSpecifications.hasUniprotId(sb, nodeValue);
            case sequence_sequence:
                return ProteinSequenceSpecifications.hasSequence(sb, nodeValue);
            case sequence_species:
                return ProteinSequenceSpecifications.hasSpecies(sb, nodeValue);
            case sequence_ecnumber:
                return ProteinSequenceSpecifications.hasEcNumber(sb, nodeValue);
            case sequence_hasinterprofamily:
                return ProteinSequenceSpecifications.hasInterProFamily(sb, nodeValue);
            case residue_position:
                return ResiduesSpecifications.hasPosition(sb, nodeValue);
            case residue_conservation:
                return ResiduesSpecifications.hasConservation(sb, nodeValue);
            case residue_bfactor:
                return ResiduesSpecifications.hasBFactor(sb, nodeValue);
            case residue_secstructure:
                return ResiduesSpecifications.hasSecondaryStructure(sb, nodeValue);
            case residue_asa:
                return ResiduesSpecifications.hasAsa(sb, nodeValue);
            case residue_strucindex:
                return ResiduesSpecifications.hasStructureIndex(sb, nodeValue);
            case residue_chain:
                return ResiduesSpecifications.hasChain(sb, nodeValue);
            case residue_incode:
                return ResiduesSpecifications.hasInsertionCode(sb, nodeValue);
            case residue_iscatalytic:
                return ResiduesSpecifications.isCatalytic(sb, nodeValue);
            case dataset_name:
                return DatasetSpecifications.hasDataset(sb, nodeValue, checkOptions);
            case dataset_version:
                return DatasetSpecifications.hasDatasetVersion(sb, nodeValue);
            case publication_doi:
                return PublicationSpecifications.hasDoi(sb, nodeValue);
            case mutation_id:
                return MutationSpecifications.hasId(sb, nodeValue);
            case mutation_aminoacid:
                return MutationSpecifications.hasMutatedAminoAcid(sb, nodeValue);
            case mutexperiment_scanrate:
                return MutationSpecifications.hasScanRate(sb, nodeValue);
            case mutexperiment_hasscanrate:
                return MutationSpecifications.existsScanRate(sb);
            case mutexperiment_hasnotscanrate:
                return MutationSpecifications.notExistsScanRate(sb);
            case mutexperiment_cp:
                return MutationSpecifications.hasCp(sb, nodeValue);
            case mutexperiment_hascp:
                return MutationSpecifications.existsCp(sb);
            case mutexperiment_hasnotcp:
                return MutationSpecifications.notExistsCp(sb);
            case mutexperiment_isstabilizing:
                return MutationSpecifications.isStabilizing(sb, em, checkOptions, optionsMap);
            case mutexperiment_isdestabilizing:
                return MutationSpecifications.isDestabilizing(sb, em, checkOptions, optionsMap);
            case mutexperiment_isneutral:
                return MutationSpecifications.isNeutral(sb, em, checkOptions, optionsMap);
            case mutexperiment_iscurated:
                return MutationSpecifications.isCurrated(sb);
            case mutexperiment_isnotcurated:
                return MutationSpecifications.isNotCurrated(sb);
            case mutexperiment_proteinconcentration:
                return MutationSpecifications.hasProteinConcentration(sb, nodeValue);
            case mutexperiment_hasproteinconcentration:
                return MutationSpecifications.existsProteinConcentration(sb);
            case mutexperiment_hasnotproteinconcentration:
                return MutationSpecifications.notExistsProteinConcentration(sb);
            case mutexperiment_samplepurity:
                return MutationSpecifications.hasSamplePurity(sb, nodeValue);
            case mutexperiment_hassamplepurity:
                return MutationSpecifications.existsSamplePurity(sb);
            case mutexperiment_hasnotsamplepurity:
                return MutationSpecifications.notExistsSamplePurity(sb);
            case mutexperiment_toffset:
                return MutationSpecifications.hasTOffset(sb, nodeValue);
            case mutexperiment_hastoffset:
                return MutationSpecifications.existsTOffset(sb);
            case mutexperiment_hasnottoffset:
                return MutationSpecifications.notExistsTOffset(sb);
            case mutexperiment_ph:
                return MutationSpecifications.hasPh(sb, nodeValue);
            case mutexperiment_hasph:
                return MutationSpecifications.existsPh(sb);
            case mutexperiment_hasnotph:
                return MutationSpecifications.notExistsPh(sb);
            case mutexperiment_ddg:
                return MutationSpecifications.hasDDG(sb, nodeValue, checkOptions);
            case mutexperiment_hasddg:
                return MutationSpecifications.existsDDG(sb);
            case mutexperiment_hasnotddg:
                return MutationSpecifications.notExistsDDG(sb);
            case mutexperiment_dtm:
                return MutationSpecifications.hasDTM(sb, nodeValue, checkOptions);
            case mutexperiment_hasdtm:
                return MutationSpecifications.existsDtm(sb);
            case mutexperiment_hasnotdtm:
                return MutationSpecifications.notExistsDtm(sb);
            case mutexperiment_tm:
                return MutationSpecifications.hasTm(sb, nodeValue);
            case mutexperiment_hastm:
                return MutationSpecifications.existsTm(sb);
            case mutexperiment_hasnottm:
                return MutationSpecifications.notExistsTm(sb);
            case mutexperiment_halflife:
                return MutationSpecifications.hasHalfLife(sb, nodeValue);
            case mutexperiment_hashalflife:
                return MutationSpecifications.existsHalfLife(sb);
            case mutexperiment_hasnothalflife:
                return MutationSpecifications.notExistsHalfLife(sb);
            case biounit_pdbid:
                return BiologicalUnitSpecifications.hasPdbId(sb, nodeValue);
            default:
                return null;
        }

    }

    
    /**
     * Create predicate for specified operator
     * @param operator operation
     * @param predicate1 predicate
     * @param predicate2 predicate
     * @return predicate
     */
    private Predicate createOpSpecification(String operator, Predicate predicate1, Predicate predicate2) {
        if (operator.equals("or")) {
            return createOrSpecification(predicate1, predicate2);
        } else {
            return createAndSpecification(predicate1, predicate2);
        }
    }

    /**
     * Create AND predicate
     * @param firstSpec predicate
     * @param secondSpec predicate
     * @return OR predicate
     */
    private Predicate createAndSpecification(Predicate firstSpec, Predicate secondSpec) {
        Predicate requestPredicate = null;
        requestPredicate = cb.and(firstSpec, secondSpec);

        return requestPredicate;
    }

    /**
     * Create OR predicate
     * @param firstSpec predicate
     * @param secondSpec predicate
     * @return OR predicate
     */
    private Predicate createOrSpecification(Predicate firstSpec, Predicate secondSpec) {
        Predicate requestPredicate = null;
        requestPredicate = cb.or(firstSpec, secondSpec);

        return requestPredicate;
    }

    /**
     * 
     */
    private List<MutationExperimentsSearch> sortExperimentsByKey(List<MutationExperimentsSearch> experiments, String sortKey, String sortOrder){
        // sort result according to user specified column
        if (sortKey.equals("ddG")) {
            if (sortOrder.equals("asc")) {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::getDdg, Comparator.nullsLast(Comparator.naturalOrder())));

            } else {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::getDdg, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
            }
        } else if (sortKey.equals("dtM")) {
            if (sortOrder.equals("asc")) {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::getDtm, Comparator.nullsLast(Comparator.naturalOrder())));

            } else {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::getDtm, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());

            }
        } else if (sortKey.equals("ph")) {
            if (sortOrder.equals("asc")) {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::getPh, Comparator.nullsLast(Comparator.naturalOrder())));

            } else {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::getPh, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
            }

        } else if (sortKey.equals("tm")) {
            if (sortOrder.equals("asc")) {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::getTm, Comparator.nullsLast(Comparator.naturalOrder())));

            } else {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::getTm, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
            }
        } else if (sortKey.equals("toffset")) {
            if (sortOrder.equals("asc")) {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::getTOffset, Comparator.nullsLast(Comparator.naturalOrder())));

            } else {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::getTOffset, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
            }
        } else if (sortKey.equals("proteinConcentration")) {
            if (sortOrder.equals("asc")) {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::getProteinConcentration, Comparator.nullsLast(Comparator.naturalOrder())));

            } else {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::getProteinConcentration, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
            }
        } else if (sortKey.equals("halfLife")) {
            if (sortOrder.equals("asc")) {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::getHalfLife, Comparator.nullsLast(Comparator.naturalOrder())));

            } else {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::getHalfLife, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
            }
        } else if (sortKey.equals("scanRate")) {
            if (sortOrder.equals("asc")) {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::getScanRate, Comparator.nullsLast(Comparator.naturalOrder())));

            } else {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::getScanRate, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
            }
        } else if (sortKey.equals("cp")) {
            if (sortOrder.equals("asc")) {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::getCp, Comparator.nullsLast(Comparator.naturalOrder())));

            } else {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::getCp, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
            }
        } else if (sortKey.equals("samplePurity")) {
            if (sortOrder.equals("asc")) {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::getPurityOfSample, Comparator.nullsLast(Comparator.naturalOrder())));

            } else {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::getPurityOfSample, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
            }
        } else if (sortKey.equals("curated")) {
            if (sortOrder.equals("asc")) {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::isCurated, Comparator.nullsLast(Comparator.naturalOrder())));

            } else {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::isCurated, Comparator.nullsFirst(Comparator.naturalOrder())).reversed());
            }
        } else if (sortKey.equals("protein")) {
            if (sortOrder.equals("asc")) {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::getProtein, Comparator.nullsLast(Comparator.naturalOrder())));

            } else {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::getProtein, Comparator.nullsLast(Comparator.naturalOrder())));
            }
        } else if (sortKey.equals("mutation")) {

            if (sortOrder.equals("asc")) {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::getPosition, Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(MutationExperimentsSearch::getMutationAbb, Comparator.nullsLast(Comparator.nullsLast(Comparator.naturalOrder()))));

            } else {
                experiments.sort(Comparator.comparing(MutationExperimentsSearch::getPosition, Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(MutationExperimentsSearch::getMutationAbb, Comparator.nullsLast(Comparator.nullsLast(Comparator.naturalOrder()))).reversed());
            }
        }
        
        return experiments;
    }
    
    
    
    
    /**
     * Full-text search for mutation experiments
     * @param query full-text query
     * @param page page number
     * @param size page size
     * @return SearchMutationPageDTO 
     */
    public SearchMutationPageDTO searchFull(String query, Integer page, Integer size) {

        Pageable searchPage = PageRequest.of(page, size);

        Page<MutationExperimentsSearch> resultPage = null;
        List<MutationExperimentsSearch> pageContent = new ArrayList<>();
        SearchMutationPageDTO resultPageDTO = new SearchMutationPageDTO();
        List<SearchMutationDTO> searchResults = new ArrayList<>();
        List<MutationExperiments> foundMutations = new ArrayList<MutationExperiments>();
        String searchValue;

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode dataNode = null;
        JsonNode filterNode = null;
        try {
            dataNode = objectMapper.readTree(query);
        } catch (IOException ex) {
            Logger.getLogger(SearchServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        // column used for filtering
        filterNode = dataNode.get("filter");
        // search query
        dataNode = dataNode.get("searchData");
        searchValue = dataNode.asText();
        
        // try to find items in elasticsearch repository
        try {
            List<Datasets> datasetExperiments = CompletableFuture.supplyAsync(() -> elasticSearchDatasets(searchValue)).get();
            foundMutations.addAll(getDatasetExperiments(datasetExperiments));
            List<EsStructure> searchedStructures = CompletableFuture.supplyAsync(() -> elasticSearchStructures(searchValue)).get();
            foundMutations.addAll(getStructuresExperiments(searchedStructures));
            List<EsProteinSequence> searchedSequences = CompletableFuture.supplyAsync(() -> elasticSearchSequence(searchValue)).get();
            foundMutations.addAll(getSequenceExperiments(searchedSequences));
            List<Authors> searchedAuthors = CompletableFuture.supplyAsync(() -> elasticSearchAuthors(searchValue)).get();
            foundMutations.addAll(getAuthorsExperiments(searchedAuthors));
            List<EsInterproEntry> searchedInterproEntries = CompletableFuture.supplyAsync(() -> elasticSearchInterproEntries(searchValue)).get();
            foundMutations.addAll(getInterproExperiments(searchedInterproEntries));

        } catch (InterruptedException ex) {
            Logger.getLogger(SearchServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(SearchServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<MutationExperiments> uniqueExperiments = foundMutations.stream().distinct().collect(Collectors.toList());

        String sortKey = filterNode.get("filterKey").asText();
        String sortOrder = filterNode.get("order").asText();

        List<String> ids = new ArrayList<>();
        uniqueExperiments.forEach(exp -> ids.add(exp.getId()));
        List<MutationExperimentsSearch> searchExperiments = new ArrayList<>();
        for (MutationExperimentsSearch exp : experimentsSearchRepository.findAll()) {
            if (ids.contains(exp.getExperimentId())) {
                searchExperiments.add(exp);
            }
        }

        
        searchExperiments = this.sortExperimentsByKey(searchExperiments, sortKey, sortOrder);
        int start = (int) searchPage.getOffset();
        int end = (start + searchPage.getPageSize()) > uniqueExperiments.size() ? uniqueExperiments.size() : (start + searchPage.getPageSize());

        resultPage = new PageImpl<MutationExperimentsSearch>(searchExperiments.subList(start, end), searchPage, foundMutations.size());
        pageContent = resultPage.getContent();

        for (MutationExperimentsSearch m : pageContent) {
            SearchMutationDTO sm = new SearchMutationDTO();
            sm.setDdG(m.getDdg() == null ? null : BigDecimal.valueOf(m.getDdg()));
            sm.setDtM(m.getDtm() == null ? null : BigDecimal.valueOf(m.getDtm()));
            sm.setCurated(m.isCurated());
            sm.setHalfLife(m.getHalfLife() == null ? null : BigDecimal.valueOf(m.getHalfLife()));
            sm.setId(m.getMutId());
            sm.setExperimentId(m.getExperimentId());
            sm.setMutation(m.getWildType().concat(m.getPosition().toString()).concat(m.getMutation()));
            sm.setPH(m.getPh() == null ? null : BigDecimal.valueOf(m.getPh()));
            sm.setProtein(m.getProtein());
            sm.setProteinConcentration(m.getProteinConcentration() == null ? null : BigDecimal.valueOf(m.getProteinConcentration()));
            sm.setPurityOfSample(m.getPurityOfSample() == null ? null : BigDecimal.valueOf(m.getPurityOfSample()));
            sm.setScanRate(m.getScanRate() == null ? null : BigDecimal.valueOf(m.getScanRate()));
            sm.setSequenceId(m.getSequenceId());
            searchResults.add(sm);

        }

        resultPageDTO.setSearchResults(searchResults);
        resultPageDTO.setCurrentPage(resultPage.getNumber());
        resultPageDTO.setSearchResultsCount(uniqueExperiments.size());
        resultPageDTO.setTotalPageCount(resultPage.getTotalPages());

        return resultPageDTO;

    }

    /**
     * Export full-text search results
     * @param searchQuery full-text query
     * @return list of experiments
     */
    public List<MutationExperiments> exportFullSearch(String searchQuery) {

        List<MutationExperiments> foundMutations = new ArrayList<MutationExperiments>();

        try {
            List<Datasets> datasetExperiments = CompletableFuture.supplyAsync(() -> elasticSearchDatasets(searchQuery)).get();
            foundMutations.addAll(getDatasetExperiments(datasetExperiments));
            List<EsStructure> searchedStructures = CompletableFuture.supplyAsync(() -> elasticSearchStructures(searchQuery)).get();
            foundMutations.addAll(getStructuresExperiments(searchedStructures));
            List<EsProteinSequence> searchedSequences = CompletableFuture.supplyAsync(() -> elasticSearchSequence(searchQuery)).get();
            foundMutations.addAll(getSequenceExperiments(searchedSequences));
            List<Authors> searchedAuthors = CompletableFuture.supplyAsync(() -> elasticSearchAuthors(searchQuery)).get();
            foundMutations.addAll(getAuthorsExperiments(searchedAuthors));

        } catch (InterruptedException ex) {
            Logger.getLogger(SearchServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(SearchServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<MutationExperiments> uniqueExperiments = foundMutations.stream().distinct().collect(Collectors.toList());

        return uniqueExperiments;
    }

    /**
     * Create data with interpro entries names and datasets used in UI
     * @return optionsDTO object with options
     */
    public OptionDTO getSeachItemOptions() {

        // create data with datasets names
        List<Datasets> datasets = datasetsRepository.findAll();
        List<SearchOptionValue> datasetsNames = new ArrayList<>();
        datasets.forEach(dataset -> {
            datasetsNames.add(new SearchOptionValue(dataset.getName(), dataset.getName()));

        });

        // create data with interpro entries names
        Query q = em.createNativeQuery("select distinct i.name from protein_sequence p JOIN protein_sequence_interpro_entries e ON p.sequence_id=e.sequence_id JOIN interpro_entries i ON e.interpro_entry_id=i.interpro_entry_id where e.sequence_id IS NOT NULL AND e.interpro_entry_id IS NOT NULL AND (i.type=\"Family\" OR i.type=\"Homologous_superfamily\")");
        List<String> interproEntries = q.getResultList();
        List<SearchOptionValue> interproFamilies = new ArrayList<>();
        interproEntries.forEach(name -> interproFamilies.add(new SearchOptionValue(name, name)));

        // get names of stored organisms
        List<SearchOptionValue> organismNames = new ArrayList<>();
        proteinSequenceRepository.findAll().forEach(protein -> {
            organismNames.add(new SearchOptionValue(protein.getSpecies(),protein.getSpecies()));
        });
        
        
        OptionDTO options = new OptionDTO();
        options.setDatasetNames(datasetsNames);
        options.setInterProFamiliesNames(interproFamilies);
        options.setOrganismNames(organismNames);

        return options;
    }

    
    
    
    
    
    /**
     * Create search results statistics based on search query
     * @param request search request
     * @return search statistics
     */
    @Override
    public SearchResultsStatisticsDTO getSearchResultsStatistics(String request, String searchType) {
        
        List<MutationExperiments> experiments = this.getSearchExperiments(request, searchType);
        List<String> ids = new ArrayList<>();
        List<MutationExperimentsSearch> searchExperiments = new ArrayList<>();
        
        experiments.forEach(exp -> ids.add(exp.getId()));
        for (MutationExperimentsSearch exp : experimentsSearchRepository.findAll()) {
            if (ids.contains(exp.getExperimentId())) {
                searchExperiments.add(exp);
            }
        }
        
        return this.createResultsStatistics(searchExperiments); 
    }
    
    /**
     * Compute top proteins statistics
     * @param results search results
     * @return list of top proteins holding protein name and number of entries for the protein
     */
    private List<TopProteins> computeSearchTopProteins(List<MutationExperimentsSearch> results){
         
        Integer topProteinsCount = 10;
        List<TopProteins> topProteins = new ArrayList<>();
        try{
             Map<String,Long> topNProteins = results.stream()
                .filter(entry -> entry.getProtein() != null)
                .collect(Collectors.groupingBy(MutationExperimentsSearch::getProtein, Collectors.counting()));
             
             topNProteins = topNProteins.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(topProteinsCount)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldVal, newVal) -> oldVal, LinkedHashMap::new));
        
            topNProteins.entrySet()
                .stream()
                .forEach((Map.Entry<String, Long> entry) -> {
                    var protein = new TopProteins();
                    protein.setProteinName(entry.getKey());
                    protein.setExperimentsCount(Integer.valueOf(entry.getValue().toString()));
                    topProteins.add(protein);
                });
        }
        catch(Exception e){
           logger.info(e.getMessage());
        }
         
        return topProteins;
    }
    
    /**
     * Compute ddg histogram for search results
     * @param results search results
     * @return List of ddg histogram entries holding interval range and number of entries
     */
    private List<DdgHistogramEntry> computeSearchDdgHistogram(List<MutationExperimentsSearch> results){
        
        List<DdgHistogramEntry> ddgHistogram = new ArrayList<>();
        List<Double> ddGValues = results.stream().filter(entry -> entry.getDdg() != null).map(MutationExperimentsSearch::getDdg).collect(Collectors.toList());
        List<Double> ddGAllValues = ddGValues.stream().sorted().collect(Collectors.toList());
        ddGValues = ddGValues.stream().sorted().distinct().collect(Collectors.toList());
        
        if(ddGValues.size() > 0){
            double maxDdg = ddGValues.stream().mapToDouble(ddG -> ddG).max().getAsDouble();
            double minDdg = ddGValues.stream().mapToDouble(ddG -> ddG).min().getAsDouble();

            // compute interval width based on number of intervals
            long intervals = 10;
            Double intervalWidth = (maxDdg - minDdg) / intervals;
            long interval = (intervalWidth.compareTo(1.0) < 0) ? 1 : Math.round(intervalWidth);

            Long n = Math.round(minDdg);

            // compute bounds of intervals
            List<Long> upperBounds = new ArrayList<Long>();

            while(n.compareTo(Math.round(maxDdg)) < 0){
                n +=  interval;
                upperBounds.add(n);    
            }


            for(int i=0; i <  upperBounds.size(); i++){
                DdgHistogramEntry entry = new DdgHistogramEntry();
                entry.setId(i);

                long count = 0;
                long upBorder =  upperBounds.get(i);
                long lowBorder = i == 0 ?   upperBounds.get(i) : upperBounds.get(i - 1);

                if(i == 0){
                    count = ddGAllValues.stream().filter(num -> (num <= upBorder)).count();
                }
                else if(i ==  upperBounds.size()){
                     count = ddGAllValues.stream().filter(num -> (num > upBorder)).count();
                }
                else{
                     count = ddGAllValues.stream().filter(num -> (num > lowBorder && num <= upBorder)).count();
                }

                entry.setIntervalMin(String.valueOf(lowBorder));
                entry.setIntervalMax(String.valueOf(upBorder));
                entry.setCount(count);   
                ddgHistogram.add(entry);

            }
        }
        return ddgHistogram;
    }
    
    /**
     * Compute dTm histogram for search results
     * @param results search results
     * @return List of dtm histogram entries holding interval range and number of entries
     */
    private List<DtmHistogramEntry> computeSearchDtmHistogram(List<MutationExperimentsSearch> results){
        
        List<DtmHistogramEntry> dtmHistogram = new ArrayList<>();
        List<Double> dtmValues = results.stream().filter(entry -> entry.getDtm() != null).map(MutationExperimentsSearch::getDtm).collect(Collectors.toList());
        List<Double> dtmAllValues = dtmValues.stream().sorted().collect(Collectors.toList());
        dtmValues = dtmValues.stream().sorted().distinct().collect(Collectors.toList());
        
        dtmValues.stream().forEach(System.out::println);
        
        if(dtmValues.size() > 0){
            double maxDtm = dtmValues.stream().mapToDouble(dtm -> dtm).max().getAsDouble();
            double minDtm = dtmValues.stream().mapToDouble(dtm -> dtm).min().getAsDouble();


            // compute interval width based on number on intervals
            Integer intervals = 10;
            Double intervalWidth = (maxDtm - minDtm) / intervals;
            Long interval = (intervalWidth.compareTo(1.0) < 0) ? 1 : Math.round(intervalWidth);

            Long n = Math.round(minDtm);

            List<Long> upperBoundsDtm = new ArrayList<Long>();

            // compute bounds of intervals
            while(n.compareTo(Math.round(maxDtm)) < 0){
                n +=  interval;
                upperBoundsDtm.add(n);    
            }

            for(int i=0; i <=  upperBoundsDtm.size(); i++){
                DtmHistogramEntry entry = new DtmHistogramEntry();
                entry.setId(i);

                long count = 0;
                long upBorderDtm = i == upperBoundsDtm.size() ? upperBoundsDtm.get(i - 1) : upperBoundsDtm.get(i);
                long lowBorderDtm = i == 0 ?  upperBoundsDtm.get(i) : upperBoundsDtm.get(i - 1);

                if(i == 0){
                    count = dtmAllValues.stream().filter(num -> (num <= upBorderDtm)).count();
                    entry.setIntervalMin(String.valueOf(lowBorderDtm));
                }
                else if(i ==  upperBoundsDtm.size()){
                     count = dtmAllValues.stream().filter(num -> (num > upBorderDtm)).count();
                      entry.setIntervalMin(String.valueOf(lowBorderDtm));
                }
                else{
                     count = dtmAllValues.stream().filter(num -> (num > lowBorderDtm && num <= upBorderDtm)).count();
                      entry.setIntervalMin(String.valueOf(lowBorderDtm));
                }

                entry.setIntervalMax(String.valueOf(upBorderDtm));
                entry.setCount(count); 
                dtmHistogram.add(entry);
            }
        }
        return dtmHistogram;
    }
    
    /**
     * Compute search results subsitutions by entries histogram 
     * @param results search results
     * @return list of amino acids substitutions object holding residue and corresponding number of substitutions for other residues
     */
    private List<AminoAcidsSubstitution> computeSearchMutationSubstitutions(List<MutationExperimentsSearch> results){
        
        HashMap<String, Integer> toSubstitutions = new HashMap<String, Integer>();
        HashMap<String, Integer> fromSubstitutions = new HashMap<String, Integer>();
        List<AminoAcidsSubstitution> mutationSubstitutionsHistogram = new ArrayList<>();
        
        String[] aminoAcids = {"A", "C", "D", "E", "F", "G", "H", "I", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "W", "Y", "V"};

        // initialize map 
        for (String aminoAcid : aminoAcids) {
            toSubstitutions.computeIfAbsent(aminoAcid, value -> 0);
            fromSubstitutions.computeIfAbsent(aminoAcid, value -> 0);
        }

        List<Integer> proteinIds = results.stream().map(MutationExperimentsSearch::getSequenceId).distinct().collect(Collectors.toList());
        List<ProteinSequence> proteins = this.proteinSequenceRepository.findAllById(proteinIds);
        List<String> experimentsIds = results.stream().map(MutationExperimentsSearch::getExperimentId).collect(Collectors.toList());
        
        for (ProteinSequence p : proteins) {
            for (Residues r : p.getResidues()) {
                for (Mutations m : r.getMutations()) {
                    List<MutationExperiments> experiments = m.getMutationExperiments().stream().filter(experiment -> experimentsIds.contains(experiment.getId())).collect(Collectors.toList());
                    for(MutationExperiments e: experiments){
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

            mutationSubstitutionsHistogram.add(entry);
        }
        
        return mutationSubstitutionsHistogram;
    }
    
    
    /**
     * Compute search result statistics
     * @param results serch results 
     * @return search results DTO
     */
    private SearchResultsStatisticsDTO createResultsStatistics(List<MutationExperimentsSearch> results){
           
        SearchResultsStatisticsDTO resultStatistics = new SearchResultsStatisticsDTO();
        List<TopProteins> topProteins = null;
        List<DdgHistogramEntry> ddgHistogram = null;
        List<DtmHistogramEntry> dtmHistogram = null;
        List<AminoAcidsSubstitution> mutationSubstitutionsHistogram = null;
       
        topProteins = this.computeSearchTopProteins(results);
        ddgHistogram = this.computeSearchDdgHistogram(results);
        dtmHistogram = this.computeSearchDtmHistogram(results);
        mutationSubstitutionsHistogram = this.computeSearchMutationSubstitutions(results);
               
        resultStatistics.setResultsTopProteins(topProteins);
        resultStatistics.setResultsDdgHistogram(ddgHistogram);
        resultStatistics.setResultsDtmHistogram(dtmHistogram);
        resultStatistics.setResultsSubstitutionHistogram(mutationSubstitutionsHistogram);
           
        return resultStatistics;
    }
}
