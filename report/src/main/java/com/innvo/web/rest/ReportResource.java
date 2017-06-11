package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innvo.domain.Report;
import com.innvo.domain.Reportparameter;
import com.innvo.jasper.GenerateReportFile;
import com.innvo.jasper.Parameters;
import com.innvo.jasper.Validation;
import com.innvo.repository.ReportRepository;
import com.innvo.repository.ReportparameterRepository;
import com.innvo.repository.search.ReportSearchRepository;
import com.innvo.web.rest.util.HeaderUtil;
import com.innvo.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Report.
 */
@RestController
@RequestMapping("/api")
public class ReportResource {

    private final Logger log = LoggerFactory.getLogger(ReportResource.class);

    private static final String ENTITY_NAME = "report";
        
    private final ReportRepository reportRepository;

    private final ReportSearchRepository reportSearchRepository;
    
    @Inject
    ReportparameterRepository reportparameterRepository;

    @Inject
    GenerateReportFile generateReportFile; 
    
    public ReportResource(ReportRepository reportRepository, ReportSearchRepository reportSearchRepository) {
        this.reportRepository = reportRepository;
        this.reportSearchRepository = reportSearchRepository;
    }

    /**
     * POST  /reports : Create a new report.
     *
     * @param report the report to create
     * @return the ResponseEntity with status 201 (Created) and with body the new report, or with status 400 (Bad Request) if the report has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/reports")
    @Timed
    public ResponseEntity<Report> createReport(@Valid @RequestBody Report report) throws URISyntaxException {
        log.debug("REST request to save Report : {}", report);
        if (report.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new report cannot already have an ID")).body(null);
        }
        Report result = reportRepository.save(report);
        reportSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/reports/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /reports : Updates an existing report.
     *
     * @param report the report to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated report,
     * or with status 400 (Bad Request) if the report is not valid,
     * or with status 500 (Internal Server Error) if the report couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/reports")
    @Timed
    public ResponseEntity<Report> updateReport(@Valid @RequestBody Report report) throws URISyntaxException {
        log.debug("REST request to update Report : {}", report);
        if (report.getId() == null) {
            return createReport(report);
        }
        Report result = reportRepository.save(report);
        reportSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, report.getId().toString()))
            .body(result);
    }

    /**
     * GET  /reports : get all the reports.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of reports in body
     */
    @GetMapping("/reports")
    @Timed
    public ResponseEntity<List<Report>> getAllReports(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Reports");
        Page<Report> page = reportRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/reports");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /reports/:id : get the "id" report.
     *
     * @param id the id of the report to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the report, or with status 404 (Not Found)
     */
    @GetMapping("/reports/{id}")
    @Timed
    public ResponseEntity<Report> getReport(@PathVariable Long id) {
        log.debug("REST request to get Report : {}", id);
        Report report = reportRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(report));
    }

    /**
     * DELETE  /reports/:id : delete the "id" report.
     *
     * @param id the id of the report to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/reports/{id}")
    @Timed
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        log.debug("REST request to delete Report : {}", id);
        reportRepository.delete(id);
        reportSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/reports?query=:query : search for the report corresponding
     * to the query.
     *
     * @param query the query of the report search 
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/reports")
    @Timed
    public ResponseEntity<List<Report>> searchReports(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Reports for query {}", query);
        Page<Report> page = reportSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/reports");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
 
    /**
     * 
     * @param reportId
     * @param parameters
     * @param response
     * @return
     * @throws Exception
     */
    
    @GetMapping("/generateReport/{reportId}/{parameters}")
    @Timed
    public byte[] generateReport(@PathVariable("reportId") String reportId,
            @PathVariable("parameters") String parameters) throws Exception {
           Report report = reportRepository.findOne(Long.parseLong(reportId));
           byte[] output=generateReportFile.generateReport(report, parameters);
 
           return output;
     }

    /**
     * 
     * @param reportId
     * @param pageable
     * @return
     * @throws Exception
     */
    
    @GetMapping("/parameterList/{reportId}")
    @Timed
    public  ResponseEntity<List<Parameters>> parameterList(@PathVariable("reportId") long reportId,
        		           Pageable pageable) throws Exception {
    	List<Parameters> list=new ArrayList<Parameters>();
    	Page<Reportparameter> reportparameters=reportparameterRepository.findByReportId(reportId,pageable);
    	HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(reportparameters, "/api/reports");

    	for(Reportparameter reportparameter:reportparameters.getContent()){
        	System.out.println(reportparameter.getValidation());
    		Parameters parameters=new Parameters();
    		 parameters.setKey(reportparameter.getLabel());
    		 parameters.setValue("");
    		 parameters.setDataType(reportparameter.getDatatype());
    		  Validation validation=new Validation();
    	    	validation.setRequired(reportparameter.getRequired());
    	    	validation.setMinlength(reportparameter.getMinlength());
    	    	validation.setMaxlength(reportparameter.getMaxlength());
    		 parameters.setValidation(validation);
    		list.add(parameters);
    	}
        return new ResponseEntity<>(list, headers, HttpStatus.OK);
      }
}
