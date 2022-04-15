package uz.devops.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.devops.domain.Task;

/**
 * Spring Data SQL repository for the Task entity.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    @Query(
        value = "select distinct task from Task task left join fetch task.professions",
        countQuery = "select count(distinct task) from Task task"
    )
    Page<Task> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct task from Task task left join fetch task.professions")
    List<Task> findAllWithEagerRelationships();

    @Query("select task from Task task left join fetch task.professions where task.id =:id")
    Optional<Task> findOneWithEagerRelationships(@Param("id") Long id);

    @Query(
        "select t from Task t" +
        " inner join Job j on t.job.id = j.id" +
        " inner join Order o on o.job.id = j.id" +
        " inner join OrderTask ot on ot.order.id = o.id" +
        " and ot.task.id = t.id and ot.status <> 'DOING'"
    )
    List<Task> findTasksByOrderId(@Param("orderId") Long orderId);
}
