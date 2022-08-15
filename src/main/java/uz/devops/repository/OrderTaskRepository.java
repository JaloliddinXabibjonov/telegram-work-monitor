package uz.devops.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.devops.domain.OrderTask;
import uz.devops.domain.enumeration.Status;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the OrderTask entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderTaskRepository extends JpaRepository<OrderTask, Long>, JpaSpecificationExecutor<OrderTask> {
    List<OrderTask> findAllByEmployeeUsernameAndStatusNotLike(@Param("username") String username, @Param("status") Status status);

    List<OrderTask> findAllByEmployeeUsernameAndStatus(@Param("username") String username, @Param("status") Status status);

//    @Query(value = "select ot from order_task ot where ot.employee_username =:username AND ot.status =:status order by ot.end_date desc", nativeQuery = true)
    Page<OrderTask> findAllByEmployeeUsernameAndStatusOrderByEndDateDesc(String employeeUsername, Status status, Pageable pageable);

    @Query("select ot from OrderTask ot inner join Order o on ot.order.id =:orderId and ot.task.id =:taskId and ot.status <> 'ACTIVE'")
    Optional<OrderTask> findByOrderAndTaskId(@Param("orderId") Long orderId, @Param("taskId") Long taskId);

    Optional<OrderTask> findByIdAndStatusNotLike(Long id, Status status);


    List<OrderTask> findAllByStatus(@Param("status") Status status);

    @Query(value = "SELECT ot.* from order_task ot join task t on t.id = ot.task_id and t.id>:task_id" +
        "    join job j on j.id=t.job_id" +
        "        where order_id=:order_id and status=:status order by  t.id limit 1 ",nativeQuery = true)
    Optional<OrderTask> findTopByOrderIdAndStatusAndTaskId(@Param("order_id") Long order_id,@Param("status") String status, @Param("task_id")Long task_id);

    boolean existsByOrderIdAndStatusNotLike(Long order_id, Status status);

    List<OrderTask> findAllByStatusAndEmployeeUsernameAndEndDateBetween(Status status, String employeeUsername, Instant endDate, Instant endDate2);
}
