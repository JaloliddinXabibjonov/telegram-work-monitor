package uz.devops.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.devops.command.SimpleResultData;
import uz.devops.domain.Task;
import uz.devops.service.dto.TaskDTO;

/**
 * Service Interface for managing {@link uz.devops.domain.Task}.
 */
public interface TaskService {
    /**
     * Save a task.
     *
     * @param taskDTO the entity to save.
     * @return the persisted entity.
     */
    TaskDTO save(TaskDTO taskDTO);

    /**
     * Partially updates a task.
     *
     * @param taskDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TaskDTO> partialUpdate(TaskDTO taskDTO);

    /**
     * Get all the tasks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TaskDTO> findAll(Pageable pageable);

    /**
     * Get all the tasks with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TaskDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" task.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TaskDTO> findOne(Long id);

    /**
     * Delete the "id" task.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    SimpleResultData<Task> findNextTask(Long jobId, Long taskId);

    SimpleResultData<Task> findFirstTaskByJobId(Long jobId);

    void addProfessionToTask(String profName, Task task);

    void checkTaskProfession(String data, Long taskId);

    SimpleResultData<Task> findTaskById(Long taskId);
}
