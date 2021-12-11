/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.loschmidt.fireprotdb.backend.service;

import cz.loschmidt.fireprotdb.backend.dto.OptionDTO;
import cz.loschmidt.fireprotdb.backend.dto.SearchMutationPageDTO;
import cz.loschmidt.fireprotdb.backend.dto.SearchResultsStatisticsDTO;
import cz.loschmidt.fireprotdb.backend.model.MutationExperiments;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Search service
 * @author Juraj Dubrava
 */

@Service
public interface SearchService {
    public List<MutationExperiments> searchExport(String request, Integer page, Integer size);
    public SearchMutationPageDTO search(String request, Integer page, Integer size);
    public SearchMutationPageDTO searchFull(String query, Integer page, Integer size);
    public List<MutationExperiments> exportFullSearch(String searchQuery);
    public OptionDTO getSeachItemOptions();
    public SearchResultsStatisticsDTO getSearchResultsStatistics(String request, String searchType);
}
