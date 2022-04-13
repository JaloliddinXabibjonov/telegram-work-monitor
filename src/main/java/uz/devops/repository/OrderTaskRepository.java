package uz.devops.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.devops.domain.OrderTask;

/**
 * Spring Data SQL repository for the OrderTask entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderTaskRepository extends JpaRepository<OrderTask, Long>, JpaSpecificationExecutor<OrderTask> {}
