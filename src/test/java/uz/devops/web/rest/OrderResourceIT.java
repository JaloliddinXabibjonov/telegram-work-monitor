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
import uz.devops.domain.Job;
import uz.devops.domain.Order;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.OrderRepository;
import uz.devops.service.criteria.OrderCriteria;
import uz.devops.service.dto.OrderDTO;
import uz.devops.service.mapper.OrderMapper;

/**
 * Integration tests for the {@link OrderResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrderResourceIT {

    private static final Instant DEFAULT_STARTED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_STARTED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Status DEFAULT_STATUS = Status.NEW;
    private static final Status UPDATED_STATUS = Status.ACTIVE;

    private static final String ENTITY_API_URL = "/api/orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderMockMvc;

    private Order order;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Order createEntity(EntityManager em) {
        Order order = new Order().startedDate(DEFAULT_STARTED_DATE).endDate(DEFAULT_END_DATE).status(DEFAULT_STATUS);
        return order;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Order createUpdatedEntity(EntityManager em) {
        Order order = new Order().startedDate(UPDATED_STARTED_DATE).endDate(UPDATED_END_DATE).status(UPDATED_STATUS);
        return order;
    }

    @BeforeEach
    public void initTest() {
        order = createEntity(em);
    }

    @Test
    @Transactional
    void createOrder() throws Exception {
        int databaseSizeBeforeCreate = orderRepository.findAll().size();
        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);
        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isCreated());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeCreate + 1);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getStartedDate()).isEqualTo(DEFAULT_STARTED_DATE);
        assertThat(testOrder.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testOrder.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createOrderWithExistingId() throws Exception {
        // Create the Order with an existing ID
        order.setId(1L);
        OrderDTO orderDTO = orderMapper.toDto(order);

        int databaseSizeBeforeCreate = orderRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllOrders() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(order.getId().intValue())))
            .andExpect(jsonPath("$.[*].startedDate").value(hasItem(DEFAULT_STARTED_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get the order
        restOrderMockMvc
            .perform(get(ENTITY_API_URL_ID, order.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(order.getId().intValue()))
            .andExpect(jsonPath("$.startedDate").value(DEFAULT_STARTED_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getOrdersByIdFiltering() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        Long id = order.getId();

        defaultOrderShouldBeFound("id.equals=" + id);
        defaultOrderShouldNotBeFound("id.notEquals=" + id);

        defaultOrderShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultOrderShouldNotBeFound("id.greaterThan=" + id);

        defaultOrderShouldBeFound("id.lessThanOrEqual=" + id);
        defaultOrderShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOrdersByStartedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where startedDate equals to DEFAULT_STARTED_DATE
        defaultOrderShouldBeFound("startedDate.equals=" + DEFAULT_STARTED_DATE);

        // Get all the orderList where startedDate equals to UPDATED_STARTED_DATE
        defaultOrderShouldNotBeFound("startedDate.equals=" + UPDATED_STARTED_DATE);
    }

    @Test
    @Transactional
    void getAllOrdersByStartedDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where startedDate not equals to DEFAULT_STARTED_DATE
        defaultOrderShouldNotBeFound("startedDate.notEquals=" + DEFAULT_STARTED_DATE);

        // Get all the orderList where startedDate not equals to UPDATED_STARTED_DATE
        defaultOrderShouldBeFound("startedDate.notEquals=" + UPDATED_STARTED_DATE);
    }

    @Test
    @Transactional
    void getAllOrdersByStartedDateIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where startedDate in DEFAULT_STARTED_DATE or UPDATED_STARTED_DATE
        defaultOrderShouldBeFound("startedDate.in=" + DEFAULT_STARTED_DATE + "," + UPDATED_STARTED_DATE);

        // Get all the orderList where startedDate equals to UPDATED_STARTED_DATE
        defaultOrderShouldNotBeFound("startedDate.in=" + UPDATED_STARTED_DATE);
    }

    @Test
    @Transactional
    void getAllOrdersByStartedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where startedDate is not null
        defaultOrderShouldBeFound("startedDate.specified=true");

        // Get all the orderList where startedDate is null
        defaultOrderShouldNotBeFound("startedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where endDate equals to DEFAULT_END_DATE
        defaultOrderShouldBeFound("endDate.equals=" + DEFAULT_END_DATE);

        // Get all the orderList where endDate equals to UPDATED_END_DATE
        defaultOrderShouldNotBeFound("endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllOrdersByEndDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where endDate not equals to DEFAULT_END_DATE
        defaultOrderShouldNotBeFound("endDate.notEquals=" + DEFAULT_END_DATE);

        // Get all the orderList where endDate not equals to UPDATED_END_DATE
        defaultOrderShouldBeFound("endDate.notEquals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllOrdersByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where endDate in DEFAULT_END_DATE or UPDATED_END_DATE
        defaultOrderShouldBeFound("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE);

        // Get all the orderList where endDate equals to UPDATED_END_DATE
        defaultOrderShouldNotBeFound("endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    void getAllOrdersByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where endDate is not null
        defaultOrderShouldBeFound("endDate.specified=true");

        // Get all the orderList where endDate is null
        defaultOrderShouldNotBeFound("endDate.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where status equals to DEFAULT_STATUS
        defaultOrderShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the orderList where status equals to UPDATED_STATUS
        defaultOrderShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllOrdersByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where status not equals to DEFAULT_STATUS
        defaultOrderShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the orderList where status not equals to UPDATED_STATUS
        defaultOrderShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllOrdersByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultOrderShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the orderList where status equals to UPDATED_STATUS
        defaultOrderShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllOrdersByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where status is not null
        defaultOrderShouldBeFound("status.specified=true");

        // Get all the orderList where status is null
        defaultOrderShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByJobIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);
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
        order.setJob(job);
        orderRepository.saveAndFlush(order);
        Long jobId = job.getId();

        // Get all the orderList where job equals to jobId
        defaultOrderShouldBeFound("jobId.equals=" + jobId);

        // Get all the orderList where job equals to (jobId + 1)
        defaultOrderShouldNotBeFound("jobId.equals=" + (jobId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOrderShouldBeFound(String filter) throws Exception {
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(order.getId().intValue())))
            .andExpect(jsonPath("$.[*].startedDate").value(hasItem(DEFAULT_STARTED_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOrderShouldNotBeFound(String filter) throws Exception {
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOrder() throws Exception {
        // Get the order
        restOrderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeUpdate = orderRepository.findAll().size();

        // Update the order
        Order updatedOrder = orderRepository.findById(order.getId()).get();
        // Disconnect from session so that the updates on updatedOrder are not directly saved in db
        em.detach(updatedOrder);
        updatedOrder.startedDate(UPDATED_STARTED_DATE).endDate(UPDATED_END_DATE).status(UPDATED_STATUS);
        OrderDTO orderDTO = orderMapper.toDto(updatedOrder);

        restOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderDTO))
            )
            .andExpect(status().isOk());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getStartedDate()).isEqualTo(UPDATED_STARTED_DATE);
        assertThat(testOrder.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testOrder.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrderWithPatch() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeUpdate = orderRepository.findAll().size();

        // Update the order using partial update
        Order partialUpdatedOrder = new Order();
        partialUpdatedOrder.setId(order.getId());

        partialUpdatedOrder.startedDate(UPDATED_STARTED_DATE).status(UPDATED_STATUS);

        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrder))
            )
            .andExpect(status().isOk());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getStartedDate()).isEqualTo(UPDATED_STARTED_DATE);
        assertThat(testOrder.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testOrder.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateOrderWithPatch() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeUpdate = orderRepository.findAll().size();

        // Update the order using partial update
        Order partialUpdatedOrder = new Order();
        partialUpdatedOrder.setId(order.getId());

        partialUpdatedOrder.startedDate(UPDATED_STARTED_DATE).endDate(UPDATED_END_DATE).status(UPDATED_STATUS);

        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrder))
            )
            .andExpect(status().isOk());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getStartedDate()).isEqualTo(UPDATED_STARTED_DATE);
        assertThat(testOrder.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testOrder.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orderDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeDelete = orderRepository.findAll().size();

        // Delete the order
        restOrderMockMvc
            .perform(delete(ENTITY_API_URL_ID, order.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
