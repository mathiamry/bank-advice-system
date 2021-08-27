package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Advisor;
import com.mycompany.myapp.domain.Appointment;
import com.mycompany.myapp.domain.Manager;
import com.mycompany.myapp.domain.enumeration.Status;
import com.mycompany.myapp.repository.AppointmentRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link AppointmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AppointmentResourceIT {

    private static final Instant DEFAULT_CREATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final LocalDate DEFAULT_APPOINTEMENT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_APPOINTEMENT_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.APPROVED;
    private static final Status UPDATED_STATUS = Status.REJECTED;

    private static final Instant DEFAULT_STATUS_CHANGE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_STATUS_CHANGE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_COMMENTARY = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTARY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/appointments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAppointmentMockMvc;

    private Appointment appointment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Appointment createEntity(EntityManager em) {
        Appointment appointment = new Appointment()
            .created(DEFAULT_CREATED)
            .appointementDate(DEFAULT_APPOINTEMENT_DATE)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .status(DEFAULT_STATUS)
            .statusChangeDate(DEFAULT_STATUS_CHANGE_DATE)
            .commentary(DEFAULT_COMMENTARY);
        // Add required entity
        Manager manager;
        if (TestUtil.findAll(em, Manager.class).isEmpty()) {
            manager = ManagerResourceIT.createEntity(em);
            em.persist(manager);
            em.flush();
        } else {
            manager = TestUtil.findAll(em, Manager.class).get(0);
        }
        appointment.setManager(manager);
        // Add required entity
        Advisor advisor;
        if (TestUtil.findAll(em, Advisor.class).isEmpty()) {
            advisor = AdvisorResourceIT.createEntity(em);
            em.persist(advisor);
            em.flush();
        } else {
            advisor = TestUtil.findAll(em, Advisor.class).get(0);
        }
        appointment.setAdvisor(advisor);
        return appointment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Appointment createUpdatedEntity(EntityManager em) {
        Appointment appointment = new Appointment()
            .created(UPDATED_CREATED)
            .appointementDate(UPDATED_APPOINTEMENT_DATE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .statusChangeDate(UPDATED_STATUS_CHANGE_DATE)
            .commentary(UPDATED_COMMENTARY);
        // Add required entity
        Manager manager;
        if (TestUtil.findAll(em, Manager.class).isEmpty()) {
            manager = ManagerResourceIT.createUpdatedEntity(em);
            em.persist(manager);
            em.flush();
        } else {
            manager = TestUtil.findAll(em, Manager.class).get(0);
        }
        appointment.setManager(manager);
        // Add required entity
        Advisor advisor;
        if (TestUtil.findAll(em, Advisor.class).isEmpty()) {
            advisor = AdvisorResourceIT.createUpdatedEntity(em);
            em.persist(advisor);
            em.flush();
        } else {
            advisor = TestUtil.findAll(em, Advisor.class).get(0);
        }
        appointment.setAdvisor(advisor);
        return appointment;
    }

    @BeforeEach
    public void initTest() {
        appointment = createEntity(em);
    }

    @Test
    @Transactional
    void createAppointment() throws Exception {
        int databaseSizeBeforeCreate = appointmentRepository.findAll().size();
        // Create the Appointment
        restAppointmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appointment)))
            .andExpect(status().isCreated());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeCreate + 1);
        Appointment testAppointment = appointmentList.get(appointmentList.size() - 1);
        assertThat(testAppointment.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testAppointment.getAppointementDate()).isEqualTo(DEFAULT_APPOINTEMENT_DATE);
        assertThat(testAppointment.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testAppointment.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testAppointment.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testAppointment.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testAppointment.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testAppointment.getStatusChangeDate()).isEqualTo(DEFAULT_STATUS_CHANGE_DATE);
        assertThat(testAppointment.getCommentary()).isEqualTo(DEFAULT_COMMENTARY);
    }

    @Test
    @Transactional
    void createAppointmentWithExistingId() throws Exception {
        // Create the Appointment with an existing ID
        appointment.setId(1L);

        int databaseSizeBeforeCreate = appointmentRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAppointmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appointment)))
            .andExpect(status().isBadRequest());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCreatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = appointmentRepository.findAll().size();
        // set the field null
        appointment.setCreated(null);

        // Create the Appointment, which fails.

        restAppointmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appointment)))
            .andExpect(status().isBadRequest());

        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAppointementDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = appointmentRepository.findAll().size();
        // set the field null
        appointment.setAppointementDate(null);

        // Create the Appointment, which fails.

        restAppointmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appointment)))
            .andExpect(status().isBadRequest());

        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = appointmentRepository.findAll().size();
        // set the field null
        appointment.setStartDate(null);

        // Create the Appointment, which fails.

        restAppointmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appointment)))
            .andExpect(status().isBadRequest());

        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = appointmentRepository.findAll().size();
        // set the field null
        appointment.setEndDate(null);

        // Create the Appointment, which fails.

        restAppointmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appointment)))
            .andExpect(status().isBadRequest());

        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusChangeDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = appointmentRepository.findAll().size();
        // set the field null
        appointment.setStatusChangeDate(null);

        // Create the Appointment, which fails.

        restAppointmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appointment)))
            .andExpect(status().isBadRequest());

        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAppointments() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList
        restAppointmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appointment.getId().intValue())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].appointementDate").value(hasItem(DEFAULT_APPOINTEMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].statusChangeDate").value(hasItem(DEFAULT_STATUS_CHANGE_DATE.toString())))
            .andExpect(jsonPath("$.[*].commentary").value(hasItem(DEFAULT_COMMENTARY)));
    }

    @Test
    @Transactional
    void getAppointment() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);

        // Get the appointment
        restAppointmentMockMvc
            .perform(get(ENTITY_API_URL_ID, appointment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(appointment.getId().intValue()))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED.toString()))
            .andExpect(jsonPath("$.appointementDate").value(DEFAULT_APPOINTEMENT_DATE.toString()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.statusChangeDate").value(DEFAULT_STATUS_CHANGE_DATE.toString()))
            .andExpect(jsonPath("$.commentary").value(DEFAULT_COMMENTARY));
    }

    @Test
    @Transactional
    void getNonExistingAppointment() throws Exception {
        // Get the appointment
        restAppointmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAppointment() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);

        int databaseSizeBeforeUpdate = appointmentRepository.findAll().size();

        // Update the appointment
        Appointment updatedAppointment = appointmentRepository.findById(appointment.getId()).get();
        // Disconnect from session so that the updates on updatedAppointment are not directly saved in db
        em.detach(updatedAppointment);
        updatedAppointment
            .created(UPDATED_CREATED)
            .appointementDate(UPDATED_APPOINTEMENT_DATE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .statusChangeDate(UPDATED_STATUS_CHANGE_DATE)
            .commentary(UPDATED_COMMENTARY);

        restAppointmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAppointment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAppointment))
            )
            .andExpect(status().isOk());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
        Appointment testAppointment = appointmentList.get(appointmentList.size() - 1);
        assertThat(testAppointment.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testAppointment.getAppointementDate()).isEqualTo(UPDATED_APPOINTEMENT_DATE);
        assertThat(testAppointment.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testAppointment.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testAppointment.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testAppointment.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAppointment.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testAppointment.getStatusChangeDate()).isEqualTo(UPDATED_STATUS_CHANGE_DATE);
        assertThat(testAppointment.getCommentary()).isEqualTo(UPDATED_COMMENTARY);
    }

    @Test
    @Transactional
    void putNonExistingAppointment() throws Exception {
        int databaseSizeBeforeUpdate = appointmentRepository.findAll().size();
        appointment.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppointmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appointment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(appointment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAppointment() throws Exception {
        int databaseSizeBeforeUpdate = appointmentRepository.findAll().size();
        appointment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppointmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(appointment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAppointment() throws Exception {
        int databaseSizeBeforeUpdate = appointmentRepository.findAll().size();
        appointment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppointmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appointment)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAppointmentWithPatch() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);

        int databaseSizeBeforeUpdate = appointmentRepository.findAll().size();

        // Update the appointment using partial update
        Appointment partialUpdatedAppointment = new Appointment();
        partialUpdatedAppointment.setId(appointment.getId());

        partialUpdatedAppointment
            .created(UPDATED_CREATED)
            .appointementDate(UPDATED_APPOINTEMENT_DATE)
            .startDate(UPDATED_START_DATE)
            .title(UPDATED_TITLE)
            .statusChangeDate(UPDATED_STATUS_CHANGE_DATE);

        restAppointmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppointment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAppointment))
            )
            .andExpect(status().isOk());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
        Appointment testAppointment = appointmentList.get(appointmentList.size() - 1);
        assertThat(testAppointment.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testAppointment.getAppointementDate()).isEqualTo(UPDATED_APPOINTEMENT_DATE);
        assertThat(testAppointment.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testAppointment.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testAppointment.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testAppointment.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testAppointment.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testAppointment.getStatusChangeDate()).isEqualTo(UPDATED_STATUS_CHANGE_DATE);
        assertThat(testAppointment.getCommentary()).isEqualTo(DEFAULT_COMMENTARY);
    }

    @Test
    @Transactional
    void fullUpdateAppointmentWithPatch() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);

        int databaseSizeBeforeUpdate = appointmentRepository.findAll().size();

        // Update the appointment using partial update
        Appointment partialUpdatedAppointment = new Appointment();
        partialUpdatedAppointment.setId(appointment.getId());

        partialUpdatedAppointment
            .created(UPDATED_CREATED)
            .appointementDate(UPDATED_APPOINTEMENT_DATE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .statusChangeDate(UPDATED_STATUS_CHANGE_DATE)
            .commentary(UPDATED_COMMENTARY);

        restAppointmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppointment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAppointment))
            )
            .andExpect(status().isOk());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
        Appointment testAppointment = appointmentList.get(appointmentList.size() - 1);
        assertThat(testAppointment.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testAppointment.getAppointementDate()).isEqualTo(UPDATED_APPOINTEMENT_DATE);
        assertThat(testAppointment.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testAppointment.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testAppointment.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testAppointment.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAppointment.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testAppointment.getStatusChangeDate()).isEqualTo(UPDATED_STATUS_CHANGE_DATE);
        assertThat(testAppointment.getCommentary()).isEqualTo(UPDATED_COMMENTARY);
    }

    @Test
    @Transactional
    void patchNonExistingAppointment() throws Exception {
        int databaseSizeBeforeUpdate = appointmentRepository.findAll().size();
        appointment.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppointmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, appointment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(appointment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAppointment() throws Exception {
        int databaseSizeBeforeUpdate = appointmentRepository.findAll().size();
        appointment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppointmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(appointment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAppointment() throws Exception {
        int databaseSizeBeforeUpdate = appointmentRepository.findAll().size();
        appointment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppointmentMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(appointment))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Appointment in the database
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAppointment() throws Exception {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment);

        int databaseSizeBeforeDelete = appointmentRepository.findAll().size();

        // Delete the appointment
        restAppointmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, appointment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Appointment> appointmentList = appointmentRepository.findAll();
        assertThat(appointmentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
