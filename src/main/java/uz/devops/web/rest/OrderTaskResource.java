package uz.devops.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;
import uz.devops.repository.OrderTaskRepository;
import uz.devops.service.OrderTaskQueryService;
import uz.devops.service.OrderTaskService;
import uz.devops.service.criteria.OrderTaskCriteria;
import uz.devops.service.dto.OrderTaskDTO;
import uz.devops.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link uz.devops.domain.OrderTask}.
 */
@RestController
@RequestMapping("/api")
public class OrderTaskResource {

    private final Logger log = LoggerFactory.getLogger(OrderTaskResource.class);

    private static final String ENTITY_NAME = "orderTask";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderTaskService orderTaskService;

    private final OrderTaskRepository orderTaskRepository;

    private final OrderTaskQueryService orderTaskQueryService;

    public OrderTaskResource(
        OrderTaskService orderTaskService,
        OrderTaskRepository orderTaskRepository,
        OrderTaskQueryService orderTaskQueryService
    ) {
        this.orderTaskService = orderTaskService;
        this.orderTaskRepository = orderTaskRepository;
        this.orderTaskQueryService = orderTaskQueryService;
    }

    /**
     * {@code POST  /order-tasks} : Create a new orderTask.
     *
     * @param orderTaskDTO the orderTaskDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderTaskDTO, or with status {@code 400 (Bad Request)} if the orderTask has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/order-tasks")
    public ResponseEntity<OrderTaskDTO> createOrderTask(@RequestBody OrderTaskDTO orderTaskDTO) throws URISyntaxException {
        log.debug("REST request to save OrderTask : {}", orderTaskDTO);
        if (orderTaskDTO.getId() != null) {
            throw new BadRequestAlertException("A new orderTask cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OrderTaskDTO result = orderTaskService.save(orderTaskDTO);
        return ResponseEntity
            .created(new URI("/api/order-tasks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /order-tasks/:id} : Updates an existing orderTask.
     *
     * @param id the id of the orderTaskDTO to save.
     * @param orderTaskDTO the orderTaskDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderTaskDTO,
     * or with status {@code 400 (Bad Request)} if the orderTaskDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orderTaskDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/order-tasks/{id}")
    public ResponseEntity<OrderTaskDTO> updateOrderTask(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OrderTaskDTO orderTaskDTO
    ) throws URISyntaxException {
        log.debug("REST request to update OrderTask : {}, {}", id, orderTaskDTO);
        if (orderTaskDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderTaskDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderTaskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        OrderTaskDTO result = orderTaskService.save(orderTaskDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderTaskDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /order-tasks/:id} : Partial updates given fields of an existing orderTask, field will ignore if it is null
     *
     * @param id the id of the orderTaskDTO to save.
     * @param orderTaskDTO the orderTaskDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderTaskDTO,
     * or with status {@code 400 (Bad Request)} if the orderTaskDTO is not valid,
     * or with status {@code 404 (Not Found)} if the orderTaskDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the orderTaskDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/order-tasks/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OrderTaskDTO> partialUpdateOrderTask(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OrderTaskDTO orderTaskDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update OrderTask partially : {}, {}", id, orderTaskDTO);
        if (orderTaskDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderTaskDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderTaskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OrderTaskDTO> result = orderTaskService.partialUpdate(orderTaskDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderTaskDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /order-tasks} : get all the orderTasks.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orderTasks in body.
     */
    @GetMapping("/order-tasks")
    public ResponseEntity<List<OrderTaskDTO>> getAllOrderTasks(OrderTaskCriteria criteria, Pageable pageable) {
        log.debug("REST request to get OrderTasks by criteria: {}", criteria);
        Page<OrderTaskDTO> page = orderTaskQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /order-tasks/count} : count all the orderTasks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/order-tasks/count")
    public ResponseEntity<Long> countOrderTasks(OrderTaskCriteria criteria) {
        log.debug("REST request to count OrderTasks by criteria: {}", criteria);
        return ResponseEntity.ok().body(orderTaskQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /order-tasks/:id} : get the "id" orderTask.
     *
     * @param id the id of the orderTaskDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orderTaskDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/order-tasks/{id}")
    public ResponseEntity<OrderTaskDTO> getOrderTask(@PathVariable Long id) {
        log.debug("REST request to get OrderTask : {}", id);
        Optional<OrderTaskDTO> orderTaskDTO = orderTaskService.findOne(id);
        return ResponseUtil.wrapOrNotFound(orderTaskDTO);
    }

    /**
     * {@code DELETE  /order-tasks/:id} : delete the "id" orderTask.
     *
     * @param id the id of the orderTaskDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/order-tasks/{id}")
    public ResponseEntity<Void> deleteOrderTask(@PathVariable Long id) {
        log.debug("REST request to delete OrderTask : {}", id);
        orderTaskService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
