package uz.devops.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.telegram.telegrambots.meta.api.objects.Message;
import uz.devops.command.SimpleResultData;
import uz.devops.domain.Job;
import uz.devops.domain.Order;
import uz.devops.domain.Task;
import uz.devops.service.dto.JobDTO;

/**
 * Service Interface for managing {@link uz.devops.domain.Job}.
 */
public interface JobService {
    /**
     * Save a job.
     *
     * @param jobDTO the entity to save.
     * @return the persisted entity.
     */
    JobDTO save(JobDTO jobDTO);

    /**
     * Partially updates a job.
     *
     * @param jobDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<JobDTO> partialUpdate(JobDTO jobDTO);

    /**
     * Get all the jobs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<JobDTO> findAll(Pageable pageable);

    /**
     * Get the "id" job.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<JobDTO> findOne(Long id);

    /**
     * Delete the "id" job.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    SimpleResultData<Task> getAvailableTask(Long jobId);

    SimpleResultData<List<Job>> findAvailableJobs();

    SimpleResultData<List<Job>> findDoneJobs();

    Job findJobByOrderId(Long orderId);

    Job createNewJob(Message message);

    void addOrderToJob(Long jobId, Order order);
}
