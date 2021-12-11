/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.loschmidt.fireprotdb.backend.controller;

import cz.loschmidt.fireprotdb.backend.dto.OptionDTO;
import cz.loschmidt.fireprotdb.backend.dto.SearchMutationPageDTO;
import cz.loschmidt.fireprotdb.backend.dto.SearchResultsStatisticsDTO;
import cz.loschmidt.fireprotdb.backend.service.SearchService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Search controller
 *
 * @author Juraj Dubrava
 */

@CrossOrigin
@RestController
@Tag(name = "Search", description = "Search API")
public class SearchController {

    @Autowired
    SearchService searchService;

    @Operation(summary = "Search")
    @RequestMapping(value = "/v1/search", method = {RequestMethod.POST},
            produces = {"application/vnd.fireprotdb.app-v1.0+json"})
    public SearchMutationPageDTO search(@RequestBody String searchRequest, @RequestParam("type") String type, @RequestParam(value = "page", required = false, defaultValue = "0") Integer page, @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {

        if (type.equals("advanced")) {
            return searchService.search(searchRequest, page, size);//.stream().map(SearchMutationDTO::new).collect(Collectors.toList());
        } else {
            return searchService.searchFull(searchRequest, page, size);
        }

    }
    
    @Hidden
    @PostMapping(value="/v1/search/statistics", produces = {"application/vnd.fireprotdb.app-v1.0+json"})
    public SearchResultsStatisticsDTO searchStatistics(@RequestBody String searchRequest, @RequestParam("type") String type){
        return searchService.getSearchResultsStatistics(searchRequest,type);
    }
    
    
    @Hidden
    @GetMapping(value = "/v1/search/options")
    public OptionDTO searchItemOptions() {
        return searchService.getSeachItemOptions();
    }
}
