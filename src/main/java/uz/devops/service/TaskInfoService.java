package uz.devops.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.devops.service.dto.TaskInfoDTO;

/**
 * Service Interface for managing {@link uz.devops.domain.TaskInfo}.
 */
public interface TaskInfoService {
    /**
     * Save a taskInfo.
     *
     * @param taskInfoDTO the entity to save.
     * @return the persisted entity.
     */
    TaskInfoDTO save(TaskInfoDTO taskInfoDTO);

    /**
     * Partially updates a taskInfo.
     *
     * @param taskInfoDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TaskInfoDTO> partialUpdate(TaskInfoDTO taskInfoDTO);

    /**
     * Get all the taskInfos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TaskInfoDTO> findAll(Pageable pageable);

    /**
     * Get the "id" taskInfo.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TaskInfoDTO> findOne(Long id);

    /**
     * Delete the "id" taskInfo.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
