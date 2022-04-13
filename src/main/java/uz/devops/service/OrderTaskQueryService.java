package uz.devops.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import uz.devops.domain.*; // for static metamodels
import uz.devops.domain.OrderTask;
import uz.devops.repository.OrderTaskRepository;
import uz.devops.service.criteria.OrderTaskCriteria;
import uz.devops.service.dto.OrderTaskDTO;
import uz.devops.service.mapper.OrderTaskMapper;

/**
 * Service for executing complex queries for {@link OrderTask} entities in the database.
 * The main input is a {@link OrderTaskCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link OrderTaskDTO} or a {@link Page} of {@link OrderTaskDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OrderTaskQueryService extends QueryService<OrderTask> {

    private final Logger log = LoggerFactory.getLogger(OrderTaskQueryService.class);

    private final OrderTaskRepository orderTaskRepository;

    private final OrderTaskMapper orderTaskMapper;

    public OrderTaskQueryService(OrderTaskRepository orderTaskRepository, OrderTaskMapper orderTaskMapper) {
        this.orderTaskRepository = orderTaskRepository;
        this.orderTaskMapper = orderTaskMapper;
    }

    /**
     * Return a {@link List} of {@link OrderTaskDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<OrderTaskDTO> findByCriteria(OrderTaskCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<OrderTask> specification = createSpecification(criteria);
        return orderTaskMapper.toDto(orderTaskRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link OrderTaskDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OrderTaskDTO> findByCriteria(OrderTaskCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<OrderTask> specification = createSpecification(criteria);
        return orderTaskRepository.findAll(specification, page).map(orderTaskMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OrderTaskCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<OrderTask> specification = createSpecification(criteria);
        return orderTaskRepository.count(specification);
    }

    /**
     * Function to convert {@link OrderTaskCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<OrderTask> createSpecification(OrderTaskCriteria criteria) {
        Specification<OrderTask> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), OrderTask_.id));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), OrderTask_.status));
            }
            if (criteria.getStartedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartedDate(), OrderTask_.startedDate));
            }
            if (criteria.getEndDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndDate(), OrderTask_.endDate));
            }
            if (criteria.getEmployeeUsername() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmployeeUsername(), OrderTask_.employeeUsername));
            }
            if (criteria.getTaskId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTaskId(), root -> root.join(OrderTask_.task, JoinType.LEFT).get(Task_.id))
                    );
            }
            if (criteria.getOrderId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getOrderId(), root -> root.join(OrderTask_.order, JoinType.LEFT).get(Order_.id))
                    );
            }
        }
        return specification;
    }
}
