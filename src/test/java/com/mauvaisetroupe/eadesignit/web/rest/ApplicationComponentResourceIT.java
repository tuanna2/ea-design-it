package com.mauvaisetroupe.eadesignit.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mauvaisetroupe.eadesignit.IntegrationTest;
import com.mauvaisetroupe.eadesignit.domain.Application;
import com.mauvaisetroupe.eadesignit.domain.ApplicationComponent;
import com.mauvaisetroupe.eadesignit.domain.enumeration.ApplicationType;
import com.mauvaisetroupe.eadesignit.domain.enumeration.SoftwareType;
import com.mauvaisetroupe.eadesignit.repository.ApplicationComponentRepository;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link ApplicationComponentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ApplicationComponentResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_TECHNOLOGY = "AAAAAAAAAA";
    private static final String UPDATED_TECHNOLOGY = "BBBBBBBBBB";

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final String DEFAULT_DOCUMENTATION_URL = "AAAAAAAAAA";
    private static final String UPDATED_DOCUMENTATION_URL = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final ApplicationType DEFAULT_APPLICATION_TYPE = ApplicationType.SOFTWARE;
    private static final ApplicationType UPDATED_APPLICATION_TYPE = ApplicationType.MIDDLEWARE;

    private static final SoftwareType DEFAULT_SOFTWARE_TYPE = SoftwareType.ONPREMISE_COTS;
    private static final SoftwareType UPDATED_SOFTWARE_TYPE = SoftwareType.ONPREMISE_CUSTOM;

    private static final String ENTITY_API_URL = "/api/application-components";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ApplicationComponentRepository applicationComponentRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restApplicationComponentMockMvc;

    private ApplicationComponent applicationComponent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ApplicationComponent createEntity(EntityManager em) {
        ApplicationComponent applicationComponent = new ApplicationComponent()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .technology(DEFAULT_TECHNOLOGY)
            .comment(DEFAULT_COMMENT)
            .documentationURL(DEFAULT_DOCUMENTATION_URL)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .applicationType(DEFAULT_APPLICATION_TYPE)
            .softwareType(DEFAULT_SOFTWARE_TYPE);
        // Add required entity
        Application application;
        if (TestUtil.findAll(em, Application.class).isEmpty()) {
            application = ApplicationResourceIT.createEntity(em);
            em.persist(application);
            em.flush();
        } else {
            application = TestUtil.findAll(em, Application.class).get(0);
        }
        applicationComponent.setApplication(application);
        return applicationComponent;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ApplicationComponent createUpdatedEntity(EntityManager em) {
        ApplicationComponent applicationComponent = new ApplicationComponent()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .technology(UPDATED_TECHNOLOGY)
            .comment(UPDATED_COMMENT)
            .documentationURL(UPDATED_DOCUMENTATION_URL)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .applicationType(UPDATED_APPLICATION_TYPE)
            .softwareType(UPDATED_SOFTWARE_TYPE);
        // Add required entity
        Application application;
        if (TestUtil.findAll(em, Application.class).isEmpty()) {
            application = ApplicationResourceIT.createUpdatedEntity(em);
            em.persist(application);
            em.flush();
        } else {
            application = TestUtil.findAll(em, Application.class).get(0);
        }
        applicationComponent.setApplication(application);
        return applicationComponent;
    }

    @BeforeEach
    public void initTest() {
        applicationComponent = createEntity(em);
    }

    @Test
    @Transactional
    void createApplicationComponent() throws Exception {
        int databaseSizeBeforeCreate = applicationComponentRepository.findAll().size();
        // Create the ApplicationComponent
        restApplicationComponentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(applicationComponent))
            )
            .andExpect(status().isCreated());

        // Validate the ApplicationComponent in the database
        List<ApplicationComponent> applicationComponentList = applicationComponentRepository.findAll();
        assertThat(applicationComponentList).hasSize(databaseSizeBeforeCreate + 1);
        ApplicationComponent testApplicationComponent = applicationComponentList.get(applicationComponentList.size() - 1);
        assertThat(testApplicationComponent.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testApplicationComponent.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testApplicationComponent.getTechnology()).isEqualTo(DEFAULT_TECHNOLOGY);
        assertThat(testApplicationComponent.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testApplicationComponent.getDocumentationURL()).isEqualTo(DEFAULT_DOCUMENTATION_URL);
        assertThat(testApplicationComponent.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testApplicationComponent.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testApplicationComponent.getApplicationType()).isEqualTo(DEFAULT_APPLICATION_TYPE);
        assertThat(testApplicationComponent.getSoftwareType()).isEqualTo(DEFAULT_SOFTWARE_TYPE);
    }

    @Test
    @Transactional
    void createApplicationComponentWithExistingId() throws Exception {
        // Create the ApplicationComponent with an existing ID
        applicationComponent.setId(1L);

        int databaseSizeBeforeCreate = applicationComponentRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restApplicationComponentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(applicationComponent))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationComponent in the database
        List<ApplicationComponent> applicationComponentList = applicationComponentRepository.findAll();
        assertThat(applicationComponentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllApplicationComponents() throws Exception {
        // Initialize the database
        applicationComponentRepository.saveAndFlush(applicationComponent);

        // Get all the applicationComponentList
        restApplicationComponentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(applicationComponent.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].technology").value(hasItem(DEFAULT_TECHNOLOGY)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)))
            .andExpect(jsonPath("$.[*].documentationURL").value(hasItem(DEFAULT_DOCUMENTATION_URL)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].applicationType").value(hasItem(DEFAULT_APPLICATION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].softwareType").value(hasItem(DEFAULT_SOFTWARE_TYPE.toString())));
    }

    @Test
    @Transactional
    void getApplicationComponent() throws Exception {
        // Initialize the database
        applicationComponentRepository.saveAndFlush(applicationComponent);

        // Get the applicationComponent
        restApplicationComponentMockMvc
            .perform(get(ENTITY_API_URL_ID, applicationComponent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(applicationComponent.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.technology").value(DEFAULT_TECHNOLOGY))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT))
            .andExpect(jsonPath("$.documentationURL").value(DEFAULT_DOCUMENTATION_URL))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.applicationType").value(DEFAULT_APPLICATION_TYPE.toString()))
            .andExpect(jsonPath("$.softwareType").value(DEFAULT_SOFTWARE_TYPE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingApplicationComponent() throws Exception {
        // Get the applicationComponent
        restApplicationComponentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewApplicationComponent() throws Exception {
        // Initialize the database
        applicationComponentRepository.saveAndFlush(applicationComponent);

        int databaseSizeBeforeUpdate = applicationComponentRepository.findAll().size();

        // Update the applicationComponent
        ApplicationComponent updatedApplicationComponent = applicationComponentRepository.findById(applicationComponent.getId()).get();
        // Disconnect from session so that the updates on updatedApplicationComponent are not directly saved in db
        em.detach(updatedApplicationComponent);
        updatedApplicationComponent
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .technology(UPDATED_TECHNOLOGY)
            .comment(UPDATED_COMMENT)
            .documentationURL(UPDATED_DOCUMENTATION_URL)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .applicationType(UPDATED_APPLICATION_TYPE)
            .softwareType(UPDATED_SOFTWARE_TYPE);

        restApplicationComponentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedApplicationComponent.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedApplicationComponent))
            )
            .andExpect(status().isOk());

        // Validate the ApplicationComponent in the database
        List<ApplicationComponent> applicationComponentList = applicationComponentRepository.findAll();
        assertThat(applicationComponentList).hasSize(databaseSizeBeforeUpdate);
        ApplicationComponent testApplicationComponent = applicationComponentList.get(applicationComponentList.size() - 1);
        assertThat(testApplicationComponent.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testApplicationComponent.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testApplicationComponent.getTechnology()).isEqualTo(UPDATED_TECHNOLOGY);
        assertThat(testApplicationComponent.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testApplicationComponent.getDocumentationURL()).isEqualTo(UPDATED_DOCUMENTATION_URL);
        assertThat(testApplicationComponent.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testApplicationComponent.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testApplicationComponent.getApplicationType()).isEqualTo(UPDATED_APPLICATION_TYPE);
        assertThat(testApplicationComponent.getSoftwareType()).isEqualTo(UPDATED_SOFTWARE_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingApplicationComponent() throws Exception {
        int databaseSizeBeforeUpdate = applicationComponentRepository.findAll().size();
        applicationComponent.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApplicationComponentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, applicationComponent.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(applicationComponent))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationComponent in the database
        List<ApplicationComponent> applicationComponentList = applicationComponentRepository.findAll();
        assertThat(applicationComponentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchApplicationComponent() throws Exception {
        int databaseSizeBeforeUpdate = applicationComponentRepository.findAll().size();
        applicationComponent.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationComponentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(applicationComponent))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationComponent in the database
        List<ApplicationComponent> applicationComponentList = applicationComponentRepository.findAll();
        assertThat(applicationComponentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamApplicationComponent() throws Exception {
        int databaseSizeBeforeUpdate = applicationComponentRepository.findAll().size();
        applicationComponent.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationComponentMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(applicationComponent))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ApplicationComponent in the database
        List<ApplicationComponent> applicationComponentList = applicationComponentRepository.findAll();
        assertThat(applicationComponentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateApplicationComponentWithPatch() throws Exception {
        // Initialize the database
        applicationComponentRepository.saveAndFlush(applicationComponent);

        int databaseSizeBeforeUpdate = applicationComponentRepository.findAll().size();

        // Update the applicationComponent using partial update
        ApplicationComponent partialUpdatedApplicationComponent = new ApplicationComponent();
        partialUpdatedApplicationComponent.setId(applicationComponent.getId());

        partialUpdatedApplicationComponent
            .technology(UPDATED_TECHNOLOGY)
            .comment(UPDATED_COMMENT)
            .documentationURL(UPDATED_DOCUMENTATION_URL)
            .softwareType(UPDATED_SOFTWARE_TYPE);

        restApplicationComponentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApplicationComponent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedApplicationComponent))
            )
            .andExpect(status().isOk());

        // Validate the ApplicationComponent in the database
        List<ApplicationComponent> applicationComponentList = applicationComponentRepository.findAll();
        assertThat(applicationComponentList).hasSize(databaseSizeBeforeUpdate);
        ApplicationComponent testApplicationComponent = applicationComponentList.get(applicationComponentList.size() - 1);
        assertThat(testApplicationComponent.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testApplicationComponent.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testApplicationComponent.getTechnology()).isEqualTo(UPDATED_TECHNOLOGY);
        assertThat(testApplicationComponent.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testApplicationComponent.getDocumentationURL()).isEqualTo(UPDATED_DOCUMENTATION_URL);
        assertThat(testApplicationComponent.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testApplicationComponent.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testApplicationComponent.getApplicationType()).isEqualTo(DEFAULT_APPLICATION_TYPE);
        assertThat(testApplicationComponent.getSoftwareType()).isEqualTo(UPDATED_SOFTWARE_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateApplicationComponentWithPatch() throws Exception {
        // Initialize the database
        applicationComponentRepository.saveAndFlush(applicationComponent);

        int databaseSizeBeforeUpdate = applicationComponentRepository.findAll().size();

        // Update the applicationComponent using partial update
        ApplicationComponent partialUpdatedApplicationComponent = new ApplicationComponent();
        partialUpdatedApplicationComponent.setId(applicationComponent.getId());

        partialUpdatedApplicationComponent
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .technology(UPDATED_TECHNOLOGY)
            .comment(UPDATED_COMMENT)
            .documentationURL(UPDATED_DOCUMENTATION_URL)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .applicationType(UPDATED_APPLICATION_TYPE)
            .softwareType(UPDATED_SOFTWARE_TYPE);

        restApplicationComponentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApplicationComponent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedApplicationComponent))
            )
            .andExpect(status().isOk());

        // Validate the ApplicationComponent in the database
        List<ApplicationComponent> applicationComponentList = applicationComponentRepository.findAll();
        assertThat(applicationComponentList).hasSize(databaseSizeBeforeUpdate);
        ApplicationComponent testApplicationComponent = applicationComponentList.get(applicationComponentList.size() - 1);
        assertThat(testApplicationComponent.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testApplicationComponent.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testApplicationComponent.getTechnology()).isEqualTo(UPDATED_TECHNOLOGY);
        assertThat(testApplicationComponent.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testApplicationComponent.getDocumentationURL()).isEqualTo(UPDATED_DOCUMENTATION_URL);
        assertThat(testApplicationComponent.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testApplicationComponent.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testApplicationComponent.getApplicationType()).isEqualTo(UPDATED_APPLICATION_TYPE);
        assertThat(testApplicationComponent.getSoftwareType()).isEqualTo(UPDATED_SOFTWARE_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingApplicationComponent() throws Exception {
        int databaseSizeBeforeUpdate = applicationComponentRepository.findAll().size();
        applicationComponent.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApplicationComponentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, applicationComponent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(applicationComponent))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationComponent in the database
        List<ApplicationComponent> applicationComponentList = applicationComponentRepository.findAll();
        assertThat(applicationComponentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchApplicationComponent() throws Exception {
        int databaseSizeBeforeUpdate = applicationComponentRepository.findAll().size();
        applicationComponent.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationComponentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(applicationComponent))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationComponent in the database
        List<ApplicationComponent> applicationComponentList = applicationComponentRepository.findAll();
        assertThat(applicationComponentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamApplicationComponent() throws Exception {
        int databaseSizeBeforeUpdate = applicationComponentRepository.findAll().size();
        applicationComponent.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationComponentMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(applicationComponent))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ApplicationComponent in the database
        List<ApplicationComponent> applicationComponentList = applicationComponentRepository.findAll();
        assertThat(applicationComponentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteApplicationComponent() throws Exception {
        // Initialize the database
        applicationComponentRepository.saveAndFlush(applicationComponent);

        int databaseSizeBeforeDelete = applicationComponentRepository.findAll().size();

        // Delete the applicationComponent
        restApplicationComponentMockMvc
            .perform(delete(ENTITY_API_URL_ID, applicationComponent.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ApplicationComponent> applicationComponentList = applicationComponentRepository.findAll();
        assertThat(applicationComponentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
