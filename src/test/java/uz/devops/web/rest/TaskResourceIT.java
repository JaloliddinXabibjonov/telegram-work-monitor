package uz.devops.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.IntegrationTest;
import uz.devops.domain.Job;
import uz.devops.domain.Profession;
import uz.devops.domain.Task;
import uz.devops.repository.TaskRepository;
import uz.devops.service.TaskService;
import uz.devops.service.criteria.TaskCriteria;
import uz.devops.service.dto.TaskDTO;
import uz.devops.service.mapper.TaskMapper;

/**
 * Integration tests for the {@link TaskResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TaskResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PRICE = "AAAAAAAAAA";
    private static final String UPDATED_PRICE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_PRIORITY = 1;
    private static final Integer UPDATED_PRIORITY = 2;
    private static final Integer SMALLER_PRIORITY = 1 - 1;

    private static final String ENTITY_API_URL = "/api/tasks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TaskRepository taskRepository;

    @Mock
    private TaskRepository taskRepositoryMock;

    @Autowired
    private TaskMapper taskMapper;

    @Mock
    private TaskService taskServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTaskMockMvc;

    private Task task;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Task createEntity(EntityManager em) {
        Task task = new Task().name(DEFAULT_NAME).price(DEFAULT_PRICE).description(DEFAULT_DESCRIPTION).priority(DEFAULT_PRIORITY);
        return task;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Task createUpdatedEntity(EntityManager em) {
        Task task = new Task().name(UPDATED_NAME).price(UPDATED_PRICE).description(UPDATED_DESCRIPTION).priority(UPDATED_PRIORITY);
        return task;
    }

    @BeforeEach
    public void initTest() {
        task = createEntity(em);
    }

    @Test
    @Transactional
    void createTask() throws Exception {
        int databaseSizeBeforeCreate = taskRepository.findAll().size();
        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);
        restTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isCreated());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeCreate + 1);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTask.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testTask.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTask.getPriority()).isEqualTo(DEFAULT_PRIORITY);
    }

    @Test
    @Transactional
    void createTaskWithExistingId() throws Exception {
        // Create the Task with an existing ID
        task.setId(1L);
        TaskDTO taskDTO = taskMapper.toDto(task);

        int databaseSizeBeforeCreate = taskRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = taskRepository.findAll().size();
        // set the field null
        task.setName(null);

        // Create the Task, which fails.
        TaskDTO taskDTO = taskMapper.toDto(task);

        restTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isBadRequest());

        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTasks() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(task.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTasksWithEagerRelationshipsIsEnabled() throws Exception {
        when(taskServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTaskMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(taskServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTasksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(taskServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTaskMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(taskServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getTask() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get the task
        restTaskMockMvc
            .perform(get(ENTITY_API_URL_ID, task.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(task.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.priority").value(DEFAULT_PRIORITY));
    }

    @Test
    @Transactional
    void getTasksByIdFiltering() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        Long id = task.getId();

        defaultTaskShouldBeFound("id.equals=" + id);
        defaultTaskShouldNotBeFound("id.notEquals=" + id);

        defaultTaskShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTaskShouldNotBeFound("id.greaterThan=" + id);

        defaultTaskShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTaskShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTasksByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name equals to DEFAULT_NAME
        defaultTaskShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the taskList where name equals to UPDATED_NAME
        defaultTaskShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTasksByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name not equals to DEFAULT_NAME
        defaultTaskShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the taskList where name not equals to UPDATED_NAME
        defaultTaskShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTasksByNameIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name in DEFAULT_NAME or UPDATED_NAME
        defaultTaskShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the taskList where name equals to UPDATED_NAME
        defaultTaskShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTasksByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name is not null
        defaultTaskShouldBeFound("name.specified=true");

        // Get all the taskList where name is null
        defaultTaskShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByNameContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name contains DEFAULT_NAME
        defaultTaskShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the taskList where name contains UPDATED_NAME
        defaultTaskShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTasksByNameNotContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name does not contain DEFAULT_NAME
        defaultTaskShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the taskList where name does not contain UPDATED_NAME
        defaultTaskShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTasksByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where price equals to DEFAULT_PRICE
        defaultTaskShouldBeFound("price.equals=" + DEFAULT_PRICE);

        // Get all the taskList where price equals to UPDATED_PRICE
        defaultTaskShouldNotBeFound("price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllTasksByPriceIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where price not equals to DEFAULT_PRICE
        defaultTaskShouldNotBeFound("price.notEquals=" + DEFAULT_PRICE);

        // Get all the taskList where price not equals to UPDATED_PRICE
        defaultTaskShouldBeFound("price.notEquals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllTasksByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where price in DEFAULT_PRICE or UPDATED_PRICE
        defaultTaskShouldBeFound("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE);

        // Get all the taskList where price equals to UPDATED_PRICE
        defaultTaskShouldNotBeFound("price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllTasksByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where price is not null
        defaultTaskShouldBeFound("price.specified=true");

        // Get all the taskList where price is null
        defaultTaskShouldNotBeFound("price.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByPriceContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where price contains DEFAULT_PRICE
        defaultTaskShouldBeFound("price.contains=" + DEFAULT_PRICE);

        // Get all the taskList where price contains UPDATED_PRICE
        defaultTaskShouldNotBeFound("price.contains=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllTasksByPriceNotContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where price does not contain DEFAULT_PRICE
        defaultTaskShouldNotBeFound("price.doesNotContain=" + DEFAULT_PRICE);

        // Get all the taskList where price does not contain UPDATED_PRICE
        defaultTaskShouldBeFound("price.doesNotContain=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description equals to DEFAULT_DESCRIPTION
        defaultTaskShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the taskList where description equals to UPDATED_DESCRIPTION
        defaultTaskShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description not equals to DEFAULT_DESCRIPTION
        defaultTaskShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the taskList where description not equals to UPDATED_DESCRIPTION
        defaultTaskShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultTaskShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the taskList where description equals to UPDATED_DESCRIPTION
        defaultTaskShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description is not null
        defaultTaskShouldBeFound("description.specified=true");

        // Get all the taskList where description is null
        defaultTaskShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description contains DEFAULT_DESCRIPTION
        defaultTaskShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the taskList where description contains UPDATED_DESCRIPTION
        defaultTaskShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description does not contain DEFAULT_DESCRIPTION
        defaultTaskShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the taskList where description does not contain UPDATED_DESCRIPTION
        defaultTaskShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTasksByPriorityIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where priority equals to DEFAULT_PRIORITY
        defaultTaskShouldBeFound("priority.equals=" + DEFAULT_PRIORITY);

        // Get all the taskList where priority equals to UPDATED_PRIORITY
        defaultTaskShouldNotBeFound("priority.equals=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    void getAllTasksByPriorityIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where priority not equals to DEFAULT_PRIORITY
        defaultTaskShouldNotBeFound("priority.notEquals=" + DEFAULT_PRIORITY);

        // Get all the taskList where priority not equals to UPDATED_PRIORITY
        defaultTaskShouldBeFound("priority.notEquals=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    void getAllTasksByPriorityIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where priority in DEFAULT_PRIORITY or UPDATED_PRIORITY
        defaultTaskShouldBeFound("priority.in=" + DEFAULT_PRIORITY + "," + UPDATED_PRIORITY);

        // Get all the taskList where priority equals to UPDATED_PRIORITY
        defaultTaskShouldNotBeFound("priority.in=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    void getAllTasksByPriorityIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where priority is not null
        defaultTaskShouldBeFound("priority.specified=true");

        // Get all the taskList where priority is null
        defaultTaskShouldNotBeFound("priority.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByPriorityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where priority is greater than or equal to DEFAULT_PRIORITY
        defaultTaskShouldBeFound("priority.greaterThanOrEqual=" + DEFAULT_PRIORITY);

        // Get all the taskList where priority is greater than or equal to UPDATED_PRIORITY
        defaultTaskShouldNotBeFound("priority.greaterThanOrEqual=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    void getAllTasksByPriorityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where priority is less than or equal to DEFAULT_PRIORITY
        defaultTaskShouldBeFound("priority.lessThanOrEqual=" + DEFAULT_PRIORITY);

        // Get all the taskList where priority is less than or equal to SMALLER_PRIORITY
        defaultTaskShouldNotBeFound("priority.lessThanOrEqual=" + SMALLER_PRIORITY);
    }

    @Test
    @Transactional
    void getAllTasksByPriorityIsLessThanSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where priority is less than DEFAULT_PRIORITY
        defaultTaskShouldNotBeFound("priority.lessThan=" + DEFAULT_PRIORITY);

        // Get all the taskList where priority is less than UPDATED_PRIORITY
        defaultTaskShouldBeFound("priority.lessThan=" + UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    void getAllTasksByPriorityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where priority is greater than DEFAULT_PRIORITY
        defaultTaskShouldNotBeFound("priority.greaterThan=" + DEFAULT_PRIORITY);

        // Get all the taskList where priority is greater than SMALLER_PRIORITY
        defaultTaskShouldBeFound("priority.greaterThan=" + SMALLER_PRIORITY);
    }

    @Test
    @Transactional
    void getAllTasksByJobIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);
        Job job;
        if (TestUtil.findAll(em, Job.class).isEmpty()) {
            job = JobResourceIT.createEntity(em);
            em.persist(job);
            em.flush();
        } else {
            job = TestUtil.findAll(em, Job.class).get(0);
        }
        em.persist(job);
        em.flush();
        task.setJob(job);
        taskRepository.saveAndFlush(task);
        Long jobId = job.getId();

        // Get all the taskList where job equals to jobId
        defaultTaskShouldBeFound("jobId.equals=" + jobId);

        // Get all the taskList where job equals to (jobId + 1)
        defaultTaskShouldNotBeFound("jobId.equals=" + (jobId + 1));
    }

    @Test
    @Transactional
    void getAllTasksByProfessionIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);
        Profession profession;
        if (TestUtil.findAll(em, Profession.class).isEmpty()) {
            profession = ProfessionResourceIT.createEntity(em);
            em.persist(profession);
            em.flush();
        } else {
            profession = TestUtil.findAll(em, Profession.class).get(0);
        }
        em.persist(profession);
        em.flush();
        task.addProfession(profession);
        taskRepository.saveAndFlush(task);
        String professionId = profession.getName();

        // Get all the taskList where profession equals to professionId
        defaultTaskShouldBeFound("professionId.equals=" + professionId);

        // Get all the taskList where profession equals to "invalid-id"
        defaultTaskShouldNotBeFound("professionId.equals=" + "invalid-id");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTaskShouldBeFound(String filter) throws Exception {
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(task.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY)));

        // Check, that the count call also returns 1
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTaskShouldNotBeFound(String filter) throws Exception {
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTask() throws Exception {
        // Get the task
        restTaskMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTask() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeUpdate = taskRepository.findAll().size();

        // Update the task
        Task updatedTask = taskRepository.findById(task.getId()).get();
        // Disconnect from session so that the updates on updatedTask are not directly saved in db
        em.detach(updatedTask);
        updatedTask.name(UPDATED_NAME).price(UPDATED_PRICE).description(UPDATED_DESCRIPTION).priority(UPDATED_PRIORITY);
        TaskDTO taskDTO = taskMapper.toDto(updatedTask);

        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taskDTO))
            )
            .andExpect(status().isOk());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTask.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTask.getPriority()).isEqualTo(UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    void putNonExistingTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTaskWithPatch() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeUpdate = taskRepository.findAll().size();

        // Update the task using partial update
        Task partialUpdatedTask = new Task();
        partialUpdatedTask.setId(task.getId());

        partialUpdatedTask.price(UPDATED_PRICE).priority(UPDATED_PRIORITY);

        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTask))
            )
            .andExpect(status().isOk());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTask.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testTask.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTask.getPriority()).isEqualTo(UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    void fullUpdateTaskWithPatch() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeUpdate = taskRepository.findAll().size();

        // Update the task using partial update
        Task partialUpdatedTask = new Task();
        partialUpdatedTask.setId(task.getId());

        partialUpdatedTask.name(UPDATED_NAME).price(UPDATED_PRICE).description(UPDATED_DESCRIPTION).priority(UPDATED_PRIORITY);

        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTask))
            )
            .andExpect(status().isOk());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTask.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTask.getPriority()).isEqualTo(UPDATED_PRIORITY);
    }

    @Test
    @Transactional
    void patchNonExistingTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, taskDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(taskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(taskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTask() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeDelete = taskRepository.findAll().size();

        // Delete the task
        restTaskMockMvc
            .perform(delete(ENTITY_API_URL_ID, task.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
