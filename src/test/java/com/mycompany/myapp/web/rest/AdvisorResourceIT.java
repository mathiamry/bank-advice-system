package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Advisor;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.domain.enumeration.Gender;
import com.mycompany.myapp.repository.AdvisorRepository;
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
 * Integration tests for the {@link AdvisorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AdvisorResourceIT {

    private static final Gender DEFAULT_GENDER = Gender.MALE;
    private static final Gender UPDATED_GENDER = Gender.FEMALE;

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/advisors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AdvisorRepository advisorRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAdvisorMockMvc;

    private Advisor advisor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Advisor createEntity(EntityManager em) {
        Advisor advisor = new Advisor().gender(DEFAULT_GENDER).telephone(DEFAULT_TELEPHONE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        advisor.setUser(user);
        return advisor;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Advisor createUpdatedEntity(EntityManager em) {
        Advisor advisor = new Advisor().gender(UPDATED_GENDER).telephone(UPDATED_TELEPHONE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        advisor.setUser(user);
        return advisor;
    }

    @BeforeEach
    public void initTest() {
        advisor = createEntity(em);
    }

    @Test
    @Transactional
    void createAdvisor() throws Exception {
        int databaseSizeBeforeCreate = advisorRepository.findAll().size();
        // Create the Advisor
        restAdvisorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(advisor)))
            .andExpect(status().isCreated());

        // Validate the Advisor in the database
        List<Advisor> advisorList = advisorRepository.findAll();
        assertThat(advisorList).hasSize(databaseSizeBeforeCreate + 1);
        Advisor testAdvisor = advisorList.get(advisorList.size() - 1);
        assertThat(testAdvisor.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testAdvisor.getTelephone()).isEqualTo(DEFAULT_TELEPHONE);
    }

    @Test
    @Transactional
    void createAdvisorWithExistingId() throws Exception {
        // Create the Advisor with an existing ID
        advisor.setId(1L);

        int databaseSizeBeforeCreate = advisorRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAdvisorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(advisor)))
            .andExpect(status().isBadRequest());

        // Validate the Advisor in the database
        List<Advisor> advisorList = advisorRepository.findAll();
        assertThat(advisorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTelephoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = advisorRepository.findAll().size();
        // set the field null
        advisor.setTelephone(null);

        // Create the Advisor, which fails.

        restAdvisorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(advisor)))
            .andExpect(status().isBadRequest());

        List<Advisor> advisorList = advisorRepository.findAll();
        assertThat(advisorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAdvisors() throws Exception {
        // Initialize the database
        advisorRepository.saveAndFlush(advisor);

        // Get all the advisorList
        restAdvisorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(advisor.getId().intValue())))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)));
    }

    @Test
    @Transactional
    void getAdvisor() throws Exception {
        // Initialize the database
        advisorRepository.saveAndFlush(advisor);

        // Get the advisor
        restAdvisorMockMvc
            .perform(get(ENTITY_API_URL_ID, advisor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(advisor.getId().intValue()))
            .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER.toString()))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE));
    }

    @Test
    @Transactional
    void getNonExistingAdvisor() throws Exception {
        // Get the advisor
        restAdvisorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAdvisor() throws Exception {
        // Initialize the database
        advisorRepository.saveAndFlush(advisor);

        int databaseSizeBeforeUpdate = advisorRepository.findAll().size();

        // Update the advisor
        Advisor updatedAdvisor = advisorRepository.findById(advisor.getId()).get();
        // Disconnect from session so that the updates on updatedAdvisor are not directly saved in db
        em.detach(updatedAdvisor);
        updatedAdvisor.gender(UPDATED_GENDER).telephone(UPDATED_TELEPHONE);

        restAdvisorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAdvisor.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAdvisor))
            )
            .andExpect(status().isOk());

        // Validate the Advisor in the database
        List<Advisor> advisorList = advisorRepository.findAll();
        assertThat(advisorList).hasSize(databaseSizeBeforeUpdate);
        Advisor testAdvisor = advisorList.get(advisorList.size() - 1);
        assertThat(testAdvisor.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testAdvisor.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    void putNonExistingAdvisor() throws Exception {
        int databaseSizeBeforeUpdate = advisorRepository.findAll().size();
        advisor.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAdvisorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, advisor.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(advisor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Advisor in the database
        List<Advisor> advisorList = advisorRepository.findAll();
        assertThat(advisorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAdvisor() throws Exception {
        int databaseSizeBeforeUpdate = advisorRepository.findAll().size();
        advisor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdvisorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(advisor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Advisor in the database
        List<Advisor> advisorList = advisorRepository.findAll();
        assertThat(advisorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAdvisor() throws Exception {
        int databaseSizeBeforeUpdate = advisorRepository.findAll().size();
        advisor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdvisorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(advisor)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Advisor in the database
        List<Advisor> advisorList = advisorRepository.findAll();
        assertThat(advisorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAdvisorWithPatch() throws Exception {
        // Initialize the database
        advisorRepository.saveAndFlush(advisor);

        int databaseSizeBeforeUpdate = advisorRepository.findAll().size();

        // Update the advisor using partial update
        Advisor partialUpdatedAdvisor = new Advisor();
        partialUpdatedAdvisor.setId(advisor.getId());

        partialUpdatedAdvisor.gender(UPDATED_GENDER);

        restAdvisorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAdvisor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAdvisor))
            )
            .andExpect(status().isOk());

        // Validate the Advisor in the database
        List<Advisor> advisorList = advisorRepository.findAll();
        assertThat(advisorList).hasSize(databaseSizeBeforeUpdate);
        Advisor testAdvisor = advisorList.get(advisorList.size() - 1);
        assertThat(testAdvisor.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testAdvisor.getTelephone()).isEqualTo(DEFAULT_TELEPHONE);
    }

    @Test
    @Transactional
    void fullUpdateAdvisorWithPatch() throws Exception {
        // Initialize the database
        advisorRepository.saveAndFlush(advisor);

        int databaseSizeBeforeUpdate = advisorRepository.findAll().size();

        // Update the advisor using partial update
        Advisor partialUpdatedAdvisor = new Advisor();
        partialUpdatedAdvisor.setId(advisor.getId());

        partialUpdatedAdvisor.gender(UPDATED_GENDER).telephone(UPDATED_TELEPHONE);

        restAdvisorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAdvisor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAdvisor))
            )
            .andExpect(status().isOk());

        // Validate the Advisor in the database
        List<Advisor> advisorList = advisorRepository.findAll();
        assertThat(advisorList).hasSize(databaseSizeBeforeUpdate);
        Advisor testAdvisor = advisorList.get(advisorList.size() - 1);
        assertThat(testAdvisor.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testAdvisor.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    void patchNonExistingAdvisor() throws Exception {
        int databaseSizeBeforeUpdate = advisorRepository.findAll().size();
        advisor.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAdvisorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, advisor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(advisor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Advisor in the database
        List<Advisor> advisorList = advisorRepository.findAll();
        assertThat(advisorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAdvisor() throws Exception {
        int databaseSizeBeforeUpdate = advisorRepository.findAll().size();
        advisor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdvisorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(advisor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Advisor in the database
        List<Advisor> advisorList = advisorRepository.findAll();
        assertThat(advisorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAdvisor() throws Exception {
        int databaseSizeBeforeUpdate = advisorRepository.findAll().size();
        advisor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdvisorMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(advisor)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Advisor in the database
        List<Advisor> advisorList = advisorRepository.findAll();
        assertThat(advisorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAdvisor() throws Exception {
        // Initialize the database
        advisorRepository.saveAndFlush(advisor);

        int databaseSizeBeforeDelete = advisorRepository.findAll().size();

        // Delete the advisor
        restAdvisorMockMvc
            .perform(delete(ENTITY_API_URL_ID, advisor.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Advisor> advisorList = advisorRepository.findAll();
        assertThat(advisorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
