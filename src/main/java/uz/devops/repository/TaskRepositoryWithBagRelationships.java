package uz.devops.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import uz.devops.domain.Task;

public interface TaskRepositoryWithBagRelationships {
    Optional<Task> fetchBagRelationships(Optional<Task> task);

    List<Task> fetchBagRelationships(List<Task> tasks);

    Page<Task> fetchBagRelationships(Page<Task> tasks);
}
