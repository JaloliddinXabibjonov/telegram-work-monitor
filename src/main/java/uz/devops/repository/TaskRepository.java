package uz.devops.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.devops.domain.Task;

/**
 * Spring Data SQL repository for the Task entity.
 */
@Repository
public interface TaskRepository extends TaskRepositoryWithBagRelationships, JpaRepository<Task, Long> {
    default Optional<Task> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<Task> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<Task> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select distinct task from Task task left join fetch task.job",
        countQuery = "select count(distinct task) from Task task"
    )
    Page<Task> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct task from Task task left join fetch task.job")
    List<Task> findAllWithToOneRelationships();

    @Query("select task from Task task left join fetch task.job where task.id =:id")
    Optional<Task> findOneWithToOneRelationships(@Param("id") Long id);
}
