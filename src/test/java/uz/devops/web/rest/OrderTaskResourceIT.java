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
import uz.devops.domain.OrderTask;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.OrderTaskRepository;
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
