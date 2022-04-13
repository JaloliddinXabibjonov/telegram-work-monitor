package uz.devops.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.IntegrationTest;
import uz.devops.domain.Profession;
import uz.devops.domain.Task;
import uz.devops.repository.ProfessionRepository;
import uz.devops.service.criteria.ProfessionCriteria;
import uz.devops.service.dto.ProfessionDTO;
import uz.devops.service.mapper.ProfessionMapper;

/**
 * Integration tests for the {@link ProfessionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProfessionResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/professions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{name}";

    @Autowired
    private ProfessionRepository professionRepository;

    @Autowired
    private ProfessionMapper professionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProfessionMockMvc;

    private Profession profession;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Profession createEntity(EntityManager em) {
        Profession profession = new Profession().description(DEFAULT_DESCRIPTION);
        return profession;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Profession createUpdatedEntity(EntityManager em) {
        Profession profession = new Profession().description(UPDATED_DESCRIPTION);
        return profession;
    }

    @BeforeEach
    public void initTest() {
        profession = createEntity(em);
    }

    @Test
    @Transactional
    void createProfession() throws Exception {
        int databaseSizeBeforeCreate = professionRepository.findAll().size();
        // Create the Profession
        ProfessionDTO professionDTO = professionMapper.toDto(profession);
        restProfessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(professionDTO)))
            .andExpect(status().isCreated());

        // Validate the Profession in the database
        List<Profession> professionList = professionRepository.findAll();
        assertThat(professionList).hasSize(databaseSizeBeforeCreate + 1);
        Profession testProfession = professionList.get(professionList.size() - 1);
        assertThat(testProfession.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createProfessionWithExistingId() throws Exception {
        // Create the Profession with an existing ID
        profession.setName("existing_id");
        ProfessionDTO professionDTO = professionMapper.toDto(profession);

        int databaseSizeBeforeCreate = professionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProfessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(professionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Profession in the database
        List<Profession> professionList = professionRepository.findAll();
        assertThat(professionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllProfessions() throws Exception {
        // Initialize the database
        profession.setName(UUID.randomUUID().toString());
        professionRepository.saveAndFlush(profession);

        // Get all the professionList
        restProfessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=name,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].name").value(hasItem(profession.getName())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getProfession() throws Exception {
        // Initialize the database
        profession.setName(UUID.randomUUID().toString());
        professionRepository.saveAndFlush(profession);

        // Get the profession
        restProfessionMockMvc
            .perform(get(ENTITY_API_URL_ID, profession.getName()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.name").value(profession.getName()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getProfessionsByIdFiltering() throws Exception {
        // Initialize the database
        professionRepository.saveAndFlush(profession);

        String id = profession.getName();

        defaultProfessionShouldBeFound("name.equals=" + id);
        defaultProfessionShouldNotBeFound("name.notEquals=" + id);
    }

    @Test
    @Transactional
    void getAllProfessionsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        professionRepository.saveAndFlush(profession);

        // Get all the professionList where description equals to DEFAULT_DESCRIPTION
        defaultProfessionShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the professionList where description equals to UPDATED_DESCRIPTION
        defaultProfessionShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProfessionsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        professionRepository.saveAndFlush(profession);

        // Get all the professionList where description not equals to DEFAULT_DESCRIPTION
        defaultProfessionShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the professionList where description not equals to UPDATED_DESCRIPTION
        defaultProfessionShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProfessionsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        professionRepository.saveAndFlush(profession);

        // Get all the professionList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultProfessionShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the professionList where description equals to UPDATED_DESCRIPTION
        defaultProfessionShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProfessionsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        professionRepository.saveAndFlush(profession);

        // Get all the professionList where description is not null
        defaultProfessionShouldBeFound("description.specified=true");

        // Get all the professionList where description is null
        defaultProfessionShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllProfessionsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        professionRepository.saveAndFlush(profession);

        // Get all the professionList where description contains DEFAULT_DESCRIPTION
        defaultProfessionShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the professionList where description contains UPDATED_DESCRIPTION
        defaultProfessionShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProfessionsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        professionRepository.saveAndFlush(profession);

        // Get all the professionList where description does not contain DEFAULT_DESCRIPTION
        defaultProfessionShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the professionList where description does not contain UPDATED_DESCRIPTION
        defaultProfessionShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProfessionsByTaskIsEqualToSomething() throws Exception {
        // Initialize the database
        professionRepository.saveAndFlush(profession);
        Task task;
        if (TestUtil.findAll(em, Task.class).isEmpty()) {
            task = TaskResourceIT.createEntity(em);
            em.persist(task);
            em.flush();
        } else {
            task = TestUtil.findAll(em, Task.class).get(0);
        }
        em.persist(task);
        em.flush();
        profession.addTask(task);
        professionRepository.saveAndFlush(profession);
        Long taskId = task.getId();

        // Get all the professionList where task equals to taskId
        defaultProfessionShouldBeFound("taskId.equals=" + taskId);

        // Get all the professionList where task equals to (taskId + 1)
        defaultProfessionShouldNotBeFound("taskId.equals=" + (taskId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProfessionShouldBeFound(String filter) throws Exception {
        restProfessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=name,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].name").value(hasItem(profession.getName())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restProfessionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=name,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProfessionShouldNotBeFound(String filter) throws Exception {
        restProfessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=name,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProfessionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=name,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProfession() throws Exception {
        // Get the profession
        restProfessionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProfession() throws Exception {
        // Initialize the database
        profession.setName(UUID.randomUUID().toString());
        professionRepository.saveAndFlush(profession);

        int databaseSizeBeforeUpdate = professionRepository.findAll().size();

        // Update the profession
        Profession updatedProfession = professionRepository.findById(profession.getName()).get();
        // Disconnect from session so that the updates on updatedProfession are not directly saved in db
        em.detach(updatedProfession);
        updatedProfession.description(UPDATED_DESCRIPTION);
        ProfessionDTO professionDTO = professionMapper.toDto(updatedProfession);

        restProfessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, professionDTO.getName())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(professionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Profession in the database
        List<Profession> professionList = professionRepository.findAll();
        assertThat(professionList).hasSize(databaseSizeBeforeUpdate);
        Profession testProfession = professionList.get(professionList.size() - 1);
        assertThat(testProfession.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingProfession() throws Exception {
        int databaseSizeBeforeUpdate = professionRepository.findAll().size();
        profession.setName(UUID.randomUUID().toString());

        // Create the Profession
        ProfessionDTO professionDTO = professionMapper.toDto(profession);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, professionDTO.getName())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(professionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Profession in the database
        List<Profession> professionList = professionRepository.findAll();
        assertThat(professionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProfession() throws Exception {
        int databaseSizeBeforeUpdate = professionRepository.findAll().size();
        profession.setName(UUID.randomUUID().toString());

        // Create the Profession
        ProfessionDTO professionDTO = professionMapper.toDto(profession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(professionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Profession in the database
        List<Profession> professionList = professionRepository.findAll();
        assertThat(professionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProfession() throws Exception {
        int databaseSizeBeforeUpdate = professionRepository.findAll().size();
        profession.setName(UUID.randomUUID().toString());

        // Create the Profession
        ProfessionDTO professionDTO = professionMapper.toDto(profession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfessionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(professionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Profession in the database
        List<Profession> professionList = professionRepository.findAll();
        assertThat(professionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProfessionWithPatch() throws Exception {
        // Initialize the database
        profession.setName(UUID.randomUUID().toString());
        professionRepository.saveAndFlush(profession);

        int databaseSizeBeforeUpdate = professionRepository.findAll().size();

        // Update the profession using partial update
        Profession partialUpdatedProfession = new Profession();
        partialUpdatedProfession.setName(profession.getName());

        partialUpdatedProfession.description(UPDATED_DESCRIPTION);

        restProfessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProfession.getName())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProfession))
            )
            .andExpect(status().isOk());

        // Validate the Profession in the database
        List<Profession> professionList = professionRepository.findAll();
        assertThat(professionList).hasSize(databaseSizeBeforeUpdate);
        Profession testProfession = professionList.get(professionList.size() - 1);
        assertThat(testProfession.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateProfessionWithPatch() throws Exception {
        // Initialize the database
        profession.setName(UUID.randomUUID().toString());
        professionRepository.saveAndFlush(profession);

        int databaseSizeBeforeUpdate = professionRepository.findAll().size();

        // Update the profession using partial update
        Profession partialUpdatedProfession = new Profession();
        partialUpdatedProfession.setName(profession.getName());

        partialUpdatedProfession.description(UPDATED_DESCRIPTION);

        restProfessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProfession.getName())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProfession))
            )
            .andExpect(status().isOk());

        // Validate the Profession in the database
        List<Profession> professionList = professionRepository.findAll();
        assertThat(professionList).hasSize(databaseSizeBeforeUpdate);
        Profession testProfession = professionList.get(professionList.size() - 1);
        assertThat(testProfession.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingProfession() throws Exception {
        int databaseSizeBeforeUpdate = professionRepository.findAll().size();
        profession.setName(UUID.randomUUID().toString());

        // Create the Profession
        ProfessionDTO professionDTO = professionMapper.toDto(profession);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, professionDTO.getName())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(professionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Profession in the database
        List<Profession> professionList = professionRepository.findAll();
        assertThat(professionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProfession() throws Exception {
        int databaseSizeBeforeUpdate = professionRepository.findAll().size();
        profession.setName(UUID.randomUUID().toString());

        // Create the Profession
        ProfessionDTO professionDTO = professionMapper.toDto(profession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(professionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Profession in the database
        List<Profession> professionList = professionRepository.findAll();
        assertThat(professionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProfession() throws Exception {
        int databaseSizeBeforeUpdate = professionRepository.findAll().size();
        profession.setName(UUID.randomUUID().toString());

        // Create the Profession
        ProfessionDTO professionDTO = professionMapper.toDto(profession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfessionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(professionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Profession in the database
        List<Profession> professionList = professionRepository.findAll();
        assertThat(professionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProfession() throws Exception {
        // Initialize the database
        profession.setName(UUID.randomUUID().toString());
        professionRepository.saveAndFlush(profession);

        int databaseSizeBeforeDelete = professionRepository.findAll().size();

        // Delete the profession
        restProfessionMockMvc
            .perform(delete(ENTITY_API_URL_ID, profession.getName()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Profession> professionList = professionRepository.findAll();
        assertThat(professionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
