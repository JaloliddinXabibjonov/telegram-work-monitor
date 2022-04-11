package uz.devops.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.devops.domain.TaskInfo;

/**
 * Spring Data SQL repository for the TaskInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaskInfoRepository extends JpaRepository<TaskInfo, Long> {}
