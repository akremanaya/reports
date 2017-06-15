package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Reportparameter;

import com.innvo.repository.ReportparameterRepository;
import com.innvo.repository.search.ReportparameterSearchRepository;
import com.innvo.service.DomainService;
import com.innvo.web.rest.util.HeaderUtil;
import com.innvo.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Reportparameter.
 */
@RestController
@RequestMapping("/api")
public class ReportparameterResource {

    private final Logger log = LoggerFactory.getLogger(ReportparameterResource.class);

    private static final String ENTITY_NAME = "reportparameter";
        
    private final ReportparameterRepository reportparameterRepository;

    private final ReportparameterSearchRepository reportparameterSearchRepository;

    @Autowired
    DomainService domainService;
    
    
    public ReportparameterResource(ReportparameterRepository reportparameterRepository, ReportparameterSearchRepository reportparameterSearchRepository) {
        this.reportparameterRepository = reportparameterRepository;
        this.reportparameterSearchRepository = reportparameterSearchRepository;
    }

    /**
     * POST  /reportparameters : Create a new reportparameter.
     *
     * @param reportparameter the reportparameter to create
     * @return the ResponseEntity with status 201 (Created) and with body the new reportparameter, or with status 400 (Bad Request) if the reportparameter has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/reportparameters")
    @Timed
    public ResponseEntity<Reportparameter> createReportparameter(@Valid @RequestBody Reportparameter reportparameter) throws URISyntaxException {
        log.debug("REST request to save Reportparameter : {}", reportparameter);
        if (reportparameter.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new reportparameter cannot already have an ID")).body(null);
        }
        ZonedDateTime lastmodifieddate = ZonedDateTime.now(ZoneId.systemDefault());
        reportparameter.setLastmodifieddatetime(lastmodifieddate);
        reportparameter.setDomain(domainService.getDomain());
        Reportparameter result = reportparameterRepository.save(reportparameter);
        reportparameterSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/reportparameters/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /reportparameters : Updates an existing reportparameter.
     *
     * @param reportparameter the reportparameter to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated reportparameter,
     * or with status 400 (Bad Request) if the reportparameter is not valid,
     * or with status 500 (Internal Server Error) if the reportparameter couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/reportparameters")
    @Timed
    public ResponseEntity<Reportparameter> updateReportparameter(@Valid @RequestBody Reportparameter reportparameter) throws URISyntaxException {
        log.debug("REST request to update Reportparameter : {}", reportparameter);
        if (reportparameter.getId() == null) {
            return createReportparameter(reportparameter);
        }
        ZonedDateTime lastmodifieddate = ZonedDateTime.now(ZoneId.systemDefault());
        reportparameter.setLastmodifieddatetime(lastmodifieddate);
        reportparameter.setDomain(domainService.getDomain());
        Reportparameter result = reportparameterRepository.save(reportparameter);
        reportparameterSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, reportparameter.getId().toString()))
            .body(result);
    }

    /**
     * GET  /reportparameters : get all the reportparameters.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of reportparameters in body
     */
    @GetMapping("/reportparameters")
    @Timed
    public ResponseEntity<List<Reportparameter>> getAllReportparameters(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Reportparameters");
        Page<Reportparameter> page = reportparameterRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/reportparameters");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /reportparameters/:id : get the "id" reportparameter.
     *
     * @param id the id of the reportparameter to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the reportparameter, or with status 404 (Not Found)
     */
    @GetMapping("/reportparameters/{id}")
    @Timed
    public ResponseEntity<Reportparameter> getReportparameter(@PathVariable Long id) {
        log.debug("REST request to get Reportparameter : {}", id);
        Reportparameter reportparameter = reportparameterRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(reportparameter));
    }

    /**
     * DELETE  /reportparameters/:id : delete the "id" reportparameter.
     *
     * @param id the id of the reportparameter to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/reportparameters/{id}")
    @Timed
    public ResponseEntity<Void> deleteReportparameter(@PathVariable Long id) {
        log.debug("REST request to delete Reportparameter : {}", id);
        reportparameterRepository.delete(id);
        reportparameterSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/reportparameters?query=:query : search for the reportparameter corresponding
     * to the query.
     *
     * @param query the query of the reportparameter search 
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/reportparameters")
    @Timed
    public ResponseEntity<List<Reportparameter>> searchReportparameters(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Reportparameters for query {}", query);
        Page<Reportparameter> page = reportparameterSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/reportparameters");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
