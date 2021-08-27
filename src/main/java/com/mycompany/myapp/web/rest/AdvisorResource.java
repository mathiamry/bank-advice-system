package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Advisor;
import com.mycompany.myapp.repository.AdvisorRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Advisor}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AdvisorResource {

    private final Logger log = LoggerFactory.getLogger(AdvisorResource.class);

    private static final String ENTITY_NAME = "advisor";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AdvisorRepository advisorRepository;

    public AdvisorResource(AdvisorRepository advisorRepository) {
        this.advisorRepository = advisorRepository;
    }

    /**
     * {@code POST  /advisors} : Create a new advisor.
     *
     * @param advisor the advisor to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new advisor, or with status {@code 400 (Bad Request)} if the advisor has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/advisors")
    public ResponseEntity<Advisor> createAdvisor(@Valid @RequestBody Advisor advisor) throws URISyntaxException {
        log.debug("REST request to save Advisor : {}", advisor);
        if (advisor.getId() != null) {
            throw new BadRequestAlertException("A new advisor cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Advisor result = advisorRepository.save(advisor);
        return ResponseEntity
            .created(new URI("/api/advisors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /advisors/:id} : Updates an existing advisor.
     *
     * @param id the id of the advisor to save.
     * @param advisor the advisor to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated advisor,
     * or with status {@code 400 (Bad Request)} if the advisor is not valid,
     * or with status {@code 500 (Internal Server Error)} if the advisor couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/advisors/{id}")
    public ResponseEntity<Advisor> updateAdvisor(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Advisor advisor
    ) throws URISyntaxException {
        log.debug("REST request to update Advisor : {}, {}", id, advisor);
        if (advisor.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, advisor.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!advisorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Advisor result = advisorRepository.save(advisor);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, advisor.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /advisors/:id} : Partial updates given fields of an existing advisor, field will ignore if it is null
     *
     * @param id the id of the advisor to save.
     * @param advisor the advisor to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated advisor,
     * or with status {@code 400 (Bad Request)} if the advisor is not valid,
     * or with status {@code 404 (Not Found)} if the advisor is not found,
     * or with status {@code 500 (Internal Server Error)} if the advisor couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/advisors/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Advisor> partialUpdateAdvisor(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Advisor advisor
    ) throws URISyntaxException {
        log.debug("REST request to partial update Advisor partially : {}, {}", id, advisor);
        if (advisor.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, advisor.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!advisorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Advisor> result = advisorRepository
            .findById(advisor.getId())
            .map(
                existingAdvisor -> {
                    if (advisor.getGender() != null) {
                        existingAdvisor.setGender(advisor.getGender());
                    }
                    if (advisor.getTelephone() != null) {
                        existingAdvisor.setTelephone(advisor.getTelephone());
                    }

                    return existingAdvisor;
                }
            )
            .map(advisorRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, advisor.getId().toString())
        );
    }

    /**
     * {@code GET  /advisors} : get all the advisors.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of advisors in body.
     */
    @GetMapping("/advisors")
    public List<Advisor> getAllAdvisors() {
        log.debug("REST request to get all Advisors");
        return advisorRepository.findAll();
    }

    /**
     * {@code GET  /advisors/:id} : get the "id" advisor.
     *
     * @param id the id of the advisor to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the advisor, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/advisors/{id}")
    public ResponseEntity<Advisor> getAdvisor(@PathVariable Long id) {
        log.debug("REST request to get Advisor : {}", id);
        Optional<Advisor> advisor = advisorRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(advisor);
    }

    /**
     * {@code DELETE  /advisors/:id} : delete the "id" advisor.
     *
     * @param id the id of the advisor to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/advisors/{id}")
    public ResponseEntity<Void> deleteAdvisor(@PathVariable Long id) {
        log.debug("REST request to delete Advisor : {}", id);
        advisorRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
