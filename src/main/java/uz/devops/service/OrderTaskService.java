package uz.devops.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.devops.service.dto.OrderTaskDTO;

/**
 * Service Interface for managing {@link uz.devops.domain.OrderTask}.
 */
public interface OrderTaskService {
    /**
     * Save a orderTask.
     *
     * @param orderTaskDTO the entity to save.
     * @return the persisted entity.
     */
    OrderTaskDTO save(OrderTaskDTO orderTaskDTO);

    /**
     * Partially updates a orderTask.
     *
     * @param orderTaskDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<OrderTaskDTO> partialUpdate(OrderTaskDTO orderTaskDTO);

    /**
     * Get all the orderTasks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<OrderTaskDTO> findAll(Pageable pageable);

    /**
     * Get the "id" orderTask.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<OrderTaskDTO> findOne(Long id);

    /**
     * Delete the "id" orderTask.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
