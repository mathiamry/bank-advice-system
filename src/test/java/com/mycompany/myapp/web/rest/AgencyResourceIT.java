package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Agency;
import com.mycompany.myapp.repository.AgencyRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AgencyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AgencyResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "SAMf+@aa.y";
    private static final String UPDATED_EMAIL = "eA@pSwp.HNq6Y";

    private static final String ENTITY_API_URL = "/api/agencies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAgencyMockMvc;

    private Agency agency;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Agency createEntity(EntityManager em) {
        Agency agency = new Agency().name(DEFAULT_NAME).address(DEFAULT_ADDRESS).contact(DEFAULT_CONTACT).email(DEFAULT_EMAIL);
        return agency;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Agency createUpdatedEntity(EntityManager em) {
        Agency agency = new Agency().name(UPDATED_NAME).address(UPDATED_ADDRESS).contact(UPDATED_CONTACT).email(UPDATED_EMAIL);
        return agency;
    }

    @BeforeEach
    public void initTest() {
        agency = createEntity(em);
    }

    @Test
    @Transactional
    void createAgency() throws Exception {
        int databaseSizeBeforeCreate = agencyRepository.findAll().size();
        // Create the Agency
        restAgencyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(agency)))
            .andExpect(status().isCreated());

        // Validate the Agency in the database
        List<Agency> agencyList = agencyRepository.findAll();
        assertThat(agencyList).hasSize(databaseSizeBeforeCreate + 1);
        Agency testAgency = agencyList.get(agencyList.size() - 1);
        assertThat(testAgency.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAgency.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testAgency.getContact()).isEqualTo(DEFAULT_CONTACT);
        assertThat(testAgency.getEmail()).isEqualTo(DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void createAgencyWithExistingId() throws Exception {
        // Create the Agency with an existing ID
        agency.setId(1L);

        int databaseSizeBeforeCreate = agencyRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAgencyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(agency)))
            .andExpect(status().isBadRequest());

        // Validate the Agency in the database
        List<Agency> agencyList = agencyRepository.findAll();
        assertThat(agencyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = agencyRepository.findAll().size();
        // set the field null
        agency.setName(null);

        // Create the Agency, which fails.

        restAgencyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(agency)))
            .andExpect(status().isBadRequest());

        List<Agency> agencyList = agencyRepository.findAll();
        assertThat(agencyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAddressIsRequired() throws Exception {
        int databaseSizeBeforeTest = agencyRepository.findAll().size();
        // set the field null
        agency.setAddress(null);

        // Create the Agency, which fails.

        restAgencyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(agency)))
            .andExpect(status().isBadRequest());

        List<Agency> agencyList = agencyRepository.findAll();
        assertThat(agencyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = agencyRepository.findAll().size();
        // set the field null
        agency.setEmail(null);

        // Create the Agency, which fails.

        restAgencyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(agency)))
            .andExpect(status().isBadRequest());

        List<Agency> agencyList = agencyRepository.findAll();
        assertThat(agencyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAgencies() throws Exception {
        // Initialize the database
        agencyRepository.saveAndFlush(agency);

        // Get all the agencyList
        restAgencyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(agency.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].contact").value(hasItem(DEFAULT_CONTACT)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)));
    }

    @Test
    @Transactional
    void getAgency() throws Exception {
        // Initialize the database
        agencyRepository.saveAndFlush(agency);

        // Get the agency
        restAgencyMockMvc
            .perform(get(ENTITY_API_URL_ID, agency.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(agency.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.contact").value(DEFAULT_CONTACT))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL));
    }

    @Test
    @Transactional
    void getNonExistingAgency() throws Exception {
        // Get the agency
        restAgencyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAgency() throws Exception {
        // Initialize the database
        agencyRepository.saveAndFlush(agency);

        int databaseSizeBeforeUpdate = agencyRepository.findAll().size();

        // Update the agency
        Agency updatedAgency = agencyRepository.findById(agency.getId()).get();
        // Disconnect from session so that the updates on updatedAgency are not directly saved in db
        em.detach(updatedAgency);
        updatedAgency.name(UPDATED_NAME).address(UPDATED_ADDRESS).contact(UPDATED_CONTACT).email(UPDATED_EMAIL);

        restAgencyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAgency.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAgency))
            )
            .andExpect(status().isOk());

        // Validate the Agency in the database
        List<Agency> agencyList = agencyRepository.findAll();
        assertThat(agencyList).hasSize(databaseSizeBeforeUpdate);
        Agency testAgency = agencyList.get(agencyList.size() - 1);
        assertThat(testAgency.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAgency.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testAgency.getContact()).isEqualTo(UPDATED_CONTACT);
        assertThat(testAgency.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void putNonExistingAgency() throws Exception {
        int databaseSizeBeforeUpdate = agencyRepository.findAll().size();
        agency.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAgencyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, agency.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(agency))
            )
            .andExpect(status().isBadRequest());

        // Validate the Agency in the database
        List<Agency> agencyList = agencyRepository.findAll();
        assertThat(agencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAgency() throws Exception {
        int databaseSizeBeforeUpdate = agencyRepository.findAll().size();
        agency.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgencyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(agency))
            )
            .andExpect(status().isBadRequest());

        // Validate the Agency in the database
        List<Agency> agencyList = agencyRepository.findAll();
        assertThat(agencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAgency() throws Exception {
        int databaseSizeBeforeUpdate = agencyRepository.findAll().size();
        agency.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgencyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(agency)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Agency in the database
        List<Agency> agencyList = agencyRepository.findAll();
        assertThat(agencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAgencyWithPatch() throws Exception {
        // Initialize the database
        agencyRepository.saveAndFlush(agency);

        int databaseSizeBeforeUpdate = agencyRepository.findAll().size();

        // Update the agency using partial update
        Agency partialUpdatedAgency = new Agency();
        partialUpdatedAgency.setId(agency.getId());

        partialUpdatedAgency.name(UPDATED_NAME).address(UPDATED_ADDRESS);

        restAgencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAgency.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAgency))
            )
            .andExpect(status().isOk());

        // Validate the Agency in the database
        List<Agency> agencyList = agencyRepository.findAll();
        assertThat(agencyList).hasSize(databaseSizeBeforeUpdate);
        Agency testAgency = agencyList.get(agencyList.size() - 1);
        assertThat(testAgency.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAgency.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testAgency.getContact()).isEqualTo(DEFAULT_CONTACT);
        assertThat(testAgency.getEmail()).isEqualTo(DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void fullUpdateAgencyWithPatch() throws Exception {
        // Initialize the database
        agencyRepository.saveAndFlush(agency);

        int databaseSizeBeforeUpdate = agencyRepository.findAll().size();

        // Update the agency using partial update
        Agency partialUpdatedAgency = new Agency();
        partialUpdatedAgency.setId(agency.getId());

        partialUpdatedAgency.name(UPDATED_NAME).address(UPDATED_ADDRESS).contact(UPDATED_CONTACT).email(UPDATED_EMAIL);

        restAgencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAgency.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAgency))
            )
            .andExpect(status().isOk());

        // Validate the Agency in the database
        List<Agency> agencyList = agencyRepository.findAll();
        assertThat(agencyList).hasSize(databaseSizeBeforeUpdate);
        Agency testAgency = agencyList.get(agencyList.size() - 1);
        assertThat(testAgency.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAgency.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testAgency.getContact()).isEqualTo(UPDATED_CONTACT);
        assertThat(testAgency.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void patchNonExistingAgency() throws Exception {
        int databaseSizeBeforeUpdate = agencyRepository.findAll().size();
        agency.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAgencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, agency.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(agency))
            )
            .andExpect(status().isBadRequest());

        // Validate the Agency in the database
        List<Agency> agencyList = agencyRepository.findAll();
        assertThat(agencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAgency() throws Exception {
        int databaseSizeBeforeUpdate = agencyRepository.findAll().size();
        agency.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(agency))
            )
            .andExpect(status().isBadRequest());

        // Validate the Agency in the database
        List<Agency> agencyList = agencyRepository.findAll();
        assertThat(agencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAgency() throws Exception {
        int databaseSizeBeforeUpdate = agencyRepository.findAll().size();
        agency.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgencyMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(agency)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Agency in the database
        List<Agency> agencyList = agencyRepository.findAll();
        assertThat(agencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAgency() throws Exception {
        // Initialize the database
        agencyRepository.saveAndFlush(agency);

        int databaseSizeBeforeDelete = agencyRepository.findAll().size();

        // Delete the agency
        restAgencyMockMvc
            .perform(delete(ENTITY_API_URL_ID, agency.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Agency> agencyList = agencyRepository.findAll();
        assertThat(agencyList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
