/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.loschmidt.fireprotdb.backend.service;

import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

/**
 * Export service
 * @author Juraj Dubrava
 */
@Service
public interface ExportService {

    String exportCSV(String exportRequest, String searchType);

    void exportDbDump(HttpServletResponse response);

}
