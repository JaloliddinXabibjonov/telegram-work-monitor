package uz.devops.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.devops.domain.OrderTask;
import uz.devops.domain.enumeration.Status;

/**
 * Spring Data SQL repository for the OrderTask entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderTaskRepository extends JpaRepository<OrderTask, Long>, JpaSpecificationExecutor<OrderTask> {
    List<OrderTask> findAllByEmployeeUsernameAndStatusNotLike(@Param("username") String username, @Param("status") Status status);

    @Query("select ot from OrderTask ot inner join Order o on ot.order.id =:orderId and ot.task.id =:taskId and ot.status <> 'TO_DO'")
    Optional<OrderTask> findByOrderAndTaskId(@Param("orderId") Long orderId, @Param("taskId") Long taskId);

    List<OrderTask> findAllByStatus(@Param("status") Status status);
}
