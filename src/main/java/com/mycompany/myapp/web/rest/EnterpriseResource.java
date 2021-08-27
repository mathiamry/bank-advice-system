package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Enterprise;
import com.mycompany.myapp.repository.EnterpriseRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Enterprise}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class EnterpriseResource {

    private final Logger log = LoggerFactory.getLogger(EnterpriseResource.class);

    private static final String ENTITY_NAME = "enterprise";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EnterpriseRepository enterpriseRepository;

    public EnterpriseResource(EnterpriseRepository enterpriseRepository) {
        this.enterpriseRepository = enterpriseRepository;
    }

    /**
     * {@code POST  /enterprises} : Create a new enterprise.
     *
     * @param enterprise the enterprise to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new enterprise, or with status {@code 400 (Bad Request)} if the enterprise has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/enterprises")
    public ResponseEntity<Enterprise> createEnterprise(@Valid @RequestBody Enterprise enterprise) throws URISyntaxException {
        log.debug("REST request to save Enterprise : {}", enterprise);
        if (enterprise.getId() != null) {
            throw new BadRequestAlertException("A new enterprise cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Enterprise result = enterpriseRepository.save(enterprise);
        return ResponseEntity
            .created(new URI("/api/enterprises/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /enterprises/:id} : Updates an existing enterprise.
     *
     * @param id the id of the enterprise to save.
     * @param enterprise the enterprise to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated enterprise,
     * or with status {@code 400 (Bad Request)} if the enterprise is not valid,
     * or with status {@code 500 (Internal Server Error)} if the enterprise couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/enterprises/{id}")
    public ResponseEntity<Enterprise> updateEnterprise(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Enterprise enterprise
    ) throws URISyntaxException {
        log.debug("REST request to update Enterprise : {}, {}", id, enterprise);
        if (enterprise.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, enterprise.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!enterpriseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Enterprise result = enterpriseRepository.save(enterprise);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, enterprise.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /enterprises/:id} : Partial updates given fields of an existing enterprise, field will ignore if it is null
     *
     * @param id the id of the enterprise to save.
     * @param enterprise the enterprise to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated enterprise,
     * or with status {@code 400 (Bad Request)} if the enterprise is not valid,
     * or with status {@code 404 (Not Found)} if the enterprise is not found,
     * or with status {@code 500 (Internal Server Error)} if the enterprise couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/enterprises/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Enterprise> partialUpdateEnterprise(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Enterprise enterprise
    ) throws URISyntaxException {
        log.debug("REST request to partial update Enterprise partially : {}, {}", id, enterprise);
        if (enterprise.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, enterprise.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!enterpriseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Enterprise> result = enterpriseRepository
            .findById(enterprise.getId())
            .map(
                existingEnterprise -> {
                    if (enterprise.getName() != null) {
                        existingEnterprise.setName(enterprise.getName());
                    }
                    if (enterprise.getAddress() != null) {
                        existingEnterprise.setAddress(enterprise.getAddress());
                    }
                    if (enterprise.getNinea() != null) {
                        existingEnterprise.setNinea(enterprise.getNinea());
                    }
                    if (enterprise.getEmail() != null) {
                        existingEnterprise.setEmail(enterprise.getEmail());
                    }

                    return existingEnterprise;
                }
            )
            .map(enterpriseRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, enterprise.getId().toString())
        );
    }

    /**
     * {@code GET  /enterprises} : get all the enterprises.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of enterprises in body.
     */
    @GetMapping("/enterprises")
    public List<Enterprise> getAllEnterprises() {
        log.debug("REST request to get all Enterprises");
        return enterpriseRepository.findAll();
    }

    /**
     * {@code GET  /enterprises/:id} : get the "id" enterprise.
     *
     * @param id the id of the enterprise to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the enterprise, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/enterprises/{id}")
    public ResponseEntity<Enterprise> getEnterprise(@PathVariable Long id) {
        log.debug("REST request to get Enterprise : {}", id);
        Optional<Enterprise> enterprise = enterpriseRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(enterprise);
    }

    /**
     * {@code DELETE  /enterprises/:id} : delete the "id" enterprise.
     *
     * @param id the id of the enterprise to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/enterprises/{id}")
    public ResponseEntity<Void> deleteEnterprise(@PathVariable Long id) {
        log.debug("REST request to delete Enterprise : {}", id);
        enterpriseRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
