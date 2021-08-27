package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Manager;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.domain.enumeration.Gender;
import com.mycompany.myapp.repository.ManagerRepository;
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
 * Integration tests for the {@link ManagerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ManagerResourceIT {

    private static final Gender DEFAULT_GENDER = Gender.MALE;
    private static final Gender UPDATED_GENDER = Gender.FEMALE;

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/managers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restManagerMockMvc;

    private Manager manager;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Manager createEntity(EntityManager em) {
        Manager manager = new Manager().gender(DEFAULT_GENDER).telephone(DEFAULT_TELEPHONE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        manager.setUser(user);
        return manager;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Manager createUpdatedEntity(EntityManager em) {
        Manager manager = new Manager().gender(UPDATED_GENDER).telephone(UPDATED_TELEPHONE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        manager.setUser(user);
        return manager;
    }

    @BeforeEach
    public void initTest() {
        manager = createEntity(em);
    }

    @Test
    @Transactional
    void createManager() throws Exception {
        int databaseSizeBeforeCreate = managerRepository.findAll().size();
        // Create the Manager
        restManagerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(manager)))
            .andExpect(status().isCreated());

        // Validate the Manager in the database
        List<Manager> managerList = managerRepository.findAll();
        assertThat(managerList).hasSize(databaseSizeBeforeCreate + 1);
        Manager testManager = managerList.get(managerList.size() - 1);
        assertThat(testManager.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testManager.getTelephone()).isEqualTo(DEFAULT_TELEPHONE);
    }

    @Test
    @Transactional
    void createManagerWithExistingId() throws Exception {
        // Create the Manager with an existing ID
        manager.setId(1L);

        int databaseSizeBeforeCreate = managerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restManagerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(manager)))
            .andExpect(status().isBadRequest());

        // Validate the Manager in the database
        List<Manager> managerList = managerRepository.findAll();
        assertThat(managerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTelephoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = managerRepository.findAll().size();
        // set the field null
        manager.setTelephone(null);

        // Create the Manager, which fails.

        restManagerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(manager)))
            .andExpect(status().isBadRequest());

        List<Manager> managerList = managerRepository.findAll();
        assertThat(managerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllManagers() throws Exception {
        // Initialize the database
        managerRepository.saveAndFlush(manager);

        // Get all the managerList
        restManagerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(manager.getId().intValue())))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)));
    }

    @Test
    @Transactional
    void getManager() throws Exception {
        // Initialize the database
        managerRepository.saveAndFlush(manager);

        // Get the manager
        restManagerMockMvc
            .perform(get(ENTITY_API_URL_ID, manager.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(manager.getId().intValue()))
            .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER.toString()))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE));
    }

    @Test
    @Transactional
    void getNonExistingManager() throws Exception {
        // Get the manager
        restManagerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewManager() throws Exception {
        // Initialize the database
        managerRepository.saveAndFlush(manager);

        int databaseSizeBeforeUpdate = managerRepository.findAll().size();

        // Update the manager
        Manager updatedManager = managerRepository.findById(manager.getId()).get();
        // Disconnect from session so that the updates on updatedManager are not directly saved in db
        em.detach(updatedManager);
        updatedManager.gender(UPDATED_GENDER).telephone(UPDATED_TELEPHONE);

        restManagerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedManager.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedManager))
            )
            .andExpect(status().isOk());

        // Validate the Manager in the database
        List<Manager> managerList = managerRepository.findAll();
        assertThat(managerList).hasSize(databaseSizeBeforeUpdate);
        Manager testManager = managerList.get(managerList.size() - 1);
        assertThat(testManager.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testManager.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    void putNonExistingManager() throws Exception {
        int databaseSizeBeforeUpdate = managerRepository.findAll().size();
        manager.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restManagerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, manager.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(manager))
            )
            .andExpect(status().isBadRequest());

        // Validate the Manager in the database
        List<Manager> managerList = managerRepository.findAll();
        assertThat(managerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchManager() throws Exception {
        int databaseSizeBeforeUpdate = managerRepository.findAll().size();
        manager.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restManagerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(manager))
            )
            .andExpect(status().isBadRequest());

        // Validate the Manager in the database
        List<Manager> managerList = managerRepository.findAll();
        assertThat(managerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamManager() throws Exception {
        int databaseSizeBeforeUpdate = managerRepository.findAll().size();
        manager.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restManagerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(manager)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Manager in the database
        List<Manager> managerList = managerRepository.findAll();
        assertThat(managerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateManagerWithPatch() throws Exception {
        // Initialize the database
        managerRepository.saveAndFlush(manager);

        int databaseSizeBeforeUpdate = managerRepository.findAll().size();

        // Update the manager using partial update
        Manager partialUpdatedManager = new Manager();
        partialUpdatedManager.setId(manager.getId());

        restManagerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedManager.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedManager))
            )
            .andExpect(status().isOk());

        // Validate the Manager in the database
        List<Manager> managerList = managerRepository.findAll();
        assertThat(managerList).hasSize(databaseSizeBeforeUpdate);
        Manager testManager = managerList.get(managerList.size() - 1);
        assertThat(testManager.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testManager.getTelephone()).isEqualTo(DEFAULT_TELEPHONE);
    }

    @Test
    @Transactional
    void fullUpdateManagerWithPatch() throws Exception {
        // Initialize the database
        managerRepository.saveAndFlush(manager);

        int databaseSizeBeforeUpdate = managerRepository.findAll().size();

        // Update the manager using partial update
        Manager partialUpdatedManager = new Manager();
        partialUpdatedManager.setId(manager.getId());

        partialUpdatedManager.gender(UPDATED_GENDER).telephone(UPDATED_TELEPHONE);

        restManagerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedManager.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedManager))
            )
            .andExpect(status().isOk());

        // Validate the Manager in the database
        List<Manager> managerList = managerRepository.findAll();
        assertThat(managerList).hasSize(databaseSizeBeforeUpdate);
        Manager testManager = managerList.get(managerList.size() - 1);
        assertThat(testManager.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testManager.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    void patchNonExistingManager() throws Exception {
        int databaseSizeBeforeUpdate = managerRepository.findAll().size();
        manager.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restManagerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, manager.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(manager))
            )
            .andExpect(status().isBadRequest());

        // Validate the Manager in the database
        List<Manager> managerList = managerRepository.findAll();
        assertThat(managerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchManager() throws Exception {
        int databaseSizeBeforeUpdate = managerRepository.findAll().size();
        manager.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restManagerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(manager))
            )
            .andExpect(status().isBadRequest());

        // Validate the Manager in the database
        List<Manager> managerList = managerRepository.findAll();
        assertThat(managerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamManager() throws Exception {
        int databaseSizeBeforeUpdate = managerRepository.findAll().size();
        manager.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restManagerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(manager)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Manager in the database
        List<Manager> managerList = managerRepository.findAll();
        assertThat(managerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteManager() throws Exception {
        // Initialize the database
        managerRepository.saveAndFlush(manager);

        int databaseSizeBeforeDelete = managerRepository.findAll().size();

        // Delete the manager
        restManagerMockMvc
            .perform(delete(ENTITY_API_URL_ID, manager.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Manager> managerList = managerRepository.findAll();
        assertThat(managerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
