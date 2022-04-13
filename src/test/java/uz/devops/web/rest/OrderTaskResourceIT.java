package uz.devops.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
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
import uz.devops.IntegrationTest;
import uz.devops.domain.Order;
import uz.devops.domain.OrderTask;
import uz.devops.domain.Task;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.OrderTaskRepository;
import uz.devops.service.criteria.OrderTaskCriteria;
import uz.devops.service.dto.OrderTaskDTO;
import uz.devops.service.mapper.OrderTaskMapper;

/**
 * Integration tests for the {@link OrderTaskResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrderTaskResourceIT {

    private static final Status DEFAULT_STATUS = Status.NEW;
    private static final Status UPDATED_STATUS = Status.ACTIVE;

    private static final Instant DEFAULT_STARTED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_STARTED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_EMPLOYEE_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_EMPLOYEE_USERNAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/order-tasks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrderTaskRepository orderTaskRepository;

    @Autowired
    private OrderTaskMapper orderTaskMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderTaskMockMvc;

    private OrderTask orderTask;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderTask createEntity(EntityManager em) {
        OrderTask orderTask = new OrderTask()
            .status(DEFAULT_STATUS)
            .startedDate(DEFAULT_STARTED_DATE)
            .endDate(DEFAULT_END_DATE)
            .employeeUsername(DEFAULT_EMPLOYEE_USERNAME);
        return orderTask;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderTask createUpdatedEntity(EntityManager em) {
        OrderTask orderTask = new OrderTask()
            .status(UPDATED_STATUS)
            .startedDate(UPDATED_STARTED_DATE)
            .endDate(UPDATED_END_DATE)
            .employeeUsername(UPDATED_EMPLOYEE_USERNAME);
        return orderTask;
    }

    @BeforeEach
    public void initTest() {
        orderTask = createEntity(em);
    }

    @Test
    @Transactional
    void createOrderTask() throws Exception {
        int databaseSizeBeforeCreate = orderTaskRepository.findAll().size();
        // Create the OrderTask
        OrderTaskDTO orderTaskDTO = orderTaskMapper.toDto(orderTask);
        restOrderTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderTaskDTO)))
            .andExpect(status().isCreated());

        // Validate the OrderTask in the database
        List<OrderTask> orderTaskList = orderTaskRepository.findAll();
        assertThat(orderTaskList).hasSize(databaseSizeBeforeCreate + 1);
        OrderTask testOrderTask = orderTaskList.get(orderTaskList.size() - 1);
        assertThat(testOrderTask.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testOrderTask.getStartedDate()).isEqualTo(DEFAULT_STARTED_DATE);
        assertThat(testOrderTask.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testOrderTask.getEmployeeUsername()).isEqualTo(DEFAULT_EMPLOYEE_USERNAME);
    }

    @Test
    @Transactional
    void createOrderTaskWithExistingId() throws Exception {
        // Create the OrderTask with an existing ID
        orderTask.setId(1L);
        OrderTaskDTO orderTaskDTO = orderTaskMapper.toDto(orderTask);

        int databaseSizeBeforeCreate = orderTaskRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderTaskMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderTaskDTO)))
            .andExpect(status().isBadRequest());

        // Validate the OrderTask in the database
        List<OrderTask> orderTaskList = orderTaskRepository.findAll();
        assertThat(orderTaskList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllOrderTasks() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);

        // Get all the orderTaskList
        restOrderTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderTask.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].startedDate").value(hasItem(DEFAULT_STARTED_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].employeeUsername").value(hasItem(DEFAULT_EMPLOYEE_USERNAME)));
    }

    @Test
    @Transactional
    void getOrderTask() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);

        // Get the orderTask
        restOrderTaskMockMvc
            .perform(get(ENTITY_API_URL_ID, orderTask.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orderTask.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.startedDate").value(DEFAULT_STARTED_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.employeeUsername").value(DEFAULT_EMPLOYEE_USERNAME));
    }

    @Test
    @Transactional
    void getOrderTasksByIdFiltering() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);

        Long id = orderTask.getId();

        defaultOrderTaskShouldBeFound("id.equals=" + id);
        defaultOrderTaskShouldNotBeFound("id.notEquals=" + id);

        defaultOrderTaskShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultOrderTaskShouldNotBeFound("id.greaterThan=" + id);

        defaultOrderTaskShouldBeFound("id.lessThanOrEqual=" + id);
        defaultOrderTaskShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOrderTasksByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);

        // Get all the orderTaskList where status equals to DEFAULT_STATUS
        defaultOrderTaskShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the orderTaskList where status equals to UPDATED_STATUS
        defaultOrderTaskShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllOrderTasksByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);

        // Get all the orderTaskList where status not equals to DEFAULT_STATUS
        defaultOrderTaskShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the orderTaskList where status not equals to UPDATED_STATUS
        defaultOrderTaskShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllOrderTasksByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);

        // Get all the orderTaskList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultOrderTaskShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the orderTaskList where status equals to UPDATED_STATUS
        defaultOrderTaskShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllOrderTasksByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);

        // Get all the orderTaskList where status is not null
        defaultOrderTaskShouldBeFound("status.specified=true");

        // Get all the orderTaskList where status is null
        defaultOrderTaskShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderTasksByStartedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);

        // Get all the orderTaskList where startedDate equals to DEFAULT_STARTED_DATE
        defaultOrderTaskShouldBeFound("startedDate.equals=" + DEFAULT_STARTED_DATE);

        // Get all the orderTaskList where startedDate equals to UPDATED_STARTED_DATE
        defaultOrderTaskShouldNotBeFound("startedDate.equals=" + UPDATED_STARTED_DATE);
    }

    @Test
    @Transactional
    void getAllOrderTasksByStartedDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);

        // Get all the orderTaskList where startedDate not equals to DEFAULT_STARTED_DATE
        defaultOrderTaskShouldNotBeFound("startedDate.notEquals=" + DEFAULT_STARTED_DATE);

        // Get all the orderTaskList where startedDate not equals to UPDATED_STARTED_DATE
        defaultOrderTaskShouldBeFound("startedDate.notEquals=" + UPDATED_STARTED_DATE);
    }

    @Test
    @Transactional
    void getAllOrderTasksByStartedDateIsInShouldWork() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);

        // Get all the orderTaskList where startedDate in DEFAULT_STARTED_DATE or UPDATED_STARTED_DATE
        defaultOrderTaskShouldBeFound("startedDate.in=" + DEFAULT_STARTED_DATE + "," + UPDATED_STARTED_DATE);

        // Get all the orderTaskList where startedDate equals to UPDATED_STARTED_DATE
        defaultOrderTaskShouldNotBeFound("startedDate.in=" + UPDATED_STARTED_DATE);
    }

    @Test
    @Transactional
    void getAllOrderTasksByStartedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);

        // Get all the orderTaskList where startedDate is not null
        defaultOrderTaskShouldBeFound("startedDate.specified=true");

        // Get all the orderTaskList where startedDate is null
        defaultOrderTaskShouldNotBeFound("startedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderTasksByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);

        // Get all the orderTaskList where endDate equals to DEFAULT_END_DATE
        defaultOrderTaskShouldBeFound("endDate.equals=" + DEFAULT_END_DATE);

        // Get all the orderTaskList where endDate equals to UPDATED_END_DATE
        defaultOrderTaskShouldNotBeFound("endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllOrderTasksByEndDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);

        // Get all the orderTaskList where endDate not equals to DEFAULT_END_DATE
        defaultOrderTaskShouldNotBeFound("endDate.notEquals=" + DEFAULT_END_DATE);

        // Get all the orderTaskList where endDate not equals to UPDATED_END_DATE
        defaultOrderTaskShouldBeFound("endDate.notEquals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllOrderTasksByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);

        // Get all the orderTaskList where endDate in DEFAULT_END_DATE or UPDATED_END_DATE
        defaultOrderTaskShouldBeFound("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE);

        // Get all the orderTaskList where endDate equals to UPDATED_END_DATE
        defaultOrderTaskShouldNotBeFound("endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllOrderTasksByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);

        // Get all the orderTaskList where endDate is not null
        defaultOrderTaskShouldBeFound("endDate.specified=true");

        // Get all the orderTaskList where endDate is null
        defaultOrderTaskShouldNotBeFound("endDate.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderTasksByEmployeeUsernameIsEqualToSomething() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);

        // Get all the orderTaskList where employeeUsername equals to DEFAULT_EMPLOYEE_USERNAME
        defaultOrderTaskShouldBeFound("employeeUsername.equals=" + DEFAULT_EMPLOYEE_USERNAME);

        // Get all the orderTaskList where employeeUsername equals to UPDATED_EMPLOYEE_USERNAME
        defaultOrderTaskShouldNotBeFound("employeeUsername.equals=" + UPDATED_EMPLOYEE_USERNAME);
    }

    @Test
    @Transactional
    void getAllOrderTasksByEmployeeUsernameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);

        // Get all the orderTaskList where employeeUsername not equals to DEFAULT_EMPLOYEE_USERNAME
        defaultOrderTaskShouldNotBeFound("employeeUsername.notEquals=" + DEFAULT_EMPLOYEE_USERNAME);

        // Get all the orderTaskList where employeeUsername not equals to UPDATED_EMPLOYEE_USERNAME
        defaultOrderTaskShouldBeFound("employeeUsername.notEquals=" + UPDATED_EMPLOYEE_USERNAME);
    }

    @Test
    @Transactional
    void getAllOrderTasksByEmployeeUsernameIsInShouldWork() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);

        // Get all the orderTaskList where employeeUsername in DEFAULT_EMPLOYEE_USERNAME or UPDATED_EMPLOYEE_USERNAME
        defaultOrderTaskShouldBeFound("employeeUsername.in=" + DEFAULT_EMPLOYEE_USERNAME + "," + UPDATED_EMPLOYEE_USERNAME);

        // Get all the orderTaskList where employeeUsername equals to UPDATED_EMPLOYEE_USERNAME
        defaultOrderTaskShouldNotBeFound("employeeUsername.in=" + UPDATED_EMPLOYEE_USERNAME);
    }

    @Test
    @Transactional
    void getAllOrderTasksByEmployeeUsernameIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);

        // Get all the orderTaskList where employeeUsername is not null
        defaultOrderTaskShouldBeFound("employeeUsername.specified=true");

        // Get all the orderTaskList where employeeUsername is null
        defaultOrderTaskShouldNotBeFound("employeeUsername.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderTasksByEmployeeUsernameContainsSomething() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);

        // Get all the orderTaskList where employeeUsername contains DEFAULT_EMPLOYEE_USERNAME
        defaultOrderTaskShouldBeFound("employeeUsername.contains=" + DEFAULT_EMPLOYEE_USERNAME);

        // Get all the orderTaskList where employeeUsername contains UPDATED_EMPLOYEE_USERNAME
        defaultOrderTaskShouldNotBeFound("employeeUsername.contains=" + UPDATED_EMPLOYEE_USERNAME);
    }

    @Test
    @Transactional
    void getAllOrderTasksByEmployeeUsernameNotContainsSomething() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);

        // Get all the orderTaskList where employeeUsername does not contain DEFAULT_EMPLOYEE_USERNAME
        defaultOrderTaskShouldNotBeFound("employeeUsername.doesNotContain=" + DEFAULT_EMPLOYEE_USERNAME);

        // Get all the orderTaskList where employeeUsername does not contain UPDATED_EMPLOYEE_USERNAME
        defaultOrderTaskShouldBeFound("employeeUsername.doesNotContain=" + UPDATED_EMPLOYEE_USERNAME);
    }

    @Test
    @Transactional
    void getAllOrderTasksByTaskIsEqualToSomething() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);
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
        orderTask.setTask(task);
        orderTaskRepository.saveAndFlush(orderTask);
        Long taskId = task.getId();

        // Get all the orderTaskList where task equals to taskId
        defaultOrderTaskShouldBeFound("taskId.equals=" + taskId);

        // Get all the orderTaskList where task equals to (taskId + 1)
        defaultOrderTaskShouldNotBeFound("taskId.equals=" + (taskId + 1));
    }

    @Test
    @Transactional
    void getAllOrderTasksByOrderIsEqualToSomething() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);
        Order order;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            order = OrderResourceIT.createEntity(em);
            em.persist(order);
            em.flush();
        } else {
            order = TestUtil.findAll(em, Order.class).get(0);
        }
        em.persist(order);
        em.flush();
        orderTask.setOrder(order);
        orderTaskRepository.saveAndFlush(orderTask);
        Long orderId = order.getId();

        // Get all the orderTaskList where order equals to orderId
        defaultOrderTaskShouldBeFound("orderId.equals=" + orderId);

        // Get all the orderTaskList where order equals to (orderId + 1)
        defaultOrderTaskShouldNotBeFound("orderId.equals=" + (orderId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOrderTaskShouldBeFound(String filter) throws Exception {
        restOrderTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderTask.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].startedDate").value(hasItem(DEFAULT_STARTED_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].employeeUsername").value(hasItem(DEFAULT_EMPLOYEE_USERNAME)));

        // Check, that the count call also returns 1
        restOrderTaskMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOrderTaskShouldNotBeFound(String filter) throws Exception {
        restOrderTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOrderTaskMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOrderTask() throws Exception {
        // Get the orderTask
        restOrderTaskMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewOrderTask() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);

        int databaseSizeBeforeUpdate = orderTaskRepository.findAll().size();

        // Update the orderTask
        OrderTask updatedOrderTask = orderTaskRepository.findById(orderTask.getId()).get();
        // Disconnect from session so that the updates on updatedOrderTask are not directly saved in db
        em.detach(updatedOrderTask);
        updatedOrderTask
            .status(UPDATED_STATUS)
            .startedDate(UPDATED_STARTED_DATE)
            .endDate(UPDATED_END_DATE)
            .employeeUsername(UPDATED_EMPLOYEE_USERNAME);
        OrderTaskDTO orderTaskDTO = orderTaskMapper.toDto(updatedOrderTask);

        restOrderTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderTaskDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderTaskDTO))
            )
            .andExpect(status().isOk());

        // Validate the OrderTask in the database
        List<OrderTask> orderTaskList = orderTaskRepository.findAll();
        assertThat(orderTaskList).hasSize(databaseSizeBeforeUpdate);
        OrderTask testOrderTask = orderTaskList.get(orderTaskList.size() - 1);
        assertThat(testOrderTask.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testOrderTask.getStartedDate()).isEqualTo(UPDATED_STARTED_DATE);
        assertThat(testOrderTask.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testOrderTask.getEmployeeUsername()).isEqualTo(UPDATED_EMPLOYEE_USERNAME);
    }

    @Test
    @Transactional
    void putNonExistingOrderTask() throws Exception {
        int databaseSizeBeforeUpdate = orderTaskRepository.findAll().size();
        orderTask.setId(count.incrementAndGet());

        // Create the OrderTask
        OrderTaskDTO orderTaskDTO = orderTaskMapper.toDto(orderTask);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderTaskDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderTaskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderTask in the database
        List<OrderTask> orderTaskList = orderTaskRepository.findAll();
        assertThat(orderTaskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrderTask() throws Exception {
        int databaseSizeBeforeUpdate = orderTaskRepository.findAll().size();
        orderTask.setId(count.incrementAndGet());

        // Create the OrderTask
        OrderTaskDTO orderTaskDTO = orderTaskMapper.toDto(orderTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderTaskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderTask in the database
        List<OrderTask> orderTaskList = orderTaskRepository.findAll();
        assertThat(orderTaskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrderTask() throws Exception {
        int databaseSizeBeforeUpdate = orderTaskRepository.findAll().size();
        orderTask.setId(count.incrementAndGet());

        // Create the OrderTask
        OrderTaskDTO orderTaskDTO = orderTaskMapper.toDto(orderTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderTaskMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderTaskDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderTask in the database
        List<OrderTask> orderTaskList = orderTaskRepository.findAll();
        assertThat(orderTaskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrderTaskWithPatch() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);

        int databaseSizeBeforeUpdate = orderTaskRepository.findAll().size();

        // Update the orderTask using partial update
        OrderTask partialUpdatedOrderTask = new OrderTask();
        partialUpdatedOrderTask.setId(orderTask.getId());

        partialUpdatedOrderTask.employeeUsername(UPDATED_EMPLOYEE_USERNAME);

        restOrderTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderTask))
            )
            .andExpect(status().isOk());

        // Validate the OrderTask in the database
        List<OrderTask> orderTaskList = orderTaskRepository.findAll();
        assertThat(orderTaskList).hasSize(databaseSizeBeforeUpdate);
        OrderTask testOrderTask = orderTaskList.get(orderTaskList.size() - 1);
        assertThat(testOrderTask.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testOrderTask.getStartedDate()).isEqualTo(DEFAULT_STARTED_DATE);
        assertThat(testOrderTask.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testOrderTask.getEmployeeUsername()).isEqualTo(UPDATED_EMPLOYEE_USERNAME);
    }

    @Test
    @Transactional
    void fullUpdateOrderTaskWithPatch() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);

        int databaseSizeBeforeUpdate = orderTaskRepository.findAll().size();

        // Update the orderTask using partial update
        OrderTask partialUpdatedOrderTask = new OrderTask();
        partialUpdatedOrderTask.setId(orderTask.getId());

        partialUpdatedOrderTask
            .status(UPDATED_STATUS)
            .startedDate(UPDATED_STARTED_DATE)
            .endDate(UPDATED_END_DATE)
            .employeeUsername(UPDATED_EMPLOYEE_USERNAME);

        restOrderTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderTask.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderTask))
            )
            .andExpect(status().isOk());

        // Validate the OrderTask in the database
        List<OrderTask> orderTaskList = orderTaskRepository.findAll();
        assertThat(orderTaskList).hasSize(databaseSizeBeforeUpdate);
        OrderTask testOrderTask = orderTaskList.get(orderTaskList.size() - 1);
        assertThat(testOrderTask.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testOrderTask.getStartedDate()).isEqualTo(UPDATED_STARTED_DATE);
        assertThat(testOrderTask.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testOrderTask.getEmployeeUsername()).isEqualTo(UPDATED_EMPLOYEE_USERNAME);
    }

    @Test
    @Transactional
    void patchNonExistingOrderTask() throws Exception {
        int databaseSizeBeforeUpdate = orderTaskRepository.findAll().size();
        orderTask.setId(count.incrementAndGet());

        // Create the OrderTask
        OrderTaskDTO orderTaskDTO = orderTaskMapper.toDto(orderTask);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orderTaskDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderTaskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderTask in the database
        List<OrderTask> orderTaskList = orderTaskRepository.findAll();
        assertThat(orderTaskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrderTask() throws Exception {
        int databaseSizeBeforeUpdate = orderTaskRepository.findAll().size();
        orderTask.setId(count.incrementAndGet());

        // Create the OrderTask
        OrderTaskDTO orderTaskDTO = orderTaskMapper.toDto(orderTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderTaskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderTask in the database
        List<OrderTask> orderTaskList = orderTaskRepository.findAll();
        assertThat(orderTaskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrderTask() throws Exception {
        int databaseSizeBeforeUpdate = orderTaskRepository.findAll().size();
        orderTask.setId(count.incrementAndGet());

        // Create the OrderTask
        OrderTaskDTO orderTaskDTO = orderTaskMapper.toDto(orderTask);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderTaskMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(orderTaskDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderTask in the database
        List<OrderTask> orderTaskList = orderTaskRepository.findAll();
        assertThat(orderTaskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrderTask() throws Exception {
        // Initialize the database
        orderTaskRepository.saveAndFlush(orderTask);

        int databaseSizeBeforeDelete = orderTaskRepository.findAll().size();

        // Delete the orderTask
        restOrderTaskMockMvc
            .perform(delete(ENTITY_API_URL_ID, orderTask.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<OrderTask> orderTaskList = orderTaskRepository.findAll();
        assertThat(orderTaskList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
