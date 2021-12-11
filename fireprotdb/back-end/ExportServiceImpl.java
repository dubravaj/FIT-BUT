/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.loschmidt.fireprotdb.backend.service;

import cz.loschmidt.fireprotdb.backend.model.MutationExperimentSummary;
import cz.loschmidt.fireprotdb.backend.model.MutationExperiments;
import cz.loschmidt.fireprotdb.backend.repository.MutationExperimentsSummaryRepository;
import cz.loschmidt.fireprotdb.backend.repository.MutationExperimentsRepository;
import cz.loschmidt.fireprotdb.backend.repository.SearchRepository;
import com.smattme.MysqlExportService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zeroturnaround.zip.commons.IOUtils;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Implementation of data export
 *
 * @author Juraj Dubrava
 *
 */
@Service
public class ExportServiceImpl implements ExportService {

    private final Logger logger = LoggerFactory.getLogger(ExportServiceImpl.class);

    // define columns in exported file
    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.withHeader(
            "experiment_id", "protein_name", "uniprot_id", "pdb_id", "chain",
            "position", "wild_type", "mutation", "ddG", "dTm", "is_curated", "type", "derived_type",
            "interpro_families", "conservation", "is_essential", "correlated_positions", "is_back_to_consensus",
            "secondary_structure", "asa", "is_in_catalytic_pocket",
            "is_in_tunnel_bottleneck", "b_factor", "method", "method_details", "technique",
            "technique_details", "pH", "tm", "notes", "publication_doi", "publication_pubmed",
            "hsw_job_id", "datasets", "sequence"
    );

    @Autowired
    MutationExperimentsRepository experimentRepository;

    @Autowired
    SearchRepository searchRepository;

    @Autowired
    SearchService searchService;

    @Autowired
    private MutationExperimentsSummaryRepository experimentsSummaryRepository;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.url}")
    private String dbSourceUrl;

    /**
     * Export search data to CSV file
     *
     * @param exportRequest search request
     * @param searchType search type
     * @return exported data
     */
    @Transactional
    @Override
    public String exportCSV(String exportRequest, String searchType) {
        logger.info("Requesting export to CSV for request {}", exportRequest);

        List<MutationExperiments> exportedExperiments = new ArrayList<>();

        // get search results according to search type
        if (searchType.equals("advanced")) {
            exportedExperiments = searchService.searchExport(exportRequest, 0, 1);
        } else {
            exportedExperiments = searchService.exportFullSearch(exportRequest);
        }

        List<String> experimentIds = exportedExperiments.stream().map(MutationExperiments::getId).collect(Collectors.toList());
        logger.debug("Number of exported results: {}", experimentIds.size());

        // write search results to CSV
        StringWriter out = new StringWriter();
        try ( CSVPrinter csvPrinter = new CSVPrinter(out, CSV_FORMAT)) {
            for (MutationExperimentSummary m : experimentsSummaryRepository.findAll()) {
                if (!experimentIds.contains(m.getExperimentId())) {
                    continue;
                }

                csvPrinter.printRecord(Arrays.asList(
                        m.getExperimentId(),
                        m.getProteinName(),
                        m.getUniprotId(),
                        m.getPdbId(),
                        m.getChain(),
                        m.getPosition(),
                        m.getWildType(),
                        m.getMutation(),
                        m.getDdg(),
                        m.getDtm(),
                        m.getIsCurated(),
                        m.getType(),
                        m.getDerivedType(),
                        m.getInterproFamilies(),
                        m.getConservation(),
                        m.getIsEssential(),
                        m.getCorrelatedPositions(),
                        m.getIsBackToConsensus(),
                        m.getSecondaryStructure(),
                        m.getAsa(),
                        m.getIsInCatalyticPocket(),
                        m.getIsInTunnelBottlenecks(),
                        m.getBFactor(),
                        m.getMethod(),
                        m.getMethodDetails(),
                        m.getTechnique(),
                        m.getTechniqueDetails(),
                        m.getPh(),
                        m.getTm(),
                        m.getNotes(),
                        m.getDoi(),
                        m.getPmid(),
                        m.getHswJobId(),
                        m.getDatasets(),
                        m.getSequence()));
            }

            return out.toString();
        } catch (IOException e) {
            logger.warn("No content has been generated due to the exception!", e);
            return "NO CONTENT";
        }
    }

    /**
     * Create SQL file with database dump
     *
     * @param response Http response
     */
    public void exportDbDump(HttpServletResponse response) {

        Properties properties = new Properties();
        properties.setProperty(MysqlExportService.DB_NAME, "fireprotdb");
        properties.setProperty(MysqlExportService.DB_USERNAME, dbUser);
        properties.setProperty(MysqlExportService.DB_PASSWORD, dbPassword);
        properties.setProperty(MysqlExportService.JDBC_CONNECTION_STRING, dbSourceUrl);

        properties.setProperty(MysqlExportService.TEMP_DIR, new File("external").getPath());
        properties.setProperty(MysqlExportService.PRESERVE_GENERATED_ZIP, "true");

        MysqlExportService mysqlExportService = new MysqlExportService(properties);
        try {
            mysqlExportService.export();
        } catch (IOException ex) {
            logger.error(null, ex);
        } catch (SQLException ex) {
            logger.error(null, ex);
        } catch (ClassNotFoundException ex) {
            logger.error(null, ex);
        }

        // create output file for dump
        FileOutputStream o;
        File f = new File("fireprotdb.sql");
        try {
            o = new FileOutputStream(f);
            byte[] strContent = mysqlExportService.getGeneratedSql().getBytes();
            o.write(strContent);
            o.close();

        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(ExportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ExportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Disposition", "attachment; filename=\"fireprotdb_dump.zip\"");

        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
            zipOutputStream.putNextEntry(new ZipEntry(f.getName()));
            FileInputStream fileInputStream = new FileInputStream(f);
            IOUtils.copy(fileInputStream, zipOutputStream);
            fileInputStream.close();
            zipOutputStream.closeEntry();
            zipOutputStream.close();

        } catch (IOException ex) {
            logger.error(null, ex);
        }

        mysqlExportService.clearTempFiles(false);

    }

}
