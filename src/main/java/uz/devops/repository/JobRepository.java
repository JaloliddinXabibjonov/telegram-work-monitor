package uz.devops.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.devops.domain.Job;
import uz.devops.domain.Task;

/**
 * Spring Data SQL repository for the Job entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    @Query("select t from Task t where t.job.id =:jobId order by t.id asc")
    List<Task> findTasksByJobId(@Param("jobId") Long jobId);

    @Query("select t from Task t where t.job.id =:jobId and t.id >:taskId order by t.id asc")
    List<Task> findNextTaskByJobId(@Param("jobId") Long jobId, @Param("taskId") Long taskId);

    @Query("select j from Job j inner join Order o on o.job.id = j.id where o.id =:orderId")
    Optional<Job> findJobByOrderId(@Param("orderId") Long orderId);
}
