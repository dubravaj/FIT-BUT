/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.loschmidt.fireprotdb.backend.service;

import cz.loschmidt.fireprotdb.backend.dto.ChartsStatisticsDTO;
import cz.loschmidt.fireprotdb.backend.dto.StatisticsDTO;
import org.springframework.stereotype.Service;

/**
 * Statistics service
 * @author Juraj Dubrava
 */

@Service
public interface StatisticsService {
    public StatisticsDTO getFireprotDBStats();
    public int updateChartStatistics();
    public ChartsStatisticsDTO getChartsStatistics();
}
